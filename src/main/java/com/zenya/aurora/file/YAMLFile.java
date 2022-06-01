package com.zenya.aurora.file;

import com.zenya.aurora.Aurora;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class YAMLFile extends StorageFile {

    private FileConfiguration origConfig;
    private FileConfiguration config;

    public YAMLFile(String fileName) {
        this(Aurora.getInstance().getDataFolder().getPath(), fileName);
    }

    public YAMLFile(String directory, String fileName) {
        this(directory, fileName, null, false, null, null);
    }

    /**
     *
     * @param directory Directory the file exists in.
     * @param fileName Full name of the file, excluding its directory.
     * @param fileVersion Version of the file as specified in "config-version.
     * @param resetFile Whether or not the file should be deleted and overwritten by the original resource.
     * @param ignoredNodes Nodes that will use the latest resource config's values.
     * @param replaceNodes Nodes that will use old config values instead of being appended (applicable to nested keys)
     */
    public YAMLFile(String directory, String fileName, Integer fileVersion, boolean resetFile, List<String> ignoredNodes, List<String> replaceNodes) {
        super(directory, fileName, fileVersion, resetFile);

        this.origConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Aurora.getInstance().getResource(fileName)));
        this.config = YamlConfiguration.loadConfiguration(file);

        if (fileVersion != null) {
            try {
                updateFile(ignoredNodes, replaceNodes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getFileVersion() {
        return getInt("config-version");
    }

    private void updateFile(List<String> ignoredNodes, List<String> replaceNodes) throws IOException {
        if (!file.exists()) {
            //Init file
            Aurora.getInstance().saveResource(fileName, false);
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            //Reset file for backward-compatibility
            if (getFileVersion() > fileVersion) {
                resetFile = true;
            }

            //Update file
            if (getFileVersion() != fileVersion) {
                File oldConfigFile = new File(directory, fileName + ".v" + getFileVersion());
                FileUtil.copy(file, oldConfigFile);
                file.delete();
                origConfig.save(file);

                FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
                config = YamlConfiguration.loadConfiguration(file);

                //Add old values
                if (!resetFile) {
                    for (String node : config.getKeys(true)) {
                        if (ignoredNodes != null && ignoredNodes.contains(node)) {
                            continue;
                        }
                        if (oldConfig.getKeys(true).contains(node + ".")) {
                            continue;
                        }
                        if (replaceNodes != null && replaceNodes.contains(node)) {
                            config.set(node, null);
                            config.createSection(node);
                        }
                        if (oldConfig.contains(node, true)) {
                            config.set(node, oldConfig.get(node));
                        }
                    }
                    config.save(file);
                } else {
                    //Doing this keeps all the yml file comments
                    Aurora.getInstance().saveResource(fileName, true);
                }
            }
        }
    }

    public String getString(String node) {
        String val;
        try {
            val = config.getString(node);
        } catch (Exception e) {
            val = "";
        }
        return val;
    }

    public int getInt(String node) {
        int val;
        try {
            val = config.getInt(node);
        } catch (Exception e) {
            val = 0;
        }
        return val;
    }

    public double getDouble(String node) {
        double val;
        try {
            val = config.getDouble(node);
        } catch (Exception e) {
            val = 0d;
        }
        return val;
    }

    public boolean getBool(String node) {
        boolean val;
        try {
            val = config.getBoolean(node);
        } catch (Exception e) {
            val = false;
        }
        return val;
    }

    public List<String> getKeys(String node) {
        List<String> val;
        try {
            val = new ArrayList<>(config.getConfigurationSection(node).getKeys(false));
        } catch (Exception e) {
            val = new ArrayList<>();
            e.printStackTrace();
        }
        return val;
    }

    public List<String> getList(String node) {
        List<String> val;
        try {
            val = new ArrayList<>(config.getStringList(node));
        } catch (Exception e) {
            val = new ArrayList<>();
            e.printStackTrace();
        }
        return val;
    }

    public boolean isList(String node) {
        return config.isList(node);
    }

    public boolean listContains(String node, String item) {
        List<String> list = getList(node);
        return list != null && !list.isEmpty() && list.contains(item);
    }
}
