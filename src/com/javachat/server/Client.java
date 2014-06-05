package com.javachat.server;

import java.net.Socket;
import java.util.Date;
import java.io.*;

public class Client extends Thread
{

    private ClientManager cManager;
    private RoomManager rManager;
    private MessageManager mManager;
    private String userName;
    private Date connectedAt;
    private boolean isAdmin;
    private Socket uSocket;
    private BufferedReader iStream;
    private PrintStream oStream;

    public Client(Socket s, ClientManager cm, RoomManager rm)
    {
        this.userName = "Anonymous";
        this.connectedAt = new Date();
        this.isAdmin = false;
        this.cManager = cm;
        this.rManager = rm;
        this.uSocket = s;
        this.setupStreams();
        this.start();
    }

    private void setupStreams()
    {
        try
        {
            this.iStream = new BufferedReader(new InputStreamReader(uSocket.getInputStream()));
            this.oStream = new PrintStream(uSocket.getOutputStream());
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void handshake()
    {
        this.send("<= Welcome to the JChat server");
        while(true)
        {
            this.send("<= Login Name?");
            try
            {
                String input[]= this.iStream.readLine().split(" ");
                if (input[0] != null && !input[0].isEmpty())
                {
                    Server.log("CLIENT ENTERED USERNAME");
                    if (this.setUserName(input[0]))
                    {
                        this.send("<= Welcome "+input[0]+"!");
                        this.send("<= /list - to list all available commands");

                        for (Client c : this.cManager.getAllClients())
                        {
                            c.send("<= * new user joined server: " + this.getUserName());
                        }
                        this.cManager.addClient(this);
                        break;
                    }
                    else
                    {
                        this.send("<= Sorry, name taken");
                    }
                }
                else
                {
                    this.send("<= Input expected");
                }
            }
            catch (IOException ex)
            {
                    this.drop();
            }
        }
    }

    @Override
    public void run()
    {
        this.handshake();
        while(true)
        {
            try
            {
                String m = this.iStream.readLine();
                System.out.println("<" + this.getUserName() + "> " + m);
                if(this.mManager==null)
                {
                    this.mManager = new MessageManager(this.cManager, this,this.rManager);
                    this.mManager.handleMessage(m);
                }
                else
                    this.mManager.handleMessage(m);
            }
            catch (Exception e)
            {
                this.drop();
                break;
            }
        }
    }

    public void send(String m)
    {
        this.oStream.println(m);
    }

    public void drop()
    {
        try
        {
            this.iStream.close();
            this.oStream.flush();
            this.oStream.close();
            this.uSocket.close();
            this.cManager.removeClient(this);
            this.interrupt();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public String getIP()
    {
        return this.uSocket.getInetAddress().toString();
    }

    public boolean setUserName(String n)
    {
        String nSplit[] = n.split(" ");
        if (!(this.cManager.userNameExists(nSplit[0])))
        {
            this.userName = nSplit[0];
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getUserName()
    {
        return this.userName;
    }

    public void setAdmin(boolean b)
    {
        this.isAdmin = b;
    }

    public boolean isAdmin()
    {
        return this.isAdmin;
    }

    public MessageManager getMessageManager()
    {
        return mManager;
    }
}
