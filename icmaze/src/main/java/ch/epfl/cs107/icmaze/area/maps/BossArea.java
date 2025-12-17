package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Boss;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class BossArea extends ICMazeArea {
    private Boss boss;

    public BossArea() {
        super("SmallArea", 8, -1);
        createPortals();
    }

    @Override
    protected boolean isChallengeResolved() {
        return boss != null && boss.isDead();
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 4);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null, "SmallArea"));

        this.boss = new Boss(this, Orientation.DOWN, new DiscreteCoordinates(size / 2, size / 2));
        registerActor(boss);

        for (Portal p : portals.values()) {
            if (p.getOrientation() == entryOrientation) {
            p.setState(Portal.State.LOCKED);
            p.setKeyId(-1);
        }
            registerActor(p);
        }

    }

    @Override
    public String getTitle() {
        return "icmaze/BossArea";
    }
}