package com.thiandz.client.module.hud;

import com.google.gson.JsonObject;
import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Thông báo gọn: nhặt đồ và cảnh báo máu thấp. */
public class NotificationModule extends HudModule {
    private record Entry(String text, long expire) {}

    private final List<Entry> entries = new ArrayList<>();
    private Map<Item, Integer> prev = new HashMap<>();
    private boolean prevValid = false;
    private boolean lowFlag = false;
    private float lowHp = 6f; // 3 tim

    public NotificationModule() { super("notify", 6, 215, true); }

    public float getLowHp() { return lowHp; }
    public void setLowHp(float hp) { lowHp = Math.max(2f, Math.min(20f, hp)); }

    private void push(String text) {
        long now = System.currentTimeMillis();
        entries.add(new Entry(text, now + 3000));
        while (entries.size() > 5) entries.remove(0);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!isEnabled() || mc.player == null) {
            prevValid = false;
            lowFlag = false;
            return;
        }

        // Máu thấp
        float hp = mc.player.getHealth();
        if (hp <= lowHp && !lowFlag) {
            lowFlag = true;
            push("§c" + Lang.t("notify.lowhp", Math.round(hp) / 2.0));
        } else if (hp > lowHp + 2f) {
            lowFlag = false;
        }

        // Nhặt đồ: so sánh tổng số lượng từng loại giữa 2 tick
        Map<Item, Integer> cur = new HashMap<>();
        Map<Item, String> names = new HashMap<>();
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            cur.merge(s.getItem(), s.getCount(), Integer::sum);
            names.putIfAbsent(s.getItem(), s.getName().getString());
        }
        if (prevValid) {
            for (Map.Entry<Item, Integer> e : cur.entrySet()) {
                int old = prev.getOrDefault(e.getKey(), 0);
                if (e.getValue() > old) {
                    push("§a" + Lang.t("notify.pickup", e.getValue() - old, names.get(e.getKey())));
                }
            }
        }
        prev = cur;
        prevValid = true;
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        entries.removeIf(e -> e.expire() < now);
        List<String> out = new ArrayList<>();
        for (Entry e : entries) out.add(e.text());
        return out;
    }

    @Override
    public void write(JsonObject o) {
        super.write(o);
        o.addProperty("lowHp", lowHp);
    }

    @Override
    public void read(JsonObject o) {
        super.read(o);
        if (o.has("lowHp")) setLowHp(o.get("lowHp").getAsFloat());
    }
}
