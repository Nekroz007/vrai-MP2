package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Diamond extends ICMazeCollectable{
    private final Sprite sprite;

    public Diamond(ICMazeArea area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.sprite = new Sprite("icmaze/diamond", 0.75f, 0.75f, this);
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
