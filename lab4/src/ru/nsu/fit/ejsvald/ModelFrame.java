package ru.nsu.fit.ejsvald;

import ru.nsu.fit.ejsvald.editor.EditorFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ModelFrame extends MainFrame implements WindowListener {
    private ModelPanel modelPanel;
    private EditorFrame editor;

    public ModelFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
        GridBagConstraints c = new GridBagConstraints();
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/Save", "Save", KeyEvent.VK_X, "save.png", "onSave");
            addMenuItem("File/Open", "Open", KeyEvent.VK_X, "onOpen");
            addSubMenu("View", KeyEvent.VK_V);
            addMenuItem("View/Fit in screen", "Fit in screen", KeyEvent.VK_F, "extend.png",
                    "onFit");
            addMenuItem("View/Editor", "Open spline editor", KeyEvent.VK_E, "spline.png",
                    "onEditor");
            addMenuItem("View/Reset angles", "Set default angles", KeyEvent.VK_E, "resetAngles.png",
                    "onResetAngles");
            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Shows program version and copyright information",
                    KeyEvent.VK_A, "about.png", "onAbout");

            addToolBarButton("File/Save");
            addToolBarButton("View/Fit in screen");
            addToolBarButton("View/Editor");
            addToolBarButton("View/Reset angles");
            addToolBarButton("Help/About...");

            addWindowListener(this);
            setMinimumSize(new Dimension(500, 400));
            modelPanel = new ModelPanel();

            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            add(modelPanel, c);
            pack();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ModelFrame modelFrame = new ModelFrame();
        modelFrame.setVisible(true);
    }


    @Override
    public void windowClosing(WindowEvent e) {
    }

    public void onAbout() {
        StringBuilder message = new StringBuilder();
        try (Scanner scanner = new Scanner(new FileInputStream("About.txt"), StandardCharsets.UTF_8);) {
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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.spm", "spm");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                StringBuilder sb = new StringBuilder();
                //file format: Zf, xAngle, yAngle, zAngle, lines,
                // lineApprox, circleApplox, points...
                sb.append(modelPanel.getZf()).append('\n')
                        .append(modelPanel.getxAngle()).append('\n').append(modelPanel.getyAngle()).append('\n')
                        .append(modelPanel.getzAngle()).append('\n').append(modelPanel.getmLines()).append('\n')
                        .append(modelPanel.getLineApproxNum()).append('\n')
                        .append(modelPanel.getCircleApproxNum()).append('\n');
                for (Coordinates point : modelPanel.getPoints()) {
                    sb.append(point.toString());
                }
                File file;
                if (!filter.accept(selectedFile)) {
                    file = new File(selectedFile.getAbsolutePath().concat(".spm"));
                } else {
                    file = selectedFile;
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(sb.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onOpen() {
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("filter", "spm");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.spm", "spm"));
        fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (extensionFilter.accept(selectedFile)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile));) {
                    //file format: Zf, xAngle, yAngle, zAngle, lines,
                    // lineApprox, circleApplox, points...
                    int Zf = Integer.parseInt(reader.readLine());
                    int xAngle = Integer.parseInt(reader.readLine());
                    int yAngle = Integer.parseInt(reader.readLine());
                    int zAngle = Integer.parseInt(reader.readLine());
                    int lines = Integer.parseInt(reader.readLine());
                    int lineApprox = Integer.parseInt(reader.readLine());
                    int circleApprox = Integer.parseInt(reader.readLine());
                    ArrayList<Coordinates> points = new ArrayList<>();
                    while (true) {
                        String fileString = reader.readLine();
                        if (fileString == null) break;
                        int[] point = Arrays.stream(fileString.split(",")).mapToInt(Integer::parseInt).toArray();
                        points.add(new Coordinates(point));
                    }
                    reader.close();
                    ModelPanel newModelPanel = new ModelPanel(points, Zf, xAngle, yAngle, zAngle, lines, lineApprox, circleApprox);
                    newModelPanel.setPanelSize(modelPanel.getSize());
                    setModelPanel(newModelPanel);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Can't open selected file");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect extension, try file of \"spm\" type");
            }
        }
    }

    public void onFit() {
        modelPanel.fitInScreen();
        repaint();
    }

    public void onEditor() {
        editor = new EditorFrame(this, true);
        editor.setVisible(true);
    }

    public void onResetAngles() {
        modelPanel.resetAngles();
        modelPanel.drawModel();
        modelPanel.repaint();
    }

    public void setModelPanel(ModelPanel modelPanel) {
        remove(this.modelPanel);
        this.modelPanel = modelPanel;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(modelPanel, c);
        pack();
        modelPanel.drawModel();
        modelPanel.repaint();
    }

    public ModelPanel getModelPanel() {
        return modelPanel;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
