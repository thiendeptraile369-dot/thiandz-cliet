package com.thiandz.client.module;

import com.google.gson.JsonObject;
import com.thiandz.client.Theme;
import com.thiandz.client.ThiandzClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

/**
 * Lớp cha của mọi thành phần HUD: nền bán trong suốt bo góc,
 * viền màu nhấn, hiệu ứng mờ dần khi bật/tắt, chỉnh vị trí/độ trong suốt/kích thước.
 */
public abstract class HudModule extends Module {
    protected final int defaultX, defaultY;
    protected int x, y;
    protected float opacity = 0.6f; // độ trong suốt nền
    protected float scale = 1.0f;   // kích thước
    private boolean errorLogged = false;
    private float anim = 0f;        // 0 = ẩn, 1 = hiện (cho hiệu ứng fade)

    protected HudModule(String id, int defaultX, int defaultY, boolean defaultEnabled) {
        super(id, defaultEnabled);
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.x = defaultX;
        this.y = defaultY;
    }

    /** Mỗi module trả về các dòng chữ cần hiển thị (hỗ trợ mã màu §). */
    protected abstract List<String> getLines(MinecraftClient mc);

    public void render(DrawContext ctx, RenderTickCounter tick) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            boolean visible = isEnabled() && mc.player != null && mc.world != null && !mc.options.hudHidden;
            anim += ((visible ? 1f : 0f) - anim) * 0.25f;
            if (anim < 0.03f || mc.player == null || mc.world == null) return;

            List<String> lines = getLines(mc);
            if (lines == null || lines.isEmpty()) return;

            TextRenderer font = mc.textRenderer;
            int padding = 4;
            int lineHeight = font.fontHeight + 2;

            int width = 0;
            for (String s : lines) width = Math.max(width, font.getWidth(s));
            width += padding * 2 + 2;
            int height = lines.size() * lineHeight + padding * 2 - 2;

            int bg = ((int) (opacity * anim * 255) << 24) | 0x101018;
            int accent = ((int) (anim * 255) << 24) | (Theme.accent & 0xFFFFFF);
            int textColor = (Math.max(4, (int) (anim * 255)) << 24) | 0xFFFFFF;

            var m = ctx.getMatrices();
            m.pushMatrix();
            try {
                m.translate((float) x, (float) y);
                m.scale(scale, scale);

                // nền bo góc (bỏ pixel 4 góc)
                ctx.fill(1, 0, width - 1, height, bg);
                ctx.fill(0, 1, 1, height - 1, bg);
                ctx.fill(width - 1, 1, width, height - 1, bg);
                // viền nhấn bên trái
                ctx.fill(0, 2, 2, height - 2, accent);

                int ty = padding;
                for (String s : lines) {
                    ctx.drawText(font, s, padding + 2, ty, textColor, true);
                    ty += lineHeight;
                }
            } finally {
                m.popMatrix();
            }
        } catch (Throwable t) {
            // Không để HUD làm crash game, nhưng ghi lỗi vào latest.log (1 lần / module)
            if (!errorLogged) {
                errorLogged = true;
                ThiandzClient.LOGGER.error("Loi khi ve HUD module " + getId(), t);
            }
        }
    }

    @Override
    public void write(JsonObject o) {
        super.write(o);
        o.addProperty("x", x);
        o.addProperty("y", y);
        o.addProperty("opacity", opacity);
        o.addProperty("scale", scale);
    }

    @Override
    public void read(JsonObject o) {
        super.read(o);
        if (o.has("x")) x = o.get("x").getAsInt();
        if (o.has("y")) y = o.get("y").getAsInt();
        if (o.has("opacity")) setOpacity(o.get("opacity").getAsFloat());
        if (o.has("scale")) setScale(o.get("scale").getAsFloat());
    }

    public void resetPosition() { x = defaultX; y = defaultY; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public float getOpacity() { return opacity; }
    public void setOpacity(float o) { this.opacity = Math.max(0f, Math.min(1f, o)); }
    public float getScale() { return scale; }
    public void setScale(float s) { this.scale = Math.max(0.5f, Math.min(2f, s)); }
}
