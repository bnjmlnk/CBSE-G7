/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.gruppe7.door;

import collision.CollisionData;
import collision.CollisionEvent;
import dk.gruppe7.common.Entity;
import dk.gruppe7.common.GameData;
import dk.gruppe7.common.IProcess;
import dk.gruppe7.common.IRender;
import dk.gruppe7.common.World;
import dk.gruppe7.common.data.Direction;
import dk.gruppe7.common.data.Rectangle;
import dk.gruppe7.common.data.Room;
import dk.gruppe7.common.data.Vector2;
import dk.gruppe7.common.graphics.Graphics;
import dk.gruppe7.playercommon.Player;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pc4
 */

@ServiceProvider(service = IProcess.class)

public class DoorSystem implements IProcess, IRender
{
    InputStream texture = getClass().getResourceAsStream("door.png");
    
    Room currentRoom = null;
    List<Door> currentDoors = new ArrayList<>();
    
    Map<Direction,Vector2> doorPosition = new HashMap<>();

    @Override
    public void start(GameData gameData, World world)
    {
        doorPosition.put(Direction.SOUTH, new Vector2(gameData.getScreenWidth()/2, 38));
        doorPosition.put(Direction.WEST, new Vector2(38, gameData.getScreenHeight()/2));
        doorPosition.put(Direction.NORTH, new Vector2(gameData.getScreenWidth()/2, gameData.getScreenHeight()-38));
        doorPosition.put(Direction.EAST, new Vector2(gameData.getScreenWidth()-38, gameData.getScreenHeight()/2));
        
    }

    @Override
    public void stop(GameData gameData, World world)
    {
        
    }

    @Override
    public void process(GameData gameData, World world)
    {
        if(currentRoom == null)
        {
            if(world.getCurrentRoom() != null)
            {
                currentRoom = world.getCurrentRoom();
                RoomChange(currentRoom, world);
            }
        }
        
        Collision(world);
        
        
            
    }
    
    int h = 125;
    int w = 25;
    private void RoomChange(Room newRoom, World world)
    {
        for (Entity currentDoor : currentDoors)
            world.removeEntity(currentDoor);
        
        
        currentDoors = new ArrayList<>();
        if(newRoom.getNorth() != null)
        {
            currentDoors.add(new Door()
            {{
                        setCollidable(true);
                        setBounds(new Rectangle( h, w));
                        setPositionCentered(doorPosition.get(Direction.NORTH));
                        setDirection(Direction.NORTH);
            }});
        }
        if(newRoom.getWest()!= null)
        {
            currentDoors.add(new Door()
            {{
                        setCollidable(true);
                        setBounds(new Rectangle(w,  h));
                        setPositionCentered(doorPosition.get(Direction.WEST));
                        
                        
                        //setRotation(90);
                        setDirection(Direction.WEST);
            }});
        }
        if(newRoom.getSouth()!= null)
        {
            currentDoors.add(new Door()
            {{
                        setCollidable(true);
                        setBounds(new Rectangle( h, w));
                        setPositionCentered(doorPosition.get(Direction.SOUTH));
                        setDirection(Direction.SOUTH);
                        setRotation(180);
                        
            }});
        }
        if(newRoom.getEast()!= null)
        {
            currentDoors.add(new Door()
            {{
                        setCollidable(true);
                        setBounds(new Rectangle(w,  h));
                        setPositionCentered(doorPosition.get(Direction.EAST));
                        //setRotation(270);
                        setDirection(Direction.EAST);
            }});
            
        }
        
        for (Entity currentDoor : currentDoors)
            world.addEntity(currentDoor);
    }
    
    private void Collision(World world)
    {
        for(ListIterator<CollisionEvent> iterator = CollisionData.getEvents().listIterator(); iterator.hasNext();)
        {
            CollisionEvent tempi = iterator.next();
            for (Door door : currentDoors)
            {
                if(tempi.getOtherID().equals(door.getId()))
                {
                    Entity e = world.getEntityByID(tempi.getTargetID());
                   
                    Player temp = e instanceof Player ? (Player)e : null;
                    System.out.println(temp);
                    if(temp != null)
                    {
                        switch(((Door)world.getEntityByID(tempi.getOtherID())).getDirection())
                        {
                            case NORTH:
                                world.setCurrentRoom(currentRoom.getNorth());
                                break;
                            case WEST:
                                world.setCurrentRoom(currentRoom.getWest());
                                break;
                            case SOUTH:
                                world.setCurrentRoom(currentRoom.getSouth());
                                break;
                            case EAST:
                                world.setCurrentRoom(currentRoom.getEast());
                                break;
                        }
                        currentRoom = world.getCurrentRoom();
                        temp.setPosition(new Vector2(650,380));
                        RoomChange(currentRoom, world);
                        iterator.remove();
                    }
                    else
                        iterator.remove();
                }
                
                

            }
        }
    }

    @Override
    public void render(Graphics g, World world)
    {
        Vector2 size;
        for (Door currentDoor : currentDoors)
        {
            //if(currentDoor.getDirection() == Direction.EAST || currentDoor.getDirection() == Direction.WEST)
            //    size = new Vector2(currentDoor.getBounds().getHeight(), currentDoor.getBounds().getWidth());
            //else
                size = new Vector2(currentDoor.getBounds().getWidth(), currentDoor.getBounds().getHeight());
            
            g.drawSprite(
                    /* Position    */ currentDoor.getPosition(), 
                    /* Size        */ size, 
                    /* InputStream */ texture, 
                    /* Rotation    */ currentDoor.getRotation()
            );
        }

    }
    
}
