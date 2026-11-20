package com.thiandz.client.module;

import com.thiandz.client.module.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static ZoomModule zoom;
    public static ChatModule chat;
    public static TooltipModule tooltip;
    public static NotificationModule notifications;

    public static void init() {
        // Muốn thêm chức năng mới: tạo class kế thừa Module/HudModule rồi register ở đây
        register(new FpsModule());
        register(new PingModule());
        register(new ClockModule());
        register(new CoordsModule());
        register(new ArmorModule());
        register(new EffectsModule());
        register(new KeystrokesModule());
        register(new ItemCountModule());
        register(new WaypointModule());
        register(new SpeedModule());
        register(new MemoryModule());
        register(new TargetModule());
        notifications = new NotificationModule();
        register(notifications);

        zoom = new ZoomModule();
        register(zoom);
        chat = new ChatModule();
        chat.register();
        register(chat);
        tooltip = new TooltipModule();
        tooltip.register();
        register(tooltip);
    }

    public static void register(Module m) { MODULES.add(m); }

    public static List<Module> all() { return Collections.unmodifiableList(MODULES); }

    public static List<HudModule> hudModules() {
        List<HudModule> out = new ArrayList<>();
        for (Module m : MODULES) if (m instanceof HudModule h) out.add(h);
        return out;
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : MODULES) m.onTick(mc);
    }

    public static void renderHud(DrawContext ctx, RenderTickCounter tick) {
        for (Module m : MODULES) {
            if (m instanceof HudModule h) h.render(ctx, tick);
        }
    }
}
