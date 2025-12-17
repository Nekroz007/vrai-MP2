package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.LogicKey;
import ch.epfl.cs107.icmaze.area.AreaLogic;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.math.random.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Boss extends Enemy implements Interactable {

    private static final int MAX_HEALTH = 3;
    private static final int ANIMATION_DURATION = 12;
    private static final int SHOOTING_INTERVAL = 60; // Vitesse de tir

    private final OrientedAnimation animation;
    private boolean isActive;
    private int shootingTimer;
    private final BossInteractionHandler handler;
    private final AreaLogic deadLogic = new AreaLogic();

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HEALTH);
        this.isActive = false;
        this.shootingTimer = SHOOTING_INTERVAL;
        this.handler = new BossInteractionHandler();

        Vector anchor = new Vector(-0.5f, 0);
        Orientation[] orders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
        this.animation = new OrientedAnimation("icmaze/boss", ANIMATION_DURATION / 4,
                this, anchor, orders, 3, 2, 2, 32, 32, true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isDead()) return;

        animation.update(deltaTime);
        if (isActive) {
            shootingTimer--;
            if (shootingTimer <= 0) {
                shoot();
                shootingTimer = SHOOTING_INTERVAL;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead()) {
            animation.draw(canvas);
            super.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }

    private void shoot() {
        int width = getOwnerArea().getWidth();
        int height = getOwnerArea().getHeight();
        DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();
        Orientation bossOri = getOrientation();

        int range;
        if (bossOri == Orientation.UP || bossOri == Orientation.DOWN) {
            range = width;
        } else {
            range = height;
        }

        int safetyGap = 1 + ch.epfl.cs107.play.math.random.RandomGenerator.getInstance().nextInt(range - 2);
        for (int i = 1; i < range - 1; i++) {
            if (i == safetyGap) continue;

            DiscreteCoordinates projPos = null;

            if (bossOri == Orientation.UP) {
                projPos = new DiscreteCoordinates(i, currentPos.y + 1);
            } else if (bossOri == Orientation.DOWN) {
                projPos = new DiscreteCoordinates(i, currentPos.y - 1);
            } else if (bossOri == Orientation.RIGHT) {
                projPos = new DiscreteCoordinates(currentPos.x + 1, i);
            } else if (bossOri == Orientation.LEFT) {
                projPos = new DiscreteCoordinates(currentPos.x - 1, i);
            }

            if (projPos != null &&
                    projPos.x > 0 && projPos.x < width - 1 &&
                    projPos.y > 0 && projPos.y < height - 1) {

                FireProjectile projectile = new FireProjectile(getOwnerArea(), bossOri, projPos);

                if (getOwnerArea().canEnterAreaCells(projectile, java.util.Collections.singletonList(projPos))) {
                    getOwnerArea().registerActor(projectile);
                }
            }
        }
    }

    public void receiveAttack() {
        if (isDead()) return; // Sécurité supplémentaire

        if (!isActive) {
            isActive = true;
            teleport(); // On se téléporte quand on se réveille
        } else {
            decreaseHealth(1);
            // CORRECTION : On ne se téléporte que si on est encore vivant
            if (!isDead()) {
                teleport();
            }
        }
    }

    private void teleport() {
        int w = getOwnerArea().getWidth();
        int h = getOwnerArea().getHeight();

        List<DiscreteCoordinates> candidates = new ArrayList<>();
        candidates.add(new DiscreteCoordinates(w / 2, 1));
        candidates.add(new DiscreteCoordinates(w / 2, h - 2));
        candidates.add(new DiscreteCoordinates(1, h / 2));
        candidates.add(new DiscreteCoordinates(w - 2, h / 2));

        Collections.shuffle(candidates, RandomGenerator.getInstance());

        for (DiscreteCoordinates coord : candidates) {
            if (!coord.equals(getCurrentMainCellCoordinates())) {
                getOwnerArea().leaveAreaCells(this, getCurrentCells());
                setCurrentPosition(coord.toVector());
                resetMotion();
                getOwnerArea().enterAreaCells(this, getCurrentCells());

                lookAtCenter(w, h, coord);
                break;
            }
        }
    }

    private void lookAtCenter(int w, int h, DiscreteCoordinates pos) {
        if (pos.x <= 1) orientate(Orientation.RIGHT);
        else if (pos.x >= w - 2) orientate(Orientation.LEFT);
        else if (pos.y <= 1) orientate(Orientation.UP);
        else orientate(Orientation.DOWN);
    }

    @Override
    protected void die() {
        super.die();

        deadLogic.setActive(true);

        getOwnerArea().registerActor(new LogicKey(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates(), -1));
    }



    @Override
    public boolean isViewInteractable() {
        return !isDead();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public boolean wantsViewInteraction() {
        return !isDead();
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
    public AreaLogic getDeadLogic() {
        return deadLogic;
    }

    private class BossInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction) player.decreaseHealth(1);
        }
    }
}