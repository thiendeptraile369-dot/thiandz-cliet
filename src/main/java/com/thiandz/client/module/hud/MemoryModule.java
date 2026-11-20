package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** RAM Java đang dùng / tối đa. */
public class MemoryModule extends HudModule {
    public MemoryModule() { super("memory", 120, 110, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
        long max = rt.maxMemory() / 1048576L;
        String c = used * 100 / Math.max(1, max) > 85 ? "§c" : "§f";
        return List.of(c + Lang.t("hud.memory", used, max));
    }
}
