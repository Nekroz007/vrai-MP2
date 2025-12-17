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

public class Boss extends Enemy implements Interactable {

    private static final int MAX_HEALTH = 5;
    private static final int ANIMATION_DURATION = 12;
    private static final int SHOOTING_INTERVAL = 30; // Vitesse de tir

    private final OrientedAnimation animation;
    private boolean isActive;
    private int shootingTimer;
    private final BossInteractionHandler handler;

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HEALTH);
        this.isActive = false; // 2. Immobile au début
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

        // 4. Une fois activé, tire régulièrement
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
            super.draw(canvas); // Affiche la barre de vie
        }
    }

    // 5. Barrage de projectiles avec un trou
    private void shoot() {
        int range;
        int width = getOwnerArea().getWidth();
        int height = getOwnerArea().getHeight();

        if (getOrientation() == Orientation.UP || getOrientation() == Orientation.DOWN) {
            range = width;
        } else {
            range = height;
        }

        int safetyGap = ch.epfl.cs107.play.math.random.RandomGenerator.getInstance().nextInt(range);

        for (int i = 0; i < range; i++) {
            if (i == safetyGap) continue;

            DiscreteCoordinates projPos = null;
            DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();

            if (getOrientation() == Orientation.UP) projPos = new DiscreteCoordinates(i, currentPos.y + 1);
            else if (getOrientation() == Orientation.DOWN) projPos = new DiscreteCoordinates(i, currentPos.y - 1);
            else if (getOrientation() == Orientation.RIGHT) projPos = new DiscreteCoordinates(currentPos.x + 1, i);
            else if (getOrientation() == Orientation.LEFT) projPos = new DiscreteCoordinates(currentPos.x - 1, i);

            // REMPLACEMENT DE .CONTAINS PAR UNE VÉRIFICATION MANUELLE
            if (projPos != null && projPos.x >= 0 && projPos.x < width && projPos.y >= 0 && projPos.y < height) {
                getOwnerArea().registerActor(new FireProjectile(getOwnerArea(), getOrientation(), projPos));
            }
        }
    }

    // 3. Activation et Téléportation
    public void receiveAttack() {
        if (!isActive) {
            isActive = true; // S'active à la première attaque
        } else {
            decreaseHealth(1); // Subit des dégâts ensuite
        }
        teleport();
    }

    private void teleport() {
        int w = getOwnerArea().getWidth();
        int h = getOwnerArea().getHeight();

        List<DiscreteCoordinates> candidates = new ArrayList<>();
        // Positions Figure 11 (milieux des murs)
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

                // IMPORTANT : Regarder vers le centre pour tirer
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
        // 8. Lâche la clé -1
        getOwnerArea().registerActor(new Key(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates(), -1));
    }

    // --- Gestion des Interactions (Visitor) ---

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public boolean wantsCellInteraction() { return !isDead(); }
    @Override
    public boolean wantsViewInteraction() { return !isDead(); } // Important pour le coup d'épée
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        // Par défaut, le champ de vision est nul ou la case devant lui.
        // Pour compiler simplement sans changer la logique :
        return Collections.emptyList();
    }
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class BossInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction) player.decreaseHealth(1);
        }
    }
}