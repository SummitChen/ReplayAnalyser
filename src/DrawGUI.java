import javax.swing.*;

public class DrawGUI implements Runnable {
    DisplayPanel panel;

    public DrawGUI(DisplayPanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
