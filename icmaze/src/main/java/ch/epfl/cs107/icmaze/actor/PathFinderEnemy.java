package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class PathFinderEnemy extends Enemy {
    protected PathFinderEnemy (Area area, Orientation orientation, DiscreteCoordinates position, int hp ){
        super (area, orientation, position, hp);
    }
    protected abstract Orientation getNextOrientation();
    protected abstract int getViewRadius();
    public List<DiscreteCoordinates> getFieldOfViewCells(){
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates center = getCurrentMainCellCoordinates();
        int r = getViewRadius();
        for (int dx = -r; dx <= r; dx++){
            for (int dy = -r; dy <= r; dy++){
                cells.add(center.jump(dx, dy));
            }
        }
        return cells;
    }
    @Override
    public boolean wantsCellInteraction(){
        return false;
    }
    @Override
    public boolean wantsViewInteraction(){
        return true;
    }
    public void update (float deltaTime){
        super.update(deltaTime);
        if (!isDead() && !isDisplacementOccurs()){
            Orientation next =  getNextOrientation();
            if (next != null){
                orientate(next);
                move(1);
            }
        }
    }
}
