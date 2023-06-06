/*
 * 
 */
package madmouse;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Robot;
import java.awt.TextField;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;


public class App {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("MadMouse");
        mainFrame.setSize(100, 100);
        mainFrame.setLayout(new GridLayout(2, 1));
        
        ButtonListener buttonListener = new ButtonListener();
        Button mainButton = new Button("Start");
        mainButton.addActionListener(buttonListener);

        TextField mainText = new TextField("1000");
        mainText.addTextListener(new TextListener(){
            @Override
            public void textValueChanged(TextEvent e) {
                
                buttonListener.setPeriod(Integer.parseInt(mainText.getText()));
            }
        });

        mainFrame.add(mainText);
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

class ButtonListener implements ActionListener{
    Timer madTimer;

    private int period = 1000;
    public void setPeriod(int jumpTime) {
        period = jumpTime;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Button curButton = (Button) e.getSource();
        switch (e.getActionCommand()){
            case "Start":
                curButton.setLabel("Stop");
                madTimer = new Timer();
                madTimer.schedule(new MadMouseMove(), period, period);
                break;
            case "Stop":
                curButton.setLabel("Start");
                madTimer.cancel();
                madTimer.purge();
                break;
        }
    }
}

class MadMouseMove extends TimerTask{
    Robot madMouse;

    private Point prevPoint = MouseInfo.getPointerInfo().getLocation();

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

            if (prevPoint.distance(curPoint) == 0) {
                prevPoint = new Point(curPoint.x+ThreadLocalRandom.current().nextInt(-5, 6), curPoint.y+ThreadLocalRandom.current().nextInt(-5, 6));
                madMouse.mouseMove(prevPoint.x, prevPoint.y);
            } else {
                prevPoint = curPoint;
            }
    }
}
