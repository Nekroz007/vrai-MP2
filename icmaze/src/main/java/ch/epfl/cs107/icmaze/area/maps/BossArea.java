package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Boss;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.LogicKey;
import ch.epfl.cs107.icmaze.area.AreaLogic;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

public class BossArea extends ICMazeArea {
    private Boss boss;
    private LogicKey key;
    private AreaLogic bossResolvedLogic;

    public BossArea() {
        super("SmallArea", 8, -1);
        createPortals();
    }
    @Override
    protected boolean isChallengeResolved(){
        return bossResolvedLogic.isOn();
    }
    public AreaLogic getChallengeLogic (){
        return bossResolvedLogic;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 4);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, "SmallArea"));
        registerActor(new Foreground(this, null, "SmallArea"));

        boss = new Boss(this, Orientation.DOWN, new DiscreteCoordinates(size / 2, size / 2));
        registerActor(boss);

        key = new LogicKey(this, Orientation.UP, new DiscreteCoordinates(6, 5), -1);
        registerActor(key);

        bossResolvedLogic = new And(boss.getDeadLogic(),
                key.getCollectedLogic());

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