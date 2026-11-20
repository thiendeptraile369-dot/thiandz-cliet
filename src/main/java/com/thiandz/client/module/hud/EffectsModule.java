package com.thiandz.client.module.hud;

import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class EffectsModule extends HudModule {
    public EffectsModule() { super("effects", 380, 6, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        List<String> out = new ArrayList<>();
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            String name = I18n.translate(e.getTranslationKey());
            String time;
            if (e.isInfinite()) {
                time = "∞";
            } else {
                int s = e.getDuration() / 20;
                time = String.format("%d:%02d", s / 60, s % 60);
            }
            out.add("§f" + name + " " + (e.getAmplifier() + 1) + " §7" + time);
        }
        return out;
    }
}
