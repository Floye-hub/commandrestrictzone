package com.floye.commandrestrictzone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.math.Box;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZoneManager {
    private static final String ZONES_FILE = "config/commandrestrictzone/zones.json";
    private static final Gson GSON = new Gson();
    private static final List<RestrictedZone> zones = new ArrayList<>();

    // Méthode pour charger les zones depuis un fichier JSON
    public static void loadZones() {
        File file = new File(ZONES_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<RestrictedZone>>() {}.getType();
                List<RestrictedZone> loadedZones = GSON.fromJson(reader, listType);
                if (loadedZones != null) {
                    zones.clear();
                    zones.addAll(loadedZones);
                }
            } catch (Exception e) {
                Commandrestrictzone.LOGGER.error("Failed to load zones: ", e);
            }
        }
    }

    // Méthode pour sauvegarder les zones dans un fichier JSON
    public static void saveZones() {
        File file = new File(ZONES_FILE);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            Commandrestrictzone.LOGGER.error("Failed to save zones: ", e);
        }
    }

    // Méthode pour ajouter une nouvelle zone
    public static void addZone(RestrictedZone zone) {
        zones.add(zone);
        saveZones();
    }

    // Récupérer une zone par son nom
    public static RestrictedZone getZoneByName(String name) {
        return zones.stream().filter(zone -> zone.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static boolean addRestrictedCommandToZone(String zoneName, String command) {
        RestrictedZone zone = getZoneByName(zoneName);
        if (zone != null) {
            zone.addRestrictedCommand(command);
            saveZones();
            return true;
        }
        return false;
    }

    // Vérifier si une commande est bloquée dans une zone spécifique
    public static boolean isCommandRestrictedInZone(String command, Box box) {
        return zones.stream()
                .filter(zone -> zone.toBox().intersects(box))
                .anyMatch(zone -> zone.isCommandRestricted(command));
    }
}