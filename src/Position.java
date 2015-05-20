/**
 * Created by megan on 20/05/2015.
 */
public class Position {
    int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Position x " + x + ", y " + y;
    }
}
