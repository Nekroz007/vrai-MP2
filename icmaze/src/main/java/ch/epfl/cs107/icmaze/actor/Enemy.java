package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Enemy extends ICMazeActor implements Interactor, Interactable {

    private int hp;
    private boolean dead;
    private Animation vanishAnimation;
    private static final int ANIMATION_DURATION = 24;

    protected Enemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHp) {
        super(area, orientation, position, maxHp, false);
        this.hp = maxHp;
        this.dead = false;
    }

    public int getHp() {
        return hp;
    }

    /**
     * Méthode pour infliger des dégâts à l'ennemi.
     * Appelle die() si PV <= 0.
     */
    public void loseHp(int amount) {
        if (!dead) {
            hp -= amount;
            if (hp <= 0) {
                hp = 0;
                die();
            }
        }
    }

    public boolean isDead() {
        return dead;
    }

    protected void die() {
        if (!dead) {
            dead = true;
            vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this, 32, 32,
                    new Vector(-0.5f, 0f), ANIMATION_DURATION / 7, false);
        }
    }

    @Override
    public boolean takeCellSpace() {
        return !dead;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (dead && vanishAnimation != null) {
            vanishAnimation.update(deltaTime);
            if (vanishAnimation.isCompleted()) {
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (dead && vanishAnimation != null) {
            vanishAnimation.draw(canvas);
        } else if (!dead) {
            super.draw(canvas);
        }
    }

    @Override
    public boolean wantsCellInteraction() {
        return !dead;
    }

    @Override
    public boolean wantsViewInteraction() {
        return !dead;
    }
}