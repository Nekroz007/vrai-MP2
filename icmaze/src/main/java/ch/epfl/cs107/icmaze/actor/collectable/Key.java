package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Key extends ICMazeCollectable{
    private final Sprite sprite;
    private final int keyId;
    public Key(Area area, Orientation orientation, DiscreteCoordinates position,int keyId){
        super(area, orientation, position);
        this.keyId = keyId;
        this.sprite = new Sprite("icmaze/key", 0.75f,0.75f, this);
    }
    public int getKeyId(){
        return keyId;
    }

    @Override
    public void draw(Canvas canvas){
        sprite.draw(canvas);
    }

    @Override
    public void collect(){
        super.collect();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
