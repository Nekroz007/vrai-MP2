package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.HashMap;
import java.util.Map;

public abstract class ICMazeArea extends Area {
    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    private final String behaviorName;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    protected final int size;

    protected final Map<AreaPortals, Portal> portals = new HashMap<>();

    protected ICMazeArea(String behaviorName, int size) {
        super();
        this.behaviorName = behaviorName;
        this.size = size;

    }

    /**
     * Méthode abstraite à implémenter dans les sous-classes pour créer les éléments de l'aire
     */
    protected abstract void createArea();

    /**
     * Position de spawn du joueur dans l'aire
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /**
     * Initialisation de l'aire
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMazeBehavior(window, behaviorName));
            createArea();
            return true;
        }
        return false;
    }

    /**
     * Énumération pour les portails
     */
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

    /**
     * Création des portails par défaut
     */
    protected void createPortals() {
        portals.put(AreaPortals.N, new Portal(this, Orientation.DOWN,
                new DiscreteCoordinates(size / 2, size + 1), Portal.NO_KEY_ID, Portal.State.INVISIBLE));
        portals.put(AreaPortals.S, new Portal(this, Orientation.UP,
                new DiscreteCoordinates(size / 2, 0), Portal.NO_KEY_ID, Portal.State.INVISIBLE));
        portals.put(AreaPortals.W, new Portal(this, Orientation.RIGHT,
                new DiscreteCoordinates(0, size / 2), Portal.NO_KEY_ID, Portal.State.INVISIBLE));
        portals.put(AreaPortals.E, new Portal(this, Orientation.LEFT,
                new DiscreteCoordinates(size + 1, size / 2), Portal.NO_KEY_ID, Portal.State.INVISIBLE));
    }

    /**
     * Récupère le facteur d'échelle de la caméra
     */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

}
