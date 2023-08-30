package online.paescape.launcher;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ErrorDialog {
    public static void showStackTraceDialog(Throwable throwable, Component parentComponent, String title, String message) {
        // create stack strace panel
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JTextArea straceTa = new JTextArea();
        final JScrollPane taPane = new JScrollPane(straceTa);
        taPane.setPreferredSize(new Dimension(360, 240));
        taPane.setVisible(false);
        // print stack trace into textarea. I don't like this hack. this sucks
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(out));
        straceTa.setForeground(Color.RED);
        straceTa.setText(out.toString());

        final JPanel stracePanel = new JPanel(new BorderLayout());
        stracePanel.add(labelPanel, BorderLayout.NORTH);
        stracePanel.add(taPane, BorderLayout.CENTER);
        taPane.setVisible(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(message), BorderLayout.NORTH);
        panel.add(stracePanel, BorderLayout.CENTER);

        JOptionPane pane = new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(parentComponent, title);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3;
        if (dialog.getWidth() > maxWidth) {
            dialog.setSize(new Dimension(maxWidth, dialog.getHeight()));
            setLocationRelativeTo(dialog, parentComponent);
        }

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                // this is a stupid bodge I think I need?
                System.exit(0);
            }
        });


        dialog.setResizable(true);
        dialog.setVisible(true);
        dialog.dispose();
    }

    private static void setLocationRelativeTo(Component c1, Component c2) {
        Container root = null;

        if (c2 != null) {
            if (c2 instanceof Window || c2 instanceof Applet) {
                root = (Container) c2;
            } else {
                Container parent;
                for (parent = c2.getParent(); parent != null; parent = parent.getParent()) {
                    if (parent instanceof Window || parent instanceof Applet) {
                        root = parent;
                        break;
                    }
                }
            }
        }

        if ((c2 != null && !c2.isShowing()) || root == null || !root.isShowing()) {
            Dimension paneSize = c1.getSize();
            Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            c1.setLocation(centerPoint.x - paneSize.width / 2, centerPoint.y - paneSize.height / 2);
        } else {
            Dimension invokerSize = c2.getSize();
            Point invokerScreenLocation = c2.getLocation();
            Rectangle windowBounds = c1.getBounds();
            int dx = invokerScreenLocation.x + ((invokerSize.width - windowBounds.width) >> 1);
            int dy = invokerScreenLocation.y + ((invokerSize.height - windowBounds.height) >> 1);
            Rectangle ss = root.getGraphicsConfiguration().getBounds();
            if (dy + windowBounds.height > ss.y + ss.height) {
                dy = ss.y + ss.height - windowBounds.height;
                if (invokerScreenLocation.x - ss.x + invokerSize.width / 2 < ss.width / 2) {
                    dx = invokerScreenLocation.x + invokerSize.width;
                } else {
                    dx = invokerScreenLocation.x - windowBounds.width;
                }
            }

            if (dx + windowBounds.width > ss.x + ss.width) {
                dx = ss.x + ss.width - windowBounds.width;
            }
            if (dx < ss.x)
                dx = ss.x;
            if (dy < ss.y)
                dy = ss.y;

            c1.setLocation(dx, dy);
        }
    }
}
