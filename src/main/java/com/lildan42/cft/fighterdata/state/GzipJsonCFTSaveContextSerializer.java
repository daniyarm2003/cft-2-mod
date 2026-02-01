package com.lildan42.cft.fighterdata.state;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipJsonCFTSaveContextSerializer implements CFTSaveContextSerializer {
    private final ObjectMapper jsonMapper;

    public GzipJsonCFTSaveContextSerializer(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public CFTState.SaveContext readSaveContextStream(InputStream stream) throws IOException {
        try(GZIPInputStream gzipInputStream = new GZIPInputStream(stream)) {
            return this.jsonMapper.readValue(gzipInputStream, CFTState.SaveContext.class);
        }
    }

    @Override
    public void writeToSaveContextStream(CFTState.SaveContext saveContext, OutputStream stream) throws IOException {
        try(GZIPOutputStream gzipOutputStream = new GZIPOutputStream(stream)) {
            this.jsonMapper.writeValue(gzipOutputStream, saveContext);
        }
    }
}
