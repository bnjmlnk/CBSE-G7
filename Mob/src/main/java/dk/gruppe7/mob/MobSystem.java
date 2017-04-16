/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.gruppe7.mob;

import collision.CollisionEvent;
import dk.gruppe7.common.Dispatcher;
import dk.gruppe7.common.Entity;
import dk.gruppe7.common.GameData;
import dk.gruppe7.common.IProcess;
import dk.gruppe7.common.IRender;
import dk.gruppe7.common.World;
import dk.gruppe7.common.data.ActionEventHandler;
import dk.gruppe7.common.data.Rectangle;
import dk.gruppe7.common.data.Vector2;
import dk.gruppe7.common.graphics.Animator;
import dk.gruppe7.common.graphics.Graphics;
import dk.gruppe7.common.resources.Image;
import dk.gruppe7.data.MobType;
import static dk.gruppe7.data.MobType.DEFENDER;
import static dk.gruppe7.data.MobType.MELEE;
import static dk.gruppe7.data.MobType.RANGED;
import static dk.gruppe7.data.MobType.SUPPORT;
import dk.gruppe7.mobcommon.Mob;
import dk.gruppe7.mobcommon.MobData;
import dk.gruppe7.mobcommon.MobEvent;
import dk.gruppe7.mobcommon.MobEventType;
import static dk.gruppe7.mobcommon.MobEventType.SPAWN;
import dk.gruppe7.mobcommon.MobID;
import dk.gruppe7.shootingcommon.Bullet;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author benjaminmlynek
 */
@ServiceProvider(service = IProcess.class)
public class MobSystem implements IProcess, IRender {

    UUID mobID;

    Image textureSkeletonRanged;
    Image textureSkeletonMelee;
    Image textureKnightRanged;
    Image health;
    Image[] framesSkeleton;
    Image[] framesKnight;

