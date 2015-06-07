import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;

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

    // Scale the map and drawings if we want to look at larger areas than a single
    // TilePosition.  This is a factor of the map width / height - this assumes they are
    // both the same (not always true?).  For example for the exploration map of 64x64 a
    // scale factor of 32 will divide the map into a 2x2 square, a factor of 16 will be a
    // 4x4 square and so on.
    int scale = 1;

    // Output a heat map
    boolean heatMap = false;

    // Output the explored (visible) area not just position of the unit
    boolean exploredArea = true;

    // Counter for how far to draw when using GUI
    int currentIndex = 0;

    // Holds the array of what squares have been explored.
    int[][] explored;

    // Holds an image of what the sight range of an SCV looks like.
    int diameter = 15;
    int radius = diameter / 2;
    int[][] sightRange = new int[diameter][diameter];

    public DisplayPanel(ReplayAnalyser ra) {
        this.ra = ra;

        explored = new int[ra.mapWidth][ra.mapHeight];

        setPreferredSize(new Dimension(width, height));

        calculateSightRange();
    }

    public void calculateSightRange() {
        int square = 20;
        int size = diameter * square;

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        // We draw a series of rectangles instead of just drawing a circl and looking at its
        // pixels as the pixel version doesn't seem to abstract correctly.
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                g.drawRect(i * square, j * square, square, square);
            }
        }

        Color c = Color.BLUE;
        g.setColor(c);
        g.fillOval(0, 0, size, size);

        for (int x = 0; x < diameter; x++) {
            for (int y = 0; y < diameter; y++) {
                boolean found = false;

                // If any part of the subsquare is coloured we consider the circle to have
                // covered it.
                for (int i = 0; i < square; i++) {
                    for (int j = 0; j < square; j++) {
                        if (bi.getRGB(x * square + i, y * square + j) == c.getRGB()) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }

                if (found) {
                    sightRange[x][y] = 1;
                }
            }
        }
    }

    public Position calculatePoint(Position p) {
        int x = (int) Math.floor(p.x / (float) scale);
        int y = (int) Math.floor(p.y / (float) scale);

        return new Position(x, y);
    }

    public Color calcColour(int location) {
        if (location == 0) {
            return Color.BLACK;
        } else if (heatMap) {
            return new Color(((255 * location) / 600) << 16);
        } else {
            return Color.BLUE;
        }
    }

    public void setExplored(Position p) {
        explored[p.x][p.y] += 1;
    }

    public void calculateCoveredArea(Position p) {
        setExplored(calculatePoint(p));

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                Position newp = calculatePoint(new Position(p.x + x, p.y + y));

                if (newp.x < 0 || newp.x > (ra.mapWidth / scale) - 1 ||
                        newp.y < 0 || newp.y > (ra.mapWidth / scale) - 1) {
                    continue;
                }

                // Check this coordinate is in our sight range array.
                if (sightRange[x + radius][y + radius] == 0) {
                    continue;
                }

                setExplored(newp);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < ra.mapWidth / scale; i++) {
            for (int j = 0; j < ra.mapHeight / scale; j++) {
                explored[i][j] = 0;
            }
        }

        g.drawRect(mapLeft, mapTop, ra.mapWidth * mult, ra.mapHeight * mult);

        for (int i = 0; i < currentIndex; i++) {
            if (exploredArea) {
                calculateCoveredArea(ra.positions.get(i));
            } else {
                setExplored(calculatePoint(ra.positions.get(i)));
            }
        }

        for (int i = 0; i < ra.mapWidth / scale; i++) {
            for (int j = 0; j < ra.mapHeight / scale; j++) {
                g.setColor(calcColour(explored[i][j]));

                g.fillRect(mapLeft + (i * scale * mult), mapTop + (j * scale * mult), scale
                        * mult, scale * mult);
            }
        }
    }

    public void writeInfo(String file) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        currentIndex = ra.positions.size() - 1;
        paintComponent(image.getGraphics());

        try {
            ImageIO.write(image, "png", new File(file + ".png"));

            StringBuilder sb = new StringBuilder();
            sb.append(ra.mapWidth / scale);
            sb.append(" ");
            sb.append(ra.mapHeight / scale);
            sb.append(" ");

            int total = (ra.mapWidth / scale) * (ra.mapHeight / scale);
            int counter = 0;

            StringBuilder dat = new StringBuilder();
            for (int i = 0; i < ra.mapWidth / scale; i++) {
                for (int j = 0; j < ra.mapHeight / scale; j++) {
                    if (explored[i][j] > 0) {
                        counter++;
                    }
                    if (heatMap) {
                        dat.append(explored[i][j]);
                    } else {
                        if (explored[i][j] == 0) {
                            dat.append("0");
                        } else {
                            dat.append("1");
                        }
                    }
                    dat.append(" ");
                }
            }

            sb.append(String.format("%.2f ", counter / (double)total));

            BufferedWriter bw = new BufferedWriter(new FileWriter(file + ".txt"));
            bw.write(sb.toString());
            bw.write(dat.toString());
            bw.flush();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeInfoFiles(String file) {
        heatMap = false;
        exploredArea = false;
        writeInfo(file + "_pos");
        exploredArea = true;
        writeInfo(file + "_xpl");

        heatMap = true;
        exploredArea = false;
        writeInfo(file + "_pos_heat");
        exploredArea = true;
        writeInfo(file + "_xpl_heat");
    }

    public void writeMovementFile(String file) {
        currentIndex = ra.positions.size() - 1;

        Position last = null;
        StringBuilder move = new StringBuilder();

        for (int i = 0; i < currentIndex; i++) {
            Position p = calculatePoint(ra.positions.get(i));

            if (last == null) {
                last = p;
                continue;
            } else if (last.x == p.x && last.y == p.y) {
                continue;
            }

            if (last.x == p.x) {
                if (last.y < p.y) {
                    move.append("D ");
                } else {
                    move.append("U ");
                }
            } else if (last.y == p.y) {
                if (last.x < p.x) {
                    move.append("R ");
                } else {
                    move.append("L ");
                }
            } else if (last.x < p.x && last.y < p.y) {
                move.append("DR ");
            } else if (last.x < p.x && last.y > p.y) {
                move.append("UR ");
            } else if (last.x > p.x && last.y < p.y) {
                move.append("DL ");
            } else if (last.x > p.x && last.y > p.y) {
                move.append("UL ");
            }
            last = p;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file + "_mov.txt"));
            bw.write(move.toString());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeData(String outPath, String file) {
        Map<Integer, String> scales = new TreeMap<Integer, String>();
        {
            scales.put(1, "64x64/");
            scales.put(2, "32x32/");
            scales.put(4, "16x16/");
            scales.put(8, "8x8/");
            scales.put(16, "4x4/");
            scales.put(32, "2x2/");
        }

        for (Map.Entry<Integer, String> pair : scales.entrySet()) {
            scale = pair.getKey();

            String scaleFile = outPath + pair.getValue() + file;

            writeInfoFiles(scaleFile);
            writeMovementFile(scaleFile);
        }
    }

    public void updatePositions() {
        currentIndex = 0;
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
