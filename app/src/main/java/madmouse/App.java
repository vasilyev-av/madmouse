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
        String delayText = "1";
        String moveText = "5";
        boolean useKeys = true;

        try {
            (new File(delayFile)).createNewFile();
            FileReader mainReader = new FileReader(delayFile);
            mainProp.load(mainReader);
            mainReader.close();
        } catch(Exception e){System.out.print(e.getMessage());}
        delayText = mainProp.getProperty("delay", "1");
        moveText = mainProp.getProperty("pixel", "5");
        useKeys = (mainProp.getProperty("keys").compareTo("1")==0) ? true : false;

        ButtonListener buttonListener = new ButtonListener(mainProp);
        Button mainButton = new Button("Start");
        mainButton.addActionListener(buttonListener);

        TextField mainText = new TextField(delayText);
        mainText.addTextListener(new TextListener(){
            @Override
            public void textValueChanged(TextEvent e) {
                String delayText=mainText.getText();
                mainProp.setProperty("delay", delayText);
                buttonListener.setProps(mainProp);
                try {
                    FileWriter mainWriter = new FileWriter(delayFile);
                    mainProp.store(mainWriter, "");
                } catch (Exception d) {System.out.print(d.getMessage());}
            }
        });

        TextField mainText2 = new TextField(moveText);
        mainText2.addTextListener(new TextListener(){
            @Override
            public void textValueChanged(TextEvent e) {
                String moveText=mainText2.getText();
                mainProp.setProperty("pixel", moveText);
                buttonListener.setProps(mainProp);
                try {
                    FileWriter mainWriter = new FileWriter(delayFile);
                    mainProp.store(mainWriter, "");
                } catch (Exception d) {System.out.print(d.getMessage());}
            }
        });

        Checkbox mainCheck = new Checkbox("use keys", useKeys);
        mainCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Integer useKeys = e.getStateChange();
				mainProp.setProperty("keys", useKeys.toString());
                buttonListener.setProps(mainProp);
                try {
                    FileWriter mainWriter = new FileWriter(delayFile);
                    mainProp.store(mainWriter, "");
                } catch (Exception d) {System.out.print(d.getMessage());}
			}
		});

        mainFrame.add(mainText);
        mainFrame.add(mainText2);
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
    private Properties settings;
    private int period;

    ButtonListener(){
        period = 1000;
    }

    ButtonListener(Properties properties){
        period = Integer.parseInt(properties.getProperty("delay", "1"))*1000;
        settings = properties;
    }

    public void setProps(Properties properties) {
        period = Integer.parseInt(properties.getProperty("delay", "1"))*1000;
        settings = properties;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Button curButton = (Button) e.getSource();
        switch (e.getActionCommand()){
            case "Start":
                curButton.setLabel("Stop");
                madTimer = new Timer();
                madTimer.schedule(new MadMouseMove(settings), period, period);
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
    int moveSize = 5;
    int[] keys = {KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL};

    private Point prevPoint = MouseInfo.getPointerInfo().getLocation();

    MadMouseMove() {
        try {
            madMouse = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    MadMouseMove(Properties properties) {
        try {
            madMouse = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        this.moveSize= Integer.parseInt(properties.getProperty("pixel", "5"));
        this.pressKey = (properties.getProperty("keys").compareTo("1")==0) ? true : false;
    }

    @Override
    public void run() {
            Point curPoint = MouseInfo.getPointerInfo().getLocation();

            if (prevPoint.distance(curPoint) == 0) {
                prevPoint = new Point(curPoint.x+ThreadLocalRandom.current().nextInt(-moveSize, moveSize+1), curPoint.y+ThreadLocalRandom.current().nextInt(-moveSize, moveSize+1));
                madMouse.mouseMove(prevPoint.x, prevPoint.y);
            } else {
                prevPoint = curPoint;
            }
            if (pressKey && ThreadLocalRandom.current().nextInt(0, 2) == 1) {
                int curKey = keys[ThreadLocalRandom.current().nextInt(0, 2)];
                madMouse.keyPress(curKey);
                madMouse.keyRelease(curKey);
            }
    }
}
