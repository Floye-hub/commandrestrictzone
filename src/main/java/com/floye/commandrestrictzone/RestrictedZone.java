package com.floye.commandrestrictzone;

import net.minecraft.util.math.Box;

import java.util.List;

public class RestrictedZone {
    private String name;
    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;
    private List<String> restrictedCommands;

    public RestrictedZone(String name, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, List<String> restrictedCommands) {
        this.name = name;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.restrictedCommands = restrictedCommands;
    }

    public String getName() {
        return name;
    }

    public Box toBox() {
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public List<String> getRestrictedCommands() {
        return restrictedCommands;
    }

    public void addRestrictedCommand(String command) {
        if (!restrictedCommands.contains(command)) {
            restrictedCommands.add(command);
        }
    }

    public boolean isCommandRestricted(String command) {
        return restrictedCommands.stream().anyMatch(command::equalsIgnoreCase);
    }
}