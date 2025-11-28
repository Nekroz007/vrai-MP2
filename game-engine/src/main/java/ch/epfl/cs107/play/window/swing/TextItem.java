package ch.epfl.cs107.play.window.swing;

import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;


/**
 * Draw a single string.
 */
public final class TextItem implements Item {

    private final String text;
    private final double fontSize;
    private final Transform transform;
    private final Color fillColor;
    private final Color outlineColor;
    private final float thickness;
    private final Font font;
    private final Vector anchor;
    private final double depth;
    private final float alpha;
    private final TextAlign.Horizontal hAlign;
    private final TextAlign.Vertical vAlign;

    /**
     * Creates a new text graphics.
     *
     * @param text         (String): content, not null
     * @param transform    (Transform): affine transform, not null
     * @param fontSize     (double): size
     * @param fillColor    (Color): fill color, may be null
     * @param outlineColor (Color): outline color, may be null
     * @param thickness    (double): outline thickness
     * @param bold         (boolean): whether to use bold font
     * @param italics      (boolean): whether to use italics font
     * @param anchor       (Vector): text anchor
     * @param hAlign       (TextAlign.Horizontal): the horizontal alignment
     * @param vAlign       (TextAlign.Vertical): the vertical alignment
     * @param alpha        (double): transparency, between 0 (invisible) and 1 (opaque)
     * @param depth        (double): render priority, lower-values drawn first
     */
    public TextItem(String text, double fontSize, Transform transform, Color fillColor, Color outlineColor, float thickness, String fontName, boolean bold, boolean italics,
                    Vector anchor, TextAlign.Horizontal hAlign, TextAlign.Vertical vAlign, double depth, float alpha) {
        this.text = text;
        this.fontSize = fontSize;
        this.transform = transform;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.thickness = thickness;
        this.font = new Font(fontName, Font.PLAIN | (bold ? Font.BOLD : 0) | (italics ? Font.ITALIC : 0), 1);
        this.anchor = anchor;
        this.depth = depth;
        this.alpha = alpha;
        this.hAlign = hAlign;
        this.vAlign = vAlign;
    }

    @Override
    public double getDepth() {
        return depth;
    }

    @Override
    public void render(Graphics2D g) {

        // Keep current state, in order to restore it later
        final Font origFont = g.getFont();
        final Color origColor = g.getColor();
        final AffineTransform origTransform = g.getTransform();
        Composite origComposite = null;
        if (alpha < 1.0f) {
            origComposite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        // Flip vertically (since g2d's text origin is top-left)
        final AffineTransform a = new AffineTransform(
                transform.m00, transform.m10,
                transform.m01, -transform.m11,
                transform.m02 + transform.m01, transform.m12 + transform.m10
        );

        // Define intrisic text properties
        g.setFont(font);
        g.setColor(fillColor);
        final AffineTransform ax = (AffineTransform) origTransform.clone();
        a.scale(fontSize, fontSize);
        ax.concatenate(a);
        g.setTransform(ax);

        // x and y position for bottom left alignment
        double x = anchor.getX() / fontSize;
        double y = anchor.getY() / fontSize;


        final FontMetrics fm = g.getFontMetrics(font);
        final double width = (double) fm.getStringBounds(text, g).getWidth();
        if (hAlign == TextAlign.Horizontal.CENTER) {
            x -= (width / 2);
        } else if (hAlign == TextAlign.Horizontal.RIGHT) {
            x -= width;
        }

        if (vAlign == TextAlign.Vertical.MIDDLE) {
            y -= 0.5f;
        } else if (vAlign == TextAlign.Vertical.TOP) {
            y -= 1;
        }

        /*
		g.drawString(text, x, -y);


        // If requested, also draw outline
		if (outlineColor != null && thickness > 0.0f) {
			g.setColor(outlineColor);
			GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), text);
			Shape shape = gv.getOutline();
			g.setStroke(new BasicStroke(thickness));
			g.translate(x, -y);
			g.draw(shape);
		}
         */

        drawTextCharByChar(g, (float) x, (float) y);

        // Restore old properties
        g.setTransform(origTransform);
        g.setFont(origFont);
        g.setColor(origColor);
        if (origComposite != null)
            g.setComposite(origComposite);
    }

    /**
     * Draw each character individually with spacing, because macOS seems to have issues with normal fonts
     */
    private void drawTextCharByChar(Graphics2D g, float x, float y) {
        double charSpacing = 0.15f;
        double spaceSpacing = 0.2f;
        for (char c : text.toCharArray()) {
            String s = String.valueOf(c);
            GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), s);
            Shape glyphShape = gv.getOutline(x, -y);

            // Draw fill
            if (fillColor != null) {
                g.setColor(fillColor);
                g.fill(glyphShape);
            }

            // Draw outline
            if (outlineColor != null && thickness > 0.0f) {
                g.setColor(outlineColor);
                g.setStroke(new BasicStroke(thickness));
                g.draw(glyphShape);
            }

            x += (double) (gv.getVisualBounds().getWidth() + (c == ' ' ? spaceSpacing : charSpacing));
        }
    }
}
