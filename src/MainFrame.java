import gears.Gears.*;
import static gears.Gears.cPair;

import java.util.Arrays;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainFrame extends JFrame {
    public static void Frame() {
        new MainFrame().init();
    }

    public void init() {
        URL scriptPath = getClass().getResource("/resource");
        System.out.println(scriptPath.toString());

        // ###ELEMENTS
        // TextPane
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(App.createJButton("Button 1", onClick -> System.out.println("Button 1 clicked")));
        buttonPanel.add(App.createJButton("Button 2", onClick -> System.out.println("Button 2 clicked")));

        // Main Panel
        JPanel mainPanel = App.addComponentsToJPanelLayout(App.createPanel(new BorderLayout(), 5, new Color(50, 50, 255)),
        Arrays.asList(cPair(textPane, BorderLayout.CENTER), cPair(buttonPanel, BorderLayout.NORTH)));

        // Frame Setup + Display
        App.InitFrame(this, "SiegeNotes", App.darktide_Icon_Path, new Component[] { mainPanel }, new Vector2Int(1200, 650),
                new Vector2Int(300, 300));
    }
}