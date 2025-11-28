package ch.epfl.cs107.icmaze.actor;


import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;

import static ch.epfl.cs107.play.math.Orientation.*;

public class ICMazePlayer extends ICMazeActor{
    private final static int MOVE_DURATION = 8;
    final Vector anchor = new Vector(0,0);
    final Orientation[] orders = {DOWN,RIGHT,UP,LEFT};
    private final OrientedAnimation animation;
    private playerStates state;

    public ICMazePlayer(Area area,Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.state = playerStates.IDLE;
        this.animation = new OrientedAnimation("icmaze/player", 4,this, anchor, orders,
                4, 1, 2, 16, 32, true);
    }

    public enum playerStates {
        IDLE,
        INTERACTING,
    }

    public void update(float deltaTime) {
        if (state == playerStates.IDLE) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            moveIfPressed(LEFT, keyboard.get(Keyboard.LEFT));
            moveIfPressed(UP, keyboard.get(Keyboard.UP));
            moveIfPressed(RIGHT, keyboard.get(Keyboard.RIGHT));
            moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    public boolean takeCellSpace() {
        return true;
    }


    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }
}
