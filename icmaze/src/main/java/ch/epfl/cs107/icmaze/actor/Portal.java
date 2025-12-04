package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Portal extends AreaEntity implements Interactable {

    public enum State { OPEN, LOCKED, INVISIBLE }

    private String destinationAreaName;
    private DiscreteCoordinates destinationCoords;
    private int keyId;
    private State state;
    private Sprite sprite;

    public static final int NO_KEY_ID = Integer.MIN_VALUE;

    public Portal(Area area, Orientation orientation, DiscreteCoordinates position, int keyId, State state) {
        super(area, orientation, position);
        this.keyId = keyId;
        this.state = state;
        createSprite();
    }

    private void createSprite() {
        switch (state) {
            case INVISIBLE:
                sprite = new Sprite("icmaze/invisibleDoor_" + getOrientation().ordinal(),
                        (getOrientation().ordinal()+1)%2+1, getOrientation().ordinal()%2+1, this);
                break;
            case LOCKED:
                sprite = new Sprite("icmaze/chained_wood_" + getOrientation().ordinal(),
                        (getOrientation().ordinal()+1)%2+1, getOrientation().ordinal()%2+1, this);
                break;
            case OPEN:
            default:
                sprite = null;
                break;
        }
    }

    @Override
    public boolean isCellInteractable() { return true; }
    @Override
    public boolean isViewInteractable() { return true; }
    @Override
    public boolean takeCellSpace() { return state != State.OPEN; }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        Vector jump = new Vector((getOrientation().ordinal()+1)%2, getOrientation().ordinal()%2);
        return List.of(coord, coord.jump(jump));
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {}
    @Override
    public void onEntering(List<DiscreteCoordinates> coordinates) {}

    public boolean tryUnlock(int playerKeyId) {
        if (keyId == NO_KEY_ID) return false;
        if (playerKeyId == keyId) {
            state = State.OPEN;
            createSprite();
            return true;
        }
        return false;
    }

    public boolean isOpen() { return state == State.OPEN; }
    public boolean isLocked() { return state == State.LOCKED; }

    public String getDestinationAreaName() { return destinationAreaName; }
    public DiscreteCoordinates getDestinationCoords() { return destinationCoords; }

    public void setDestination(String areaName, DiscreteCoordinates coords) {
        this.destinationAreaName = areaName;
        this.destinationCoords = coords;
    }

    public void setState(State newState) {
        this.state = newState;
        createSprite();
    }

    public void setKeyId(int keyId) { this.keyId = keyId; }

    @Override
    public void draw(Canvas canvas) {
        if (sprite != null) sprite.draw(canvas);
    }

    @Override
    public void acceptInteraction(ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor) {
            ((ICMazeInteractionVisitor)v).interactWith(this, isCellInteraction);
        }
    }
}
