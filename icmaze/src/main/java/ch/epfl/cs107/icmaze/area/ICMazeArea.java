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
    private static final float MAXIMUM_SCALE = 50f;

    protected final int size;
    protected final int keyId;
    protected final AreaGraph graph = new AreaGraph();
    protected final Map<AreaPortals, Portal> portals = new HashMap<>();

    // NOUVEAU : Pour savoir quels murs casser
    protected Orientation entryOrientation = null;
    protected Orientation exitOrientation = null;

    protected ICMazeArea(String behaviorName, int size, int keyId) {
        super();
        this.behaviorName = behaviorName;
        this.size = size;
        this.keyId = keyId;
    }

    protected abstract void createArea();

    public void setEntryOrientation(Orientation entryOrientation) {
        this.entryOrientation = entryOrientation;
    }
    public void setExitOrientation(Orientation exitOrientation) {
        this.exitOrientation = exitOrientation;
    }
    public int getKeyId() {
        return keyId;
    }
    public int getSize() {
        return size;
    }
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

    @Override
    public float getCameraScaleFactor() {
        // Calcul : Taille * Multiplicateur
        float calculatedScale = size * DYNAMIC_SCALE_MULTIPLIER;

        // On s'assure juste que ça ne devient pas GIGANTESQUE (plafond de sécurité)
        // Avec MAXIMUM_SCALE à 50f, une LargeArea (taille 32 -> scale 44) passera sans être bloquée.
        return Math.min(calculatedScale, MAXIMUM_SCALE);
    }

    protected void generateMazeAndPlaceRocks(int difficulty) {
        int[][] maze = MazeGenerator.createMaze(getWidth(), getHeight(), difficulty);

        int midY = getHeight() / 2;
        int midX = getWidth() / 2;
        int maxX = getWidth() - 1;
        int maxY = getHeight() - 1;

        // --- CORRECTION 1 : REBOUCHER TOUS LES BORDS D'ABORD ---
        // On s'assure que les 4 emplacements de portails sont des MURS (1) par défaut
        // Ouest
        maze[midY][0] = 1;      maze[midY][1] = 1;
        // Est
        maze[midY][maxX] = 1;   maze[midY][maxX - 1] = 1;
        // Nord
        maze[maxY][midX] = 1;   maze[maxY - 1][midX] = 1;
        // Sud
        maze[0][midX] = 1;      maze[1][midX] = 1;

        // --- CORRECTION 2 : OUVRIR UNIQUEMENT CEUX NÉCESSAIRES ---

        // Ouvrir OUEST seulement si c'est l'entrée ou la sortie
        if (entryOrientation == Orientation.LEFT || exitOrientation == Orientation.LEFT) {
            maze[midY][0] = 0; maze[midY][1] = 0;
        }
        // Ouvrir EST seulement si c'est l'entrée ou la sortie
        if (entryOrientation == Orientation.RIGHT || exitOrientation == Orientation.RIGHT) {
            maze[midY][maxX] = 0; maze[midY][maxX - 1] = 0;
        }
        // Ouvrir NORD seulement si c'est l'entrée ou la sortie
        if (entryOrientation == Orientation.UP || exitOrientation == Orientation.UP) {
            maze[maxY][midX] = 0; maze[maxY - 1][midX] = 0;
        }
        // Ouvrir SUD seulement si c'est l'entrée ou la sortie
        if (entryOrientation == Orientation.DOWN || exitOrientation == Orientation.DOWN) {
            maze[0][midX] = 0; maze[1][midX] = 0;
        }

        // (Le reste de la méthode ne change pas : printMaze, placement rochers, clés...)
        // ...
        // Placement des Rochers
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (maze[y][x] == 1) {
                    boolean isBorder = (x == 0 || x == maxX || y == 0 || y == maxY);
                    if (!isBorder) registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(x, y)));
                } else {
                    boolean hasLeft = x > 0 && maze[y][x - 1] == 0;
                    boolean hasUp = y < maxY && maze[y + 1][x] == 0;
                    boolean hasRight = x < maxX && maze[y][x + 1] == 0;
                    boolean hasDown = y > 0 && maze[y - 1][x] == 0;
                    graph.addNode(new DiscreteCoordinates(x, y), hasLeft, hasUp, hasRight, hasDown);
                }
            }
        }

        // Placement clé
        if (keyId != Portal.NO_KEY_ID) {
            List<DiscreteCoordinates> coords = new ArrayList<>(graph.keySet());
            if (!coords.isEmpty()) {
                Collections.shuffle(coords, RandomGenerator.rng);
                registerActor(new Key(this, Orientation.DOWN, coords.get(0), keyId));
            }
        }
    }

    // Ta méthode registerPortal existante est OK, assure-toi juste qu'elle gère bien les updates
    protected void registerPortal(AreaPortals type, String destName, DiscreteCoordinates destCoords, Portal.State state, int keyId) {
        Portal portal = portals.get(type);
        if (portal != null) {
            portal.setDestination(destName, destCoords);
            portal.setState(state); // Rend visible/ouvert uniquement si demandé
            portal.setKeyId(keyId);
        }
    }

    private DiscreteCoordinates getPortalCoordinates(Orientation orient, int midX, int midY, int maxX, int maxY) {
        if (orient == null) return null;
        switch (orient) {
            case LEFT:  return new DiscreteCoordinates(0, midY);      // Ouest
            case RIGHT: return new DiscreteCoordinates(maxX, midY);   // Est
            case UP:    return new DiscreteCoordinates(midX, maxY);   // Nord
            case DOWN:  return new DiscreteCoordinates(midX, 0);      // Sud
            default:    return null;
        }
    }
}


