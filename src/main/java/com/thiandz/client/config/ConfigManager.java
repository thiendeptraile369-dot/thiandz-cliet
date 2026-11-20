package com.thiandz.client.config;

import com.google.gson.*;
import com.thiandz.client.Theme;
import com.thiandz.client.module.Module;
import com.thiandz.client.module.ModuleManager;
import com.thiandz.client.waypoint.Waypoint;
import com.thiandz.client.waypoint.WaypointManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Lưu/đọc cấu hình tại .minecraft/config/thiandzclient.json */
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("thiandzclient.json");

    public static void save() {
        try {
            JsonObject root = new JsonObject();

            JsonObject theme = new JsonObject();
            theme.addProperty("accent", Theme.accent);
            root.add("theme", theme);

            JsonObject modules = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject o = new JsonObject();
                m.write(o);
                modules.add(m.getId(), o);
            }
            root.add("modules", modules);

            JsonArray wps = new JsonArray();
            for (Waypoint w : WaypointManager.all()) {
                JsonObject o = new JsonObject();
                o.addProperty("name", w.name);
                o.addProperty("x", w.x);
                o.addProperty("y", w.y);
                o.addProperty("z", w.z);
                o.addProperty("dimension", w.dimension);
                wps.add(o);
            }
            root.add("waypoints", wps);

            Files.writeString(FILE, GSON.toJson(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();

            if (root.has("theme") && root.getAsJsonObject("theme").has("accent")) {
                Theme.accent = root.getAsJsonObject("theme").get("accent").getAsInt();
            }
            if (root.has("modules")) {
                JsonObject modules = root.getAsJsonObject("modules");
                for (Module m : ModuleManager.all()) {
                    if (modules.has(m.getId())) m.read(modules.getAsJsonObject(m.getId()));
                }
            }
            if (root.has("waypoints")) {
                WaypointManager.clear();
                for (JsonElement el : root.getAsJsonArray("waypoints")) {
                    JsonObject o = el.getAsJsonObject();
                    WaypointManager.add(new Waypoint(
                        o.get("name").getAsString(),
                        o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt(),
                        o.get("dimension").getAsString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
