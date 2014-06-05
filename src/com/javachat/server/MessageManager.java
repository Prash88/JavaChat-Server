package com.javachat.server;

import java.util.*;

public class MessageManager {

    private ClientManager cManager;
    private RoomManager rManager;
    private Client sender;
    private String[] message;
    private boolean isChatMode;
    private String roomJoined;

    public MessageManager(ClientManager cm, Client s, RoomManager rm)
    {
        this.cManager = cm;
        this.sender = s;
        this.rManager= rm;

    }
    public void handleMessage(String m)
    {
        if(isChatMode)
        {
            ArrayList<Client> value = (ArrayList<Client>) this.rManager.getRooms().get(this.roomJoined);
            this.message = m.split(" ");
            if(this.message[0].equals("/quit")||this.message[0].equals(null))
            {
                value.remove(this.sender);
                for (Client c : value) {
                    if(!c.equals(this.sender))
                    {
                        c.send("<= * a user went out of your chatroom:"+this.sender.getUserName());
                    }
                }
                this.isChatMode = false;
                this.roomJoined = "";
                this.sender.send("<= you came out of the chat room");
            }
            else
            {
                for (Client c : value)
                {
                    c.send("<= " + this.sender.getUserName() + " : " + m);
                }
            }
        }
        else
        {
            this.message = m.split(" ");
            if(this.message[0].equals("/join"))
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter roomname");
                }
                else
                {
                    Map<String, ArrayList<Client>> rooms = this.rManager.getRooms();
                    for (Map.Entry<String, ArrayList<Client>> room : rooms.entrySet()) {
                        ArrayList <Client> value = (ArrayList<Client>) room.getValue();
                        if(room.getKey().equals(this.message[1]))
                        {
                            value.add(this.sender);
                            this.sender.send("<= Enetring room :"+room.getKey());
                            for (Client c : value) {
                                if(c.equals(this.sender))
                                {
                                    this.sender.send("* "+c.getUserName()+"(** this is you)");
                                }
                                else
                                {
                                    this.sender.send("* "+c.getUserName());
                                    c.send("<= * new user joined your chatroom:"+this.sender.getUserName());
                                }
                            }
                            this.sender.send("<= end of list");
                            this.sender.send("<= /quit to quit the chatroom");
                            this.isChatMode = true;
                            this.roomJoined = this.message[1];
                        }
                        else
                        {
                            this.sender.send("<= room not found");
                        }
                    }
                }
            }
            else if(this.message[0].equals("/admin"))
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter admin password");
                }
                else if (this.message[1].equals("letmein"))
                {
                    this.sender.setAdmin(true);
                    this.sender.send("<= Admin logged in!");

                    for (Client c: this.cManager.getAllClients())
                    {
                        c.send("<= "+this.sender.getUserName() + " has been given admin powers.");
                    }
                    this.commandList(this.sender.isAdmin());
                }
                else
                {
                    this.sender.send("<= Admin Incorrect password.");
                    this.commandList(this.sender.isAdmin());
                }
            }
            else if(this.message[0].equals("/addroom") && this.sender.isAdmin())
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter roomname to add");
                }
                else
                {
                    this.rManager.addRoom(this.message[1]);
                    this.sender.send("<= Room "+this.message[1]+" added");
                }
            }
            else if(this.message[0].equals("/rooms"))
            {
                this.sender.send("<= Active rooms are :");
                Map<String, ArrayList<Client>> rooms = this.rManager.getRooms();
                for (Map.Entry<String, ArrayList<Client>> room : rooms.entrySet()) {
                    ArrayList <Client> value = (ArrayList<Client>) room.getValue();
                    this.sender.send("* "+room.getKey()+" ("+value.size()+")");
                }
                this.sender.send("<= end of list");
            }
            else if(this.message[0].equals("/exit"))
            {
                this.sender.send("<= Bye " + this.sender.getUserName() + "!");
                this.sender.drop();
            }
            else if(this.message[0].equals("/list"))
            {
                this.commandList(this.sender.isAdmin());
            }
            else if(this.message[0].equals("/status"))
            {
                this.sender.send("<= Server Status : " + this.cManager.getSize() + " clients connected");
            }
            else if(this.message[0].equals("/users"))
            {
                this.sender.send("<= Connected users : ");
                for (Client c : this.cManager.getAllClients()) {
                    this.sender.send("<= "+ c.getUserName());
                }
                this.sender.send("<= end of list");
            }
            else if(this.message[0].equals("/changeusername"))
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter a new username");
                }
                else
                {
                    String oldUserName = this.sender.getUserName();
                    if (this.sender.setUserName(this.message[1])) {
                        for (Client c : this.cManager.getAllClients()) {
                            c.send("<= " + oldUserName + " changed username to " + this.sender.getUserName());
                        }
                    }
                    else {
                        this.sender.send("<= Invalid username/ username already taken");
                    }
                }
            }
            else if(this.message[0].equals("/echo"))
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter a string to echo back to you");
                }
                else
                {
                    String echoString = "<=";
                    for(int i=1;i<this.message.length;i++)
                        echoString = echoString +" "+ this.message[i];
                    this.sender.send(echoString);
                }
            }
            else if(this.message[0].equals("/privatemessage"))
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter a username to send private message");
                }
                else if(this.message.length == 2)
                {
                    this.sender.send("<= Please enter a message to send to the user");
                }
                else
                {
                    Client recepient = this.cManager.getClientByUserName(this.message[1]);
                    String messageString = "";
                    for(int i=2;i<this.message.length;i++)
                        messageString = messageString +" "+ this.message[i];
                    if (recepient != null)
                    {
                        String pmlayout = "<= (PM) " + this.sender.getUserName() + " -> " + recepient.getUserName() + ": " + messageString;
                        this.sender.send(pmlayout);
                        recepient.send(pmlayout);
                    }
                }
            }
            else if(this.message[0].equals("/removeuser") && this.sender.isAdmin())
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter a username to remove from the server");
                }
                else
                {
                    Client target = cManager.getClientByUserName(this.message[1]);
                    if (target != null)
                    {
                        Server.log("Removing user " + target.getUserName());
                        String clientRoomJoined = target.getMessageManager().getRoomJoined();
                        if(!(clientRoomJoined==null)&&!clientRoomJoined.isEmpty()&&!clientRoomJoined.equals(""))
                        {
                            ArrayList<Client> value = (ArrayList<Client>) this.rManager.getRooms().get(clientRoomJoined);
                            value.remove(target);
                        }
                        target.send("<= You have been removed from the server by admin");
                        if (this.message.length > 3) {
                            target.send("<= Reason: " + this.message[2]);
                        }
                        target.drop();
                        this.sender.send("<= Username :"+this.message[1]+"removed from the server");
                    }
                    else
                    {
                        this.sender.send("<= Username not found. Enter a valid username to remove");
                    }
                }
            }
            else if(this.message[0].equals("/removeroom") && this.sender.isAdmin())
            {
                if(this.message.length == 1)
                {
                    this.sender.send("<= Please enter roomname to remove");
                }
                else
                {
                    ArrayList<Client> value= (ArrayList<Client>) this.rManager.getRooms().get(this.message[1]);
                    if(value != null)
                    {
                        for (Client c : value) {
                            c.getMessageManager().setIsChatMode(false);
                            c.getMessageManager().setRoomJoined("");
                            c.send("<= Chat room you are in is deleted");
                        }
                        this.rManager.removeRoom(this.message[1]);
                        this.sender.send("<= Room "+this.message[1]+" removed");
                    }
                    else
                    {
                        this.sender.send("<= Room entered not found");

                    }
                }
            }
            else
            {
                this.sender.send("Wrong command");
                this.commandList(this.sender.isAdmin());
            }
        }
    }

    public void commandList(Boolean isAdmin)
    {
        if(!isAdmin)
        {
            this.sender.send("Commands available");
            this.sender.send("--------");
            this.sender.send("/list - to list all available commands");
            this.sender.send("/status - gives server status");
            this.sender.send("/users - lists all connected users to the chat server");
            this.sender.send("/rooms - to list rooms");
            this.sender.send("/join <roomname> - to join a room");
            this.sender.send("/admin <password> - to login as admin");
            this.sender.send("/changeusername <newusername> - to change your username");
            this.sender.send("/echo <string> - to echo back the string to my client");
            this.sender.send("/privatemessage <username> <messagestring> - to send a private message to a user");
            this.sender.send("/exit - to quit chat server");
            this.sender.send("--------");
        }
        else
        {
            this.sender.send("Commands available");
            this.sender.send("--------");
            this.sender.send("/list - to list all available commands");
            this.sender.send("/status - gives server status");
            this.sender.send("/users - lists all connected users to the chat server");
            this.sender.send("/rooms - to list rooms");
            this.sender.send("/join <roomname> - to join a room");
            this.sender.send("/admin <password> - to login as admin");
            this.sender.send("/changeusername <newusername> - to change your username");
            this.sender.send("/echo <string> - to echo back the string to my client");
            this.sender.send("/privatemessage <username> <messagestring> - to send a private message to a user");
            this.sender.send("/exit - to quit chat server");
            this.sender.send("Commands available - Admin commands");
            this.sender.send("--------");
            this.sender.send("/addroom <roomname> - to add a room to chat server");
            this.sender.send("/removeroom <roomname> - to remove a room from chat server");
            this.sender.send("/removeuser <username> <reason> - removes the user from the server");
            this.sender.send("--------");
        }
    }

    public String getRoomJoined()
    {
        return this.roomJoined;
    }

    public void setRoomJoined(String s)
    {
        this.roomJoined = s;
    }

    public void setIsChatMode(Boolean b)
    {
        this.isChatMode = b;
    }

}
