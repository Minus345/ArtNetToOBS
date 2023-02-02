package org.obscontrol;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.event.ui.StudioModeStateChangedEvent;
import io.obswebsocket.community.client.message.request.ui.GetStudioModeEnabledRequest;
import io.obswebsocket.community.client.message.response.ui.GetStudioModeEnabledResponse;
import io.obswebsocket.community.client.model.Scene;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Websocet {
    private static final Logger log = getLogger(Websocet.class);
    private OBSRemoteController obsRemoteController;
    private boolean isReconnecting = false;

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
                .host("127.0.0.1")                        // Set the host
                .port(4455)                               // Set the port
                .password("QHDicHNDmEZiVoH5")                       // Set the password
                .lifecycle()                              // Add some lifecycle callbacks
                .onReady(this::onReady)                 // Add onReady callback
                .and()                                  // Build the LifecycleListenerBuilder
                .registerEventListener(StudioModeStateChangedEvent.class,
                        this::onStudioModeStateChanged)       // Register a StudioModeStateChangedEvent
                .build();                                 // Build the OBSRemoteController
    }

    private void onReady() {
        if (!this.isReconnecting) {
            this.onFirstConnection();
            Main.setObsRemoteController(this.obsRemoteController);
        } else {
            // Second connection
            //this.onSecondConnection();
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
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("Name: " + list.get(i).getSceneName() + " Index: " + list.get(i).getSceneIndex());
                }
                Main.setSceneList(list);
                //getSceneListResponse.getScenes().forEach(scene -> log.info("Scene: {}", scene));
            }
        });
    }

    private void onSecondConnection() {
        // Send a Request through specific Request builder
        this.obsRemoteController.sendRequest(GetStudioModeEnabledRequest.builder().build(),
                (GetStudioModeEnabledResponse requestResponse) -> {
                    if (requestResponse.isSuccessful()) {
                        if (requestResponse.getStudioModeEnabled()) {
                            log.info("Studio mode enabled");
                        } else {
                            log.info("Studio mode not enabled");
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }

                    // Disconnect
                    this.obsRemoteController.disconnect();
                });
    }

    private void disconnectAndReconnect() {
        // Disconnect
        this.obsRemoteController.disconnect();

        // Set a flag
        this.isReconnecting = true;

//    // Recreate instance
        this.createOBSRemoteController();

        // Reconnect
        this.obsRemoteController.connect(); // onReady will be called another time
    }

    private void onStudioModeStateChanged(StudioModeStateChangedEvent studioModeStateChangedEvent) {
        log.info(
                "Studio Mode State Changed to: {}",
                studioModeStateChangedEvent.getStudioModeEnabled());
    }
}
