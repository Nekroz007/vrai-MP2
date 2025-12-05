package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class MediumArea extends ICMazeArea {
    private int difficulty;

    public MediumArea() {
        super("MediumArea", 16);
        difficulty = Difficulty.HARDEST;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(1, size / 2 + 1); }


    protected void createArea() {
        registerActor(new Background(this, "MediumArea"));
        registerActor(new Foreground(this, null, "MediumArea"));

        createPortals();

        Portal westPortal = portals.get(AreaPortals.W);
        westPortal.setState(Portal.State.OPEN);
        westPortal.setDestination("icmaze/SmallArea", new DiscreteCoordinates(size, size / 2 + 1));

        Portal eastPortal = portals.get(AreaPortals.E);
        eastPortal.setState(Portal.State.OPEN);
        eastPortal.setDestination("icmaze/LargeArea", new DiscreteCoordinates(1, size/ 2 + 1));


        for (Portal p : portals.values()) registerActor(p);
    }

    @Override
    public String getTitle() { return "icmaze/MediumArea"; }
}
