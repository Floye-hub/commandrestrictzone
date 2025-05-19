package com.floye.commandrestrictzone;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.List;

public class RestrictedZone {
    private String name;
    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;
    private List<String> restrictedCommands;
    private Identifier dimension; // Ajout de l'identifiant de la dimension

    public RestrictedZone(String name, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, List<String> restrictedCommands, Identifier dimension) {
        this.name = name;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.restrictedCommands = restrictedCommands;
        this.dimension = dimension;
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

    // Ajout d'un getter pour la dimension
    public Identifier getDimension() {
        return dimension;
    }
}