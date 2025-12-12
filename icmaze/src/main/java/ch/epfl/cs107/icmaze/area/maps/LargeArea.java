package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class LargeArea extends ICMazeArea {
    private final int difficulty;
    public static final int KEY_ID = 3;


    public LargeArea() {
        super("LargeArea", 32);
        this.difficulty = Difficulty.HARDEST;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(1, size / 2 + 1); }


    protected void createArea() {
        generateMazeAndPlaceRocks(difficulty);

        createPortals();

        Portal westPortal = portals.get(AreaPortals.W);
        westPortal.setState(Portal.State.OPEN);
        westPortal.setDestination("icmaze/MediumArea", new DiscreteCoordinates(1, size / 2 + 1));

        Portal eastPortal = portals.get(AreaPortals.E);
        eastPortal.setState(Portal.State.OPEN);
        eastPortal.setDestination("icmaze/Boss", new DiscreteCoordinates(size, size / 2 + 1));


        for (Portal p : portals.values()) registerActor(p);
    }

    @Override
    public String getTitle() { return "icmaze/LargeArea"; }
}
