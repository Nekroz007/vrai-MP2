package ch.epfl.cs107.play.engine.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.engine.Updatable;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class OrientedAnimation implements Updatable, Graphics {

    private final Animation[] animations;
    private final AreaEntity parent;
    @Deprecated
    private Animation current;

    public OrientedAnimation(Animation[] animations, AreaEntity parent) {
        this.animations = animations;
        this.parent = parent;
        this.current = null;
    }

    public OrientedAnimation(String name, int duration, AreaEntity parent, Vector anchor) {
        this(name, duration, parent, anchor,
                new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT},
                4, 1, 2, 16, 32);
    }

    /**
     *
     * @param name         (String): the name of the image
     * @param nbFrames     (int): number of frames in each row
     * @param width        (int): actual image width, before transformation
     * @param height       (int): actual image height, before transformation
     * @param parent       (Positionable): parent of this, not null
     * @param anchor       (Vector) : image anchor, not null
     * @param regionWidth  (int): width of frame (number of pixels in the image)
     * @param regionHeight (int): height of frame (number of pixels in the image)
     * @param order        (Orientation[]): order of the frames in the image
     * @param repeat       (boolean : true if the animation must be repeated
     *
     */
    public OrientedAnimation(String name, int duration, AreaEntity parent, Vector anchor,
                             Orientation[] order, int nbFrames, int width, int height, int regionWidth, int regionHeight, boolean repeat) {
        final Sprite[][] sprites = Sprite.extractSprites(name, nbFrames, width, height, parent, regionWidth, regionHeight, anchor, order);
        this.animations = Animation.createAnimations(duration, sprites,repeat);
        this.parent = parent;
        this.current = null;
    }
public OrientedAnimation(String name, int duration, AreaEntity parent, Vector anchor,
                             Orientation[] order, int nbFrames, int width, int height, int regionWidth, int regionHeight) {
        this(name, duration, parent, anchor, order, nbFrames, width, height, regionWidth, regionHeight, false);
    }
    @Override
    public void update(float deltaTime) {
        current().update(deltaTime);
    }

    private Animation current() {
        if (current != null) {
            return current;
        }
        return animations[parent.getOrientation().ordinal()];
    }

    @Override
    public void draw(Canvas canvas) {
        current().draw(canvas);
    }

    public void reset() {
        for (Animation animation : animations) {
            animation.reset();
        }
    }

    @Deprecated
    public void orientate(Orientation orientation) {
        current = animations[orientation.ordinal()];
    }

    public boolean isCompleted() {
        return current().isCompleted();
    }

}
