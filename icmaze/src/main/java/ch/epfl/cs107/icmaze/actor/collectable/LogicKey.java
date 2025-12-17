package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.area.AreaLogic;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class LogicKey extends Key {
    private final AreaLogic collectedLogic = new AreaLogic();
    public LogicKey(ch.epfl.cs107.play.areagame.area.Area area,
                    Orientation orientation,
                    DiscreteCoordinates position,
                    int keyId) {
        super(area, orientation, position, keyId);
    }

    @Override
    public void collect() {
        super.collect();
        collectedLogic.setActive(true); // ✅ le signal devient ON
    }

    public AreaLogic getCollectedLogic() {
        return collectedLogic;
    }
}
