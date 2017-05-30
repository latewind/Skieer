package cn.net.ecode.skieer.gui;

import cn.net.ecode.skieer.runner.TaskRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RunnerWin extends JDialog implements MsgObserver{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea;
    private TaskRunner taskRunner=new TaskRunner();

    public RunnerWin() {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        initDialogLocation();
        taskRunner.registeObserver(this);
    }
    private void initDialogLocation(){
      this.setPreferredSize(new Dimension(500,300));
      this.setLocationRelativeTo(null);
    }

    private void onOK() {
        taskRunner.runTask();
        // add your code here
     //   dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    public static void main(String[] args) {
        RunnerWin dialog = new RunnerWin();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public void update(String msg) {
        textArea.append(msg+"\n\r");
    }
}
