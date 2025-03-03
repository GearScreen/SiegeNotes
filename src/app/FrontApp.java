package app;

import gears.Gears;
import gears.Gears.Action;

import static app.App.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import app.App.LinkLabel;

public class FrontApp {

    //#region Panels

    public static JPanel cJPanel(LayoutManager layout, int borderSize, Color color, List<Component> components) {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));

        if (components != null) {
            for (Component compo : components) {
                panel.add(compo);
            }
        }

        return panel;
    }

    public static JPanel cJPanel(LayoutManager layout, int borderSize, Color color) {
        return cJPanel(layout, borderSize, color, null);
    }

    public static JPanel cTabPanel() {
        return cJPanel(new FlowLayout(FlowLayout.LEFT), 1, Color.gray, Arrays.asList(
            cJButton("+ Tab", mouseEvent -> {
                    newTab();
                })));
    }

    //#endregion

    //#region Labels

    public static JLabel cClickableLabel(String labelText, Font font, Color textColor, Boolean underline,
            Action<MouseEvent> onClick) {
        JLabel clickableLabel = new JLabel(labelText);
        clickableLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clickableLabel.setAlignmentY(.85f); // Aligned with Text
        clickableLabel.setForeground(textColor);

        // Font
        if (underline) {
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            clickableLabel.setFont(font.deriveFont(attributes));
        } else {
            clickableLabel.setFont(font);
        }

        // On Click
        clickableLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                onClick.call(e);
            }
        });

        return clickableLabel;
    }

    public static JLabel cLinkLabel(String labelText, String link, Integer location) {
        String cleanLink = Gears.sanitizeURL(link);
        String finalLink = cleanLink;

        // On Click
        Action<MouseEvent> onClick = e -> Gears.openLinkDefaultBrowser(cleanLink);

        // If link is not a URL
        if (cleanLink == null) {
            if (Gears.isValidFilePath(link)) {
                finalLink = link;
                onClick = e -> newTab(link, true);
            } else {
                System.out.println("Link was neither a valid web link or valid filePath Link");
                return null;
            }
        }

        // Create + Add
        JLabel linkLabel = cClickableLabel(labelText, mainFont, Color.BLUE, true, onClick);

        getCurrentLabelList().add(new LinkLabel(linkLabel, labelText, finalLink, location));
        // System.out.println("LabelList Size : " + getCurrentLabelList().size());

        insertComponentAt(linkLabel, location);

        return linkLabel;
    }

    //#endregion

    //#region Buttons

    public static JButton cJButton(String buttonText, Action<ActionEvent> onClick) {
        JButton button = new JButton(buttonText);
        button.setBackground(defaultColor);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClick.call(e);
            }
        });

        return button;
    }

    public static JButton cJButton(String buttonText, Color bgColor, Action<ActionEvent> onClick) {
        JButton button = cJButton(buttonText, onClick);
        button.setBackground(bgColor);
        return button;
    }

    //#endregion

    public static JPanel tab(int tabIndex, String title) {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tab Button
        gbc.weightx = 0.9;
        gbc.gridx = 0;
        String tabTitle = Gears.stringIsNullOrEmpty(title) ? "Tab " + (tabIndex + 1) : Gears.getFileNameFromPath(title);
        tabPanel.add(cJButton(tabTitle, mEvent -> tabButtonOnClick(tabIndex, mEvent)), gbc);

        // Close / X Button
        JButton closeButton = cJButton("X", Color.DARK_GRAY, mEvent -> closeTabButtonOnClick(tabIndex, mEvent));

        closeButton.setForeground(Color.WHITE);

        gbc.weightx = 0.1;
        gbc.gridx = 1;
        tabPanel.add(closeButton, gbc);

        return tabPanel;
    }

    //#region Windows

    public static JFrame smallWindow(Component ref, String windowTitle, List<Component> components) {
        JFrame popup = new JFrame(windowTitle);

        popup.setSize(300, 200);
        popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (Component compo : components) {
            popup.add(compo);
        }

        popup.setLocationRelativeTo(ref);

        popup.setVisible(true);

        return popup;
    }

    public static JDialog popupWindow(Component ref, String windowTitle, List<Component> components) {
        JDialog popup = new JDialog(mainFrame, windowTitle, true); // true for modal

        popup.setSize(300, 200);
        popup.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        if (components != null) {
            for (Component compo : components) {
                popup.add(compo);
            }
        }

        popup.setLocationRelativeTo(ref);

        popup.setVisible(true);

        return popup;
    }

    public static void insertLinkWindow(Component ref) {
        // Components
        JLabel linkFieldLabel = new JLabel("Link : ");
        JTextField linkField = new JTextField("https://google.com", 20);

        JLabel linkTextFieldLabel = new JLabel("Text : ");
        JTextField linkTextField = new JTextField("Google", 20);

        JButton confirmButton = cJButton("Confirm", e -> {
            //System.out.println("Confirm Insert");

            // Insert Link
            cLinkLabel(linkTextField.getText(), linkField.getText(), textPane.getCaretPosition());

            // Get Popup
            JDialog popup = (JDialog) ((JButton) e.getSource()).getParent().getParent().getParent().getParent()
                    .getParent();
            popup.dispose();
        });

        // Panels
        JPanel linkFieldPanel = new JPanel(new GridBagLayout());
        linkFieldPanel.add(linkFieldLabel);
        linkFieldPanel.add(linkField);

        JPanel linkTextFieldPanel = new JPanel(new GridBagLayout());
        linkTextFieldPanel.add(linkTextFieldLabel);
        linkTextFieldPanel.add(linkTextField);

        // Main
        JPanel linkPanel = new JPanel(new GridLayout(3, 1));
        linkPanel.add(linkFieldPanel);
        linkPanel.add(linkTextFieldPanel);
        linkPanel.add(confirmButton);

        popupWindow(ref, "Insert Link Window", Arrays.asList(linkPanel));
    }

    //#endregion

    public static JMenu cMenu(String menuText, Font font, Dimension dimension, Boolean border) {
        JMenu menu = new JMenu(menuText);
        menu.setFont(font);
        menu.setPreferredSize(dimension);

        if (border)
            menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return menu;
    }

    public static JMenu cMenu(String menuText, Font font, Dimension dimension, Boolean border, List<JMenuItem> items) {
        JMenu menu = cMenu(menuText, font, dimension, border);

        for (JMenuItem item : items) {
            menu.add(item);
        }

        return menu;
    }

    public static JMenuBar menuBar(Component ref) {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenuItem openFileItem = setupMenuItem("Open File...", e -> fileExplorerOpenFile());

        JMenuItem saveItem = setupMenuItem("Save    CTRL + S", e -> saveFile(siegeConverterSave()));

        JMenuItem saveAsItem = setupMenuItem("Save As...", e -> saveAs(siegeConverterSave()));

        JMenu fileMenu = cMenu("File", menuFont, new Dimension(40, 25), true,
                Arrays.asList(openFileItem, saveItem, saveAsItem));

        menuBar.add(fileMenu);

        // Edit Menu
        JMenuItem insertLinkItem = setupMenuItem("Insert Link...    CTRL + L", e -> insertLinkWindow(ref));

        JMenu editMenu = cMenu("Edit", menuFont, new Dimension(40, 25), true,
                Arrays.asList(insertLinkItem));

        menuBar.add(editMenu);

        // setJMenuBar(menuBar);

        return menuBar;
    }

    public static JMenuItem setupMenuItem(String text, Action<ActionEvent> onClick) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(menuFont);
        item.addActionListener(e -> onClick.call(e));
        return item;

    }

    public static void setupTextAreaFont() {
        Font font = textAreaFont;

        try {
            InputStream is = singleton.getClass().getResourceAsStream("/resources/NotoColorEmoji-Regular.ttf");
            if (is == null) {
                throw new IOException("Font file not found");
            }
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 15);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        textPane.setFont(font);
    }
}
