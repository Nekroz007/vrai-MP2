package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class SmallArea extends ICMazeArea {
    private static final int KEY_ID = 1;
    private int difficulty;

    public SmallArea() {
        super("SmallArea", 8);
        this.difficulty = Difficulty.HARDEST;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(1, size / 2 + 1); }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null, "SmallArea"));

        generateMazeAndPlaceRocks(difficulty);
        createPortals();

        Portal westPortal = portals.get(AreaPortals.W);
        westPortal.setState(Portal.State.OPEN);
        westPortal.setDestination("icmaze/Spawn", new DiscreteCoordinates(8, 4));

        Portal eastPortal = portals.get(AreaPortals.E);
        eastPortal.setState(Portal.State.OPEN);
        eastPortal.setDestination("icmaze/MediumArea", new DiscreteCoordinates( 1, 8));

        for (Portal p : portals.values()) registerActor(p);
    }

    @Override
    public String getTitle() { return "icmaze/SmallArea"; }
}
