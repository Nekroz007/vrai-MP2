package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Spawn extends ICMazeArea {
    public Spawn() {
        super("SmallArea");
    }
    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }
    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null,"SmallArea"));
        registerActor(new Pickaxe(this,Orientation.DOWN, new DiscreteCoordinates(5,4)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(4,5)));
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
