package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class CoordsModule extends HudModule {
    public CoordsModule() { super("coords", 6, 90, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        BlockPos pos = mc.player.getBlockPos();
        String dir = Lang.t("dir." + mc.player.getHorizontalFacing().asString());
        int yaw = Math.round(MathHelper.wrapDegrees(mc.player.getYaw()));

        String biome = mc.world.getBiome(pos).getKey()
            .map(k -> {
                String key = "biome." + k.getValue().getNamespace() + "." + k.getValue().getPath();
                return I18n.hasTranslation(key) ? I18n.translate(key) : k.getValue().getPath();
            }).orElse("?");

        return List.of(
            Lang.t("hud.xyz", pos.getX(), pos.getY(), pos.getZ()),
            Lang.t("hud.facing", dir, yaw),
            Lang.t("hud.biome", biome)
        );
    }
}
