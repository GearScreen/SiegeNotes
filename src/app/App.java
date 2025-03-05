package app;

import static app.FrontApp.*;

import gears.Gears;
import gears.Gears.Action;
import gears.Gears.Pair;
import gears.Gears.Vector2Int;

import static gears.Gears.*;

import org.json.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;

public class App {
    // #Main
    public static App singleton = new App();

    public static JFrame mainFrame;
    public static StyledDocument doc;
    public static JTextPane textPane;
    public static JPanel tabsPanel;

    public static int currentTabIndex = 0;
    public static List<Pair<String, List<LinkLabel>>> tabs = new ArrayList<>(); // CurrentFilePath Array

    public static boolean changesMade = false;

    // #Save
    // Config
    public static String configFilePath = ""; // src/siegeNoteConfig.json
    public static final String currentTabKey = "CurrentTab";
    public static final String tabsKey = "Tab";

    // Label code parts
    public static final String labelCodeOpen = "***labelTitle:";
    public static final String labelCodeLink = ", labelLink:";
    public static final String labelCodeClose = "***";

    // #Style
    public static final String icon_Path = "resources/ParchmentIcon.png";

    public static final Font mainFont = new Font("Dialog", Font.PLAIN, 15);
    public static final Font menuFont = new Font("Dialog", Font.BOLD, 14);

    public static final Font textAreaFont = new Font("Segoe UI Emoji", Font.PLAIN, 15);

    public static final Color defaultColor = new Color(238, 238, 238);

    private static SimpleAttributeSet linkStyle = new SimpleAttributeSet();

    // #region Main

