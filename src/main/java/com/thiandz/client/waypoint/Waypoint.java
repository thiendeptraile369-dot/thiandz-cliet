package com.thiandz.client.waypoint;

public class Waypoint {
    public final String name;
    public final int x, y, z;
    public final String dimension;

    public Waypoint(String name, int x, int y, int z, String dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
}
