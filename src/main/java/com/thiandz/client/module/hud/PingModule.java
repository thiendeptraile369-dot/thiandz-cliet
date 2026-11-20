package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class PingModule extends HudModule {
    public PingModule() { super("ping", 6, 24, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        var handler = mc.getNetworkHandler();
        if (handler == null) return List.of();
        PlayerListEntry e = handler.getPlayerListEntry(mc.player.getUuid());
        int ping = e == null ? 0 : e.getLatency();
        String c = ping < 80 ? "§a" : ping < 150 ? "§e" : "§c";
        return List.of(c + Lang.t("hud.ping", ping));
    }
}
