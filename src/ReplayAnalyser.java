import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by megan on 20/05/2015.
 */
public class ReplayAnalyser {
    String file;
    int mapHeight, mapWidth;

    // Using a simple ArrayList since we record every frame and frames
    // increment by one each time.
    List<Position> positions = new ArrayList<Position>();

    public ReplayAnalyser(String file) {
        this.file = file;
        try {
            Scanner scan = new Scanner(new File(file));
            scan.next("Map");
            mapHeight = scan.nextInt();
            mapWidth = scan.nextInt();

            int currentFrame = 0;
            while (scan.hasNext()) {
                scan.next("Frame:");
                int frame = scan.nextInt();
                if (frame != currentFrame) {
                    throw new Exception("Missing a frame, expected " +
                            currentFrame + " found " + frame);
                }
                currentFrame++;

                scan.next("Position:");
                int x = scan.nextInt();
                int y = scan.nextInt();
                positions.add(new Position(x, y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Map height " + mapHeight + ", width " + mapWidth + "\n");

        int frame = 1;
        for (Position p : positions) {
            sb.append("Frame " + frame++ + ", position x " + p.x + ", y " +
                    p.y + "\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        ReplayAnalyser ra = new ReplayAnalyser("HES002E.txt");

        DisplayPanel panel = new DisplayPanel(ra);

        //SwingUtilities.invokeLater(new DrawGUI(panel));

        panel.updatePositions();
    }
}
