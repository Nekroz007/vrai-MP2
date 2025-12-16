package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;

public abstract class PathFinderEnemy extends Enemy {

    protected PathFinderEnemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHp) {
        super(area, orientation, position, maxHp);
    }

    /**
     * Méthode abstraite pour déterminer la prochaine orientation (stratégie de mouvement)
     */
    protected abstract Orientation getNextOrientation();

    /**
     * Méthode abstraite pour définir le rayon du champ de vision
     */
    protected abstract int getViewRadius();

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates center = getCurrentMainCellCoordinates();
        int r = getViewRadius();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                cells.add(center.jump(x, y));
            }
        }
        return cells;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }
}