package ch.epfl.cs107.play.engine.actor;

import ch.epfl.cs107.play.math.Node;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Image;


/**
 * Contains information to render a single image, which can be attached to any positionable.
 */
public class ImageGraphics extends Node implements Graphics {

    /// Region of interest as a rectangle in the image
    private final RegionOfInterest roi;
    /// Image name
    private String name;
    /// Image dimension
    private double width, height;
    /// Anchor of the image (i.e. if the origin of the image is not the origin of the parent)
    private Vector anchor;
    /// Transparency of the image. Between 0 (invisible) amd 1 (opaque)
    private float alpha;
    /// Depth used as render priority. It is the third axis. See it as altitude : lower values are drawn first
    private double depth;
    ///
    private final boolean removeBackground;

    /**
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     * @param roi (RegionOfInterest): region of interest as a rectangle in the image
     * @param anchor (Vector): image anchor, not null
     * @param alpha (double): transparency, between 0 (invisible) and 1 (opaque)
     * @param depth (double): render priority, lower-values drawn first
     * @param removeBackground (boolean): indicate if we need to remove the uniform color background before using this image
     */
    public ImageGraphics(String name, double width, double height, RegionOfInterest roi, Vector anchor, float alpha, double depth, boolean removeBackground) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.roi = roi;
        this.anchor = anchor;
        this.alpha = alpha;
        this.depth = depth;
        this.removeBackground = removeBackground;
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     * @param roi (RegionOfInterest): region of interest as a rectangle in the image
     * @param anchor (Vector): image anchor, not null
     * @param alpha (double): transparency, between 0 (invisible) and 1 (opaque)
     * @param depth (double): render priority, lower-values drawn first
     */
    public ImageGraphics(String name, double width, double height, RegionOfInterest roi, Vector anchor, float alpha, double depth) {
        this(name, width, height, roi, anchor, alpha, depth, false);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     * @param roi (RegionOfInterest): region of interest as a rectangle in the image
     * @param anchor (Vector): image anchor, not null
     */
    public ImageGraphics(String name, double width, double height, RegionOfInterest roi, Vector anchor) {
        this(name, width, height, roi, anchor, 1.0f, 0.0f, false);
    }

    /**
     * Creates a new image graphics.
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     * @param roi (RegionOfInterest): region of interest as a rectangle in the image
     */
    public ImageGraphics(String name, double width, double height, RegionOfInterest roi) {
        this(name, width, height, roi, Vector.ZERO);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     * @param roi (RegionOfInterest): region of interest as a rectangle in the image
     * @param removeBackground (boolean): indicate if we need to remove the uniform color background before using this image
     */
    public ImageGraphics(String name, double width, double height, RegionOfInterest roi, boolean removeBackground) {
        this(name, width, height, roi, Vector.ZERO, 1.0f, 0.0f, removeBackground);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, may be null
     * @param width (double): actual image width, before transformation
     * @param height (double): actual image height, before transformation
     */
    public ImageGraphics(String name, double width, double height) {
        this(name, width, height, null, Vector.ZERO);
    }
   
    /**
     * Sets image name.
     * @param name (String): new image name, may be null
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return (String): image name, may be null */
    public String getName() {
        return name;
    }

    /**
     * Sets actual image width, before transformation.
     * @param width (double): image width
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /** @return (double): actual image width, before transformation */
    public double getWidth() {
        return width;
    }

    /**
     * Sets actual image height, before transformation.
     * @param height (double): image height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /** @return (double): actual image height, before transformation */
    public double getHeight() {
        return height;
    }

    /**
     * Sets image anchor location, i.e. where is the center of the image.
     * @param anchor (Vector): image anchor, not null
     */
    public void setAnchor(Vector anchor) {
        this.anchor = anchor;
    }

    /** @return (Vector): image anchor, not null */
    public Vector getAnchor() {
        return anchor;
    }
    
    /**
     * Sets transparency.
     * @param alpha (double): transparency, between 0 (invisible) and 1 (opaque)
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /** @return (double): transparency, between 0 (invisible) and 1 (opaque) */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets rendering depth.
     * @param depth (double): render priority, lower-values drawn first
     */
    public void setDepth(double depth) {
        this.depth = depth;
    }

    /** @return (double): render priority, lower-values drawn first */
    public double getDepth() {
        return depth;
    }
    
    @Override
    public void draw(Canvas canvas) {
        if (name == null)
            return;
        Image image = canvas.getImage(name, roi, removeBackground);
        Transform transform = Transform.I.scaled(width, height).translated(anchor.x, anchor.y).transformed(getTransform());
        canvas.drawImage(image, transform, alpha, depth);
    }
}
