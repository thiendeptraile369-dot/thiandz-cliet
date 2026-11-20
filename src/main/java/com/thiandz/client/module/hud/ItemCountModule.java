package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

/** Tổng số vật phẩm cùng loại với món đang cầm trong túi đồ. */
public class ItemCountModule extends HudModule {
    public ItemCountModule() { super("itemcount", 120, 72, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        ItemStack hand = mc.player.getMainHandStack();
        if (hand.isEmpty()) return List.of();
        PlayerInventory inv = mc.player.getInventory();
        int total = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (s.isOf(hand.getItem())) total += s.getCount();
        }
        return List.of(Lang.t("hud.itemcount", hand.getName().getString(), total));
    }
}