    @Override
    public void start(GameData gameData, World world) {

        framesSkeleton = new Image[]{
            gameData.getResourceManager().addImage("frameSkeleton0", getClass().getResourceAsStream("SkeletonFeet0.png")),
            gameData.getResourceManager().addImage("frameSkeleton1", getClass().getResourceAsStream("SkeletonFeet1.png")),
            gameData.getResourceManager().addImage("frameSkeleton2", getClass().getResourceAsStream("SkeletonFeet2.png")),
            gameData.getResourceManager().addImage("frameSkeleton3", getClass().getResourceAsStream("SkeletonFeet3.png")),
            gameData.getResourceManager().addImage("frameSkeleton4", getClass().getResourceAsStream("SkeletonFeet4.png")),
            gameData.getResourceManager().addImage("frameSkeleton5", getClass().getResourceAsStream("SkeletonFeet5.png")),
            gameData.getResourceManager().addImage("frameSkeleton6", getClass().getResourceAsStream("SkeletonFeet6.png")),
            gameData.getResourceManager().addImage("frameSkeleton7", getClass().getResourceAsStream("SkeletonFeet7.png")),
            gameData.getResourceManager().addImage("frameSkeleton8", getClass().getResourceAsStream("SkeletonFeet8.png")),
            gameData.getResourceManager().addImage("frameSkeleton9", getClass().getResourceAsStream("SkeletonFeet9.png")),
            gameData.getResourceManager().addImage("frameSkeleton10", getClass().getResourceAsStream("SkeletonFeet10.png")),
            gameData.getResourceManager().addImage("frameSkeleton11", getClass().getResourceAsStream("SkeletonFeet11.png")),
            gameData.getResourceManager().addImage("frameSkeleton12", getClass().getResourceAsStream("SkeletonFeet12.png"))

        };
        framesKnight = new Image[]{
            gameData.getResourceManager().addImage("frameKnight0", getClass().getResourceAsStream("KnightFeet0.png")),
            gameData.getResourceManager().addImage("frameKnight1", getClass().getResourceAsStream("KnightFeet1.png")),
            gameData.getResourceManager().addImage("frameKnight2", getClass().getResourceAsStream("KnightFeet2.png")),
            gameData.getResourceManager().addImage("frameKnight3", getClass().getResourceAsStream("KnightFeet3.png")),
            gameData.getResourceManager().addImage("frameKnight4", getClass().getResourceAsStream("KnightFeet4.png")),
            gameData.getResourceManager().addImage("frameKnight5", getClass().getResourceAsStream("KnightFeet5.png")),
            gameData.getResourceManager().addImage("frameKnight6", getClass().getResourceAsStream("KnightFeet6.png")),
            gameData.getResourceManager().addImage("frameKnight7", getClass().getResourceAsStream("KnightFeet7.png")),
            gameData.getResourceManager().addImage("frameKnight8", getClass().getResourceAsStream("KnightFeet8.png")),
            gameData.getResourceManager().addImage("frameKnight9", getClass().getResourceAsStream("KnightFeet9.png")),
            gameData.getResourceManager().addImage("frameKnight10", getClass().getResourceAsStream("KnightFeet10.png")),
            gameData.getResourceManager().addImage("frameKnight11", getClass().getResourceAsStream("KnightFeet11.png")),
            gameData.getResourceManager().addImage("frameKnight12", getClass().getResourceAsStream("KnightFeet12.png"))

        };

        textureSkeletonRanged = gameData.getResourceManager().addImage("torso", getClass().getResourceAsStream("SkeletonRanged.png"));
        textureSkeletonMelee = gameData.getResourceManager().addImage("torso", getClass().getResourceAsStream("SkeletonMelee.png"));
        textureKnightRanged = gameData.getResourceManager().addImage("torso", getClass().getResourceAsStream("KnightRanged.png"));
        health = gameData.getResourceManager().addImage("healthBar", getClass().getResourceAsStream("healthGreen.png"));

        Entity mob;
        // Add mobs to the world

        for (int i = 0; i < GetRandomNumberBetween(2, 7); i++) {
            mob = createMob(
                    GetRandomNumberBetween(0, gameData.getScreenWidth()),
                    GetRandomNumberBetween(0, gameData.getScreenHeight()),
                    pickRandomMobType(MobType.class));
            world.addEntity(mob);
            MobData.getEvents().add(new MobEvent((Mob) mob, SPAWN));
        }

        Dispatcher.subscribe(CollisionEvent.class, bulletCollisionHandler);
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove mobs from world.
        world.removeEntity(world.getEntityByID(mobID));
        mobID = null;

        Dispatcher.unsubscribe(CollisionEvent.class, bulletCollisionHandler);
    }

    @Override
    public void process(GameData gameData, World world) {
        ArrayList<Mob> listOfMobsToBeRemoved = new ArrayList<>();

        for(Mob mob : world.<Mob>getEntitiesByClass(Mob.class)) {
            if(mob.getVelocity().len() > .1f){
                mob.getAnimator().setInterval(15*1.0f/mob.getVelocity().len());
                mob.getAnimator().update(gameData);
            }

            if (mob.getWanderTimer() <= 0) {
                mob.setVelocity(new Vector2(GetRandomNumberBetween(-10, 10), GetRandomNumberBetween(-10, 10)));
                mob.setWanderTimer(GetRandomNumberBetween(1, 4));
            } else {
                mob.setWanderTimer(mob.getWanderTimer() - gameData.getDeltaTime());
            }

            mob.setPosition(mob.getPosition().add(mob.getVelocity().mul(gameData.getDeltaTime())));

            if (mob.getHealthData().getHealth() <= 0) {
                listOfMobsToBeRemoved.add(mob);
                MobData.getEvents(gameData.getTickCount()).add(new MobEvent(mob, MobEventType.DEATH, gameData.getTickCount()));
            }
        }
        
        // The below line should be moved to a dispose event handler.
        world.removeEntities(listOfMobsToBeRemoved);
    }

