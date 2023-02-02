package org.obscontrol;

import ch.bildspur.artnet.ArtNetClient;
import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.model.Scene;

import java.net.UnknownHostException;
import java.util.List;

public class Main {

    private static ArtNetClient artnet;
    private static int subnet = 0;
    private static int universe = 0;
    private static int channel = 0;
    private static Websocet websocet;

    private static List<Scene> sceneList;

    private static OBSRemoteController obsRemoteController;

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("OBS Websocket Control");
        System.out.println("s: " + subnet + " u: " + universe + " c: " + channel);
        websocet = new Websocet();

        artnet = new ArtNetClient();
        artnet.start("192.168.178.131");

        Runnable runnable = () -> {
            while (Thread.currentThread().isAlive()) {
                ArtNet.getArtNet();
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public static ArtNetClient getArtnet() {
        return artnet;
    }

    public static int getSubnet() {
        return subnet;
    }

    public static int getUniverse() {
        return universe;
    }

    public static int getChannel() {
        return channel;
    }

    public static Websocet getWebsocet() {
        return websocet;
    }

    public static OBSRemoteController getObsRemoteController() {
        return obsRemoteController;
    }

    public static void setObsRemoteController(OBSRemoteController obsRemoteController) {
        Main.obsRemoteController = obsRemoteController;
    }

    public static List<Scene> getSceneList() {
        return sceneList;
    }

    public static void setSceneList(List<Scene> sceneList) {
        Main.sceneList = sceneList;
    }
}