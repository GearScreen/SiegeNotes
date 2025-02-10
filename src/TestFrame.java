import gears.Gears;
import gears.Gears.Out;
import gears.Gears.Pair;
import gears.Gears.Vector2Int;
import static gears.Gears.cPair;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;

import org.json.JSONArray;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestFrame extends JFrame {
    // public Component linkLabelTest;

    public static void Frame() {
        // new TestFrame().init();

        // exempleFrame();

        // testLabelFrame();

        new TestFrame().clickableTextWithCallback();
    }

    public void init() {
        JTextField tfFirstName, tfLastName;
        JLabel lbTest;

        // First Name Field
        JLabel lbFirstName = new JLabel("First Name Reject");
        lbFirstName.setFont(App.oldFont);

        tfFirstName = new JTextField();
        tfFirstName.setFont(App.oldFont);

        // Last Name Field
        JLabel lbLastName = new JLabel("Last Name Servitor");
        lbLastName.setFont(App.oldFont);

        tfLastName = new JTextField();
        tfLastName.setFont(App.oldFont);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 5, 5));
        formPanel.setOpaque(false);
        formPanel.add(lbFirstName);
        formPanel.add(tfFirstName);
        formPanel.add(lbLastName);
        formPanel.add(tfLastName);

        // Label
        lbTest = new JLabel();
        lbTest.setFont(App.oldFont);

        // Button OK
        JButton btnOk = new JButton("OK");
        btnOk.setFont(App.oldFont);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = tfFirstName.getText();
                String lastName = tfLastName.getText();
                lbTest.setText("Welcome Reject " + firstName + " " + lastName);
            }
        });

        // Button CLEAR
        JButton btnClear = new JButton("CLEAR");
        btnClear.setFont(App.oldFont);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfFirstName.setText("");
                tfLastName.setText("");
                lbTest.setText("");
            }
        });

        // Button Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(btnOk);
        buttonsPanel.add(btnClear);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(128, 128, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(lbTest, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Frame
        // Add Panels
        add(mainPanel);

        // Properies
        URL imgURL = getClass().getResource(App.darktide_Icon_Path);
        setIconImage(new ImageIcon(imgURL, "Icon").getImage());
        setTitle("Hive Tertium");
        setSize(500, 600);
        setMinimumSize(new Dimension(300, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void exempleFrame() {
        // Frame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // TextPane
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");

        // Label
        JLabel clickableLabel = new JLabel("Click me 1");
        clickableLabel.setFont(textPane.getFont());
        clickableLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clickableLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked");
            }
        });

        textPane.insertComponent(clickableLabel);

        frame.add(new JScrollPane(textPane));
        frame.setVisible(true);
    }

    public void testLabelFrame() {
        // TextPane
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        // textPane.setFont(mainFont);

        // Label
        JLabel clickableLabel = new JLabel("Click me 2");
        // clickableLabel.setAlignmentY(10f);
        clickableLabel.setFont(textPane.getFont()); // mainFont
        clickableLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clickableLabel.addMouseListener(new MouseAdapter() {
            /*
             * public void mouseClicked(MouseEvent e) {
             * System.out.println("Label clicked");
             * }
             */

            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse pressed");
            }

            public void mouseReleased(MouseEvent e) {
                System.out.println("Mouse released");
            }

            public void mouseEntered(MouseEvent e) {
                System.out.println("Mouse entered");
            }

            public void mouseExited(MouseEvent e) {
                System.out.println("Mouse exited");
            }
        });

        JTextField t = new JTextField();
        // t.setSize(1, 1);
        // t.setColumns(10);
        t.setText("Text");

        JTextPane t2 = new JTextPane();
        t2.setSize(1, 1);
        t2.setText("Sacabambaspis 123 🗿");

        // Order matter -> q
        // textPane.insertComponent(t);
        textPane.insertComponent(t2);
        textPane.insertComponent(clickableLabel);

        // System.out.println("Text : " + textPane.getText());

        JScrollPane scroll = new JScrollPane(textPane);

        // ###PANELS
        Runnable testPanelRun = () -> {
            // Text Panel
            JPanel textPanePanel = App.addComponentsToJPanel(
                    App.createPanel(new GridLayout(4, 1, 5, 5), 10, getForeground()),
                    Arrays.asList(clickableLabel, scroll));

            textPanePanel.setSize(500, 500);
            textPanePanel.setOpaque(false);

            // Test Panel
            App.addComponentsToJPanelLayout(App.createPanel(new GridLayout(1, 1), 20, getForeground()),
                    Arrays.asList(cPair(textPanePanel, BorderLayout.CENTER), cPair(clickableLabel, null)));
        };
        // testPanelRun.run();

    }

    private void testFunc() {
        Runnable r2 = () -> {
            // isComponent() exemple Use
            // System.out.println("Is Component " +
            // App.isComponent(doc.getCharacterElement(298)));

            // textPane.remove(linkLabelTest); // Doesn't work

            // System.out.println("Components : " + textPane.getComponents());

            for (Component comp : App.textPane.getComponents()) {
                System.out.println("Remove Comp : " + comp);
                App.textPane.remove(comp);
            }
        };
        // r2.run();

        // Remove the component from the document
        Runnable r1 = () -> {
            Element root = App.doc.getDefaultRootElement();

            for (int i = 0; i < root.getElementCount(); i++) {
                Element elem = root.getElement(i);
                AttributeSet attr = elem.getAttributes();
                Component comp = StyleConstants.getComponent(attr); // null

                System.out.println("Elem : " + elem);

                if (comp == App.labelLinkList.get(0).comp) {
                    try {
                        App.doc.remove(elem.getStartOffset(), elem.getEndOffset() - elem.getStartOffset());
                        break;
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // Delete linkLabel
            // App.deleteElementAt(App.findComponentOffsets(linkLabelTest, doc).x, doc);

            // Delete Text
            // App.deleteElementAt(250, 10, doc);

            // Replace Component
            // App.replaceComponentWithText(doc, linkLabelTest, "Test Replace Text");
        };
        // r1.run();

        // Create and Insert component at location
        Runnable r3 = () -> {
            var linkLabelTest = App.createLinkLabel("Link Label", "https://google.com");
            App.insertComponentAt(linkLabelTest, 250);
        };
        // r3.run();

        // insert string in string
        Runnable r4 = () -> {
            StringBuffer buffer = new StringBuffer("Test String");
            buffer.insert(1, "New String");
            String newString = buffer.toString();
            System.out.println(newString); // TNew Stringest String
        };
        // r4.run();

        // cut string at location
        Runnable r5 = () -> {
            String s = "TestTextString";
            int cutPos = s.length() / 2;
            String firstPart = s.substring(0, cutPos);
            String secondPart = s.substring(cutPos);
            System.out.println("frst part : " + firstPart);
            System.out.println("Scd part : " + secondPart);
        };
        // r5.run();

        // Gears.insertManyStrings() Test
        Runnable r6 = () -> {
            // Expected result : "Test String Text Length MegaTank" -> "TSacabambaspisest
            // DeudroedolionStrZealoting Text Length MegaTank"
            String mainString = "Test String Text Length MegaTank";
            var list = Arrays.asList(cPair(1, "Sacabambaspis"), cPair(5, "Deudroedolion"), cPair(8, "Zealot"));

            System.out.println(Gears.insertManyStrings(false, mainString, list, stringInfo -> stringInfo));
        };
        // r6.run();

        // App.siegeConverterToText Test
        Runnable r7 = () -> {
            // System.out.println("Doc Length : " + textPane.getDocument().getLength());
            // System.out.println("Text Length : " + textPane.getText().length());
            String convertedText = App.siegeConverterSave();
            System.out.println("Convertion!!!! : " + convertedText);
            App.saveFile(convertedText);
        };
        // r7.run();

        // ASCII-FICATION!!!!
        Runnable r8 = () -> {
            System.out.println("ASCIIFICATION!!!!! \r\n" + //
                    "       ____\r\n" + //
                    "  _-~'~~~~~~\\\r\n" + //
                    "<< °'  !!!!  )\r\n" + //
                    "   ' -\\_____/  \r\n" + //
                    "      #    #\r\n" + //
                    "      |    |\r\n" + //
                    "  ___ | ___|\r\n" + //
                    "     / \\  / \\\r\n" + //
                    "    /   \\/   \\" + "");
        };
        // r8.run();

        // App.siegeConverterToNote() Test
        Runnable r9 = () -> {
            String s = "Deud cat table cat Sacabampaspis";
            int i = s.indexOf("cat", 6);
            // System.out.println("Index 1 : " + s.indexOf("cataq") + " Index 2 : " + i);
            // System.out.println(Gears.deleteStringAt(s, i, i + 4));
            // System.out.println("Should be 14 : " + App.labelCodeOpen.length());
            // System.out.println("text length : " + textPane.getText().length() + " doc
            // Length : " + doc.getLength());
            App.siegeConverterLoad();
        };
        // r9.run();

        // Test Open File dfault program
        Runnable r10 = () -> {
            // System.out.println(Gears.findIn(App.tabs, App.getCurrentFilePath()));

            File file = new File("E:\\MultiMedia\\Cours\\ArchiN-tier\\WA_Architectures_Client_Serveur_1.pdf");
            Gears.openFileDefault(file);
        };
        // r10.run();

        // Infinit/Back Windows
        Runnable r11 = () -> {
            backWindows();
        };
        // r11.run();

        // Test Iterable Converter
        Runnable r12 = () -> {
            // System.out.println(Gears.iterableConverter(App.tabs, tab -> tab.elem1));
            // //Old
            var t = Gears.iterableConverter(Arrays.asList("1", "2", "3"), s -> Integer.parseInt(s));
            System.out.println(t); // .get(0).getClass()
            System.out.println(new JSONArray(t).getInt(2));
        };
        // r12.run();

        // Test Tab
        Runnable r13 = () -> {
            System.out.println(App.getCurrentTabButton().getText());
            App.closeTab(App.tabs.size() - 1);
        };
        //r13.run();

        // Test Tab
        Runnable r14 = () -> {
            App.getAllLinkLabelsInDocument();
        };
        r14.run();

        App.textPane.revalidate();
        App.textPane.repaint();
    }

    public static void backWindows() {
        Out<JFrame> lastWindow = new Out<JFrame>(null);

        JButton openNewWindowButton = new JButton("New Window");
        openNewWindowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // On Click
                lastWindow.param = App.smallWindow(lastWindow.param, "Back Window",
                        Arrays.asList(openNewWindowButton));
            }
        });

        lastWindow.param = App.smallWindow(App.mainFrame, "Back Window",
                Arrays.asList(openNewWindowButton));
    }

    private void clickableTextWithCallback() {
        Map<Vector2Int, Runnable> callBackMap = new HashMap<>();

        // Panes :
        // Center :
        // Text Pane
        App.textPane = new JTextPane();
        App.doc = App.textPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(App.textPane);

        // Open file
        // textPane.setText(Gears.readTextFileCombined(getCurrentFilePath()));

        // North :
        // Button Panel TOP
        App.tabsPanel = App.addComponentsToJPanel(App.createPanel(new FlowLayout(), 0, Color.gray),
                Arrays.asList(
                        // App.tab(0, "Tab 0"), //Test default tab
                        App.createJButton("+ Tab", mouseEvent -> {
                            App.newTab();
                        })));

        // BOT
        JButton openFileButton = App.createOpenFileButton(this);

        JButton addTestLinkButton = App.createJButton("Add Test Links", clickEvent -> {
            System.out.println("Add test Link");

            App.insertTextWithCallBackOnClick(callBackMap, App.getLinkStyle(), "Google", () -> {
                Gears.openLinkDefaultBrowser("https://google.com");
            });
            App.insertInDoc(App.doc.getLength(), " | ", null);
            App.insertTextWithCallBackOnClick(callBackMap, App.getLinkStyle(), "GitHub",
                    () -> Gears.openLinkDefaultBrowser("https://www.github.com"));
            App.insertInDoc(App.doc.getLength(), " | ", null);
            App.insertTextWithCallBackOnClick(callBackMap, App.getLinkStyle(), "StackOverflow",
                    () -> Gears.openLinkDefaultBrowser("https://stackoverflow.com"));

            App.testLink(App.doc.getLength() - 1);
        });

        // /!\ Self Destruction Button /!\
        JButton selfDestructionButton = App.createJButton("/!\\ Self Destruction 🗿 🐒", Color.red, clickEvent -> {
            // System.out.println("Button clicked");

            // Remove Components :
            // Self Destruction
            System.out.println("Components : " + getComponents());
            for (Component comp : getComponents()) {
                System.out.println("Remove Comp : " + comp);
                remove(comp);
            }
            revalidate();
            repaint();
            System.out.println("Application Destroyed Successfully");
        });

        // Back Windows Button 💀
        JButton backWindowsButton = App.createJButton("Back Windows 💀", Color.DARK_GRAY, clickEvent -> {
            System.out.println("Back Windows $$ffzglb^ç-");
            backWindows();
        });
        backWindowsButton.setForeground(Color.red);

        // Test Button
        JButton testButton = App.createJButton("🪵 Test Button 🪵", clickEvent -> {
            // System.out.println("TestButton clicked");
            testFunc();
        });

        // Create Link Button
        JButton createLinkButton = App.createJButton("Insert Link...", clickEvent -> {
            System.out.println("Create Link...");

            App.insertLinkWindow(this);
        });

        // Button Panel BOT
        JPanel buttonPanelMenu = App.addComponentsToJPanel(
                App.createPanel(new FlowLayout(FlowLayout.LEFT), 0, App.defaultColor),
                Arrays.asList(openFileButton, addTestLinkButton, testButton, selfDestructionButton, backWindowsButton,
                        createLinkButton));

        // Main ButtonPanel Wrap Top + Bot
        JPanel buttonPanel = App.addComponentsToJPanel(App.createPanel(new GridLayout(2, 2), 0, Color.WHITE),
                Arrays.asList(App.tabsPanel, buttonPanelMenu));

        // Callbacks :
        // OnClick
        App.textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                App.triggerCallBack(mouseEvent, callBackMap);
            }
        });

        // OnKeyPress
        App.textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                // System.out.println("Key pressed: " + keyEvent.getKeyChar());
                // System.out.println("Key Code Pressed : " + keyEvent.getKeyCode());
                // System.out.println("CTRL : " + e.isControlDown());
                // System.out.println("ALT : " + e.isAltDown());

                // Detect CTRL + E -> Test
                if (keyEvent.getKeyCode() == KeyEvent.VK_E && keyEvent.isControlDown()) {
                    System.out.println("CTRL + E was pressed");
                    // Selector pos
                    App.textPane.setCaretPosition(250);
                }

                // Save file
                if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isControlDown()) {
                    // App.saveFile(textPane.getText());
                    App.saveFile(App.siegeConverterSave());
                }

                // Insert Link
                if (keyEvent.getKeyCode() == KeyEvent.VK_L && keyEvent.isControlDown()) {
                    App.textPane.insertComponent(App.createLinkLabel("Link Label", "https://google.com"));
                }
            }

            /*
             * @Override
             * public void keyReleased(KeyEvent e) {
             * System.out.println("Key released: " + e.getKeyChar());
             * }
             *
             * @Override
             * public void keyTyped(KeyEvent e) {
             * System.out.println("Key typed: " + e.getKeyChar());
             * }
             */
        });

        // Test Keypress -> Didn't work
        Runnable controlA = () -> {
            // Create an action for Ctrl key
            AbstractAction controlAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Ctrl key pressed");
                }
            };

            App.textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, false), "control pressed");
            App.textPane.getActionMap().put("control pressed", controlAction);
        };
        // controlA.run();

        // Main Panel / Layout :
        JPanel mainPanel = App.addComponentsToJPanelLayout(App.createPanel(new BorderLayout(), 0, Color.BLACK),
                Arrays.asList(cPair(scroll, BorderLayout.CENTER), cPair(buttonPanel, BorderLayout.NORTH)));

        // Frame Setup + Display
        App.InitFrame(this, "Siege Notes Test Window", App.darktide_Icon_Path, new Component[] { mainPanel },
                new Vector2Int(1200, 650), new Vector2Int(300, 300));
    }
}