package com.thiandz.client.module.hud;

import com.thiandz.client.module.HudModule;
import com.thiandz.client.waypoint.Waypoint;
import com.thiandz.client.waypoint.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Hiển thị waypoint gần nhất cùng khoảng cách và mũi tên chỉ hướng. */
public class WaypointModule extends HudModule {
    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

    public WaypointModule() { super("waypoints", 6, 175, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        String dim = mc.world.getRegistryKey().getValue().toString();
        double px = mc.player.getX(), pz = mc.player.getZ();

        List<Waypoint> near = new ArrayList<>();
        for (Waypoint w : WaypointManager.all()) {
            if (w.dimension.equals(dim)) near.add(w);
        }
        near.sort(Comparator.comparingDouble(w -> dist(w, px, mc.player.getY(), pz)));

        List<String> out = new ArrayList<>();
        for (int i = 0; i < Math.min(5, near.size()); i++) {
            Waypoint w = near.get(i);
            double dx = w.x + 0.5 - px, dz = w.z + 0.5 - pz;
            // Yaw Minecraft: 0 = nam (+Z), tăng theo chiều kim đồng hồ
            float target = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float rel = MathHelper.wrapDegrees(target - mc.player.getYaw());
            int idx = Math.floorMod(Math.round(rel / 45f), 8);
            out.add("§b" + ARROWS[idx] + " §f" + w.name + " §7" + (int) dist(w, px, mc.player.getY(), pz) + "m");
        }
        return out;
    }

    private static double dist(Waypoint w, double px, double py, double pz) {
        double dx = w.x + 0.5 - px, dy = w.y - py, dz = w.z + 0.5 - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
