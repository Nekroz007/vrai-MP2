package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;

public class ICMazePlayer extends ICMazeActor implements Interactor {
    private final static int MOVE_DURATION = 8;
    private final Vector anchor = new Vector(0, 0);
    private final Vector anchor1 = new Vector(-0.5f, 0);

    private final Orientation[] orders = {DOWN, RIGHT, UP, LEFT};
    private final Orientation[] orders1 = {DOWN, UP, RIGHT, LEFT};
    private final OrientedAnimation animation;
    private final OrientedAnimation pickaxeAnimation;

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();
    private enum State {IDLE, MOVING, INTERACTING, ATTACKING_WITH_PICKAXE}
    private State state = State.IDLE;
    private Portal currentPortal = null;

    private final List<Integer> collectedKeys = new ArrayList<>();
    private boolean hasPickaxe = false;


    public ICMazePlayer(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.state = State.IDLE;
        this.animation = new OrientedAnimation("icmaze/player", 4, this, anchor, orders,
                4, 1, 2, 16, 32, true);
        this.pickaxeAnimation = new OrientedAnimation("icmaze/player.pickaxe",
               5, this ,
                anchor1 , orders1 , 4, 2, 2, 32, 32);
    }

    @Override
    public void acceptInteraction(ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button interactKey = keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.interact());
        Button itemKey = keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.pickaxe());

        if (state == State.IDLE && itemKey.isPressed() && hasPickaxe) {
            state = State.ATTACKING_WITH_PICKAXE;
            pickaxeAnimation.reset();
        }

        if (state == State.IDLE && interactKey.isPressed()) state = State.INTERACTING;
        if (state == State.INTERACTING && !interactKey.isDown()) state = State.IDLE;
        if (state == State.IDLE) {
            moveIfPressed(LEFT, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.left()));
            moveIfPressed(UP, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.up()));
            moveIfPressed(RIGHT, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.right()));
            moveIfPressed(DOWN, keyboard.get(KeyBindings.PLAYER_KEY_BINDINGS.down()));
        }

        if (state == State.ATTACKING_WITH_PICKAXE) {
            pickaxeAnimation.update(deltaTime);
            if (pickaxeAnimation.isCompleted()) {
                state = State.IDLE;
                pickaxeAnimation.reset();
            }
        } else {
            if (isDisplacementOccurs()) animation.update(deltaTime);
            else animation.reset();
        }

        if (isDisplacementOccurs()) animation.update(deltaTime);
        else animation.reset();
    }

    public void collectKey(int keyId) {
        if (!collectedKeys.contains(keyId)) collectedKeys.add(keyId);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public boolean takeCellSpace() { return true; }

    public Portal getCurrentPortal() { return currentPortal; }
    public void resetPortal() { currentPortal = null; }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() { return true; }
    @Override
    public boolean wantsViewInteraction() {
        return state == State.INTERACTING || state == State.ATTACKING_WITH_PICKAXE;
    }

    public void centerCamera() { getOwnerArea().setViewCandidate(this); }

    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(MOVE_DURATION);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // C'est ICI que l'animation est choisie
        if (state == State.ATTACKING_WITH_PICKAXE) {
            pickaxeAnimation.draw(canvas);
        } else {
            animation.draw(canvas);
        }
    }

    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (state == State.INTERACTING) {
                for (int keyId : collectedKeys) {
                    if (portal.tryUnlock(keyId)) break;
                }
            }
            if (isCellInteraction && portal.isOpen()) currentPortal = portal;
        }

        public void interactWith(Pickaxe pickaxe, boolean isCellInteractable) {
            if (isCellInteractable) {
                pickaxe.collect();
                hasPickaxe = true;
            }
        }

        public void interactWith(Heart heart, boolean isCellInteractable) {
            if (isCellInteractable) heart.collect();
        }

        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                collectKey(key.getKeyId());
                key.collect();
            }
        }

        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (state == State.ATTACKING_WITH_PICKAXE && !isCellInteraction) {
                rock.takeDamage(1);
            }
        }
    }
}
