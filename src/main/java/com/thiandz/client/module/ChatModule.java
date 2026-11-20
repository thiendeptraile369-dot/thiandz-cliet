package com.thiandz.client.module;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Chat gọn hơn: thêm giờ vào mỗi tin nhắn và lọc tin nhắn lặp (spam). */
public class ChatModule extends Module {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    private boolean timestamp = true;
    private boolean antiSpam = true;
    private String lastText = "";
    private long lastTime = 0;

    public ChatModule() { super("chat", true); }

    public boolean isTimestamp() { return timestamp; }
    public void setTimestamp(boolean b) { timestamp = b; }
    public boolean isAntiSpam() { return antiSpam; }
    public void setAntiSpam(boolean b) { antiSpam = b; }

    public void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signed, sender, params, ts) -> handle(message));
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> overlay || handle(message));
    }

    /** @return true nếu cho phép tin nhắn gốc hiển thị như bình thường */
    private boolean handle(Text message) {
        if (!isEnabled()) return true;
        long now = System.currentTimeMillis();
        String plain = message.getString();

        if (antiSpam && plain.equals(lastText) && now - lastTime < 8000) {
            lastTime = now;
            return false; // bỏ tin nhắn lặp
        }
        lastText = plain;
        lastTime = now;

        if (timestamp) {
            MinecraftClient mc = MinecraftClient.getInstance();
            MutableText out = Text.literal("[" + LocalTime.now().format(FMT) + "] ").formatted(Formatting.GRAY)
                .append(message);
            mc.inGameHud.getChatHud().addMessage(out);
            return false; // đã tự thêm bản có giờ
        }
        return true;
    }

    @Override
    public void write(JsonObject o) {
        super.write(o);
        o.addProperty("timestamp", timestamp);
        o.addProperty("antiSpam", antiSpam);
    }

    @Override
    public void read(JsonObject o) {
        super.read(o);
        if (o.has("timestamp")) timestamp = o.get("timestamp").getAsBoolean();
        if (o.has("antiSpam")) antiSpam = o.get("antiSpam").getAsBoolean();
    }
}
