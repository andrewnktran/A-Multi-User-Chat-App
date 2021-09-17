import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;


class ClientUI extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private PrintWriter output;
    private BufferedReader input;
    private String message;
    private String serverIP;
    private Socket connection;
    private String name;
    private JFrame f1,f2;

    public ClientUI(String host){
        f1 =new JFrame();
        serverIP = host;
        f1.setSize(300,500);
        JLabel l1 = new JLabel("Enter you name:");
        JTextField tf = new JTextField(16);
        JButton b = new JButton("Submit");
        JPanel p = new JPanel();
        p.add(l1);
        p.add(tf);
        p.add(b);
        f1.add(p);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name = tf.getText();
                f1.setVisible(false);
                f1.dispose();
                f2.setVisible(true);
            }
        });
        f1.setVisible(true);
        f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        f2 = new JFrame();
        chatWindow = new JTextArea();
        userText = new JTextField();
        Button end = new Button("End");
        userText.setEditable(true);
        chatWindow.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage("EXIT");
                CloseStreams();
                f2.dispose();
                System.exit(0);
            }
        });
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
        mainPanel.add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        bottomPanel.add(userText, BorderLayout.CENTER);
        bottomPanel.add(end, BorderLayout.LINE_END);
        f2.getContentPane().add(mainPanel);
        f2.setSize(300,500);
        f2.setVisible(false);
        f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException e){
            showMessage("\n Server not Found");
        }catch(IOException e){
            e.printStackTrace();
        }
        finally{
            CloseStreams();
        }
    }


    private void connectToServer() throws IOException{
        // TO DO: Should handle if the connection failed connection.
        showMessage("Attempting Connection..\n");
        connection= new Socket(InetAddress.getByName(serverIP),1500);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }


    private void setupStreams() throws IOException{
        output=new PrintWriter(connection.getOutputStream(),true);
        input=new BufferedReader(new InputStreamReader(connection.getInputStream()));
        showMessage("\nChat room now has connected.\n");
    }


    private void whileChatting() throws IOException{
        while(true)
        {
            try{
                message=(String)input.readLine();
                showMessage("\n"+message);
            }catch(Exception e){
                showMessage("Some Error Occurred");
            }

        }
    }


    private void CloseStreams(){
        try {
            output.close();
            input.close();
            connection.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private void sendMessage(String message){
        try{
            output.println(name+": "+message);
            showMessage("\n"+name+": "+message);
        }catch(Exception e){
            chatWindow.append("\n Message not sent,some error occurred");
        }

    }


    private void showMessage(final String s) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(s);
                    }
                }
        );
    }
}

public class Client {
    public static void main(String args[]){
        ClientUI CLIENT=new ClientUI("127.0.0.1");
        CLIENT.startRunning();
    }
}
