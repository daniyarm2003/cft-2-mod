package com.lildan42.cft.initialization;

import org.slf4j.Logger;

public interface CFT2Initializer {
    String getInitializationStageName();
    void initialize(Logger logger);
}