    public static void InitFrame(JFrame frame, String title, String iconPath, Component[] components, Vector2Int size,
            Vector2Int minimumSize) {
        // Objects.requireNonNull(message, "message cannot be null");

        // Add Components
        for (Component compo : components) {
            frame.add(compo);
        }

        // Window Icon
        URL imgURL = App.class.getClassLoader().getResource(iconPath);
        if (imgURL != null) {
            frame.setIconImage(new ImageIcon(imgURL, "Icon").getImage());
        } else {
            System.out.println("Icon not found: " + iconPath);
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

    public static void saveConfig() {
        // Create a JSONObject
        JSONObject jsonObject = new JSONObject();

        // Save Values
        // jsonObject.put(currentFilePathKey, getCurrentFilePath()); // Old

        // Current TabIndex
        jsonObject.put(currentTabKey, currentTabIndex);

        // Tabs FilePath
        jsonObject.put(tabsKey, new JSONArray(getTabsFilePathList()));

        writeJsonFile(getConfigFilePath(), jsonObject, () -> System.out.println("Config Saved"));
    }

    public static void loadConfig() {
        JSONObject jsonObject = null;

        try {
            // Read Config
            jsonObject = readJsonFileFromFileSystem(getConfigFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            System.out.println("No Config Found -> Creating New Config");
            saveConfig();
            newTab();
        } else {
            // Load
            currentTabIndex = jsonObject.getInt(currentTabKey);
            JSONArray tabsFilepath = jsonObject.getJSONArray(tabsKey);
            // System.out.println("Filepath found : " + tabsFilepath);
            // System.out.println("Current Tab Index : " + currentTabIndex);

            // Tabs
            // System.out.println("Tabs Found : " + tabsFilepath.length());
            if (tabsFilepath.length() == 0) {
                // No Tabs found -> Create Default Tab
                newTab();
            } else {
                for (int i = 0; i <= tabsFilepath.length() - 1; i++) {
                    newTab(tabsFilepath.getString(i), false);
                }
            }

            siegeConverterLoad();

            // System.out.println("Config Loaded -> Current File : " +
            // getCurrentFilePath());
        }
    }

    public static void saveFile(String text) {
        // if (!changesMade) {return;}

        String currentFilePath = getCurrentFilePath();

        if (currentFilePath == "") {
            System.out.println("No File to Save -> Save As");
            saveAs(text);
            return;
        }

        Gears.writeTextToFile(currentFilePath, text);
        System.out.println("File Saved");
    }

    public static void saveAs(String text) {
        System.out.println("Save As...");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Save as .txt", "txt");

        openFileExplorer(mainFrame, "Save .txt file", filter, "Save", "TextFile.txt", file -> {
            String filePath = file.getPath();
            // Write File
            Gears.writeTextToFile(filePath, text);

            // Update Tab
            tabs.get(currentTabIndex).elem1 = filePath;

            // Change Tab Name
            getCurrentTabButton().setText(Gears.getFileNameFromPath(filePath));
            System.out.println("File Saved As : " + filePath);

            saveConfig();
        });
    }

    // #endregion

    // #region SiegeConverter

    public static String siegeConverterSave() {
        // Conversion logic : linkLabelsText to trueText

        // Get Text
        String mainString = textPane.getText();
        int differenceTextDoc = mainString.length() - doc.getLength() - 1;

        // Generate Saved String
        String finalString = Gears.insertManyStrings(true, mainString, getCurrentLabelList(), labelLink -> {
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
        if (!Gears.isValidFilePath(filePath)) {
            System.out.println("/!\\ File Path is not valid : " + filePath + " /!\\");
            return;
        }

        String trueText = Gears.readTextFileCombined(filePath);
        textPane.setText(trueText);

        // Conversion logic trueText -> linkLabelsText/Note :
        List<LinkLabel> labelsToCreate = new ArrayList<>();

        // Line breaks create a difference between trueText and doc
        List<Integer> lineBreaks = Gears.getAllLineBreakIndexes(trueText);

        int offset = 0; // Difference between text with/without label codes

        // While label codes are found -> get Label Name + link + location
        int startIndex = trueText.indexOf(labelCodeOpen, 0);

        while (startIndex != -1) {
            int startIndexCopy = startIndex;
            int nbrOfLineBreak = countElementUntilCondition(lineBreaks,
                    lineBreakIndex -> startIndexCopy < lineBreakIndex);

            // Label Indexs
            int startIndexEnd = startIndex + labelCodeOpen.length();
            int linkIndex = trueText.indexOf(labelCodeLink, startIndex);

            int EndIndexStart = trueText.indexOf(labelCodeClose, startIndexEnd);
            int endIndex = EndIndexStart + labelCodeClose.length();

            // System.out.println("Label Found : " + index + " to " + startEndIndex);

            // Label Values
            String labelTitle = trueText.substring(startIndexEnd, linkIndex);
            String labelLink = trueText.substring(linkIndex + labelCodeLink.length(), EndIndexStart);

            // Remove Label Code
            trueText = Gears.deleteStringAt(trueText, startIndex, endIndex);

            // Store new label
            labelsToCreate.add(new LinkLabel(null, labelTitle, labelLink, startIndex - nbrOfLineBreak));

            // Add size of the label code
            offset += endIndex - startIndex;

            // Find Next Label code
            startIndex = trueText.indexOf(labelCodeOpen, endIndex - offset);
        }

        textPane.setText(trueText);

        // Insert Labels
        if (labelsToCreate.size() == 0) {
            return;
        }

        // Offset First Label
        LinkLabel firstLabel = labelsToCreate.get(0);
        cLinkLabel(firstLabel.text, firstLabel.link, firstLabel.location);

        for (int i = 1; i < labelsToCreate.size(); i++) {
            LinkLabel label = labelsToCreate.get(i);
            cLinkLabel(label.text, label.link, label.location - i - 1);
        }
    }

    public static void siegeConverterLoad() {
        siegeConverterLoad(getCurrentFilePath());
    }

    // #endregion

    // #region Tabs

    public static void newTab(String filePath, boolean load) {
        // System.out.println("Adding a new Tab... ");

        String newTabFilePath = "";

        if (Gears.isValidFilePath(filePath)) {
            newTabFilePath = filePath;
        }

        // Create Back Tab
        tabs.add(cPair(newTabFilePath, new ArrayList<>()));
        int newTabIndex = tabs.size() - 1;

        // Manage + Tab Button
        Component plusTabButton = tabsPanel.getComponent(tabsPanel.getComponentCount() - 1);
        tabsPanel.remove(plusTabButton);

        // Add Front Tab
        // tabsPanel.add(tabButton(currentTabIndex)); // -1 = index
        tabsPanel.add(tab(newTabIndex, newTabFilePath));
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

    public static void tabButtonOnClick(int tabIndex, ActionEvent clickEvent) {
        if (tabIndex == currentTabIndex) {
            return;
        }

        openTab(tabIndex);
    }

    public static void closeTabButtonOnClick(int tabIndex, ActionEvent clickEvent) {
        closeTab(tabIndex);
    }

    public static class LinkLabel {
        public JLabel comp;
        public String text;
        public String link;
        public int location;

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

    // #endregion

    // #endregion

    // #region AppUtils

    // #region Getters

    public static String getConfigFilePath() {
        if (configFilePath == "") {
            String projectPath = System.getProperty("user.dir");
            //File projectDir = new File(projectPath);
            //String parentPath = projectDir.getParent(); // Navigate up one directory level
            String fullPath = projectPath + File.separator + "siegeNotesConfig.json";
            configFilePath = fullPath;
        }

        return configFilePath;
    }

    public static String getCurrentFilePath() {
        try {
            return tabs.get(currentTabIndex).elem1;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentTrueText() {
        return Gears.readTextFileCombined(getCurrentFilePath());
    }

    public static JButton getCurrentTabButton() {
        // System.out.println("Get Current Tab Button : " + currentTabIndex);
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
        if (linkStyle == null) {
            linkStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(linkStyle, Color.BLUE);
            StyleConstants.setUnderline(linkStyle, true);
        }

        return linkStyle;
    }

    public static List<LinkLabel> getCurrentLabelList() {
        return tabs.get(currentTabIndex).elem2;
    }

    public static List<String> getTabsFilePathList() {
        return Gears.iterableConverter(tabs, tab -> tab.elem1);
    }

    // #endregion

    // #region Checkers

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

    // #endregion

    // #region Add/Delete/Update

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

    public static JPanel addComponentsToJPanelLayout(JPanel panel, List<Pair<Component, Object>> components) {
        for (Pair<Component, Object> component : components) {
            panel.add(component.elem1, component.elem2);
        }

        return panel;
    }

    // #endregion

    // #region FileExplorer

    public static void openFileExplorer(Component parent, String explorerTitle, FileNameExtensionFilter filter,
            String buttonText, String defaultFileName, Action<File> onGet) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle(explorerTitle);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Set custom text for the approve button
        fileChooser.setApproveButtonText(buttonText);

        // Set default file name
        if (!Gears.stringIsNullOrEmpty(defaultFileName)) {
            fileChooser.setSelectedFile(new File(defaultFileName));
        }

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

    public static void fileExplorerOpenFile() {
        // System.out.println("Choosing file to open...");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Only .txt files", "txt");

        openFileExplorer(mainFrame, "Select a .txt file", filter, "Open", "", file -> {
            // On File Selected
            String filePath = file.getPath();

            // Update Tab
            tabs.set(currentTabIndex, cPair(filePath, new ArrayList<>()));

            // Change Tab Name
            getCurrentTabButton().setText(Gears.getFileNameFromPath(filePath));

            siegeConverterLoad();
            saveConfig();
        });
    }

    // #endregion

    // #endregion
}