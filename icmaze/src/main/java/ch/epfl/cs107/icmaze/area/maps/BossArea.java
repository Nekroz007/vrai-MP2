package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class BossArea extends ICMazeArea {

    public BossArea() {
        super("SmallArea", 8, Portal.NO_KEY_ID);
        createPortals();
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 4);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null, "SmallArea"));

        for (Portal p : portals.values()) {
            registerActor(p);
        }
    }

    @Override
    public String getTitle() {
        return "icmaze/BossArea";
    }
}