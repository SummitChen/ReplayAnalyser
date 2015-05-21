import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by megan on 20/05/2015.
 */
public class DisplayPanel extends JPanel {
    ReplayAnalyser ra;

    // Preferred size for the panel
    int width = 280;
    int height = 280;

    // Push the map from the edge
    int mapLeft = 10;
    int mapTop = 10;

    int mult = 4; // How many pixels per TilePosition
    int frameSkip = 1; // How many frames to advance each time

    int currentIndex = 0;

    public DisplayPanel(ReplayAnalyser ra) {
        this.ra = ra;

        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawRect(mapLeft, mapTop, ra.mapWidth * mult, ra.mapHeight * mult);

        for (int i = 0; i < currentIndex; i++) {
            Position p = ra.positions.get(i);
            g.setColor(new Color(0x0000ff));
            g.fillRect(mapLeft + (p.x * mult), mapTop + (p.y * mult), mult, mult);
        }
    }

    public void updatePositions() {
        // Dump the entire movement info to a image file first.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        currentIndex = ra.positions.size() - 1;
        paintComponent(image.getGraphics());
        currentIndex = 0;
        try {
            String png_file = ra.file.substring(0, ra.file.lastIndexOf('.')) + ".png";
            ImageIO.write(image, "png", new File(png_file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.println(currentIndex);
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentIndex += frameSkip;
            if (currentIndex == ra.positions.size()) {
                currentIndex = 0;
            }

            repaint();
        }
    }
}
