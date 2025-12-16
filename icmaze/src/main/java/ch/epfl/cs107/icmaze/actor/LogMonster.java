package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.LinkedList;
import java.util.Queue;

public class LogMonster extends PathFinderEnemy {
    private static final int MAX_HP = 5;
    private static final int ANIMATION_DURATION = 30;
    private static final int DAMAGE = 1;

    private static final float ORIENTATION_COOLDOWN = 0.75f;
    private static final float STATE_COOLDOWN = 3.f;
    private static final int MOVE_DURATION = 10;

    private final int difficulty;

    public enum State {
        SLEEPING,
        RANDOM,
        CHASING
    }

    private State state;
    private ICMazePlayer target;

    private OrientedAnimation chasingAnimation;
    private OrientedAnimation randomAnimation;
    private OrientedAnimation sleepingAnimation;

    private final Cooldown orientationCooldown = new Cooldown(ORIENTATION_COOLDOWN);
    private final Cooldown stateCooldown = new Cooldown(STATE_COOLDOWN);

    private final LogMonsterInteractionHandler handler = new LogMonsterInteractionHandler();

    private Path graphicPath;

    /**
     * Constructeur complet
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, State initialState) {
        super(area, orientation, position, MAX_HP);
        this.state = initialState;
        this.difficulty = Difficulty.HARDEST;
        this.target = null;
        createAnimations();
    }
    /**
     * Permet aux autres entités de connaître les dégâts de ce monstre
     */
    public int getDamage() {
        return DAMAGE;
    }

    private void createAnimations() {
        Orientation[] orders = {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
        chasingAnimation = new OrientedAnimation("icmaze/logMonster", ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), orders, 4, 2, 2, 32, 32, true);

        randomAnimation = new OrientedAnimation("icmaze/logMonster_random", ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), orders, 4, 2, 2, 32, 32, true);

        Orientation[] sleepOrders = {Orientation.DOWN, Orientation.LEFT, Orientation.UP, Orientation.RIGHT};
        sleepingAnimation = new OrientedAnimation("icmaze/logMonster.sleeping", ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), sleepOrders, 1, 2, 2, 32, 32, true);
    }

    public boolean isSleeping() {
        return state == State.SLEEPING;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isDead()) return;

        switch (state) {
            case SLEEPING -> sleepingAnimation.update(deltaTime);
            case RANDOM -> randomAnimation.update(deltaTime);
            case CHASING -> chasingAnimation.update(deltaTime);
        }
        switch (state) {
            case SLEEPING:
                if (orientationCooldown.ready(deltaTime)) {
                    orientate(getOrientation().hisLeft());
                }
                if (stateCooldown.ready(deltaTime)) {
                    if (RandomGenerator.rng.nextDouble() < getTransitionProbability()) {
                        state = State.RANDOM;
                    }
                }
                break;

            case RANDOM:
                if (orientationCooldown.ready(deltaTime) && !isDisplacementOccurs()) {
                    Orientation randomDir = Orientation.values()[RandomGenerator.rng.nextInt(4)];
                    orientate(randomDir);
                    move(MOVE_DURATION);
                }
                if (stateCooldown.ready(deltaTime) && target != null) {
                    if (RandomGenerator.rng.nextDouble() < getTransitionProbability()) {
                        state = State.CHASING;
                    }
                }
                break;

            case CHASING:
                if (target == null) {
                    state = State.RANDOM;
                    break;
                }
                if (orientationCooldown.ready(deltaTime) && !isDisplacementOccurs()) {
                    Orientation next = getNextOrientation();
                    if (next != null) {
                        orientate(next);
                        move(MOVE_DURATION);
                    }
                }
                if (stateCooldown.ready(deltaTime)) {
                    if (RandomGenerator.rng.nextDouble() > getTransitionProbability()) {
                        state = State.SLEEPING;
                    }
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (isDead()) {
            super.draw(canvas);
            return;
        }

        if (graphicPath != null && state == State.CHASING) {
            graphicPath.draw(canvas);
        }

        switch (state) {
            case SLEEPING -> sleepingAnimation.draw(canvas);
            case RANDOM -> randomAnimation.draw(canvas);
            case CHASING -> chasingAnimation.draw(canvas);
        }
    }

    @Override
    protected Orientation getNextOrientation() {
        if (target == null) {
            graphicPath = null;
            return null;
        }
        Queue<Orientation> path = ((ICMazeArea) getOwnerArea()).shortestPath(
                getCurrentMainCellCoordinates(),
                target.getCurrentMainCellCoordinates()
        );
        if (path == null || path.isEmpty()) {
            graphicPath = null;
            return null;
        }
        graphicPath = new Path(getPosition(), new LinkedList<>(path));

        return path.poll();
    }

    private double getTransitionProbability() {
        return (double) Difficulty.HARDEST / difficulty;
    }

    @Override
    protected int getViewRadius() {
        return 5;
    }

    @Override
    public boolean wantsViewInteraction() {
        return !isSleeping();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * Gestionnaire des interactions du LogMonster
     */
    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isSleeping()) return;
            if (isCellInteraction) {
                player.decreaseHealth(DAMAGE);
            }
            else {
                target = player;

                DiscreteCoordinates myPos = getCurrentMainCellCoordinates();
                DiscreteCoordinates facePos = myPos.jump(getOrientation().toVector());

                if (player.getCurrentMainCellCoordinates().equals(facePos)) {
                    player.decreaseHealth(DAMAGE);
                }
            }
        }
    }
}