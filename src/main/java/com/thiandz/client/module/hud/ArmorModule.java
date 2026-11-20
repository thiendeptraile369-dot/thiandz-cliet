package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final String[] KEYS = {"head", "chest", "legs", "feet"};

    public ArmorModule() { super("armor", 120, 6, true); }

    static String durability(ItemStack s) {
        int max = s.getMaxDamage();
        int left = max - s.getDamage();
        float pct = (float) left / max;
        String c = pct > 0.5f ? "§a" : pct > 0.25f ? "§e" : "§c";
        return c + left + "§7/" + max;
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack s = mc.player.getEquippedStack(SLOTS[i]);
            if (!s.isEmpty() && s.isDamageable()) {
                out.add("§7" + Lang.t("hud.armor." + KEYS[i]) + " " + durability(s));
            }
        }
        ItemStack hand = mc.player.getMainHandStack();
        if (!hand.isEmpty()) {
            String line = "§f" + hand.getName().getString();
            if (hand.isDamageable()) line += " " + durability(hand);
            if (hand.getCount() > 1) line += " §7x" + hand.getCount();
            out.add(line);
        }
        return out;
    }
}
