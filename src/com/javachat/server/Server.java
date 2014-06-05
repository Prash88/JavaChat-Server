package com.javachat.server;

import java.net.ServerSocket;
import java.util.Date;

public class Server
{

    private static int PORT;

    public static void main(String[] args)
    {
        PORT = 9000;
        ClientManager cManager = new ClientManager();
        RoomManager rManager = new RoomManager();
        try
        {
            log("Java Chat Server Initiated");
            ServerSocket listener = new ServerSocket(PORT);
            log("Now listening for incoming connections on port " + PORT);
            while(true)
            {
                Client newClient = new Client(listener.accept(), cManager, rManager);
                log("Incoming connection from " + newClient.getIP());
            }
        }
        catch(Exception e)
        {
            log(e.getMessage());
            System.exit(-1);
        }
    }

    public static void log(String message)
    {
        System.out.println("[" + new Date() + "] " + message);
    }

}
