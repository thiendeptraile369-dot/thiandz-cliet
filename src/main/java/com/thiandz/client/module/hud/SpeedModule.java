package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** Tốc độ di chuyển ngang (block/giây). */
public class SpeedModule extends HudModule {
    private double px, pz;
    private boolean has = false;
    private double bps = 0;

    public SpeedModule() { super("speed", 120, 92, true); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) { has = false; return; }
        double x = mc.player.getX(), z = mc.player.getZ();
        if (has) {
            double d = Math.sqrt((x - px) * (x - px) + (z - pz) * (z - pz)) * 20.0;
            bps += (d - bps) * 0.3; // làm mượt
        }
        px = x;
        pz = z;
        has = true;
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        return List.of(Lang.t("hud.speed", String.format("%.1f", bps)));
    }
}
