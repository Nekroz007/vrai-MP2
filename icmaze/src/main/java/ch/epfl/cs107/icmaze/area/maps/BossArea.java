package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class BossArea extends ICMazeArea {

    private final Orientation entry;

    public BossArea(Orientation entry, int keyId) {
        super("SmallArea", 8, keyId); // Utilise le background "SmallArea"
        this.entry = entry;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(size, size / 2 + 1); }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null, "SmallArea"));

        createPortals();

        registerPortal(AreaPortals.W, "icmaze/LargeArea", new DiscreteCoordinates(32, 16), Portal.State.OPEN, Portal.NO_KEY_ID);

        for (Portal p : portals.values()) registerActor(p);
    }

    @Override
    public String getTitle() { return "icmaze/Boss"; }
}
