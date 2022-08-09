package FIT_19207_Eysvald_Init;

import ru.nsu.fit.ejsvald.filters.*;
import ru.nsu.fit.ejsvald.filters.pencil.PencilTool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MainPanel extends JPanel implements MouseListener {
    private final int imageShift = 4;
    private final HashMap<String, Filter> filters;
    private BufferedImage originalImage;
    private BufferedImage originalFitInImage;
    private BufferedImage filteredImage;
    private BufferedImage filteredFitInImage;
    private BufferedImage activeImage;
    private JScrollPane scrollPane = null;
    private boolean isFitInScreen = false;

    public MainPanel() {
        addMouseListener(this);
        filters = new HashMap<>();
        filters.put(ImageScaler.NAME, new ImageScaler());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        float[] dash1 = {2f, 0f, 2f};
        BasicStroke bs1 = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                1.0f,
                dash1,
                2f);
        g2d.setStroke(bs1);
        if (activeImage != null) {
            g2d.drawImage(activeImage, imageShift + 1, imageShift + 1, null);
            g2d.drawRect(imageShift, imageShift, activeImage.getWidth() + 1, activeImage.getHeight() + 1);
        } else {
            g2d.drawRect(imageShift, imageShift, getWidth() - 2 * imageShift, getHeight() - 2 * imageShift);
        }
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void saveImage(File path) throws IOException {
        ImageIO.write(filteredImage, "png", path);
    }

    public void setFilteredImage(BufferedImage image) {
        if (activeImage == filteredImage) {
            this.filteredImage = image;
        } else if (activeImage == filteredFitInImage){
            this.filteredImage = image;
            ImageScaler scaler = getImageScaler();
            filteredFitInImage = scaler.apply(filteredImage, originalFitInImage.getWidth(),
                    originalFitInImage.getHeight());
        }
        setActiveImage(filteredImage);
        setPreferredSize(new Dimension(activeImage.getWidth(), activeImage.getHeight()));
        scrollPane.revalidate();
    }

    public void setActiveImage(BufferedImage imageToSet) {
        if (imageToSet == filteredImage) {
            if (isFitInScreen) {
                activeImage = filteredFitInImage;
            } else {
                activeImage = filteredImage;
            }
        } else if (imageToSet == originalImage){
            if (isFitInScreen) {
                activeImage = originalFitInImage;
            } else {
                activeImage = originalFitInImage;
            }
        }
        setPreferredSize(new Dimension(activeImage.getWidth(), activeImage.getHeight()));
        scrollPane.revalidate();
        repaint();
    }

    public void changeView() {
        if (isFitInScreen) {
            setFitInScreen(false);
            setActiveImage(getFilteredImage());
        } else {
            setFitInScreen(true);
            fitImage();
        }
    }

    public void fitImage() {
        ImageScaler scaler = getImageScaler();
        int imageShift = getImageShift() + 5;
        setOriginalFitInImage(scaler.apply(getOriginalImage(),
                scrollPane.getWidth() - imageShift,
                scrollPane.getHeight() - imageShift));
        setFilteredFitInImage(scaler.apply(getFilteredImage(),
                scrollPane.getWidth() - imageShift,
                scrollPane.getHeight() - imageShift));
        setActiveImage(filteredImage);
        scrollPane.revalidate();
        repaint();
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) return;
        if (activeImage == filteredImage) {
            activeImage = originalImage;
        } else if (activeImage == filteredFitInImage) {
            activeImage = originalFitInImage;
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) return;
        if (activeImage == originalImage) {
            activeImage = filteredImage;
        } else if (activeImage == originalFitInImage){
            activeImage = filteredFitInImage;
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage image) {
        this.originalImage = image;
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imageCopy.createGraphics();
        g.drawImage(image, null, 0, 0);
        this.filteredImage = imageCopy;
        setActiveImage(filteredImage);
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        fitImage();
        repaint();
    }

    public PencilTool getPencilTool() {
        return (PencilTool) filters.get(PencilTool.NAME);
    }

    public void setPencilTool(PencilTool pencilTool) {
        filters.put(PencilTool.NAME, pencilTool);
    }

    public EdgeDetectionFilter getEdgeDetectionFilter() {
        return (EdgeDetectionFilter) filters.get(EdgeDetectionFilter.NAME);
    }

    public void setEdgeDetectionFilter(EdgeDetectionFilter edgeDetectionFilter) {
        filters.put(EdgeDetectionFilter.NAME, edgeDetectionFilter);
    }

    public BlurFilter getBlurFilter() {
        return (BlurFilter) filters.get(BlurFilter.NAME);
    }

    public void setBlurFilter(BlurFilter blurFilter) {
        filters.put(BlurFilter.NAME, blurFilter);
    }

    public GammaCorrection getGammaCorrection() {
        return (GammaCorrection) filters.get(GammaCorrection.NAME);
    }

    public void setGammaCorrection(GammaCorrection gammaCorrection) {
        filters.put(GammaCorrection.NAME, gammaCorrection);
    }

    public AquarelleFilter getAquarelleFilter() {
        return (AquarelleFilter) filters.get(AquarelleFilter.NAME);
    }

    public void setAquarelleFilter(AquarelleFilter aquarelleFilter) {
        filters.put(AquarelleFilter.NAME, aquarelleFilter);
    }

    public ImageScaler getImageScaler() {
        return (ImageScaler) filters.get(ImageScaler.NAME);
    }

    public void setImageScaler(ImageScaler scaler) {
        filters.put(ImageScaler.NAME, scaler);
    }

    public Dither getDither() {
        return (Dither) filters.get(Dither.NAME);
    }

    public void setDither(Dither dither) {
        filters.put(Dither.NAME, dither);
    }

    public RotationTool getRotationTool() {
        return (RotationTool) filters.get(RotationTool.NAME);
    }

    public void setRotationTool(RotationTool rotationTool) {
        filters.put(RotationTool.NAME, rotationTool);
    }

    public boolean isFitInScreen() {
        return isFitInScreen;
    }

    public void setFitInScreen(boolean fitInScreen) {
        isFitInScreen = fitInScreen;
    }


    public BufferedImage getOriginalFitInImage() {
        return originalFitInImage;
    }

    public void setOriginalFitInImage(BufferedImage originalFitInImage) {
        this.originalFitInImage = originalFitInImage;
    }

    public BufferedImage getFilteredFitInImage() {
        return filteredFitInImage;
    }

    public void setFilteredFitInImage(BufferedImage filteredFitInImage) {
        this.filteredFitInImage = filteredFitInImage;
    }


    public int getImageShift() {
        return imageShift;
    }
}
