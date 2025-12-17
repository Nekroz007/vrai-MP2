package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class Projectile extends MovableAreaEntity implements Interactor {
    protected static final int MAX_DISTANCE = 7;
    protected static final int SPEED = 1;

    private int remainingDistance;
    private boolean stopped;
    private static final int MOVE_DURATION = 10;

    protected Projectile(Area area, Orientation orientation, DiscreteCoordinates position) {
        super (area, orientation, position);
        remainingDistance = MAX_DISTANCE;
        stopped = false;
    }

    @Override
    public void update (float deltaTime) {
        super.update(deltaTime);

        if (stopped) {
            getOwnerArea().unregisterActor(this);
            return;
        }

        if (remainingDistance <= 0) {
            getOwnerArea().unregisterActor(this);
            return;
        }

        if (!isDisplacementOccurs()){
            move(MOVE_DURATION / SPEED);
            remainingDistance--;
        }
    }

    protected void stop(){
        stopped = true;
    }

    @Override
    public boolean wantsCellInteraction(){
        return !stopped && remainingDistance > 0;
    }

    @Override
    public boolean wantsViewInteraction(){
        return false;
    }

    @Override
    public boolean isCellInteractable(){
        return false;
    }

    @Override
    public boolean isViewInteractable(){
        return false;
    }
    @Override
    public boolean takeCellSpace(){
        return false;
    }
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells(){
        return Collections.emptyList();
    }

    public List<DiscreteCoordinates> getCurrentCells(){
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}
