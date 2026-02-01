package com.lildan42.cft.fighterdata.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCFTStateSaver implements CFTState.Saver {
    private File file;
    private final CFTSaveContextSerializer serializer;

    public FileCFTStateSaver(File file, CFTSaveContextSerializer serializer) {
        this.file = file;
        this.serializer = serializer;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void save(CFTState.SaveContext context) throws IOException {
        try(FileOutputStream outputStream = new FileOutputStream(this.file)) {
            this.serializer.writeToSaveContextStream(context, outputStream);
        }
    }

    @Override
    public boolean isSaved() {
        return this.file.exists();
    }

    @Override
    public CFTState.SaveContext load() throws IOException {
        try(FileInputStream inputStream = new FileInputStream(this.file)) {
            return this.serializer.readSaveContextStream(inputStream);
        }
    }
}