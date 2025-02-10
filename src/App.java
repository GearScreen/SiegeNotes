import gears.Gears;
import gears.Gears.*;
import gears.Gears.Action;

import org.json.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import static gears.Gears.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;

public class App {
    // #Main
    public static JFrame mainFrame;
    public static StyledDocument doc;
    public static JTextPane textPane;
    public static JPanel tabsPanel;

    public static int currentTabIndex = 0;
    public static List<String> tabs = new ArrayList<>(); // CurrentFilePath Array

    public static List<LinkLabel> labelLinkList = new ArrayList<>();

    public static boolean changesMade = false;

    // #Save
    // Config
    public static final String configFilePath = "src/siegeNoteConfig.json";
    public static final String currentTabKey = "CurrentTab";
    public static final String tabsKey = "Tab";

    // Label code parts
    public static final String labelCodeOpen = "***labelTitle:";
    public static final String labelCodeLink = ", labelLink:";
    public static final String labelCodeClose = "***";

    // #Style
    public static final String darktide_Icon_Path = "resource/Darktide.png";
    public static final Font mainFont = new Font("Dialog", Font.PLAIN, 12);
    public static final Font oldFont = new Font("Monospaced", Font.BOLD, 18);
    public static final Color defaultColor = new Color(238, 238, 238);

    private static SimpleAttributeSet linkStyle = new SimpleAttributeSet();

    public static void main(String[] args) throws Exception {
        init();

        // #Frame
        // MainFrame.Frame();
        TestFrame.Frame();

        loadConfig();
    }

    // #region BackApp / Main

    public static void init() {
        StyleConstants.setForeground(linkStyle, Color.BLUE);
        StyleConstants.setUnderline(linkStyle, true);
    }

    public static void InitFrame(JFrame frame, String title, String iconPath, Component[] components, Vector2Int size,
            Vector2Int minimumSize) {
        // Objects.requireNonNull(message, "message cannot be null");

        // Add Components
        for (Component compo : components) {
            frame.add(compo);
        }

        // Window Icon
        URL imgURL = frame.getClass().getResource(iconPath);
        if (imgURL != null) {
            frame.setIconImage(new ImageIcon(imgURL, "Icon").getImage());
        }

        // Window Title
        frame.setTitle(title);

        // Window Size
        frame.setSize(size.x, size.y);
        frame.setMinimumSize(new Dimension(minimumSize.x, minimumSize.y));

        // Center frame on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mainFrame = frame;
    }

    // #region Save / Load

    // Config
    public static void saveConfig() {
        // Create a JSONObject
        JSONObject jsonObject = new JSONObject();

        // Save Values
        // jsonObject.put(currentFilePathKey, getCurrentFilePath()); // Old

        // Current TabIndex
        jsonObject.put(currentTabKey, currentTabIndex);

        // Tabs FilePath
        jsonObject.put(tabsKey, new JSONArray(tabs));

        Gears.writeJsonFile(configFilePath, jsonObject, () -> System.out.println("Config Saved"));
    }

    public static void loadConfig() {
        // Read
        JSONObject jsonObject = Gears.readJsonFile(configFilePath);

        // Load
        currentTabIndex = jsonObject.getInt(currentTabKey);
        JSONArray tabsFilepath = jsonObject.getJSONArray(tabsKey);
        // System.out.println("Filepath found : " + tabsFilepath);
        // System.out.println("Current Tab Index : " + currentTabIndex);

        // Tabs
        for (int i = 0; i <= tabsFilepath.length() - 1; i++) {
            newTab(tabsFilepath.getString(i), false);
        }

        siegeConverterLoad();

        // System.out.println("Config Loaded -> Current File : " +
        // getCurrentFilePath());
    }

    public static void saveFile(String text) {
        // if (!changesMade) {return;}

        String currentFilePath = getCurrentFilePath();

        if (currentFilePath == "") {
            System.out.println("No File to Save -> Save As");
            saveAs();
            return;
        }

        Gears.writeTextToFile(currentFilePath, text);
        System.out.println("File Saved");
    }

    public static void saveAs() {
        // TODO
    }

    // #endregion

    //#region TextArea

    public static String siegeConverterSave() {
        // Conversion logic : linkLabelsText to trueText

        // Get Text
        String mainString = textPane.getText();
        int differenceTextDoc = mainString.length() - doc.getLength() - 1;

        // Generate Saved String
        String finalString = Gears.insertManyStrings(true, mainString, labelLinkList, labelLink -> {
            Vector2Int space = findComponentSpace(labelLink.comp);

            int insertPosition = space.x + differenceTextDoc;
            String labelTextCode = labelCodeOpen + labelLink.text + labelCodeLink
                    + labelLink.link + labelCodeClose;

            return cPair(insertPosition, labelTextCode);
        });

        // System.out.println("NoteText Converted to TrueText : " + finalString);
        return finalString;
    }

