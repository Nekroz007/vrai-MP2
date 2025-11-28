package ch.epfl.cs107.play.tuto2.area;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

/**
 * Base class for all the areas of Tuto2
 */
public abstract class Tuto2Area extends Area {
    public final static float DEFAULT_SCALE_FACTOR = 13.f;
	private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    /**
     * Area specific callback to initialise the instance
     */
    protected abstract void createArea();

    /**
     * @return the player's spawn position in the area
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /**
     * Callback to initialise the instance of the area
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the area is instantiated correctly, false otherwise
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new Tuto2Behavior(window, getTitle()));
            createArea();
            return true;
        }
        return false;
    }

    /**
	 * Getter for Tuto2's scale factor
	 * @return Scale factor in both the x-direction and the y-direction
	 */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

}
