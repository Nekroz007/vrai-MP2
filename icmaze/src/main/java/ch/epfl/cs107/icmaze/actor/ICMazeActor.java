package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class ICMazeActor extends MovableAreaEntity{

    private Health health;
    protected float immunityTimer = 0;
    private float MAX_IMMUNITY = 3f;

    public ICMazeActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates, int maxHealth, boolean friendly) {
        super(owner, orientation, coordinates);
        health = new Health(this, Transform.I.translated(0, 1.75f), maxHealth, true);
    }

    @Override
    public boolean takeCellSpace() { return false; }

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

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // check if immunity timer is on and reduce it
        if (immunityTimer > 0) {
            immunityTimer = Math.max(immunityTimer - deltaTime, 0);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (immunityTimer > 0) health.draw(canvas);
    }

    // TODO: what if some entity removes more than MaxHp ? although this shouldn't be possible
    // TODO: need to think of a defensive way of handling this
    protected void decreaseHealth(int amount) {
        health.decrease(amount);
    }

    protected void increaseHealth(int amount) {
        health.increase(amount);
    }

    public void resetHealth() {
        health.resetHealth();
    }

    public boolean isDead() {
        return health.isOff();
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }
}
