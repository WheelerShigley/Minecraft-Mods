package me.wheelershigley.live_catch;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class EntityLink {
    private static final ArrayList<EntityLink> LinkedEntities = new ArrayList<>();

    private final Entity parent, child;
    private final Vec3d start, end;

    public static final int MAXIMUM_TICK_COUNT = 30; //1.5seconds -> 1.5*20 = 30
    private int accumulator = 0;

    public EntityLink(Entity parent, Entity child) {
        this.parent = parent;
        this.child = child;

        this.start = new Vec3d( child.getX(),  child.getY(),  child.getZ()  );
        this.end =   new Vec3d( parent.getX(), parent.getY(), parent.getZ() );

        EntityLink.AddToList(this);
    }

    private static void AddToList(EntityLink link) {
        if( !LinkedEntities.contains(link) ) {
            LinkedEntities.add(link);
        }
    }

    private static void RemoveFromList(EntityLink link) {
        LinkedEntities.remove(link);
    }

    @Deprecated
    public static void removeEntriesFromListWithParent(Entity parent) {
        ArrayList<EntityLink> completedLinks = new ArrayList<>();
        for(EntityLink linkedEntity : LinkedEntities) {
            if( linkedEntity.parent.equals(parent) ) {
                completedLinks.add(linkedEntity);
            }
        }
        for(EntityLink completedLink : completedLinks) {
            RemoveFromList(completedLink);
        }
    }

    public static void tickAll() {
        ArrayList<EntityLink> completedLinks = new ArrayList<>();
        for(EntityLink linkedEntity : LinkedEntities) {
            EntityLink deallocation = linkedEntity.tick();
            if(deallocation != null) {
                completedLinks.add(deallocation);
            }
        }
        for(EntityLink completedLink : completedLinks) {
            completedLink.child.setVelocity(0.0, 0.0, 0.0);
            RemoveFromList(completedLink);
        }
    }

    private EntityLink tick() {
//        LiveCatch.LOGGER.info(
//            "\n"+ Integer.toString( this.hashCode() ) +":\n"+
//            "\t"+ Integer.toString(MAXIMUM_TICK_COUNT) +" <=? "+ Integer.toString(accumulator) +" -> "+ Boolean.toString(MAXIMUM_TICK_COUNT <= accumulator)
//        );
//        LiveCatch.LOGGER.info(
//            "\n"+ Integer.toString( this.hashCode() )+":\n"+
//            "\tparent: ("+ parent.getX() +", "+ parent.getY() +", "+ parent.getZ() +")\n"+
//            "\tchild: ("+ child.getX() +", "+ child.getY() +", "+ child.getZ() +")"
//        );

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double dz = end.getZ() - start.getZ();
        //If parent and child are close enough, remove link
        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if(distance <= 1.0) {
            return this;
        }
        child.setVelocity(dx/10.0, dy/10.0, dz/10.0);
        //If the fish is physically restricted, remove link
        if(    child.verticalCollision || child.horizontalCollision
            || child.collidedSoftly    || child.groundCollision
        ) {
//            LiveCatch.LOGGER.info(
//                "\n"+ Integer.toString( this.hashCode() ) +":\n"+
//                "\tsoftly: "+ Boolean.toString(child.collidedSoftly) +"\n"+
//                "\tground: "+ Boolean.toString(child.groundCollision) +"\n"+
//                "\tvertical: "+ Boolean.toString(child.verticalCollision) +"\n"+
//                "\thorizontal: "+ Boolean.toString(child.horizontalCollision)
//            );
            return this;
        }

        ++accumulator;
        //If the link has lasted too long, remove link
        if(MAXIMUM_TICK_COUNT <= accumulator) {
            child.setVelocity(dx, dy, dz);
            return this;
        }

        return null;
    }
}
