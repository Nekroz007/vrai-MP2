package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.actor.collectable.Key;
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

public class Boss extends Enemy {

    private static final int MAX_HEALTH = 5;
    private static final int ANIMATION_DURATION = 12; // Valeur adaptée selon ton snippetFromBack (PDF)
    private static final int SHOOTING_INTERVAL = 40;

    private final OrientedAnimation animation;
    private boolean isActive;
    private int shootingTimer;
    private final BossInteractionHandler handler;

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HEALTH);
        this.isActive = false;
        this.shootingTimer = SHOOTING_INTERVAL;
        this.handler = new BossInteractionHandler();

        Vector anchor = new Vector(-0.5f, 0);
        Orientation[] orders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
        // 3 frames, 4 orientations, using ANIMATION_DURATION
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
        }
    }

    private void shoot() {
        int range;
        int width = getOwnerArea().getWidth();
        int height = getOwnerArea().getHeight();

        if (getOrientation() == Orientation.UP || getOrientation() == Orientation.DOWN) {
            range = width;
        } else {
            range = height;
        }

        // Utilisation du RandomGenerator correct
        int safetyGap = RandomGenerator.getInstance().nextInt(range);

        for (int i = 0; i < range; i++) {
            if (i == safetyGap) continue;

            DiscreteCoordinates projPos = null;
            DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();

            if (getOrientation() == Orientation.UP) {
                projPos = new DiscreteCoordinates(i, currentPos.y + 1);
            } else if (getOrientation() == Orientation.DOWN) {
                projPos = new DiscreteCoordinates(i, currentPos.y - 1);
            } else if (getOrientation() == Orientation.RIGHT) {
                projPos = new DiscreteCoordinates(currentPos.x + 1, i);
            } else if (getOrientation() == Orientation.LEFT) {
                projPos = new DiscreteCoordinates(currentPos.x - 1, i);
            }

            // Vérification manuelle des bornes car Area.contains n'existe pas
            if (projPos != null &&
                    projPos.x >= 0 && projPos.x < width &&
                    projPos.y >= 0 && projPos.y < height) {

                FireProjectile projectile = new FireProjectile(getOwnerArea(), getOrientation(), projPos);
                // On enregistre le projectile pour qu'il apparaisse
                getOwnerArea().registerActor(projectile);
            }
        }
    }

    public void receiveAttack() {
        if (!isActive) {
            isActive = true;
        } else {
            decreaseHealth(1);
        }
        teleport();
    }

    private void teleport() {
        int width = getOwnerArea().getWidth();
        int height = getOwnerArea().getHeight();

        List<DiscreteCoordinates> candidates = new ArrayList<>();
        // Ajout des positions candidates (milieux des murs)
        candidates.add(new DiscreteCoordinates(width / 2, 1));
        candidates.add(new DiscreteCoordinates(width / 2, height - 2));
        candidates.add(new DiscreteCoordinates(1, height / 2));
        candidates.add(new DiscreteCoordinates(width - 2, height / 2));

        Collections.shuffle(candidates, RandomGenerator.getInstance());

        for (DiscreteCoordinates coord : candidates) {
            if (!coord.equals(getCurrentMainCellCoordinates())) {
                // Gestion manuelle du déplacement car MovableAreaEntity gère mal le teleport instantané sans reset
                getOwnerArea().leaveAreaCells(this, getCurrentCells()); // Utilisation de getCurrentCells()
                setCurrentPosition(coord.toVector());
                resetMotion(); // Important pour annuler tout mouvement en cours
                getOwnerArea().enterAreaCells(this, getCurrentCells());
                break;
            }
        }
    }

    @Override
    protected void die() {
        super.die();
        // Spawn de la clé finale à la mort (ID -1)
        Key key = new Key(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates(), -1);
        getOwnerArea().registerActor(key);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        // Par défaut, le champ de vision est nul ou la case devant lui.
        // Pour compiler simplement sans changer la logique :
        return Collections.emptyList();
    }
    @Override
    public boolean wantsCellInteraction() {
        return !isDead();
    }

    @Override
    public boolean wantsViewInteraction() {
        // Enemy met ça à true, on le garde si besoin, ou false si le boss est passif à distance
        return !isDead();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class BossInteractionHandler implements ICMazeInteractionVisitor {
        // Ajouter ici les interactions spécifiques si le Boss doit réagir à quelque chose qu'il touche
    }
}