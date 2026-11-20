package com.thiandz.client.gui;

import com.thiandz.client.Lang;
import com.thiandz.client.Theme;
import com.thiandz.client.ThiandzClient;
import com.thiandz.client.config.ConfigManager;
import com.thiandz.client.module.HudModule;
import com.thiandz.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Menu cài đặt dạng tab: HUD / Chat / Zoom / Giao diện / Khác. */
public class ThiandzScreen extends Screen {
    private enum Tab {
        HUD("tab.hud"), CHAT("tab.chat"), ZOOM("tab.zoom"), THEME("tab.theme"), OTHER("tab.other");
        final String key;
        Tab(String key) { this.key = key; }
    }

    private static final int PANEL_W = 350, PANEL_H = 230;

    private Tab tab = Tab.HUD;
    private HudModule selected = null;
    private int left, top;

    public ThiandzScreen() { super(Text.literal("Thiandz Client")); }

    @Override
    protected void init() {
        left = (width - PANEL_W) / 2;
        top = Math.max(8, (height - PANEL_H) / 2);

        // Thanh tab
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            ButtonWidget b = ButtonWidget.builder(Lang.text(t.key), btn -> {
                tab = t;
                selected = null;
                clearAndInit();
            }).dimensions(left + 6 + i * 68, top + 8, 66, 20).build();
            b.active = (t != tab);
            addDrawableChild(b);
        }

        switch (tab) {
            case HUD -> initHud();
            case CHAT -> initChat();
            case ZOOM -> initZoom();
            case THEME -> initTheme();
            case OTHER -> initOther();
        }

