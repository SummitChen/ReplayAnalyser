import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by megan on 25/05/2015.
 */
public class DifferenceCalc {
    int maxWidth, maxHeight;
    int[][] data;

    public DifferenceCalc(String file) {
        try {
            Scanner scan = new Scanner(new File(file));
            maxHeight = scan.nextInt();
            maxWidth = scan.nextInt();
            data = new int[maxWidth][maxHeight];

            for (int i = 0; i < maxHeight; i++) {
                for (int j = 0; j < maxWidth; j++) {
                    data[i][j] = scan.nextInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int difference(DifferenceCalc other) {
        int value = 0;

        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                if (data[i][j] != other.data[i][j]) {
                    value++;
                }
            }
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Size " + maxWidth + ", " + maxHeight + "\n");
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                sb.append(data[i][j] + " ");
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        ArrayList<DifferenceCalc> diffcalcs = new ArrayList<DifferenceCalc>();
        String path = "ReplayAnalysis/";
        String outPath = "Differences/";
        String type = "64x64";

        for (int i = 2; i <= 28; i++ ) {
            String file = "/HES0";
            if (i < 10) {
                file += '0';
            }
            file += i + "E.dat";
            DifferenceCalc dc = new DifferenceCalc(path + type + file);
            diffcalcs.add(dc);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diffcalcs.size(); i++) {
            for (int j = 0; j < diffcalcs.size(); j++) {
                sb.append(diffcalcs.get(i).difference(diffcalcs.get(j)));
                if (j < diffcalcs.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outPath + type + ".csv"));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
