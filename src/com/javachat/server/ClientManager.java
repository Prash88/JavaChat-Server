package com.javachat.server;

import java.util.ArrayList;

public class ClientManager
{

    private ArrayList<Client> cArray;

    public ClientManager()
    {
        this.cArray = new ArrayList();
    }

    public void addClient(Client c)
    {
        this.cArray.add(c);
    }

    public void removeClient(Client c)
    {
        if (this.cArray.contains(c))
        {
            this.cArray.remove(c);
            for (Client cl : this.cArray)
            {
                cl.send("<= " + c.getUserName() + " has disconnected from server");
            }
        }
    }

    public ArrayList<Client> getAllClients()
    {
        return this.cArray;
    }

    public boolean userNameExists(String n)
    {
        boolean result = false;
        for (Client c : this.cArray)
        {
            if(c.getUserName().equals(n))
            {
                result = true;
            }
        }
        return result;
    }

    public Client getClientByUserName(String n)
    {
        Client result = null;
        for (Client c : this.cArray)
        {
            if(c.getUserName().equals(n))
            {
                result = c;
            }
        }
        return result;
    }

    public int getSize()
    {
        return this.cArray.size();
    }

}
