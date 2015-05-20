import javax.swing.*;
import java.awt.*;

/**
 * Created by megan on 20/05/2015.
 */
public class DisplayPanel extends JPanel {
    ReplayAnalyser ra;

    // Push the map from the edge
    int mapLeft = 10;
    int mapTop = 10;

    int mult = 4; // How many pixels per TilePosition
    int frameSkip = 1; // How many frames to advance each time

    int currentIndex = 0;

    public DisplayPanel(ReplayAnalyser ra) {
        this.ra = ra;

        setPreferredSize(new Dimension(280, 280));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawRect(mapLeft, mapTop, ra.mapWidth * mult, ra.mapHeight * mult);

        Position p = ra.positions.get(currentIndex);
        g.setColor(new Color(0x0000ff));
        g.fillRect(mapLeft + (p.x * mult), mapTop + (p.y * mult), mult, mult);
    }

    public void updatePositions() {
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
