package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.List;

/** Tên block / thực thể đang nhìn vào. */
public class TargetModule extends HudModule {
    public TargetModule() { super("target", 120, 128, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        HitResult hit = mc.crosshairTarget;
        if (hit == null) return List.of();
        if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult b) {
            String name = mc.world.getBlockState(b.getBlockPos()).getBlock().getName().getString();
            return List.of(Lang.t("hud.target", name));
        }
        if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult e) {
            return List.of(Lang.t("hud.target", e.getEntity().getName().getString()));
        }
        return List.of();
    }
}