    public static void siegeConverterLoad(String filePath) {
        String trueText = Gears.readTextFileCombined(filePath);
        textPane.setText(trueText);

        // Conversion logic trueText -> linkLabelsText/Note :
        List<LinkLabel> labels = new ArrayList<>();
        int index = trueText.indexOf(labelCodeOpen, 0);

        int offset = 0; // Difference between text with and without label codes
        int textDocOffset = trueText.length() - doc.getLength(); // Difference between text and doc

        int firstOffset = -1;

        // While label codes are found -> get Label Name + link + location
        while (index != -1) {
            // Label Indexs
            int linkIndex = trueText.indexOf(labelCodeLink, index);
            int endIndex = trueText.indexOf(labelCodeClose, index + 3);
            int trueEndIndex = endIndex + 3;

            // System.out.println("Label Found : " + index + " to " + endIndex);

            // Label Values
            String labelTitle = trueText.substring(index + labelCodeOpen.length(), linkIndex);
            String labelLink = trueText.substring(linkIndex + labelCodeLink.length(), endIndex);

            // System.out.println("Values : LabelTitle : " + labelTitle + ", LabelLink : " +
            // labelLink);

            // Remove text
            trueText = Gears.deleteStringAt(trueText, index, trueEndIndex);

            // Store new label
            // System.out.println("Location : " + (index - textDocOffset - xd));
            labels.add(new LinkLabel(null, labelTitle, labelLink, index - textDocOffset + firstOffset));

            firstOffset = 0;

            // Find Next Label code
            offset += trueEndIndex - index; // Add size of the label code
            index = trueText.indexOf(labelCodeOpen, trueEndIndex - offset);
        }

        textPane.setText(trueText);

        // Insert Labels
        for (LinkLabel label : labels) {
            JLabel linkLabel = createLinkLabel(label.text, label.link);
            App.insertComponentAt(linkLabel, label.location + labels.size());
        }
    }

    public static void siegeConverterLoad() {
        siegeConverterLoad(getCurrentFilePath());
    }

    //#endregion

    // #region Tabs

    public static void newTab(String filePath, boolean load) {
        // System.out.println("Adding a new Tab... ");

        // Create new Tab
        tabs.add(filePath);
        int newTabIndex = tabs.size() - 1;

        // Manage + Tab Button
        Component plusTabButton = tabsPanel.getComponent(tabsPanel.getComponentCount() - 1);
        tabsPanel.remove(plusTabButton);

        // Add Buttons in order
        // tabsPanel.add(tabButton(currentTabIndex)); // -1 = index
        tabsPanel.add(tab(newTabIndex, filePath));
        tabsPanel.add(plusTabButton);

        // Rerender
        tabsPanel.revalidate();
        tabsPanel.repaint();

        if (load) {
            // Load added tab
            currentTabIndex = newTabIndex;
            siegeConverterLoad();
        }
    }

    public static void newTab() {
        newTab("", true);
    }

    public static void openTab(int tabIndex) {
        if (tabIndex < 0) {
            System.out.println("Open Tab -> Invalid Tab Index : " + tabIndex);
            return;
        }

        // System.out.println("Opening Tab : " + tabIndex);
        currentTabIndex = tabIndex;
        siegeConverterLoad();
        saveConfig();
    }

    public static void closeTab(int tabIndex) {
        boolean isCurrentTab = currentTabIndex == tabIndex;

        if (isCurrentTab && changesMade) {
            System.out.println("/!\\ Tab With unsaved changed is being closed /!\\");
            // Popup window to save changes or cancel
        }

        // Remove Tab
        tabs.remove(tabIndex);
        tabsPanel.remove(tabIndex);
        tabsPanel.revalidate();
        tabsPanel.repaint();

        // System.out.println("Tab " + tabIndex + " closed");

        if (isCurrentTab) {
            // Open Last Remaining Tab
            openTab(tabs.size() - 1);
        } else {
            currentTabIndex--;
        }

        // Check for button on Click Update
        if (tabIndex < tabs.size()) {
            updateAllTabsButtonOnClick();
        }

        saveConfig();
    }

