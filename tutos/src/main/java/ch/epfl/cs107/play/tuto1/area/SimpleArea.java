package ch.epfl.cs107.play.tuto1.area;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.window.Image;
import ch.epfl.cs107.play.window.Window;

/**
 * Base class for all the areas of Tuto1
 */
public abstract class SimpleArea extends Area {
	public final static float DEFAULT_SCALE_FACTOR = 10.f;
	private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    private Window window;

	/**
	 * Area specific callback to initialise the instance
	 */
    protected abstract void createArea();
	/**
	 * Callback to initialise the instance of the area
	 * @param window (Window): display context. Not null
	 * @param fileSystem (FileSystem): given file system. Not null
	 * @return true if the area is instantiated correctly, false otherwise
	 */
	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		this.window = window;
		if (super.begin(window, fileSystem)) {
			// Set the behavior map
			createArea();
			return true;
		}
		return false;
	}

    @Override
    public int getWidth() {
        Image behaviorMap = window.getImage(ResourcePath.getBehavior(getTitle()), null, false);
        return behaviorMap.getWidth();
    }
    @Override
    public int getHeight() {
        Image behaviorMap = window.getImage(ResourcePath.getBehavior(getTitle()), null, false);
        return behaviorMap.getHeight();
    }
	/**
	 * Getter for Tuto1's scale factor
	 * @return Scale factor in both the x-direction and the y-direction
	 */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

}
