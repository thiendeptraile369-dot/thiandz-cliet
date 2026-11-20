package com.thiandz.client.module;

import com.google.gson.JsonObject;
import com.thiandz.client.Lang;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String id;
    private boolean enabled;

    protected Module(String id, boolean defaultEnabled) {
        this.id = id;
        this.enabled = defaultEnabled;
    }

    public String getId() { return id; }
    public String getName() { return Lang.t("module." + id); }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { this.enabled = !this.enabled; }

    /** Gọi mỗi client tick (20 lần/giây). */
    public void onTick(MinecraftClient mc) {}

    /** Ghi cấu hình ra JSON. Module con ghi đè để thêm tùy chọn riêng. */
    public void write(JsonObject o) {
        o.addProperty("enabled", enabled);
    }

    /** Đọc cấu hình từ JSON. */
    public void read(JsonObject o) {
        if (o.has("enabled")) enabled = o.get("enabled").getAsBoolean();
    }
}
