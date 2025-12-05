package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class ICMazeCollectable extends CollectableAreaEntity {

    public ICMazeCollectable(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
    }

     @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}