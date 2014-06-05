package com.javachat.server;

import java.util.*;

/**
 * Created by gazelle on 6/2/14.
 */
public class RoomManager
{

    private Map<String, ArrayList<Client>> roomMembers;

    public RoomManager()
    {
        this.roomMembers = new HashMap<String, ArrayList<Client>>();
    }

    public void addRoom(String s)
    {
        this.roomMembers.put(s, new ArrayList<Client>());
    }

    public Map getRooms()
    {
        return this.roomMembers;
    }

    public void removeRoom(String s)
    {
        if (this.roomMembers.containsKey(s))
        {
            this.roomMembers.remove(s);
        }
    }

}