        addDrawableChild(ButtonWidget.builder(Lang.text("ui.done"), b -> close())
            .dimensions(left + PANEL_W / 2 - 50, top + PANEL_H - 28, 100, 20).build());
    }

    // ---------------- Tab HUD ----------------
    private void initHud() {
        if (selected == null) {
            var list = ModuleManager.hudModules();
            for (int i = 0; i < list.size(); i++) {
                HudModule m = list.get(i);
                int col = i % 2, row = i / 2;
                int x = left + 15 + col * 164;
                int y = top + 40 + row * 24;
                addToggle(x, y, 134, m.getName(), m::isEnabled, m::setEnabled);
                addDrawableChild(ButtonWidget.builder(Text.literal("..."), b -> {
                    selected = m;
                    clearAndInit();
                }).dimensions(x + 136, y, 22, 20).build());
            }
        } else {
            HudModule m = selected;
            int sx = left + (PANEL_W - 200) / 2;
            int y = top + 50;
            addToggle(sx, y, 200, m.getName(), m::isEnabled, m::setEnabled);
            int maxX = client != null ? client.getWindow().getScaledWidth() : 480;
            int maxY = client != null ? client.getWindow().getScaledHeight() : 270;
            addDrawableChild(new ValueSlider(sx, y + 24, 200, "ui.x", 0, maxX, m.getX(),
                v -> m.setPosition((int) Math.round(v), m.getY())));
            addDrawableChild(new ValueSlider(sx, y + 48, 200, "ui.y", 0, maxY, m.getY(),
                v -> m.setPosition(m.getX(), (int) Math.round(v))));
            addDrawableChild(new ValueSlider(sx, y + 72, 200, "ui.opacity", 0, 100, m.getOpacity() * 100,
                v -> m.setOpacity((float) (v / 100.0))));
            addDrawableChild(new ValueSlider(sx, y + 96, 200, "ui.scale", 50, 200, m.getScale() * 100,
                v -> m.setScale((float) (v / 100.0))));
            addDrawableChild(ButtonWidget.builder(Lang.text("ui.back"), b -> {
                selected = null;
                clearAndInit();
            }).dimensions(sx, y + 124, 200, 20).build());
        }
    }

    // ---------------- Tab Chat ----------------
    private void initChat() {
        int sx = left + (PANEL_W - 220) / 2;
        var chat = ModuleManager.chat;
        addToggle(sx, top + 40, 220, Lang.t("module.chat"), chat::isEnabled, chat::setEnabled);
        addToggle(sx, top + 64, 220, Lang.t("ui.timestamp"), chat::isTimestamp, chat::setTimestamp);
        addToggle(sx, top + 88, 220, Lang.t("ui.antispam"), chat::isAntiSpam, chat::setAntiSpam);
    }

    // ---------------- Tab Zoom ----------------
    private void initZoom() {
        int sx = left + (PANEL_W - 220) / 2;
        var zoom = ModuleManager.zoom;
        addToggle(sx, top + 40, 220, Lang.t("module.zoom"), zoom::isEnabled, zoom::setEnabled);
        addDrawableChild(new ValueSlider(sx, top + 64, 220, "ui.fov", 30, 70, zoom.getZoomFov(),
            v -> zoom.setZoomFov((int) Math.round(v))));
    }

    // ---------------- Tab Giao diện ----------------
    private void initTheme() {
        int sx = left + (PANEL_W - 220) / 2;
        addDrawableChild(ButtonWidget.builder(Lang.text("ui.accent"), b -> Theme.nextAccent())
            .dimensions(sx, top + 40, 220, 20).build());
        addDrawableChild(ButtonWidget.builder(Lang.text("ui.reset_pos"), b -> {
            for (HudModule m : ModuleManager.hudModules()) m.resetPosition();
        }).dimensions(sx, top + 64, 220, 20).build());
    }

    // ---------------- Tab Khác ----------------
    private void initOther() {
        int sx = left + (PANEL_W - 220) / 2;
        var tip = ModuleManager.tooltip;
        var noti = ModuleManager.notifications;
        addToggle(sx, top + 40, 220, Lang.t("module.tooltip"), tip::isEnabled, tip::setEnabled);
        addDrawableChild(new ValueSlider(sx, top + 64, 220, "ui.lowhp", 1, 10, noti.getLowHp() / 2.0,
            v -> noti.setLowHp((float) (v * 2.0))));
    }

    // ---------------- Helper ----------------
    private static Text toggleText(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "§a" + Lang.t("ui.on") : "§c" + Lang.t("ui.off")));
    }

    private void addToggle(int x, int y, int w, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(toggleText(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(toggleText(name, get.getAsBoolean()));
        }).dimensions(x, y, w, 20).build());
    }

    /** Thanh trượt giá trị số. */
    private static class ValueSlider extends SliderWidget {
        private final String labelKey;
        private final double min, max;
        private final Consumer<Double> setter;

        ValueSlider(int x, int y, int w, String labelKey, double min, double max, double current,
                    Consumer<Double> setter) {
            super(x, y, w, 20, Text.empty(), Math.max(0, Math.min(1, (current - min) / (max - min))));
            this.labelKey = labelKey;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private double real() { return min + value * (max - min); }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(Lang.t(labelKey) + ": " + Math.round(real())));
        }

        @Override
        protected void applyValue() { setter.accept(real()); }
    }

    // ---------------- Vẽ ----------------
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(left, top, left + PANEL_W, top + PANEL_H, 0xE6101018);
        ctx.fill(left, top, left + PANEL_W, top + 2, 0xFF000000 | Theme.accent);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = left + PANEL_W / 2;

        if (tab == Tab.HUD && selected != null) {
            drawCentered(ctx, "§l" + selected.getName(), cx, top + 36);
        }
        if (tab == Tab.ZOOM) {
            String key = ThiandzClient.ZOOM_KEY.getBoundKeyLocalizedText().getString();
            drawCentered(ctx, "§7" + Lang.t("ui.zoom_key", key), cx, top + 94);
        }
        if (tab == Tab.THEME) {
            ctx.fill(left + (PANEL_W - 220) / 2 + 226, top + 42, left + (PANEL_W - 220) / 2 + 242, top + 58,
                0xFF000000 | Theme.accent);
        }
        if (tab == Tab.OTHER) {
            drawCentered(ctx, "§7" + Lang.t("ui.lang_hint"), cx, top + 94);
        }
        if (tab == Tab.HUD && selected == null) {
            drawCentered(ctx, "§7" + Lang.t("ui.hud_hint"), cx, top + 172);
        }
    }

    private void drawCentered(DrawContext ctx, String s, int cx, int y) {
        ctx.drawText(textRenderer, s, cx - textRenderer.getWidth(s) / 2, y, 0xFFFFFFFF, true);
    }

    @Override
    public void removed() {
        ConfigManager.save();
    }
}
