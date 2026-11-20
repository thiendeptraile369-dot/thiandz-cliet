package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class FpsModule extends HudModule {
    public FpsModule() { super("fps", 6, 6, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        int fps = mc.getCurrentFps();
        String c = fps >= 60 ? "§a" : fps >= 30 ? "§e" : "§c";
        return List.of(c + Lang.t("hud.fps", fps));
    }
}
