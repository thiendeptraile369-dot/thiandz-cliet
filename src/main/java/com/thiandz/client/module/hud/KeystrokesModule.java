package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayDeque;
import java.util.List;

/** Hiển thị phím WASD, Space và CPS (số click mỗi giây) chuột trái/phải. */
public class KeystrokesModule extends HudModule {
    private final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
    private final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
    private boolean lastLeft, lastRight;

    public KeystrokesModule() { super("keystrokes", 6, 130, true); }

    private static String k(KeyBinding kb, String label) {
        return (kb.isPressed() ? "§a" : "§8") + "[" + label + "]";
    }

    private static int cps(ArrayDeque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
        return q.size();
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        long now = System.currentTimeMillis();

        // Phát hiện click bằng cạnh lên của phím tấn công / dùng
        boolean l = mc.options.attackKey.isPressed();
        boolean r = mc.options.useKey.isPressed();
        if (l && !lastLeft) leftClicks.addLast(now);
        if (r && !lastRight) rightClicks.addLast(now);
        lastLeft = l;
        lastRight = r;

        var o = mc.options;
        return List.of(
            "    " + k(o.forwardKey, "W"),
            k(o.leftKey, "A") + k(o.backKey, "S") + k(o.rightKey, "D"),
            "  " + k(o.jumpKey, "SPACE"),
            "§7" + Lang.t("hud.cps", cps(leftClicks, now), cps(rightClicks, now))
        );
    }
}
