package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "192.168.0.106";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();//поле где будут все сообщения
    private final JTextField fieldNickname = new JPasswordField("dmitry");//поле ника, можно сразу не писать
    private final JTextField fieldInput = new JTextField();//поле куда мы будем вводить сообщение

    private TCPConnection connection;
    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//красный крестик для закрытия
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);// окно по центру
        setAlwaysOnTop(true);//окно всегда сверху

        log.setEditable(false);//в поле где будут все сообщения мы запрещаем редактирование
        log.setLineWrap(true);//в поле где все сообщение автоматический перенос строки
        add(log, BorderLayout.CENTER);// поле со всеми сообщениями будет по центру

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);//ввод текста на юг - вниз
        add(fieldNickname, BorderLayout.NORTH);//никнейм на север - вверх


        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) { //монитор нажатия кнопки отправить
        String msg = fieldInput.getText();
        if(msg.equals("")) return; //если строка пустая
        fieldInput.setText(null);//стираем то что у нас находится в поле input
        connection.sendString(fieldNickname.getText() + ": " + msg); // какой будет вывод у строки "Ник: Сообщение"

    }


    @Override
    public void onConnectionReady(TCPConnection topConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveStrting(TCPConnection topConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection topConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection topConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());//скролл в самый низ сообщений
            }
        });
    }
}
