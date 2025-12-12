package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

public class ICMazeBehavior extends AreaBehavior{

    public ICMazeBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICMazeCellType color = ICMazeCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICMazeCell(x, y, color));
            }
        }
    }

    public enum ICMazeCellType {
        NULL(0, false),
        GROUND(-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true),
        ;

        final int type;
        final boolean isWalkable;

        ICMazeCellType(int type, boolean isWalkable) {
            this.type = type;
            this.isWalkable = isWalkable;
        }

        public static ICMazeCellType toType(int type) {
            for (ICMazeCellType ict : ICMazeCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            return NULL;
        }
    }


    public class ICMazeCell extends AreaBehavior.Cell {

        private final ICMazeCellType type;

        public ICMazeCell(int x, int y, ICMazeCellType type) {
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            if (!this.type.isWalkable) {
                return false;
            }
            if (entity.takeCellSpace()) {
                for (Interactable existingEntity : entities) {
                    if (existingEntity.takeCellSpace()) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        }

    }
}
