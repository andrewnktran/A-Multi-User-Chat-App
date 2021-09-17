import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server
{
    static Vector<ClientHandler> sockets = new Vector<ClientHandler>(); //Vector to hold the an each instance of class client_handler(using vectors instead of lists because vectors are thread safe)
    public static void main(String args[]) throws IOException
    {
        ServerSocket socket = new ServerSocket(1500); //Server Socket running at port 2020
        int id = 0;
        System.out.println("Server Is Waiting For Connections...");
        while(true){
            System.out.println("Current Client Count: "+ sockets.size());
            Socket connection = socket.accept();
            String clientID = "Client: " + id;
            PrintWriter output = new PrintWriter(connection.getOutputStream(),true);//OUTPUT STREAM
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));//INPUT STREAM
            ClientHandler client = new ClientHandler(clientID,connection,output,input);
            sockets.add(client);
            Thread t =new Thread(client);
            t.start();
            ++id;
        }
    }
}


class ClientHandler implements Runnable{
    Socket connection;
    PrintWriter output;
    BufferedReader input;
    String client_name;


    ClientHandler(String client_name,Socket connection,PrintWriter output,BufferedReader input) {
        this.connection = connection;
        this.output = output;
        this.input = input;
        this.client_name = client_name;
    }


    String receiveMsg() throws IOException {
        return input.readLine();
    }


    void sendMsg(String msg) {
        output.println(msg);
    }


    void CloseStreams() throws IOException {
        output.close();
        input.close();
        connection.close();
        Server.sockets.remove(this);
        System.out.println("Current Client Count: "+ Server.sockets.size());
    }


    public void run() {
        try{
            while(true) {
                String msg = receiveMsg();

                if(msg.endsWith("EXIT")) {
                    msg = msg.substring(0,msg.indexOf('-'))+" has left the server.";
                    for(ClientHandler c:Server.sockets) {
                        if(!c.client_name.equals(client_name)) c.sendMsg(msg);
                    }
                    CloseStreams();
                    break;
                }

                for(ClientHandler c:Server.sockets) {
                    if(!c.client_name.equals(client_name))  c.sendMsg(msg);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}