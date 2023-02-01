package com.bc;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class Ventura {
    private static final String PROP_SETTINGS_FILE = "settingsFile";
    private static final String PROP_DEBUG = "debug";
    private static final String PROP_VENTURA_LAF = "venturaLaf";
    @SuppressWarnings("unused")
    private static final boolean DEBUG = Boolean.getBoolean(PROP_DEBUG);

    public static void main(String[] args) throws Exception {
        loadSettings();
        setLaf();

        EventQueue.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Ventura Test App");
                addMainMenu(frame);
                Dimension screenSize = frame.getToolkit().getScreenSize();
                frame.setSize(screenSize.width / 2, screenSize.height / 2);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void addMainMenu(JFrame frame) {

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        fileMenu.add(createMenuItem("Open"));
        fileMenu.add(createMenuItem("Close", a -> System.exit(0)));

        JMenu otherMenu = new JMenu("Other");
        menuBar.add(otherMenu);
        otherMenu.add(createMenuItem("Settings", a -> showSettings(frame)));
        JMenu toolsMenu = new JMenu("Tools");
        otherMenu.add(toolsMenu);
        toolsMenu.add(createMenuItem("Tools 1"));
        toolsMenu.add(createMenuItem("Tools 2"));
        toolsMenu.add(createMenuItem("Tools 3"));
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        helpMenu.add(createMenuItem("About"));
        helpMenu.add(createMenuItem("Properties", a -> showProperties(frame)));
    }

    private static void showSettings(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Settings", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setPreferredSize(new Dimension(180, 200));
        JPanel settingsPanel = new JPanel(new GridLayout(4, 1, 4, 4));
        dialog.add(settingsPanel);

        JCheckBox menuBarBox = new JCheckBox("Use Screen menu Bar");
        menuBarBox.setSelected(Boolean.getBoolean("apple.laf.useScreenMenuBar"));
        menuBarBox.addActionListener(e -> System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(menuBarBox.isSelected())));
        settingsPanel.add(menuBarBox);

        JCheckBox brushLookBox = new JCheckBox("Use Brush Metal Look");
        brushLookBox.setSelected(Boolean.getBoolean("apple.awt.brushMetalLook"));
        brushLookBox.addActionListener(e -> System.setProperty("apple.awt.brushMetalLook", String.valueOf(brushLookBox.isSelected())));
        settingsPanel.add(brushLookBox);

        JCheckBox aliasingBox = new JCheckBox("Use Text Anti-Aliasing");
        aliasingBox.setSelected(Boolean.getBoolean("apple.awt.textantialiasing"));
        aliasingBox.addActionListener(e -> System.setProperty("apple.awt.textantialiasing", String.valueOf(aliasingBox.isSelected())));
        settingsPanel.add(aliasingBox);

        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 60));
        closeButton.setDefaultCapable(true);
        closeButton.addActionListener(e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        settingsPanel.add(closeButton);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private static void showProperties(Component parent) {
        JTable table = new JTable(createSystemPropertiesTableModel()) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                                             int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    String s = getValueAt(row, column).toString();
                    jc.setToolTipText(s);
                }
                return c;
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize(200, 300);
        JFrame jFrame = new JFrame("Properties");
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setLocationRelativeTo(parent);
        jFrame.add(scrollPane);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private static DefaultTableModel createSystemPropertiesTableModel() {
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Property");
        model.addColumn("Value");

        Properties p = System.getProperties();
        Set<Object> keys = p.keySet();
        SortedSet<Object> sortedKeys = new TreeSet<>(keys);

        for (Object sortedKey : sortedKeys) {
            String key = sortedKey.toString();
            String value = p.getProperty(key);
            String[] row = {key, value};
            model.addRow(row);
        }

        return model;
    }

    private static JMenuItem createMenuItem(String itemName, ActionListener al) {
        JMenuItem item = new JMenuItem(itemName);
        item.addActionListener(al);
        return item;
    }

    private static JMenuItem createMenuItem(String itemName) {
        JMenuItem item = new JMenuItem(itemName);
        item.addActionListener(e -> showMessage(item));
        return item;
    }

    private static void setLaf() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        String venturaLaf = System.getProperty(PROP_VENTURA_LAF);
        if (venturaLaf != null) {
            UIManager.setLookAndFeel(venturaLaf);
        }
    }

    private static void loadSettings() throws IOException {
        String propFile = System.getProperty(PROP_SETTINGS_FILE);
        if (propFile != null) {
            try (InputStream inStream = Files.newInputStream(Paths.get(propFile))) {
                System.getProperties().load(inStream);
            }
        }
    }

    private static void showMessage(JMenuItem clickedItem) {
        JOptionPane.showMessageDialog(getParentFrame(clickedItem), "Hello! You have clicked '" + clickedItem.getText() + "'.", clickedItem.getText() + " Dialog", JOptionPane.INFORMATION_MESSAGE);
    }

    private static JFrame getParentFrame(Component component) {
        if (component == null) {
            return null;
        } else if (component instanceof JFrame) {
            return (JFrame) component;
        } else {
            return getParentFrame(component.getParent());
        }
    }
}