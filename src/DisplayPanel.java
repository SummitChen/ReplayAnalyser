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

    // Scale the map and drawings if we want to look at larger areas than a
    // single TilePosition.  This is a factor of the map width / height - this
    // assumes they are both the same (not always true?).  For example for the
    // exploration map of 64x64 a scale factor of 32 will divide the map into
    // a 2x2 square, a factor of 16 will be a 4x4 square and so on.
    int scale = 16;

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
            int x = (int)Math.floor(p.x / (float)scale);
            int y = (int)Math.floor(p.y / (float)scale);
            g.setColor(new Color(0x0000ff));
            g.fillRect(mapLeft + (x * scale * mult), mapTop + (y * scale * mult),
                    scale * mult, scale * mult);
        }
    }

    public void updatePositions() {
        // Dump the entire movement info to a image file first.
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        currentIndex = ra.positions.size() - 1;
        paintComponent(image.getGraphics());
        currentIndex = 0;
        try {
            String png_file = ra.file.substring(0, ra.file.lastIndexOf('.')) +
                    ".png";
            ImageIO.write(image, "png", new File(png_file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
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
