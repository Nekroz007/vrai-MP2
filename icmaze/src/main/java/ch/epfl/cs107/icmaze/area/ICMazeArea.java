package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Or;
import ch.epfl.cs107.play.window.Window;

import java.util.HashMap;
import java.util.Map;

public abstract class ICMazeArea extends Area {

    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    private final String behaviorName;
    private static final float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    private static final float MAXIMUM_SCALE = 30f;

    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    protected final int size;

    protected final Map<AreaPortals, Portal> portals = new HashMap<>();
    private static final int WALL_V = 1;

    protected ICMazeArea(String behaviorName, int size) {
        super();
        this.behaviorName = behaviorName;
        this.size = size;
    }

    protected abstract void createArea();
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMazeBehavior(window, behaviorName));
            createArea();
            return true;
        }
        return false;
    }

    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);

        private final Orientation orientation;

        AreaPortals(Orientation orientation) {
            this.orientation = orientation;
        }

        public Orientation getOrientation() {
            return orientation;
        }
    }

    protected void createPortals() {
        portals.put(AreaPortals.N, new Portal(this,
                AreaPortals.N.getOrientation(),
                new DiscreteCoordinates(size / 2, size + 1),
                Portal.NO_KEY_ID,
                Portal.State.INVISIBLE));

        portals.put(AreaPortals.S, new Portal(this,
                AreaPortals.S.getOrientation(),
                new DiscreteCoordinates(size / 2, 0),
                Portal.NO_KEY_ID,
                Portal.State.INVISIBLE));

        portals.put(AreaPortals.W, new Portal(this,
                AreaPortals.W.getOrientation(),
                new DiscreteCoordinates(0, size / 2),
                Portal.NO_KEY_ID,
                Portal.State.INVISIBLE));

        portals.put(AreaPortals.E, new Portal(this,
                AreaPortals.E.getOrientation(),
                new DiscreteCoordinates(size + 1, size / 2),
                Portal.NO_KEY_ID,
                Portal.State.INVISIBLE));
    }

    @Override
    public float getCameraScaleFactor() {
        return (float) Math.min(size * DYNAMIC_SCALE_MULTIPLIER, MAXIMUM_SCALE);
    }

    protected void generateMazeAndPlaceRocks(int difficulty) {

        int[][] grid = MazeGenerator.createMaze(size, size, difficulty);

        DiscreteCoordinates entrance = getPlayerSpawnPosition();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                if (grid[x][y] == 1) {
                    int gameY = size - 1 - y;   // <- ici la vraie correction

                    // skip outer walls
                    if (x == 0 || x == size - 1 || gameY == 0 || gameY == size - 1)
                        continue;

                    // skip entrance
                    if (entrance.x == x && entrance.y == gameY)
                        continue;

                    registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(x, gameY)));
                }
            }
        }

        MazeGenerator.printMaze(
                grid,
                entrance,
                new DiscreteCoordinates(size - 1, size / 2 + 1)
        );
    }
}
