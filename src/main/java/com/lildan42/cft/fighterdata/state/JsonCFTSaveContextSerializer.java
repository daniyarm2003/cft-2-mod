package com.lildan42.cft.fighterdata.state;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class JsonCFTSaveContextSerializer implements CFTSaveContextSerializer {
    private final ObjectMapper jsonMapper;
    private final boolean gzipEnabled;

    public JsonCFTSaveContextSerializer(ObjectMapper jsonMapper, boolean gzipEnabled) {
        this.jsonMapper = jsonMapper;
        this.gzipEnabled = gzipEnabled;
    }

    @Override
    public CFTState.SaveContext readSaveContextStream(InputStream stream) throws IOException {
        if(this.gzipEnabled) {
            try(GZIPInputStream inputStream = new GZIPInputStream(stream)) {
                return this.jsonMapper.readValue(inputStream, CFTState.SaveContext.class);
            }
        }

        return this.jsonMapper.readValue(stream, CFTState.SaveContext.class);
    }

    @Override
    public void writeToSaveContextStream(CFTState.SaveContext saveContext, OutputStream stream) throws IOException {
        if(this.gzipEnabled) {
            try(GZIPOutputStream outputStream = new GZIPOutputStream(stream)) {
                this.jsonMapper.writeValue(outputStream, saveContext);
            }
        }
        else {
            this.jsonMapper.writeValue(stream, saveContext);
        }
    }
}