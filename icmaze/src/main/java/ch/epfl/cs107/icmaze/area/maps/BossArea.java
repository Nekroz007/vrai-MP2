package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class BossArea extends ICMazeArea {
    public BossArea() {
        super("SmallArea");
    }
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 15);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null,"SmallArea"));
        //registerActor(new ICMazePlayer(new Vector(20, 10), "ghost.2"));
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
