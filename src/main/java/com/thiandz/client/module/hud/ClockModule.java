package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockModule extends HudModule {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClockModule() { super("clock", 6, 42, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        // Giờ trong game (xấp xỉ theo tổng số tick; sai lệch nếu server chỉnh /time)
        long t = mc.world.getTime() % 24000L;
        int hour = (int) ((t / 1000 + 6) % 24);
        int min = (int) ((t % 1000) * 60 / 1000);
        String weather = mc.world.isThundering() ? Lang.t("hud.weather.thunder")
            : mc.world.isRaining() ? Lang.t("hud.weather.rain")
            : Lang.t("hud.weather.clear");
        return List.of(
            Lang.t("hud.real_time", LocalTime.now().format(FMT)),
            Lang.t("hud.game_time", String.format("%02d:%02d", hour, min)),
            Lang.t("hud.weather", weather),
            Lang.t("hud.day", mc.world.getTime() / 24000L + 1)
        );
    }
}
