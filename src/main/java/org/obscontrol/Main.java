package org.obscontrol;

import ch.bildspur.artnet.ArtNetClient;
import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.model.Scene;
import org.apache.commons.cli.*;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static ArtNetClient artnet;
    private static int subnet = 0;
    private static int universe = 0;
    private static int channel = 0;
    private static String ip = "127.0.0.0";
    private static String websocketPassword;
    private static int websocketPort = 4455;
    private static List<Scene> sceneList;

    private static OBSRemoteController obsRemoteController;

    public static void main(String[] args) {
        System.out.println("OBS Websocket Control 2");

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        Logger.getLogger("").addHandler(ch);
        Logger.getLogger("").setLevel(Level.WARNING);

        setupOptions(args);

        System.out.println("SubNet: " + subnet);
        System.out.println("Universe: " + universe);
        System.out.println("Channel: " + channel);
        System.out.println("IP: " + ip);
        new Websocet();

        artnet = new ArtNetClient();
        artnet.start(ip);

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

    public static void setupOptions(String[] args) {

        var options = new Options()
                .addOption("h", "help", false, "Help Message")
                .addOption(Option.builder("s")
                        .longOpt("subnet")
                        .hasArg(true)
                        .desc("Art Net: Sub Net [0 -15]")
                        .argName("0")
                        .build())
                .addOption(Option.builder("u")
                        .longOpt("universe")
                        .hasArg(true)
                        .desc("Art Net: Universe [0 -15]")
                        .argName("0")
                        .build())
                .addOption(Option.builder("c")
                        .longOpt("channel")
                        .hasArg(true)
                        .desc("Art Net: Channel [0 - 511]")
                        .argName("0")
                        .build())
                .addOption(Option.builder("i")
                        .longOpt("ip")
                        .hasArg(true)
                        .desc("Network Ip where Art Net comes from")
                        .argName("192.xxx.xxx.xxx")
                        .build())
                .addOption(Option.builder("p")
                        .longOpt("password")
                        .hasArg(true)
                        .desc("Password for OBS Websocket")
                        .build())
                .addOption(Option.builder("po")
                        .longOpt("port")
                        .hasArg(true)
                        .desc("Port for OBS Websocket")
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar OBSWebsocet.jar ", options);
                System.exit(0);
            }
            if (line.hasOption("s")) {
                subnet = Integer.parseInt(line.getOptionValue("s"));
            }
            if (line.hasOption("u")) {
                universe = Integer.parseInt(line.getOptionValue("u"));
            }
            if (line.hasOption("c")) {
                channel = Integer.parseInt(line.getOptionValue("c"));
            }
            if (line.hasOption("i")) {
                ip = line.getOptionValue("i");
            }
            if (line.hasOption("p")) {
                websocketPassword = line.getOptionValue("p");
            }
            if (line.hasOption("po")) {
                websocketPort = Integer.parseInt(line.getOptionValue("po"));
            }
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
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

    public static String getWebsocketPassword() {
        return websocketPassword;
    }

    public static int getWebsocketPort() {
        return websocketPort;
    }
}