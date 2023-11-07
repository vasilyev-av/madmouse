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
import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.event.WindowAdapter;
import java.awt.Robot;
import java.awt.TextField;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;


public class App {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("MadMouse");
        mainFrame.setSize(150, 100);
        mainFrame.setLayout(new GridLayout(2, 1));

        Properties mainProp = new Properties();
        String delayFile = System.getProperty("user.home") + "/madmouse.ini";
        String delayText = "1000";
        boolean useKeys = true;
        try {
            (new File(delayFile)).createNewFile();
            FileReader mainReader = new FileReader(delayFile);
            mainProp.load(mainReader);
            mainReader.close();
        } catch(Exception e){System.out.print(e.getMessage());}
        delayText = mainProp.getProperty("delay", "1000");
        useKeys = mainProp.getProperty("keys", "1") == "1" ? true : false;

        ButtonListener buttonListener = new ButtonListener(Integer.parseInt(delayText), useKeys);
        Button mainButton = new Button("Start");
        mainButton.addActionListener(buttonListener);

        TextField mainText = new TextField(delayText);
        mainText.addTextListener(new TextListener(){
            @Override
            public void textValueChanged(TextEvent e) {
                String delayText=mainText.getText();
                buttonListener.setPeriod(Integer.parseInt(delayText));
                mainProp.setProperty("delay", delayText);
                try {
                    FileWriter mainwriter = new FileWriter(delayFile);
                    mainProp.store(mainwriter, "");
                } catch (Exception d) {System.out.print(d.getMessage());}
            }
        });

        Checkbox mainCheck = new Checkbox("use keys", useKeys);
        mainCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Boolean useKeys=(e.getStateChange() == 1 ? true : false);
				mainProp.setProperty("keys", useKeys.toString());
                try {
                    FileWriter mainwriter = new FileWriter(delayFile);
                    mainProp.store(mainwriter, "");
                } catch (Exception d) {System.out.print(d.getMessage());}
			}
		});

        mainFrame.add(mainText);
        mainFrame.add(mainCheck);
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
    private int period;
    private boolean pressKey;

    ButtonListener(){
        period = 1000;
        pressKey = true;
    }

    ButtonListener(int delay){
        period = delay;
        pressKey = true;
    }

    ButtonListener(boolean useKeys){
        period = 1000;
        pressKey = useKeys;
    }

    ButtonListener(int delay, boolean useKeys){
        period = delay;
        pressKey = useKeys;
    }

    public void setKeys(boolean useKeys) {
         pressKey = useKeys;
    }

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
                madTimer.schedule(new MadMouseMove(pressKey), period, period);
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
    boolean pressKey = true;
    int[] keys = {KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL};

    private Point prevPoint = MouseInfo.getPointerInfo().getLocation();

    MadMouseMove() {
        try {
            madMouse = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    MadMouseMove(boolean useKeys) {
        try {
            madMouse = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        this.pressKey = useKeys;
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
            if (pressKey && ThreadLocalRandom.current().nextInt(0, 1) == 1) {
                int curKey = keys[ThreadLocalRandom.current().nextInt(0, 1)];
                madMouse.keyPress(curKey);
                madMouse.keyRelease(curKey);
            }
            
    }
}
