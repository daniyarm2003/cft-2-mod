package com.lildan42.cft.initialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class CFT2Properties {

    @JsonIgnore
    private final File saveFile;

    @JsonIgnore
    private final boolean debugMode;

    public CFT2Properties(File saveFile, boolean debugMode) {
        this.saveFile = saveFile;
        this.debugMode = debugMode;
    }

    @JsonCreator
    public CFT2Properties(@JsonProperty("saveFileName") String saveFileName, @JsonProperty("debugMode") boolean debugMode) {
        this(new File(saveFileName), debugMode);
    }

    @JsonIgnore
    public File getSaveFile() {
        return this.saveFile;
    }

    public String getSaveFileName() {
        return this.saveFile.getPath();
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }
}
