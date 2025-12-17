package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class FireProjectile extends Projectile{
    private static final int DAMAGE = 1;
    private static final int ANIMATION_DURATION = 12;

    private final Animation animation;
    private final FireProjectileInteractionHandler handler = new FireProjectileInteractionHandler();

    public FireProjectile(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position);

        animation = new Animation("icmaze/magicFireProjectile" , 4, 1, 1, this , 32, 32,
                ANIMATION_DURATION/4, true);
    }
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        animation.update(deltaTime);
    }
    @Override
    public void draw(Canvas canvas){
        animation.draw(canvas);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    private class FireProjectileInteractionHandler implements ICMazeInteractionVisitor{
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction) {
                player.decreaseHealth(DAMAGE);
                stop();
            }
        }
    }
}
