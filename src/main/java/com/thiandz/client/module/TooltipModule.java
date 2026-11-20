package com.thiandz.client.module;

import com.thiandz.client.Lang;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Tooltip cải tiến: thêm độ bền (kèm %) và số lượng stack. */
public class TooltipModule extends Module {
    public TooltipModule() { super("tooltip", true); }

    public void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (!isEnabled()) return;
            if (stack.isDamageable() && !type.isAdvanced()) {
                int max = stack.getMaxDamage();
                int left = max - stack.getDamage();
                int pct = Math.round(100f * left / max);
                lines.add(Text.literal(Lang.t("tooltip.durability", left, max, pct)).formatted(Formatting.GRAY));
            }
            if (stack.getMaxCount() > 1) {
                lines.add(Text.literal(Lang.t("tooltip.stack", stack.getCount(), stack.getMaxCount()))
                    .formatted(Formatting.DARK_GRAY));
            }
        });
    }
}
