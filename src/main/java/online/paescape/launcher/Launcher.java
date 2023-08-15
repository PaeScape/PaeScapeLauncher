package online.paescape.launcher;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;


public class Launcher {
    public static void main(String[] args) {
        SplashScreen.showSplash();
        Start();
    }

    public static void Start() {
        try {
            try {
                File file = new File(clientLocation());
                if (!file.exists() || !isLatestFile(new FileInputStream(clientLocation()), Settings.CLIENT_CHECKSUM_URL)) {
                    System.out.println("Updating PaeScape Client");
                    try {
                        file.getParentFile().mkdir();
                        URLConnection conn = new URL(Settings.CLIENT_DOWNLOAD_URL).openConnection();
                        conn.addRequestProperty("User-Agent", "PaeScape Launcher");
                        InputStream in = conn.getInputStream();
                        Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        in.close();
                        try {
                            launchClient();
                            System.exit(0);
                        } catch (Exception er) {
                            er.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Client is up to date.");
                    launchClient();
                    System.exit(0);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLatestFile(FileInputStream localfile, String checksumEndpoint) throws URISyntaxException {
        try {
            URLConnection checksumEndpointConnection = new URL(checksumEndpoint).openConnection();
            checksumEndpointConnection.addRequestProperty("User-Agent", "PaeScape Launcher");
            try (Scanner scanner = new Scanner(checksumEndpointConnection.getInputStream(), StandardCharsets.UTF_8.toString())) {
                scanner.useDelimiter("\\A");
                String checksum = scanner.hasNext() ? scanner.next() : "";
                String localChecksum = DigestUtils.md5Hex(localfile);
                System.out.printf("Fetched checksum: %s%n", checksum);
                System.out.printf("Local file checksum: %s%n", localChecksum);
                return localChecksum.equalsIgnoreCase(checksum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String clientLocation() {
        return Settings.SAVE_DIR + Settings.SAVE_NAME;
    }

    public static void launchClient() {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", Settings.SAVE_DIR + Settings.SAVE_NAME);
        pb.directory(new File(System.getProperty("java.home") + File.separator + "bin"));
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}