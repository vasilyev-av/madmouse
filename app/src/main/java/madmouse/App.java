/*
 * 
 */
package madmouse;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Robot;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;




public class App {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("MadMouse");
        mainFrame.setSize(100, 100);
        Button mainButton = new Button("Start");
        mainButton.addActionListener(new ButtonListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Button curButton = (Button) e.getSource();
                switch (e.getActionCommand()){
                    case "Start":
                        curButton.setLabel("Stop");
                        madTimer = new Timer();
                        madTimer.schedule(new MadMouseMove(), 500, 1000);
                        break;
                    case "Stop":
                        curButton.setLabel("Start");
                        madTimer.cancel();
                        madTimer.purge();
                        break;
                }
            }
            
        });
        mainFrame.add(mainButton);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                we.getWindow().dispose();
                System.exit(0);
            }
            }
        );
    }

}

abstract class ButtonListener implements ActionListener{
    Timer madTimer;
}


class MadMouseMove extends TimerTask{
    Robot madMouse;

    MadMouseMove() {
        try {
            madMouse = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
            Point curPoint = MouseInfo.getPointerInfo().getLocation();
            madMouse.mouseMove(curPoint.x+ThreadLocalRandom.current().nextInt(-5, 6), curPoint.y+ThreadLocalRandom.current().nextInt(-5, 6));
    }
}
