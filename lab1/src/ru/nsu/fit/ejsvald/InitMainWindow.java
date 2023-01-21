package ru.nsu.fit.ejsvald;

import ru.nsu.fit.ejsvald.setting.ColorChooserPanel;
import ru.nsu.fit.ejsvald.setting.SettingDialog;
import ru.nsu.fit.ejsvald.tool.FloodFillTool;
import ru.nsu.fit.ejsvald.tool.LineTool;
import ru.nsu.fit.ejsvald.tool.StampTool;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class InitMainWindow extends MainFrame implements ComponentListener {
    JScrollPane scrollPane;
    ButtonGroup toolsMenuGroup = new ButtonGroup();
    ButtonGroup colorMenuGroup = new ButtonGroup();
    ButtonGroup toolBarToolGroup = new ButtonGroup();
    ButtonGroup toolBarColorGroup = new ButtonGroup();
    HashMap<String, JRadioButtonMenuItem> toolsMenu = new HashMap<>();
    HashMap<String, JRadioButtonMenuItem> colorMenu = new HashMap<>();
    HashMap<String, JToggleButton> toolBarTools = new HashMap<>();
    HashMap<String, JToggleButton> toolBarColors = new HashMap<>();

    public InitMainWindow() {
        super(600, 400, "Paint");
        addComponentListener(this);

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/Save", "Save image", KeyEvent.VK_X, "onSave");
            addMenuItem("File/Open", "Open image", KeyEvent.VK_X, "onOpen");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");

            addSubMenu("Tools", KeyEvent.VK_T);
            addSubMenu("Colors", KeyEvent.VK_W);

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Shows program version and copyright information",
                    KeyEvent.VK_A, "About.gif", "onAbout");

            toolsMenu.put("Line", addRadioMenuItem("Tools/Line", "Activate line drawing tool", KeyEvent.VK_L,
                    "Line.gif", "onLine", toolsMenuGroup));
            toolsMenu.put("N-Gon", addRadioMenuItem("Tools/N-Gon", "Draw n-gon", KeyEvent.VK_N, "polygon.gif", "onNGon", toolsMenuGroup));
            toolsMenu.put("Star", addRadioMenuItem("Tools/Star", "Draw star", KeyEvent.VK_S, "star.gif", "onStar", toolsMenuGroup));
            toolsMenu.put("Flood fill", addRadioMenuItem("Tools/Flood fill", "Activate flood fill tool", KeyEvent.VK_F,
                    "floodfill.gif", "onFloodFill", toolsMenuGroup));

            addMenuItem("Tools/Tools Settings", "Open active tool settings", KeyEvent.VK_S,
                    "settings.gif", "onToolSettings");
            addMenuItem("Tools/Clear", "Clear canvas", KeyEvent.VK_C, "clear.gif", "onClear");

            colorMenu.put("Black", addRadioMenuItem("Colors/Black", "Black color", KeyEvent.VK_1, "BLACK.gif", "onBlack", colorMenuGroup));
            colorMenu.put("Red", addRadioMenuItem("Colors/Red", "Red color", KeyEvent.VK_2, "RED.gif", "onRed", colorMenuGroup));
            colorMenu.put("Green", addRadioMenuItem("Colors/Green", "Green color", KeyEvent.VK_3, "GREEN.gif", "onGreen", colorMenuGroup));
            colorMenu.put("Yellow", addRadioMenuItem("Colors/Yellow", "Yellow color", KeyEvent.VK_4, "YELLOW.gif", "onYellow", colorMenuGroup));
            colorMenu.put("Blue", addRadioMenuItem("Colors/Blue", "Blue color", KeyEvent.VK_5, "BLUE.gif", "onBlue", colorMenuGroup));
            colorMenu.put("Light blue", addRadioMenuItem("Colors/Light blue", "Light blue color", KeyEvent.VK_6, "LIGHT_BLUE.gif", "onLightBlue", colorMenuGroup));
            colorMenu.put("Purple", addRadioMenuItem("Colors/Purple", "Purple color", KeyEvent.VK_7, "PURPLE.gif", "onPurple", colorMenuGroup));
            colorMenu.put("White", addRadioMenuItem("Colors/White", "White color", KeyEvent.VK_8, "WHITE.gif", "onWhite", colorMenuGroup));
            colorMenu.put("Choose color", addRadioMenuItem("Colors/Choose color...", "Choose color", KeyEvent.VK_9, "colorChooser.gif", "onChooseColor", colorMenuGroup));


            addToolBarButton("File/Exit");
            addToolBarSeparator();
            addToolBarButton("Tools/Tools Settings");
            addToolBarSeparator();
            addToolBarButton("Tools/Clear");
            addToolBarSeparator();

            toolBarTools.put("Line", addToolBarToggleButton("Tools/Line", toolBarToolGroup));
            toolBarTools.put("N-Gon", addToolBarToggleButton("Tools/N-Gon", toolBarToolGroup));
            toolBarTools.put("Star", addToolBarToggleButton("Tools/Star", toolBarToolGroup));
            toolBarTools.put("Flood fill", addToolBarToggleButton("Tools/Flood fill", toolBarToolGroup));

            addToolBarSeparator();
            toolBarColors.put("Black", addToolBarToggleButton("Colors/Black", toolBarColorGroup));
            toolBarColors.put("Red", addToolBarToggleButton("Colors/Red", toolBarColorGroup));
            toolBarColors.put("Green", addToolBarToggleButton("Colors/Green", toolBarColorGroup));
            toolBarColors.put("Yellow", addToolBarToggleButton("Colors/Yellow", toolBarColorGroup));
            toolBarColors.put("Blue", addToolBarToggleButton("Colors/Blue", toolBarColorGroup));
            toolBarColors.put("Light blue", addToolBarToggleButton("Colors/Light blue", toolBarColorGroup));
            toolBarColors.put("Purple", addToolBarToggleButton("Colors/Purple", toolBarColorGroup));
            toolBarColors.put("White", addToolBarToggleButton("Colors/White", toolBarColorGroup));
            toolBarColors.put("Choose color", addToolBarToggleButton("Colors/Choose color...", toolBarColorGroup));
            addToolBarSeparator();
            addToolBarButton("Help/About...");
            addToolBarSeparator();

            mainPanel = new MainPanel(new Dimension(800, 600));
            scrollPane = new JScrollPane(mainPanel);
            add(scrollPane);

            pack();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Help/About... - shows program version and copyright information
     */
    public void onAbout() {
        StringBuilder message = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileInputStream("About.txt"), StandardCharsets.UTF_8);
            while (scanner.hasNextLine()) {
                message.append(scanner.nextLine()).append("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "About.txt not found");
        }
        if (!message.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, message.toString());
        } else {
            JOptionPane.showMessageDialog(this, "About.txt is empty");
        }
    }

    public void onSave() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.png", "png");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                if (!filter.accept(selectedFile)) {
                    mainPanel.saveImage(new File(selectedFile.getAbsolutePath().concat(".png")));
                } else {
                    mainPanel.saveImage(selectedFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onOpen() {
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("filter", "png", "jpeg",
                "bmp", "gif");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.png", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.jpeg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.bmp", "bmp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.gif", "gif"));
        fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (extensionFilter.accept(selectedFile)) {
                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    mainPanel.setImage(image);
                    mainPanel.repaint();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Can't open selected file");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect extension, try image of \"png\", \"jpeg\",\n" +
                        "\"bmp\" or \"gif\" type");
            }
        }
        scrollPane.revalidate();
    }

    public void onRed() {
        mainPanel.setCurColor(BasicColors.RED);
        colorMenuGroup.setSelected(colorMenu.get("Red").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Red").getModel(), true);
    }

    public void onGreen() {
        mainPanel.setCurColor(BasicColors.GREEN);
        colorMenuGroup.setSelected(colorMenu.get("Green").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Green").getModel(), true);
    }

    public void onBlue() {
        mainPanel.setCurColor(BasicColors.BLUE);
        colorMenuGroup.setSelected(colorMenu.get("Blue").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Blue").getModel(), true);
    }

    public void onYellow() {
        mainPanel.setCurColor(BasicColors.YELLOW);
        colorMenuGroup.setSelected(colorMenu.get("Yellow").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Yellow").getModel(), true);
    }

    public void onPurple() {
        mainPanel.setCurColor(BasicColors.PURPLE);
        colorMenuGroup.setSelected(colorMenu.get("Purple").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Purple").getModel(), true);
    }

    public void onLightBlue() {
        mainPanel.setCurColor(BasicColors.LIGHT_BLUE);
        colorMenuGroup.setSelected(colorMenu.get("Light blue").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Light blue").getModel(), true);
    }

    public void onWhite() {
        mainPanel.setCurColor(BasicColors.WHITE);
        colorMenuGroup.setSelected(colorMenu.get("White").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("White").getModel(), true);
    }

    public void onBlack() {
        mainPanel.setCurColor(BasicColors.BLACK);
        colorMenuGroup.setSelected(colorMenu.get("Black").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Black").getModel(), true);
    }

    public void onChooseColor() {
        JDialog dialog = new JDialog(this, true);
        dialog.add(new ColorChooserPanel(dialog, mainPanel));
        dialog.pack();
        dialog.setVisible(true);
        colorMenuGroup.setSelected(colorMenu.get("Choose color").getModel(), true);
        toolBarColorGroup.clearSelection();
        toolBarColorGroup.setSelected(toolBarColors.get("Choose color").getModel(), true);
    }


    public void onLine() {
        if (mainPanel.getActiveTool() != null && mainPanel.getActiveTool().getName().equals("Line")) {
            return;
        }
        mainPanel.setActiveTool(new LineTool(mainPanel.getImage()));
        toolsMenuGroup.setSelected(toolsMenu.get("Line").getModel(), true);
        toolBarToolGroup.clearSelection();
        toolBarToolGroup.setSelected(toolBarTools.get("Line").getModel(), true);
    }

    public void onNGon() {
        if (mainPanel.getActiveTool() == null || !mainPanel.getActiveTool().getName().equals("Stamp")) {
            mainPanel.setActiveTool(new StampTool(mainPanel.getImage(), StampTool.State.NGON));
        } else {
            StampTool stampTool = (StampTool) mainPanel.getActiveTool();
            stampTool.setState(StampTool.State.NGON);
        }
        toolsMenuGroup.setSelected(toolsMenu.get("N-Gon").getModel(), true);
        toolBarToolGroup.clearSelection();
        toolBarToolGroup.setSelected(toolBarTools.get("N-Gon").getModel(), true);
    }

    public void onStar() {
        if (mainPanel.getActiveTool() == null || !mainPanel.getActiveTool().getName().equals("Star")) {
            mainPanel.setActiveTool(new StampTool(mainPanel.getImage(), StampTool.State.STAR));
        } else {
            StampTool stampTool = (StampTool) mainPanel.getActiveTool();
            stampTool.setState(StampTool.State.STAR);
        }
        toolsMenuGroup.setSelected(toolsMenu.get("Star").getModel(), true);
        toolBarToolGroup.clearSelection();
        toolBarToolGroup.setSelected(toolBarTools.get("Star").getModel(), true);
    }

    public void onFloodFill() {
        if (mainPanel.getActiveTool() != null && mainPanel.getActiveTool().getName().equals("FloodFill")) {
            return;
        }
        mainPanel.setActiveTool(new FloodFillTool(mainPanel.getImage()));
        toolsMenuGroup.setSelected(toolsMenu.get("Flood fill").getModel(), true);
        toolBarToolGroup.clearSelection();
        toolBarToolGroup.setSelected(toolBarTools.get("Flood fill").getModel(), true);
    }

    public void onToolSettings() {
        if (mainPanel.getActiveTool() == null) {
            JOptionPane.showMessageDialog(this, "No active tool");
            return;
        }
        if (mainPanel.getActiveTool().getSettings() == null) {
            JOptionPane.showMessageDialog(this, "Tool doesn't have settings");
            return;
        }
        SettingDialog dialog = new SettingDialog(this, mainPanel.getActiveTool().getSettings(), "Settings", true);
        dialog.setLocationRelativeTo(this);
        dialog.pack();
        dialog.setVisible(true);
    }


    public void onClear() {
        mainPanel.clear();
    }

    public void onExit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (this.getWidth() < mainPanel.getImage().getWidth()
                && this.getHeight() < mainPanel.getImage().getHeight()) {
            return;
        }
        int newWidth = Math.max(mainPanel.getImage().getWidth(), getWidth());
        int newHeight = Math.max(mainPanel.getImage().getHeight(), getHeight());

        BufferedImage newSizeImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newSizeImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, newWidth, newHeight);
        g.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.drawImage(mainPanel.getImage(), 0, 0, null);
        mainPanel.setImage(newSizeImage);
        scrollPane.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
