/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package madmouse;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;


public class App {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("MadMouse");
        mainFrame.setSize(100, 100);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                we.getWindow().dispose();
            }
            }
        );
    }
}