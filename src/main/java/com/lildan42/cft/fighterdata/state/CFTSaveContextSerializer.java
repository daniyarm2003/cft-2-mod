package com.lildan42.cft.fighterdata.state;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CFTSaveContextSerializer {
    CFTState.SaveContext readSaveContextStream(InputStream stream) throws IOException;
    void writeToSaveContextStream(CFTState.SaveContext saveContext, OutputStream stream) throws IOException;
}