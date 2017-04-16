/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.gruppe7.powerup;

import collision.CollisionEvent;
import dispose.DisposeEvent;
import dk.gruppe7.common.Dispatcher;
import dk.gruppe7.common.Entity;
import dk.gruppe7.common.GameData;
import dk.gruppe7.common.IProcess;
import dk.gruppe7.common.IRender;
import dk.gruppe7.common.World;
import dk.gruppe7.common.data.ActionEventHandler;
import dk.gruppe7.common.data.Vector2;
import dk.gruppe7.common.graphics.Graphics;
import dk.gruppe7.powerupcommon.Powerup;
import dk.gruppe7.common.data.Rectangle;
import dk.gruppe7.playercommon.Player;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author JOnes
 */
@ServiceProviders(value = {
    @ServiceProvider(service = IProcess.class)
    , @ServiceProvider(service = IRender.class)
})

public class PowerupSystem implements IProcess, IRender {

    private List<Powerup> listOfPowerups;
    ArrayList<Powerup> listOfPowerupsToBeRemoved = new ArrayList<>();
    InputStream texture = getClass().getResourceAsStream("speedBoost.png");
    Entity player = null;

    @Override
    public void start(GameData gameData, World world) {
        listOfPowerups = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            Entity e = makePowerup();
            listOfPowerups.add((Powerup) e);
            world.addEntity(e);
        }

        Dispatcher.subscribe(CollisionEvent.class, pickupCollisionHandler);
        Dispatcher.subscribe(DisposeEvent.class, disposalHandler);
    }

    @Override
    public void stop(GameData gameData, World world) {
        Dispatcher.unsubscribe(CollisionEvent.class, pickupCollisionHandler);
        Dispatcher.unsubscribe(DisposeEvent.class, disposalHandler);
        
        world.removeEntities(world.<Powerup>getEntitiesByClass(Powerup.class));
    }

    @Override
    public void process(GameData gameData, World world) {
        if (player == null) {
            // finds and sets the player object
            for (Entity element : world.getEntities()) {
                if (element instanceof Player) {
                    player = (Player) element;
                }
            }
        }
    }
    
    ActionEventHandler<CollisionEvent> pickupCollisionHandler = (event, world) -> {
        listOfPowerupsToBeRemoved.clear();
        
        Entity targetEntity = world.getEntityByID(event.getTargetID());
        // if the targetEntity is a Powerup and the "other" is the player we wanna do something
        if (targetEntity instanceof Powerup && event.getOtherID().equals(player.getId())) {
            Powerup powerup = (Powerup) targetEntity; // we know it is a Powerup so we set it to a Powerup Object type;
            player.setMaxVelocity(powerup.getNewMaxVelocity()); // sets the new value;
            listOfPowerupsToBeRemoved.add(powerup);
        }
    };
    
    ActionEventHandler<DisposeEvent> disposalHandler = (event, world) -> {
        world.removeEntities(listOfPowerupsToBeRemoved);
        listOfPowerups.removeAll(listOfPowerupsToBeRemoved);
    };

    @Override
    public void render(Graphics g, World world) {
        for (Powerup powerup : listOfPowerups) {
            g.drawSprite(powerup.getPosition(), new Vector2(powerup.getBounds().getWidth(), powerup.getBounds().getHeight()), texture, 0);
        }
    }

    private Entity makePowerup() {
        return new Powerup() {
            {
                setPosition(new Vector2(200.f, 50.f));
                setCollidable(true);
                setBounds(new Rectangle(64, 64));
                setNewMaxVelocity(1000);
            }
        };
    }

}
