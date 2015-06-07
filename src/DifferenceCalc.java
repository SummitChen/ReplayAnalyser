import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DifferenceCalc {
    int maxWidth, maxHeight;
    double percent;
    int[][] data;
    int[] differences;

    public DifferenceCalc(String file) {
        try {
            Scanner scan = new Scanner(new File(file));
            maxHeight = scan.nextInt();
            maxWidth = scan.nextInt();
            percent = scan.nextDouble();
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
                value += Math.abs(data[i][j] - other.data[i][j]);
            }
        }

        return value;
    }

    public void checkFirst(StringBuilder sb) {
        if (sb.length() != 0) {
            sb.append(", ");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Size ").append(maxWidth).append(", ").append(
                maxHeight).append("\n");
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                sb.append(data[i][j]).append(" ");
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        ArrayList<DifferenceCalc> diffCalcs = new ArrayList<DifferenceCalc>();
        String path = "ReplayAnalysis/";
        String outPath = "Differences/";
        String type = "64x64";

        for (int i = 2; i <= 28; i++ ) {
            String file = "/HES0";
            if (i < 10) {
                file += '0';
            }
            file += i + "E_pos.txt";
            DifferenceCalc dc = new DifferenceCalc(path + type + file);
            diffCalcs.add(dc);
        }

        for (DifferenceCalc dc : diffCalcs) {
            dc.differences = new int[diffCalcs.size()];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diffCalcs.size(); i++) {
            for (int j = 0; j < diffCalcs.size(); j++) {
                int diff = diffCalcs.get(i).difference(diffCalcs.get(j));
                diffCalcs.get(i).differences[j] = diff;
                sb.append(diff);
                if (j < diffCalcs.size() - 1) {
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