    ActionEventHandler<CollisionEvent> bulletCollisionHandler = (event, world) -> {
        for(Mob mob : world.<Mob>getEntitiesByClass(Mob.class)) {
            if (event.getOtherID().equals(mob.getId())) {
                Entity hitBy = world.getEntityByID(event.getTargetID());
                Bullet b = Bullet.class.isInstance(hitBy) ? (Bullet) hitBy : null;
                if (b != null) {
                    mob.getHealthData().setHealth(mob.getHealthData().getHealth() - b.getDamageData().getDamage());
                    //Temporary: to avoid bullets hitting multiple times
                    b.getDamageData().setDamage(0);
                }
            }
        }
    };

    private Entity createMob(float x, float y, MobType type) {
        Mob mob = new Mob();
        MobID.setMobID(mobID = mob.getId());
        mob.setPosition(new Vector2(x, y));
        mob.setMobType(type);
        mob.setMaxVelocity(300.f);
        mob.setAcceleration(80.f);
        mob.setCollidable(true);
        mob.setBounds(new Rectangle(64, 64));
        mob.setAnimator(new Animator(framesSkeleton, 1.f));

        // Create different types of mobs with different behavior
        if (type == SUPPORT) {
            // TO DO
            System.out.println("Support Mob " + mobID);
            mob.setMaxVelocity(250.f);
            mob.setAcceleration(90);
        }

        if (type == DEFENDER) {
            // TO DO
            System.out.println("Defender Mob " + mobID);
            mob.setMaxVelocity(200.f);
            mob.setAcceleration(70);
        }

        if (type == RANGED) {
            // TO DO
            System.out.println("Ranged Mob " + mobID);
            mob.setMaxVelocity(150.f);
            mob.setAcceleration(50);
        }

        if (type == MELEE) {
            // TO DO
            System.out.println("Melee Mob " + mobID);
            mob.setMaxVelocity(100.f);
            mob.setAcceleration(30);
        }

        return mob;
    }

    private int GetRandomNumberBetween(int start, int end) {
        Random r = new Random();
        return start + r.nextInt(end - start + 1);
    }

    private float GetRandomNumber(float range) {
        Random r = new Random();
        return r.nextFloat() * range;
    }

    private static <T extends Enum<?>> T pickRandomMobType(Class<T> mobType) {
        Random r = new Random();
        int pick = r.nextInt(mobType.getEnumConstants().length);
        return mobType.getEnumConstants()[pick];
    }

    @Override
    public void render(Graphics g, World world) {
        for (Mob mob : world.<Mob>getEntitiesByClass(Mob.class)) {
            Image texture;
            //Temporary, until we can distinguish between skeletons and knights.
            texture = textureSkeletonRanged;

            /*switch (mob.getMobType()){
                case MELEE:
                    texture = textureSkeletonMelee;
                    break;
                    
                default:
                    //Random selection of texture for the ranged mobs.
                    Random r = new Random(mob.getId().hashCode());
                    if(r.nextDouble() >= 0.51){
                        texture = textureSkeletonRanged;
                    }
                    else texture = textureKnightRanged;
                    break;
            }*/
            g.drawSprite(
                    /* Position    */mob.getPosition(),
                    /* Size        */ new Vector2(mob.getBounds().getWidth(), mob.getBounds().getHeight()),
                    /* InputStream */ texture.getInputStream(),
                    /* Rotation    */ mob.getRotation()
            );
            g.drawSprite(
                    /* Position    */mob.getPosition(),
                    /* Size        */ new Vector2(mob.getBounds().getWidth(), mob.getBounds().getHeight()),
                    /* InputStream */ mob.getAnimator().getTexture(),
                    /* Rotation    */ (float) Math.toDegrees(Math.atan2(mob.getVelocity().y, mob.getVelocity().x))
            );
            g.drawSprite(mob.getPosition().add(0, -2), new Vector2(64 * ((Mob) mob).getHealthData().getHealth() / ((Mob) mob).getHealthData().getStartHealth(), 5), health.getInputStream(), 0);
        }
    }
}