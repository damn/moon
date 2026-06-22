package space.earlygrey.shapedrawer.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Allows ShapeDrawer to be implemented in Scene2D.UI Stages via the Drawable interface. Users must override
 * {@link ShapeDrawerDrawable#drawShapes(ShapeDrawer, float, float, float, float)} to draw their own shapes. Care must
 * be taken to ensure that the Batch of the ShapeDrawer must be the same Batch at rendering time.
 * @author Raymond "Raeleus" Buckley
 */
public abstract class ShapeDrawerDrawable extends BaseDrawable {
    public transient ShapeDrawer shapeDrawer;
    
    /**
     * A no-argument constructor necessary for serialization. {@link ShapeDrawerDrawable#shapeDrawer} must be defined
     * before this Drawable is drawn.
     */
    public ShapeDrawerDrawable() {
    }
    
    /**
     * Constructs a ShapeDrawerDrawable. The Batch of the provided ShapeDrawer must be the same Batch at rendering time.
     * @param shapeDrawer
     */
    public ShapeDrawerDrawable(ShapeDrawer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
    }
    
    /**
     * Calls {@link ShapeDrawerDrawable#drawShapes(ShapeDrawer, float, float, float, float)} and draws the user defined
     * shapes. May throw an {@link IllegalArgumentException} if the argument batch and shapeDrawer.batch are not the
     * same.
     * @throws IllegalArgumentException
     * @param batch
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (shapeDrawer == null) {
            throw new NullPointerException("shapeDrawer must be defined before the Drawable can be drawn.");
        }
        if (!batch.equals(shapeDrawer.getBatch())) {
            throw new IllegalArgumentException("Argument \"batch\" does not match \"shapeDrawer.batch\"");
        }
        drawShapes(shapeDrawer, x, y, width, height);
    }
    
    /**
     * Draws the user defined shapes with the given shapeDrawer.
     * @param shapeDrawer
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public abstract void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height);
    
    /**
     * Returns the ShapeDrawer that renders the user defined shapes. The Batch of the provided ShapeDrawer must be the
     * same Batch at rendering time.
     * @return
     */
    public ShapeDrawer getShapeDrawer() {
        return shapeDrawer;
    }
    
    /**
     * Sets the ShapeDrawer that renders the user defined shapes. The Batch of the provided ShapeDrawer must be the same
     * Batch at rendering time.
     * @param shapeDrawer
     */
    public void setShapeDrawer(ShapeDrawer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
    }
}
