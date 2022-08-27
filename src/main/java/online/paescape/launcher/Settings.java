package online.paescape.launcher;

import java.io.File;

public class Settings {
    public static String CLIENT_DOWNLOAD_URL = "https://cdn.paescape.online/PaeScape.jar";
    public static String CLIENT_CHECKSUM_URL = "https://cdn.paescape.online/ClientChecksum";
    public static String CACHE_NAME = "PaeScapeCache";
    public static String SAVE_NAME = "PaeScape.jar";
    public static String SAVE_DIR = System.getProperty("user.home") + File.separator + CACHE_NAME + File.separator;
}