package org.obscontrol;

import io.obswebsocket.community.client.model.Scene;

import java.util.List;

public class ArtNet {

    private static int channelOld = 0;
    private static int channel;
    public static void getArtNet() {
        byte[] dmx = Main.getArtnet().readDmxData(Main.getSubnet(), Main.getUniverse());
        channel = dmx[Main.getChannel()];
        if (channel == channelOld) {
            return;
        } else {
            System.out.println("Artnet: " + Byte.toUnsignedInt((byte) channel));
            channelOld = channel;
        }

        //only do this if channel has changed

        for (int i = 0; i <= 255; i++) {
            if (channel == i) {
                if (Main.getSceneList() == null) return;
                if (Main.getObsRemoteController() == null) return;
                if (Main.getSceneList().stream().map(Scene::getSceneName).anyMatch(String.valueOf(channel)::equals)) {
                    System.out.println("Send Scene Change to: " + channel);
                    for (int j = 0; j < Main.getSceneList().size(); j++) {
                        if (Main.getSceneList().get(j).getSceneName().equals(String.valueOf(channel))) {
                            if (Main.getObsRemoteController().getCurrentProgramScene(1).getCurrentProgramSceneName().equals(Main.getSceneList().get(j).getSceneName())) {
                                System.out.println("Scene is active");
                            } else {
                                Main.getObsRemoteController().setCurrentProgramScene(String.valueOf(channel), 1);
                            }
                        }
                    }
                }
            }
        }
    }
}
