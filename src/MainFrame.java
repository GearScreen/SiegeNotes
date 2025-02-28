import gears.Gears.*;
import static gears.Gears.*;

import static app.App.*;
import static app.FrontApp.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class MainFrame extends JFrame {
    public static void Frame() {
        new MainFrame().init();
    }

    public void init() {
        // Panes :
        // Center :
        // Text Pane
        textPane = new JTextPane();
        textPane.setFont(textAreaFont);
        doc = textPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(textPane);

        // North :
        // Button Panel TOP
        tabsPanel = cTabPanel();

        // BOT
        // Menu Bar
        JMenuBar menuBar = menuBar(this);

        // Button Panel BOT
        JPanel buttonPanelMenu = cJPanel(new FlowLayout(FlowLayout.LEFT), 0, defaultColor,
                Arrays.asList(menuBar));

        // Main ButtonPanel Wrap Top + Bot
        JPanel buttonPanel = cJPanel(new GridLayout(2, 2), 0, Color.WHITE,
                Arrays.asList(tabsPanel, menuBar));

        // #region Callbacks

        // OnClick
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }
        });

        // OnKeyPress
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                // CTRL + S -> Save file
                if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isControlDown()) {
                    saveFile(siegeConverterSave());
                }

                // CTRL + L -> Insert Link
                if (keyEvent.getKeyCode() == KeyEvent.VK_L && keyEvent.isControlDown()) {
                    insertLinkWindow(MainFrame.this);
                }
            }
        });

        // #endregion

        // Main Panel / Layout :
        JPanel mainPanel = addComponentsToJPanelLayout(cJPanel(new BorderLayout(), 0, Color.BLACK),
                Arrays.asList(cPair(scroll, BorderLayout.CENTER), cPair(buttonPanel, BorderLayout.NORTH)));

        // Frame Setup + Display
        InitFrame(this, "Siege Notes", icon_Path, new Component[] { mainPanel },
                new Vector2Int(1200, 650), new Vector2Int(300, 300));
    }
}