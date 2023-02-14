package org.obscontrol;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.model.Scene;

import java.util.List;

public class Websocet {
    private OBSRemoteController obsRemoteController;

    private List<Scene> list;

    Websocet() {
        this.createOBSRemoteController();

        // Connect
        this.obsRemoteController.connect();
    }

    private void createOBSRemoteController() {
        // Create OBSRemoteController through its builder
        this.obsRemoteController = OBSRemoteController.builder()
                .autoConnect(false)                       // Do not connect automatically
                .host("localhost")                        // Set the host
                .port(Main.getWebsocketPort())                               // Set the port
                .password(Main.getWebsocketPassword())                       // Set the password
                .lifecycle()                              // Add some lifecycle callbacks
                .onReady(this::onReady)                 // Add onReady callback
                .and()                                  // Build the LifecycleListenerBuilder
                .build();                                 // Build the OBSRemoteController
    }

    private void onReady() {
        boolean isReconnecting = false;
        if (!isReconnecting) {
            this.onFirstConnection();

        } else {
            System.out.println("Error connecting");
        }
    }

    private void onFirstConnection() {
        // Send a blocking call (last parameter is a timeout instead of a callback)
        //this.obsRemoteController.setStudioModeEnabled(true, 1000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        // Send a request through a convenience method
        this.obsRemoteController.getSceneList(getSceneListResponse -> {
            if (getSceneListResponse.isSuccessful()) {
                // Print each Scene
                list = getSceneListResponse.getScenes();
                System.out.println("Scene:");
                for (Scene scene : list) {
                    System.out.println("Name: " + scene.getSceneName() + " Index: " + scene.getSceneIndex());
                }
                Main.setSceneList(list);
                //getSceneListResponse.getScenes().forEach(scene -> log.info("Scene: {}", scene));
            }
        });
        Main.setObsRemoteController(this.obsRemoteController);
        Main.getObsRemoteController().getCurrentProgramScene(getCurrentProgramSceneResponse -> {
            if (getCurrentProgramSceneResponse.isSuccessful()) {
                //System.out.println("Current Scenen Name: " + getCurrentProgramSceneResponse.getCurrentProgramSceneName());
                Main.getObsRemoteController().setCurrentProgramScene("0",1);
            }
        });
    }
}
