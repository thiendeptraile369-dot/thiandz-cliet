package com.thiandz.client.waypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaypointManager {
    private static final List<Waypoint> LIST = new ArrayList<>();

    public static List<Waypoint> all() { return Collections.unmodifiableList(LIST); }

    public static void add(Waypoint w) {
        remove(w.name);
        LIST.add(w);
    }

    public static boolean remove(String name) {
        return LIST.removeIf(w -> w.name.equalsIgnoreCase(name));
    }

    public static void clear() { LIST.clear(); }
}
