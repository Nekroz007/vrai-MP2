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
    private static int MAX_HEALTH = 10; // TODO: look for correct health value
    private static final int ANIMATION_DURATION = 24;
    private Animation vanishAnimation;

    protected Enemy (Area area, Orientation orientation, DiscreteCoordinates position, int hp) {
        super(area, orientation, position, MAX_HEALTH, false);
        this.hp = hp;
        this.dead = false;
    }
    public int getHp() {
        return hp;
    }
    public void loseHp(int amount){
        if (dead){
            return;
        }
        hp -= amount;
        if (hp <= 0){
            die();
        }
    }
    public boolean isDead(){
        return dead;
    }
    public abstract int getMaxHp();
    protected void die (){
        dead = true;
        vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new
                Vector(-0.5f, 0f), ANIMATION_DURATION/7, false);
    }
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }
    @Override
    public boolean wantsViewInteraction() {
        return true;
    }
    @Override
    public boolean takeCellSpace(){
        return !isDead();
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (dead && vanishAnimation != null){
            vanishAnimation.update(deltaTime);
            if (vanishAnimation.isCompleted()){
                getOwnerArea().unregisterActor(this);
            }
        }

    }
    @Override
    public void draw(Canvas canvas){
        if (dead && vanishAnimation != null){
            vanishAnimation.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }
}
