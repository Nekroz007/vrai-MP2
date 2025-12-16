package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.LinkedList;
import java.util.Queue;

public class LogMonster extends PathFinderEnemy implements Interactor, Interactable {
    private static final int ANIMATION_DURATION = 30;
    private static final int MAX_HP = 10; // maxHP à changer en fonction de ce qui est demandé

    public enum State{
        SLEEPING,
        RANDOM,
        CHASING
    }
    private State state;
    private OrientedAnimation chasingAnimation;
    private OrientedAnimation randomAnimation;
    private OrientedAnimation sleepingAnimation;
    private ICMazePlayer target;
    private final LogMonsterInteractionHandler handler = new LogMonsterInteractionHandler();
    private static final float ORIENTATION_COOLDOWN = 0.75f;
    private static final float STATE_COOLDOWN = 3.f;
    private static final int MOVE_DURATION = 10;
    private int difficulty;
    private Cooldown orientationCooldown = new Cooldown(ORIENTATION_COOLDOWN);
    private Cooldown stateCooldown = new Cooldown(STATE_COOLDOWN);
    private Path graphicPath;

    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, State initialState){
        super (area, orientation, position, MAX_HP);
        this.state = initialState;
        this.target = null;

        createAnimation();
    }
    public boolean isSleeping(){
        return state == State.SLEEPING;
    }
    private void createAnimation(){
        // En mode déplacement ciblé
        Orientation[] orders = new Orientation []{
                Orientation.DOWN,
                Orientation.UP,
                Orientation.RIGHT,
                Orientation.LEFT
        };
        chasingAnimation = new OrientedAnimation("icmaze/logMonster",

                ANIMATION_DURATION/3, this ,
                new Vector(-0.5f, 0.25f), orders ,
                4, 2, 2, 32, 32, true);

        // En mode déplacement aléatoire
        orders = new Orientation []{
                Orientation.DOWN,
                Orientation.UP,
                Orientation.RIGHT,
                Orientation.LEFT
        };
        randomAnimation = new OrientedAnimation("icmaze/logMonster_random",
                ANIMATION_DURATION/3, this ,
                new Vector(-0.5f, 0.25f), orders ,
                4, 2, 2, 32, 32, true);


        // En mode endormi
        orders = new Orientation []{
                Orientation.DOWN ,
                Orientation.LEFT,
                Orientation.UP,
                Orientation.RIGHT
        };
        sleepingAnimation = new OrientedAnimation("icmaze/logMonster.sleeping",
                ANIMATION_DURATION/3, this ,
                new Vector(-0.5f, 0.25f), orders ,
                1, 2, 2, 32, 32, true);
    }

    @Override
    public int getMaxHp(){
        return MAX_HP;
    }
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        if (state != State.SLEEPING){
            chasingAnimation.update(deltaTime);
            randomAnimation.update(deltaTime);
        } else{
            sleepingAnimation.update(deltaTime);
        }
        switch (state){
            case SLEEPING:
                if (orientationCooldown.ready(deltaTime)){
                    orientate(getOrientation().hisLeft());
                }
                if (stateCooldown.ready(deltaTime)){
                    if (Math.random() < getTransitionProbability()){
                        state = State.RANDOM;
                    }
                }
                sleepingAnimation.update (deltaTime);
                break;
            case RANDOM:
                if (orientationCooldown.ready(deltaTime) && !isDisplacementOccurs()){
                    Orientation random = Orientation.values()[(int)(Math.random() * 4)];
                    orientate(random);
                    move (MOVE_DURATION);
                }
                if (stateCooldown.ready(deltaTime) && target != null){
                    if (Math.random() < getTransitionProbability()){
                        state = state.CHASING;
                    }
                }
                randomAnimation.update(deltaTime);
                break;
            case CHASING:
                if (target == null){
                    state = State.RANDOM;
                    break;
                }
                if (orientationCooldown.ready(deltaTime) && !isDisplacementOccurs()){
                    Orientation next = getNextOrientation();
                    if (next != null){
                        orientate(next);
                        move(MOVE_DURATION);
                    }
                }
                if (stateCooldown.ready(deltaTime)){
                    if (Math.random() > getTransitionProbability()){
                        state = State.SLEEPING;
                    }
                }
                chasingAnimation.update(deltaTime);
                break;
        }

    }
    @Override
    public void draw (Canvas canvas){
        if (graphicPath != null){
            graphicPath.draw(canvas);
        }

        switch (state){
            case CHASING:
                chasingAnimation.draw(canvas);
                break;
            case RANDOM:
                randomAnimation.draw(canvas);
                break;
            case SLEEPING:
                sleepingAnimation.draw(canvas);
                break;
        }
    }
    @Override
    protected Orientation getNextOrientation() {
        if (target == null){
            graphicPath = null;
            return null;
        }

        Queue<Orientation> path =
                ((ICMazeArea) getOwnerArea())
                        .shortestPath(
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

    private double getTransitionProbability(){
        return (double) Difficulty.HARDEST / difficulty;
    }
    @Override
    protected int getViewRadius(){
        return 5;
    }
    @Override
    public boolean wantsViewInteraction() {
        return !isSleeping();
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor{
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (!isSleeping() && isCellInteraction){
                // TODO: lose hp (-1): fonctionalité à ajouter
            }
        }
    }
}
