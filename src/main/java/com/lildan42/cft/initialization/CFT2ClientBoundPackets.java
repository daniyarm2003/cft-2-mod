package com.lildan42.cft.initialization;

import com.lildan42.cft.packets.ClientBoundCFTFightEndPacket;
import com.lildan42.cft.packets.ClientBoundCFTFightStartPacket;
import org.slf4j.Logger;

public class CFT2ClientBoundPackets implements CFT2Initializer {
    @Override
    public String getInitializationStageName() {
        return "Packet registration";
    }

    @Override
    public void initialize(Logger logger) {
        ClientBoundCFTFightStartPacket.registerServerToClient();
        ClientBoundCFTFightEndPacket.registerServerToClient();

        logger.info("Registered all client bound packets successfully");
    }
}
