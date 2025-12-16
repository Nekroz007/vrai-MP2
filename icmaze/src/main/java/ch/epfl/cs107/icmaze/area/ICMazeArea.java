package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.*;

public abstract class ICMazeArea extends Area {

    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    private final String behaviorName;
    private static final float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    private static final float MAXIMUM_SCALE = 30f;

    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    protected final int size;

    protected final int keyId;
    protected final AreaGraph graph = new AreaGraph();

    protected final Map<AreaPortals, Portal> portals = new HashMap<>();
    private static final int WALL_V = 1;

    protected ICMazeArea(String behaviorName, int size, int keyId) {
        super();
        this.behaviorName = behaviorName;
        this.size = size;
        this.keyId = keyId;
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
    public Queue<Orientation> shortestPath(
            DiscreteCoordinates from,
            DiscreteCoordinates to) {

        return graph.shortestPath(from, to);
    }


    protected void registerPortal(AreaPortals type, String destName, DiscreteCoordinates destCoords, Portal.State state, int keyId) {
        Portal portal = portals.get(type);
        if (portal != null) {
            portal.setDestination(destName, destCoords);
            portal.setState(state);
            portal.setKeyId(keyId);
        }
    }

    @Override
    public float getCameraScaleFactor() {
        return (float) Math.min(size * DYNAMIC_SCALE_MULTIPLIER, MAXIMUM_SCALE);
    }

    protected void generateMazeAndPlaceRocks(int difficulty) {

        // cree le labyrinthe
        int[][] maze = MazeGenerator.createMaze(getWidth(), getHeight(), difficulty);
        int mid = size / 2;
        int midY = getHeight() / 2;
        int midX = getWidth() / 2;
        int maxX = getWidth() - 1;
        int maxY = getHeight() - 1;

        // on verifie que les portails ne sont pas bloques
        // PORTAIL OUEST
        maze[midY][0] = 0;
        maze[midY][1] = 0;

        // PORTAIL EST
        maze[midY][maxX] = 0;
        maze[midY][maxX - 1] = 0;

        // PORTAIL NORD
        maze[0][midX] = 0;

        // PORTAIL SUD
        maze[maxY][midX] = 0;

        // affichage du labyrinthe dans la console
        System.out.println("Génération Labyrinthe (Size: " + size + ")");
        MazeGenerator.printMaze(maze, getPlayerSpawnPosition(), new DiscreteCoordinates(size - 1, mid));

        // placement des rochers et construction du graphe
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {

                // 1 = mur
                if (maze[y][x] == 1) {

                    // on evite les rochers sur les bordures
                    boolean isBorder = (x == 0 || x == getWidth() - 1 || y == 0 || y == getHeight() - 1);
                    if (!isBorder) {
                        registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(x, y)));
                    }
                }
                // 0 = passage
                else {
                    boolean hasLeft  = (x > 0 && maze[y][x - 1] == 0);
                    boolean hasUp    = (y < getHeight() - 1 && maze[y + 1][x] == 0);
                    boolean hasRight = (x < getWidth() - 1 && maze[y][x + 1] == 0);
                    boolean hasDown  = (y > 0 && maze[y - 1][x] == 0);

                    graph.addNode(new DiscreteCoordinates(x, y), hasLeft, hasUp, hasRight, hasDown);
                }
            }
        }

        // placement aleatoire de la cle sur un passage
        List<DiscreteCoordinates> coords = graph.keySet();
        if (!coords.isEmpty()) {
            Collections.shuffle(coords, RandomGenerator.rng);
            DiscreteCoordinates keyPos = coords.get(0);
            registerActor(new Key(this, Orientation.DOWN, keyPos, keyId));
        }
    }

}