    public static void updateAllTabsButtonOnClick() {
        int nbrOfTabToUpdate = tabsPanel.getComponentCount() - 1;
        System.out.println("Updating All Tabs Button On Click : " + nbrOfTabToUpdate);

        for (int i = 0; i <= nbrOfTabToUpdate - 1; i++) {
            System.out.println("Updating Tab " + i);
            Component component = tabsPanel.getComponent(i);

            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;

                // Tab Button
                JButton tabButton = (JButton) panel.getComponent(0);
                int tabIndex = i;
                updateButtonAction(tabButton, e -> tabButtonOnClick(tabIndex, e));

                // Close Button
                JButton closeTabButton = (JButton) panel.getComponent(1);
                updateButtonAction(closeTabButton, e -> closeTabButtonOnClick(tabIndex, e));
            }
        }
    }

    public static void updateButtonAction(JButton button, Action<ActionEvent> action) {
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.call(e);
            }
        });
    }

    public static void updateLabelList() {
        labelLinkList.clear();
        getAllLinkLabelsInDocument();
    }

    public static void tabButtonOnClick(int tabIndex, ActionEvent clickEvent) {
        openTab(tabIndex);
    }

    public static void closeTabButtonOnClick(int tabIndex, ActionEvent clickEvent) {
        closeTab(tabIndex);
    }

    // #endregion

    // #endregion

    // #region AppUtils

    // Getters
    public static String getCurrentFilePath() {
        try {
            return tabs.get(currentTabIndex);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentTrueText() {
        return Gears.readTextFileCombined(getCurrentFilePath());
    }

    public static JButton getCurrentTabButton() {
        System.out.println(
                "Get Current Tab Button : " + currentTabIndex + " -> " + (tabsPanel.getComponentCount() - 1) + " =? "
                        + tabs.size());
        JPanel tab = (JPanel) tabsPanel.getComponent(currentTabIndex);

        return (JButton) tab.getComponent(0);
    }

    public static List<JLabel> getAllLinkLabelsInDocument() {
        List<JLabel> labels = new ArrayList<>();

        ElementIterator iterator = new ElementIterator(doc);
        Element element;

        while ((element = iterator.next()) != null) {
            AttributeSet as = element.getAttributes();
            Component component = StyleConstants.getComponent(as);

            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                Color foreground = label.getForeground();

                Font font = label.getFont();
                Map<TextAttribute, ?> attributes = font.getAttributes();
                boolean isUnderlined = TextAttribute.UNDERLINE_ON.equals(attributes.get(TextAttribute.UNDERLINE));

                if (foreground == Color.BLUE && isUnderlined) {
                    labels.add(label);
                }
            }
        }

        return labels;
    }

    public static SimpleAttributeSet getLinkStyle() {
        return linkStyle;
    }

    // Checkers
    public static boolean testLink(int position) {
        AttributeSet attributes = doc.getCharacterElement(position).getAttributes();
        System.out.println("Text at : " + position + " isUnderline : " + StyleConstants.isUnderline(attributes) +
                " isSameColor : "
                + (StyleConstants.getForeground(attributes) == StyleConstants.getForeground(linkStyle)));
        return StyleConstants.isUnderline(attributes);
    }

    public static boolean isComponent(Element element) {
        return element.getName() == "component";
    }

    // #region Add/Delete in Doc

    public static void insertInDoc(int position, String str, AttributeSet style) {
        try {
            doc.insertString(position, str, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void insertInDoc(int position, String str) {
        insertInDoc(position, str, null);
    }

    public static void insertComponentAt(Component component, int location) {
        textPane.setCaretPosition(location);
        textPane.insertComponent(component);
    }

    public static Vector2Int findComponentSpace(Component comp) {
        // 1D position
        Vector2Int space = new Vector2Int(0, 0);

        // Search All Elements in doc
        ElementIterator iterator = new ElementIterator(doc);
        Element element;
        while ((element = iterator.next()) != null) {
            // System.out.println("Elem : " + element.getName() + ", start : " +
            // element.getStartOffset());

            // Element is a component
            if (element.getName() == StyleConstants.ComponentElementName) {
                AttributeSet as = element.getAttributes();
                Component component = StyleConstants.getComponent(as);

                // Component Found
                if (component == comp) {
                    int startOffset = element.getStartOffset();
                    int endOffset = element.getEndOffset();
                    // System.out.println("Component found at offsets: " + startOffset + " to " +
                    // endOffset);

                    space.x = startOffset;
                    space.y = endOffset;
                }
            }
        }

        return space;
    }

    public static void deleteElementAt(int position, int length, StyledDocument doc) {
        try {
            if (doc.getLength() > 0) {
                doc.remove(position, length);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteElementAt(int position, StyledDocument doc) {
        deleteElementAt(position, 1, doc);
    }

    public static void replaceComponentWithText(Component component, String text) {
        Vector2Int offsets = findComponentSpace(component);

        // Delete at comp location
        deleteElementAt(offsets.x, doc);

        // Add at comp location
        insertInDoc(offsets.x, text);
    }

    public static JPanel addComponentsToJPanel(JPanel panel, List<Component> components) {
        for (Component component : components) {
            panel.add(component);
        }

        return panel;
    }

    public static JPanel addComponentsToJPanelLayout(JPanel panel, List<Pair<Component, Object>> components) {
        for (Pair<Component, Object> component : components) {
            panel.add(component.elem1, component.elem2);
        }

        return panel;
    }

    // #endregion

    // Misc
    public static void openFileExplorer(Component parent, String explorerTitle, FileNameExtensionFilter filter,
            Action<File> onGet) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle(explorerTitle);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Files Filter
        if (filter != null) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(filter);
        }

        int returnVal = fileChooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            onGet.call(fileChooser.getSelectedFile());
        }
    }

    // #endregion

    // #region FrontApp / Create Elements

    public static JPanel createPanel(LayoutManager layout, int borderSize, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));

        return panel;
    }

    public static JLabel createClickableLabel(String labelText, Font font, Color textColor, Boolean underline,
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

    public static JLabel createLinkLabel(String labelText, String link) {
        // TODO : Adapt for open new tab
        JLabel linkLabel = createClickableLabel(labelText, mainFont, Color.BLUE, true,
                clickEvent -> Gears.openLinkDefaultBrowser(link));

        labelLinkList.add(new LinkLabel(linkLabel, labelText, link));
        return linkLabel;
    }

    public static JButton createOpenFileButton(JFrame frame) {
        return App.createJButton("Open File...", defaultColor, clickEvent -> {
            // System.out.println("Choosing file to open...");
            // Can be made 1 line
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Only .txt files", "txt");
            App.openFileExplorer(frame, "Select a .txt file", new FileNameExtensionFilter("Only .txt files", "txt"),
                    file -> {
                        // On File Selected
                        String filePath = file.getPath();
                        getCurrentTabButton().setText(Gears.getFileNameFromPath(filePath));
                        tabs.set(currentTabIndex, filePath);
                        siegeConverterLoad();
                        saveConfig();
                    });
        });
    }

    public static JButton createJButton(String buttonText, Action<ActionEvent> onClick) {
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

    public static JButton createJButton(String buttonText, Color bgColor, Action<ActionEvent> onClick) {
        JButton button = createJButton(buttonText, onClick);
        button.setBackground(bgColor);
        return button;
    }

    public static JPanel tab(int tabIndex, String title) {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tab Button
        gbc.weightx = 0.9;
        gbc.gridx = 0;
        String tabTitle = Gears.stringIsNullOrEmpty(title) ? "Tab " + tabIndex : Gears.getFileNameFromPath(title);
        tabPanel.add(App.createJButton(tabTitle, mEvent -> tabButtonOnClick(tabIndex, mEvent)), gbc);

        // Close / X Button
        JButton closeButton = createJButton("X", Color.DARK_GRAY, mEvent -> closeTabButtonOnClick(tabIndex, mEvent));

        closeButton.setForeground(Color.WHITE);

        gbc.weightx = 0.1;
        gbc.gridx = 1;
        tabPanel.add(closeButton, gbc);

        return tabPanel;
    }

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
        JTextField linkField = new JTextField("", 20);

        JLabel linkTextFieldLabel = new JLabel("Text : ");
        JTextField linkTextField = new JTextField("", 20);

        JButton confirmButton = createJButton("Confirm", e -> {
            System.out.println("Confirm Insert");

            // Insert Link
            textPane.insertComponent(createLinkLabel(linkTextField.getText(), linkField.getText()));

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

    // #endregion

    // #region Old

    // only parameter style can be null
    public static void insertTextWithCallBackOnClick(Map<Vector2Int, Runnable> callBackMap,
            AttributeSet style, String text, Runnable callBack) {
        callBackMap.put(new Vector2Int(doc.getLength(), text.length()), callBack);

        insertInDoc(doc.getLength(), text, style);
    }

    public static void triggerCallBack(MouseEvent mouseEvent, Map<Vector2Int, Runnable> callBackMap) {
        int mouseCharPosition = textPane.viewToModel2D(mouseEvent.getPoint());
        // System.out.println("Character clicked : " + mouseCharPosition);

        for (Map.Entry<Vector2Int, Runnable> entry : callBackMap.entrySet()) {
            int charStart = entry.getKey().x;
            int charNbr = entry.getKey().y;
            Runnable callBack = entry.getValue();

            // Text Clicked
            if (callBack != null && charStart <= mouseCharPosition && mouseCharPosition < charStart + charNbr) {
                callBack.run();
            }
        }
    }

    // #endregion

    public static class LinkLabel {
        JLabel comp;
        String text;
        String link;
        int location;

        public LinkLabel(JLabel comp, String text, String link) {
            this.comp = comp;
            this.text = text;
            this.link = link;
        }

        public LinkLabel(JLabel comp, String text, String link, int location) {
            this.comp = comp;
            this.text = text;
            this.link = link;
            this.location = location;
        }
    }
}