package client;

import network.TCPConnection;
import network.TCPConnectionListner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListner {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;

    public static void main(String[] args) {
        String parametr = args[0];
        SwingUtilities.invokeLater(()->new ClientWindow(parametr));
//        new ClientWindow(parametr);

    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("anna");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow(String parametr) {
//        try{
//
//            setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("F://все и полностью//Мои работы фотографии//Мое фото 1.png")))));
//
//        }   catch (IOException e) {
//            e.printStackTrace();
//        }


        try (InputStream input = new FileInputStream(parametr)) {

            Properties prop = new Properties();
            prop.load(input);
            String ipAddr = prop.getProperty("client.ipAddr");
            String port = prop.getProperty("client.port");


            JFrame frame = new JFrame();
            JLabel background = null;

            background = new JLabel(new ImageIcon(ImageIO.read(new File("F://все и полностью//Мои работы фотографии//Мое фото 1.png"))));

            frame.setContentPane(background);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);

            log.setEditable(false);
            log.setLineWrap(true);
            add(log, BorderLayout.CENTER);

            fieldInput.addActionListener(this);
            add(fieldInput, BorderLayout.SOUTH);
            add(fieldNickname, BorderLayout.NORTH);


            setVisible(true);

            connection = new TCPConnection(this, ipAddr, Integer.parseInt(port));
        } catch (IOException e) {
            printMsg("Connection exception" + e);
        }
    }

//    class BgPanel extends JPanel {
//        public void paintComponent(Graphics g) {
//            Image im = null;
//            try {
//                im = ImageIO.read(new File("F://все и полностью//Мои работы фотографии//Мое фото 1.png"));
//            } catch (IOException e) {
//                throw new RuntimeException("БЛЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯ");
//            }
//            g.drawImage(im, 0, 0, null);
//        }
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception" + e);
    }

    private synchronized void printMsg (String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
