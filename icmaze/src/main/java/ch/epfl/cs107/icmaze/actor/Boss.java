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
    private static final int ANIMATION_DURATION = 12;
    private static final int SHOOTING_INTERVAL = 40;

    private final OrientedAnimation animation;
    private boolean isActive;
    private int shootingTimer;
    private final BossInteractionHandler handler;

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HEALTH);
        this.isActive = false; // Le boss commence immobile/inactif
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

        // Ne tire que si le combat a commencé
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

        // Détermine la portée (largeur ou hauteur selon l'orientation)
        if (getOrientation() == Orientation.UP || getOrientation() == Orientation.DOWN) {
            range = width;
        } else {
            range = height;
        }

        // On laisse une case "sûre" au hasard pour que le joueur puisse esquiver
        int safetyGap = RandomGenerator.getInstance().nextInt(range);

        for (int i = 0; i < range; i++) {
            if (i == safetyGap) continue;

            DiscreteCoordinates projPos = null;
            DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();

            // Calcule la position de départ du projectile (juste devant le boss)
            if (getOrientation() == Orientation.UP) {
                projPos = new DiscreteCoordinates(i, currentPos.y + 1);
            } else if (getOrientation() == Orientation.DOWN) {
                projPos = new DiscreteCoordinates(i, currentPos.y - 1);
            } else if (getOrientation() == Orientation.RIGHT) {
                projPos = new DiscreteCoordinates(currentPos.x + 1, i);
            } else if (getOrientation() == Orientation.LEFT) {
                projPos = new DiscreteCoordinates(currentPos.x - 1, i);
            }

            // Vérification des bornes pour éviter de faire spawn hors map
            if (projPos != null && projPos.x >= 0 && projPos.x < width && projPos.y >= 0 && projPos.y < height) {
                // Création du projectile avec l'orientation du boss
                FireProjectile projectile = new FireProjectile(getOwnerArea(), getOrientation(), projPos);
                getOwnerArea().registerActor(projectile);
            }
        }
    }

    // Gestion de l'attaque reçue (Activation ou Dégâts)
    public void receiveAttack() {
        if (!isActive) {
            // Première attaque : activation seulement (pas de dégâts)
            isActive = true;
        } else {
            // Attaques suivantes : dégâts
            decreaseHealth(1);
        }
        // Dans tous les cas (réveil ou dégât), il se téléporte
        teleport();
    }

    private void teleport() {
        int w = getOwnerArea().getWidth();
        int h = getOwnerArea().getHeight();

        List<DiscreteCoordinates> candidates = new ArrayList<>();
        // Positions de la Figure 11 (milieux des murs intérieurs)
        candidates.add(new DiscreteCoordinates(w / 2, 1));       // Sud
        candidates.add(new DiscreteCoordinates(w / 2, h - 2));   // Nord
        candidates.add(new DiscreteCoordinates(1, h / 2));       // Ouest
        candidates.add(new DiscreteCoordinates(w - 2, h / 2));   // Est

        Collections.shuffle(candidates, RandomGenerator.getInstance());

        for (DiscreteCoordinates coord : candidates) {
            // On évite de se téléporter sur sa position actuelle
            if (!coord.equals(getCurrentMainCellCoordinates())) {
                getOwnerArea().leaveAreaCells(this, getCurrentCells());
                setCurrentPosition(coord.toVector());
                resetMotion();
                getOwnerArea().enterAreaCells(this, getCurrentCells());

                // IMPORTANT : Le boss doit se tourner vers le centre pour tirer
                lookAtCenter(w, h, coord);
                break;
            }
        }
    }

    private void lookAtCenter(int w, int h, DiscreteCoordinates pos) {
        // Logique simple : si on est sur un bord, on regarde vers l'opposé
        if (pos.x == 1) orientate(Orientation.RIGHT);
        else if (pos.x >= w - 2) orientate(Orientation.LEFT);
        else if (pos.y == 1) orientate(Orientation.UP);
        else orientate(Orientation.DOWN);
    }

    @Override
    protected void die() {
        super.die();
        // Drop la clé finale (ID -1) pour sortir
        Key key = new Key(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates(), -1);
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
    public boolean wantsCellInteraction() { return !isDead(); }

    @Override
    public boolean wantsViewInteraction() { return !isDead(); }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class BossInteractionHandler implements ICMazeInteractionVisitor {
            @Override
            public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
                // Si le Boss touche le joueur (contact), le joueur perd de la vie
                if (isCellInteraction) {
                    player.decreaseHealth(1);
                }
            }
    }
}