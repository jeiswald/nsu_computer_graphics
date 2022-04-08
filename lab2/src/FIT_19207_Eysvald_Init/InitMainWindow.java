package FIT_19207_Eysvald_Init;

import ru.nsu.fit.ejsvald.filters.*;
import ru.nsu.fit.ejsvald.setting.FitInSetPanel;
import ru.nsu.fit.ejsvald.setting.SettingDialog;
import ru.nsu.fit.ejsvald.setting.SettingsPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InitMainWindow extends MainFrame implements ComponentListener {
    private final JScrollPane scrollPane;

    public InitMainWindow() {
        super(600, 400, "Image Filtering");
        addComponentListener(this);

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/Save", "Save image", KeyEvent.VK_X, "onSave");
            addMenuItem("File/Open", "Open image", KeyEvent.VK_X, "onOpen");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");

            addSubMenu("Filters", KeyEvent.VK_T);

            addSubMenu("Edit", KeyEvent.VK_T);

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Shows program version and copyright information",
                    KeyEvent.VK_A, "About.gif", "onAbout");

            addMenuItem("Filters/Black-White", "Black & White filter", KeyEvent.VK_L,
                    "black-white.gif", "onBlackWhite");
            addMenuItem("Filters/Negative", "Color inverting filter", KeyEvent.VK_L,
                    "negative.gif", "onNegative");
            addMenuItem("Filters/Gamma", "Gamma correction", KeyEvent.VK_L,
                    "gamma.gif", "onGamma");
            addMenuItem("Filters/Sharpness", "Sharpness increasing filter", KeyEvent.VK_L,
                    "sharpness.gif", "onSharpness");
            addMenuItem("Filters/Embossing", "Embossing filter", KeyEvent.VK_L,
                    "emboss.gif", "onEmbossing");
            addMenuItem("Filters/Blur", "Bluring filter", KeyEvent.VK_L,
                    "blur.gif", "onBlur");
            addMenuItem("Filters/Edge detection", "Edge detection filter", KeyEvent.VK_L,
                    "edge.gif", "onEdgeDetection");
            addMenuItem("Filters/Dither", "Dither", KeyEvent.VK_L,
                    "dither.png", "onDither");
            addMenuItem("Filters/Aquarelle", "Aquarelle", KeyEvent.VK_L,
                    "aquarelle.png", "onAquarelle");
            addMenuItem("Filters/Pencil", "Make image look like it was draw with pencil", KeyEvent.VK_P,
                    "pencil.png", "onPencil");


            addMenuItem("Edit/Clear", "Set original image", KeyEvent.VK_L,
                    "clear.gif", "onClear");
            addMenuItem("Edit/Rotate", "Rotate image", KeyEvent.VK_L,
                    "rotation.png", "onRotate");
            addMenuItem("Edit/Change display mode", "Change between fir in screen and real size modes", KeyEvent.VK_L,
                    "changeMode.gif", "onChangeMode");
            addMenuItem("Edit/Display mode settings", "Display mode settings", KeyEvent.VK_L,
                    "onChangeModeSettings");

            addToolBarButton("File/Exit");
            addToolBarButton("Edit/Clear");
            addToolBarButton("Edit/Change display mode");
            addToolBarButton("Edit/Rotate");
            addToolBarSeparator();

            addToolBarButton("Filters/Black-White");
            addToolBarButton("Filters/Negative");
            addToolBarButton("Filters/Gamma");
            addToolBarButton("Filters/Sharpness");
            addToolBarButton("Filters/Embossing");
            addToolBarButton("Filters/Blur");
            addToolBarButton("Filters/Edge detection");
            addToolBarButton("Filters/Dither");
            addToolBarButton("Filters/Aquarelle");
            addToolBarButton("Filters/Pencil");

            addToolBarSeparator();
            addToolBarButton("Help/About...");
            addToolBarSeparator();

            mainPanel = new MainPanel();
            scrollPane = new JScrollPane(mainPanel);
            mainPanel.setScrollPane(scrollPane);
            add(scrollPane);
            MouseAdapter mouseAdapter = createMouseAdapter();
            mainPanel.addMouseListener(mouseAdapter);
            mainPanel.addMouseMotionListener(mouseAdapter);
            scrollPane.revalidate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MouseAdapter createMouseAdapter() {
        return new MouseAdapter() {

            private Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                origin = new Point(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, mainPanel);
                    if (viewPort != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();

                        Rectangle rect = viewPort.getViewRect();
                        rect.x += deltaX;
                        rect.y += deltaY;
                        mainPanel.scrollRectToVisible(rect);
                    }
                }
            }
        };
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
            return;
        }
        if (!message.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, message.toString());
        } else {
            JOptionPane.showMessageDialog(this, "About.txt is empty");
        }
    }

    public void onChangeModeSettings() {
        ImageScaler scaler = mainPanel.getImageScaler();
        if (scaler == null) {
            mainPanel.setImageScaler(new ImageScaler());
            scaler = mainPanel.getImageScaler();
        }
        FitInSetPanel settingsPanel = (FitInSetPanel) scaler.getSettingsPanel();
        SettingDialog settingsDialog = new SettingDialog(this, settingsPanel,
                "Image scalier", true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
        if (settingsDialog.isOk()) {
            try {
                settingsPanel.applySettings();
                mainPanel.fitImage();
                mainPanel.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onChangeMode() {
        if (mainPanel.getOriginalImage() == null) return;
        mainPanel.changeView();
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
                "jpg", "bmp", "gif");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.png", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.jpeg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.jpg", "jpg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.bmp", "bmp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.gif", "gif"));
        fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (extensionFilter.accept(selectedFile)) {
                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    mainPanel.setOriginalImage(image);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Can't open selected file");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect extension, try image of \"png\", \"jpeg\",\n" +
                        "\"jpeg\", \"bmp\" or \"gif\" type");
            }
        }
        scrollPane.revalidate();
        mainPanel.repaint();
    }

    public void onRotate() {
        if (mainPanel.getOriginalImage() == null) return;
        RotationTool rotationTool = mainPanel.getRotationTool();
        if (rotationTool == null) {
            mainPanel.setRotationTool(new RotationTool());
            rotationTool = mainPanel.getRotationTool();
        }
        SettingsPanel settingsPanel = rotationTool.getSettingsPanel();
        SettingDialog settingDialog = new SettingDialog(this, settingsPanel, "Rotation tool", true);
        settingDialog.setLocationRelativeTo(this);
        settingDialog.pack();
        settingDialog.setVisible(true);
        if (settingDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(rotationTool.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onBlackWhite() {
        if (mainPanel.getOriginalImage() == null) return;
        BlackWhiteFilter blackWhiteFilter = new BlackWhiteFilter();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainPanel.setFilteredImage(blackWhiteFilter.apply(mainPanel.getOriginalImage()));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainPanel.repaint();
    }

    public void onNegative() {
        if (mainPanel.getOriginalImage() == null) return;
        NegativeFilter negativeFilter = new NegativeFilter();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainPanel.setFilteredImage(negativeFilter.apply(mainPanel.getOriginalImage()));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainPanel.repaint();
    }

    public void onSharpness() {
        if (mainPanel.getOriginalImage() == null) return;
        SharpnessFilter sharpnessFilter = new SharpnessFilter();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainPanel.setFilteredImage(sharpnessFilter.apply(mainPanel.getOriginalImage()));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainPanel.repaint();
    }

    public void onEmbossing() {
        if (mainPanel.getOriginalImage() == null) return;
        EmbossingFilter embossingFilter = new EmbossingFilter();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainPanel.setFilteredImage(embossingFilter.apply(mainPanel.getOriginalImage()));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainPanel.repaint();
    }

    public void onBlur() {
        if (mainPanel.getOriginalImage() == null) return;
        BlurFilter blurFilter = mainPanel.getBlurFilter();
        if (blurFilter == null) {
            mainPanel.setBlurFilter(new BlurFilter());
            blurFilter = mainPanel.getBlurFilter();
        }
        SettingsPanel settingsPanel = blurFilter.getSettingsPanel();
        SettingDialog settingDialog = new SettingDialog(this, settingsPanel, "Blur filter", true);
        settingDialog.setLocationRelativeTo(this);
        settingDialog.pack();
        settingDialog.setVisible(true);
        if (settingDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(blurFilter.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onEdgeDetection() {
        if (mainPanel.getOriginalImage() == null) return;
        EdgeDetectionFilter edgeDetectionFilter = mainPanel.getEdgeDetectionFilter();
        if (edgeDetectionFilter == null) {
            mainPanel.setEdgeDetectionFilter(new EdgeDetectionFilter());
            edgeDetectionFilter = mainPanel.getEdgeDetectionFilter();
        }
        SettingsPanel settingsPanel = edgeDetectionFilter.getSettingsPanel();
        SettingDialog settingsDialog = new SettingDialog(this, settingsPanel,
                "Edge detecting filter", true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
        if (settingsDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(edgeDetectionFilter.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onPencil() {
        PencilTool pencilTool = new PencilTool();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainPanel.setFilteredImage(pencilTool.apply(mainPanel.getOriginalImage()));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainPanel.repaint();
    }

    public void onDither() {
        if (mainPanel.getOriginalImage() == null) return;
        Dither dither = mainPanel.getDither();
        if (dither == null) {
            mainPanel.setDither(new Dither());
            dither = mainPanel.getDither();
        }
        SettingsPanel settingsPanel = dither.getSettingsPanel();
        SettingDialog settingsDialog = new SettingDialog(this, settingsPanel,
                "Dither", true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
        if (settingsDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(dither.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onGamma() {
        if (mainPanel.getOriginalImage() == null) return;
        GammaCorrection gammaCorrection = mainPanel.getGammaCorrection();
        if (gammaCorrection == null) {
            mainPanel.setGammaCorrection(new GammaCorrection());
            gammaCorrection = mainPanel.getGammaCorrection();
        }
        SettingsPanel settingsPanel = gammaCorrection.getSettingsPanel();
        SettingDialog settingsDialog = new SettingDialog(this, settingsPanel,
                "Gamma correction filter", true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
        if (settingsDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(gammaCorrection.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onAquarelle() {
        if (mainPanel.getOriginalImage() == null) return;
        AquarelleFilter aquarelleFilter = mainPanel.getAquarelleFilter();
        if (aquarelleFilter == null) {
            mainPanel.setAquarelleFilter(new AquarelleFilter());
            aquarelleFilter = mainPanel.getAquarelleFilter();
        }
        SettingsPanel settingsPanel = aquarelleFilter.getSettingsPanel();
        SettingDialog settingsDialog = new SettingDialog(this, settingsPanel,
                "Aquarelle filter", true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
        if (settingsDialog.isOk()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mainPanel.setFilteredImage(aquarelleFilter.apply(mainPanel.getOriginalImage()));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainPanel.repaint();
        }
    }

    public void onClear() {
        if (mainPanel.getOriginalImage() == null) return;
        mainPanel.setFilteredImage(mainPanel.getOriginalImage());
        mainPanel.repaint();
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
        scrollPane.revalidate();
        if (mainPanel.isFitInScreen()) {
            mainPanel.fitImage();
        }
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
