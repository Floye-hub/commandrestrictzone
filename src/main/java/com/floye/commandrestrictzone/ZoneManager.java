package com.floye.commandrestrictzone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZoneManager {
    private static final String ZONES_FILE = "config/commandrestrictzone/zones.json";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter()) // Pour la (dé)sérialisation de Identifier
            .create();
    private static final List<RestrictedZone> zones = new ArrayList<>();

    // Chargement des zones depuis le fichier JSON
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

    // À ajouter dans ZoneManager.java
    public static List<RestrictedZone> getZones() {
        return new ArrayList<>(zones);
    }
    // Sauvegarde des zones dans le fichier JSON
    public static void saveZones() {
        File file = new File(ZONES_FILE);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            Commandrestrictzone.LOGGER.error("Failed to save zones: ", e);
        }
    }

    // Ajoute une zone seulement si elle n'existe pas déjà
    public static void addZone(RestrictedZone zone) {
        if (getZoneByName(zone.getName()) != null) {
            return;
        }
        zones.add(zone);
        saveZones();
    }

    // Retourne une zone par son nom
    public static RestrictedZone getZoneByName(String name) {
        return zones.stream()
                .filter(zone -> zone.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    // Ajoute une commande restreinte à une zone existante
    public static boolean addRestrictedCommandToZone(String zoneName, String command) {
        RestrictedZone zone = getZoneByName(zoneName);
        if (zone != null) {
            zone.addRestrictedCommand(command);
            saveZones();
            return true;
        }
        return false;
    }

    // Supprime une zone par son nom
    public static boolean removeZone(String name) {
        RestrictedZone zone = getZoneByName(name);
        if (zone != null) {
            zones.remove(zone);
            saveZones();
            return true;
        }
        return false;
    }

    // Supprime une commande restreinte d'une zone
    public static boolean removeRestrictedCommandFromZone(String zoneName, String command) {
        RestrictedZone zone = getZoneByName(zoneName);
        if (zone != null) {
            boolean removed = zone.getRestrictedCommands().removeIf(cmd -> cmd.equalsIgnoreCase(command));
            if (removed) {
                saveZones();
            }
            return removed;
        }
        return false;
    }

    // Vérifie si une commande est bloquée dans une zone donnée
    public static boolean isCommandRestrictedInZone(String command, Box box, Identifier dimension) {
        return zones.stream()
                .filter(zone -> zone.toBox().intersects(box) && zone.getDimension().equals(dimension)) // Ajout de la vérification de la dimension
                .anyMatch(zone -> zone.isCommandRestricted(command));
    }

    // Classe interne pour la sérialisation et désérialisation de Identifier avec Gson
    public static class IdentifierTypeAdapter extends com.google.gson.TypeAdapter<Identifier> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, Identifier value) throws java.io.IOException {
            out.value(value.toString());
        }

        @Override
        public Identifier read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            return Identifier.of(in.nextString());
        }
    }
}