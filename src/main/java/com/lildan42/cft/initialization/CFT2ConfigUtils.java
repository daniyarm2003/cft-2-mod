package com.lildan42.cft.initialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fighterdata.state.CFTSaveContextSerializer;
import com.lildan42.cft.fighterdata.state.CFTState;
import com.lildan42.cft.fighterdata.state.FileCFTStateSaver;
import com.lildan42.cft.fighterdata.state.GzipJsonCFTSaveContextSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CFT2ConfigUtils {
    private static final File DEFAULT_CONFIG_FILE = new File("cft-properties.json");
    private static final File DEFAULT_SAVE_FILE = new File("cft2.dat");

    public static CFT2Properties loadCFTProperties(ObjectMapper jsonMapper) {
        try {
            File configFile = DEFAULT_CONFIG_FILE;
            CFT2Properties config;

            if(!configFile.exists()) {
                CFT2Mod.LOGGER.warn("Unable to find CFT configuration file at path \"{}\", a new one will be created", configFile.getAbsolutePath());
                config = new CFT2Properties(DEFAULT_SAVE_FILE, false);

                try(FileOutputStream configStream = new FileOutputStream(configFile)) {
                    jsonMapper.writeValue(configStream, config);
                }
            }
            else {
                try(FileInputStream configStream = new FileInputStream(configFile)) {
                    config = jsonMapper.readValue(configStream, CFT2Properties.class);
                }
            }

            return config;
        }
        catch (IOException e) {
            CFT2Mod.LOGGER.error("Unable to load or write to CFT configuration due to an I/O error: {}", e.getLocalizedMessage());
            CFT2Mod.LOGGER.error("Default configuration will be used instead");
        }

        return new CFT2Properties(DEFAULT_SAVE_FILE, false);
    }

    public static CFTState loadCFTState(ObjectMapper jsonMapper, CFT2Properties config) {
        File saveFile = config.getSaveFile();

        CFTSaveContextSerializer contextSerializer = new GzipJsonCFTSaveContextSerializer(jsonMapper);
        FileCFTStateSaver stateSaver = new FileCFTStateSaver(saveFile, contextSerializer);
        CFTState cftState = new CFTState(stateSaver);

        try {
            if(!stateSaver.isSaved()) {
                CFT2Mod.LOGGER.warn("Unable to find CFT state file at path \"{}\", a new one will be created", saveFile.getAbsolutePath());
                cftState.saveState();

                return cftState;
            }

            cftState.loadState();
        }
        catch (IOException e) {
            CFT2Mod.LOGGER.error("Unable to load or save CFT state due to an I/O error: {}", e.getLocalizedMessage());
        }

        return cftState;
    }
}
