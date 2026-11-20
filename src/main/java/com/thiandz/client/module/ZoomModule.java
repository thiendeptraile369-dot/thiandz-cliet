package com.thiandz.client.module;

import com.google.gson.JsonObject;
import com.thiandz.client.ThiandzClient;
import net.minecraft.client.MinecraftClient;

/** Giữ phím (mặc định C) để zoom mượt bằng cách hạ FOV tạm thời. */
public class ZoomModule extends Module {
    private int zoomFov = 30; // FOV tối thiểu của game là 30
    private int saved = -1;
    private double cur;

    public ZoomModule() { super("zoom", true); }

    public int getZoomFov() { return zoomFov; }
    public void setZoomFov(int f) { zoomFov = Math.max(30, Math.min(70, f)); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) { saved = -1; return; }
        var opt = mc.options.getFov();
        boolean want = isEnabled() && ThiandzClient.ZOOM_KEY.isPressed() && mc.currentScreen == null;

        if (want) {
            if (saved < 0) { saved = opt.getValue(); cur = saved; }
            cur += (zoomFov - cur) * 0.4;
            opt.setValue((int) Math.round(cur));
        } else if (saved >= 0) {
            cur += (saved - cur) * 0.4;
            if (Math.abs(saved - cur) < 1) {
                opt.setValue(saved);
                saved = -1;
            } else {
                opt.setValue((int) Math.round(cur));
            }
        }
    }

    @Override
    public void write(JsonObject o) { super.write(o); o.addProperty("fov", zoomFov); }

    @Override
    public void read(JsonObject o) {
        super.read(o);
        if (o.has("fov")) setZoomFov(o.get("fov").getAsInt());
    }
}
