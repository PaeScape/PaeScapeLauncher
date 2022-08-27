package online.paescape.launcher;

import javax.swing.*;
import java.awt.*;


public class SplashScreen
        extends JWindow {
    private static final long serialVersionUID = 6812420466718945931L;
    private static int[] dimensions = new int[]{994, 479};


    public static void showSplash() {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.getRootPane().setWindowDecorationStyle(0);
        frame.setSize(dimensions[0], dimensions[1]);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - (frame.getSize()).width / 2, dim.height / 2 - (frame.getSize()).height / 2);
        JLabel label = new JLabel(new ImageIcon(SplashScreen.class.getClassLoader().getResource("splashscreen.png")));
        frame.add(label);
        frame.setVisible(true);
    }
}
