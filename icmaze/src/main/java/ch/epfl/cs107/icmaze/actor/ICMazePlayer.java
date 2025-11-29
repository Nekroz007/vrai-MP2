package ch.epfl.cs107.icmaze.actor;


import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;

public class ICMazePlayer extends ICMazeActor implements Interactor {
    private final static int MOVE_DURATION = 8;
    final Vector anchor = new Vector(0,0);
    final Orientation[] orders = {DOWN,RIGHT,UP,LEFT};
    private final OrientedAnimation animation;
    private playerStates state;
    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

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
        super.update(deltaTime);
        if (state == playerStates.IDLE) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            moveIfPressed(Orientation.LEFT, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.left()));
            moveIfPressed(Orientation.UP, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.up()));
            moveIfPressed(Orientation.RIGHT, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.right()));
            moveIfPressed(Orientation.DOWN, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.down()));
        }

        if (isDisplacementOccurs()) {
            animation.update(deltaTime);
        } else {
            animation.reset();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }
    @Override
    public boolean takeCellSpace() {
        return true;
    }
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    public boolean wantsCellInteraction() {
        return true;
    }
    public boolean wantsViewInteraction() {
        if (state == playerStates.INTERACTING) {
            return true;
        } else {
            return false;
        }
    }

    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }



    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {
        public void interactWith(Pickaxe pickaxe, boolean isCellInteractable) {
            if (isCellInteractable) {
                pickaxe.collect();
                System.out.println("La pioche a été ramasée !");
            }
        }

        public void interactWith(Heart heart, boolean isCellInteractable) {
            if (isCellInteractable) {
                heart.collect();
                System.out.println("Un coeur a été recupéré !");
            }
        }
    }
}
