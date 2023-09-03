package online.paescape.launcher;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Launcher {
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        setupLogging();
        logger.info("Launcher started");

        try {
            SplashScreen.showSplash();
            updateClient();
            launchClient();
            SplashScreen.closeSplash();
        } catch (IOException | URISyntaxException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            ErrorDialog.showStackTraceDialog(e, SplashScreen.getSplashWindow(), "PaeScape Launcher Error", "Please create a ticket on discord, and attach this entire error message");
        }
    }

    public static void setupLogging() {
        new File(Settings.SAVE_DIR + "launcher").mkdir();
        try {
            logger.setLevel(Level.FINE);
            FileHandler fh = new FileHandler(Settings.SAVE_DIR + "launcher/launcher.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateClient() throws IOException, URISyntaxException {
        File file = new File(clientLocation());
        if (!file.exists() || !isLatestFile(clientLocation(), Settings.CLIENT_CHECKSUM_URL)) {
            logger.info("Updating PaeScape Client");
            file.getParentFile().mkdir();
            HttpURLConnection conn = (HttpURLConnection) new URL(Settings.CLIENT_DOWNLOAD_URL).openConnection();
            conn.addRequestProperty("User-Agent", "PaeScape Launcher");
            InputStream in = conn.getInputStream();
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            in.close();
            conn.disconnect();
        }
    }

    public static boolean isLatestFile(String localFileLocation, String checksumEndpoint) throws IOException {
        FileInputStream localFile = new FileInputStream(localFileLocation);
        URLConnection checksumEndpointConnection = new URL(checksumEndpoint).openConnection();
        checksumEndpointConnection.addRequestProperty("User-Agent", "PaeScape Launcher");
        Scanner scanner = new Scanner(checksumEndpointConnection.getInputStream(), StandardCharsets.UTF_8.toString());
        scanner.useDelimiter("\\A");
        String checksum = scanner.hasNext() ? scanner.next() : "";
        String localChecksum = DigestUtils.md5Hex(localFile);
        logger.info("Fetched checksum: " + checksum);
        logger.info("Local file checksum: " + localChecksum);
        return localChecksum.equalsIgnoreCase(checksum);
    }

    public static String clientLocation() {
        return Settings.SAVE_DIR + Settings.SAVE_NAME;
    }

    public static void launchClient() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", Settings.SAVE_DIR + Settings.SAVE_NAME);
        pb.directory(new File(Settings.SAVE_DIR));
        pb.start();
    }
}