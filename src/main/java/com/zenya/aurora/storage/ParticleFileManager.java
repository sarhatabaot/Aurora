package com.zenya.aurora.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.Logger;
import com.zenya.aurora.util.RandomNumber;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParticleFileManager {

    public static ParticleFileManager INSTANCE = new ParticleFileManager();
    private final File PARTICLE_FOLDER = new File(Aurora.getInstance().getDataFolder(), "particles");
    private HashMap<String, ParticleFile> particleFileMap = new HashMap<>();
    private Gson gson;

    public ParticleFileManager() {
        //Register RandomNumber GSON Object
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(RandomNumber.class, new RandomNumber.RandomNumberDeserializer());
        gson = builder.create();

        //todo, uhrm, we can get this through bukkit api
        try {
            //Create default particle files if not exists
            if (!PARTICLE_FOLDER.isDirectory() || !Files.newDirectoryStream(PARTICLE_FOLDER.toPath()).iterator().hasNext()) {
                PARTICLE_FOLDER.mkdirs();

                try ( //Use ChatBuilder.fileSeparator instead of File.separator to sanitise escape characters in Windows filepaths
                         JarFile jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().
                                replace("%20", " "))) //Read .jar file //todo no need for all of this..
                        ) {
                    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                    while (entries.hasMoreElements()) {
                        final String path = entries.nextElement().getName();
                        final String name = path.split("/")[path.split("/").length - 1];
                        if (path.contains("particles/") && (name.endsWith(".json") || name.endsWith(".txt"))) {
                            //todo, uhrm, we can get this through bukkit api
                            Files.copy(this.getClass().getClassLoader().getResourceAsStream("particles/" + name), new File(PARTICLE_FOLDER,
                                    name).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } //gives ALL entries in jar
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        //Register all files inside a hashmap as ParticleFile classes
        for (String filename : PARTICLE_FOLDER.list()) {
            if (filename.toLowerCase().endsWith(".json")) {
                registerClass(filename);
            }
        }
    }

    public Set<String> getFileNames() {
        return particleFileMap.keySet();
    }

    public List<ParticleFile> getParticles() {
        List<ParticleFile> classes = new ArrayList<>();
        for (String fileName : getFileNames()) {
            classes.add(particleFileMap.get(fileName));
        }
        return classes;
    }

    public List<String> getParticleNames() {
        List<String> names = new ArrayList<>();
        for (ParticleFile clazz : getParticles()) {
            names.add(clazz.getName().toUpperCase());
        }
        return names;
    }

    public ParticleFile getParticleByName(String particleName) {
        for (ParticleFile particleFile : getParticles()) {
            if (particleFile.getName().equalsIgnoreCase(particleName)) {
                return particleFile;
            }
        }
        return null;
    }

    private ParticleFile getParticleByFile(String fileName) {
        if (!particleFileMap.containsKey(fileName) || particleFileMap.get(fileName) == null) {
            registerClass(fileName);
        }
        return particleFileMap.get(fileName);
    }

    private void registerClass(String filename) {
        try {
            particleFileMap.put(filename, gson.fromJson(new FileReader(new File(PARTICLE_FOLDER, filename)), ParticleFile.class));
        } catch (Exception e) {
            Logger.logError("Error parsing particle file " + filename);
            e.printStackTrace();
            particleFileMap.put(filename, null);
        }
    }

    private void unregisterClass(String name) {
        particleFileMap.remove(name);
    }

    public static void reload() {
        INSTANCE = new ParticleFileManager();
    }
}
