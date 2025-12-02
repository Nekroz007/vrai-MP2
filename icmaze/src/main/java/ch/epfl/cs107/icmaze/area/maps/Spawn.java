package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Spawn extends ICMazeArea {

    public Spawn() { super("SmallArea", 8); }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(5, 7); }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null,"SmallArea"));

        registerActor(new Pickaxe(this,Orientation.DOWN, new DiscreteCoordinates(5,4)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(4,5)));
        registerActor(new Key(this, Orientation.UP, new DiscreteCoordinates(6,5), Integer.MAX_VALUE));
        registerActor(new Key(this, Orientation.UP, new DiscreteCoordinates(1,2), Integer.MAX_VALUE - 1));

        createPortals();

        Portal eastPortal = portals.get(ICMazeArea.AreaPortals.E);
        eastPortal.setState(Portal.State.LOCKED);
        eastPortal.setDestination("icmaze/Boss", new DiscreteCoordinates(1, size / 2 + 1));
        eastPortal.setKeyId(Integer.MAX_VALUE);

        for (Portal p : portals.values()) registerActor(p);
    }

    @Override
    public String getTitle() { return "icmaze/Spawn"; }
}
