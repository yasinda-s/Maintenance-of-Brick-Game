package brickdestroy.brick;

import java.awt.*;
import java.awt.Point;

/**
 * The ClayBrick class refers to the default brick we see in game. It is inheriting from the Brick class directly.
 *
 * Created by filippo on 04/09/16.
 */
public class ClayBrick extends Brick {

    //properties of clay brick shown below - the default brick type
    private static final Color DEF_INNER = new Color(178, 34, 34).darker();
    private static final Color DEF_BORDER = Color.GRAY;
    private static final int CLAY_STRENGTH = 1; //breaks in one impact with the ball

    /**
     * This is the constructor for the ClayBrick, it inherits most from the super constructor.
     * @param point Refers to the top left coordinates of the Clay Brick.
     * @param size Refers to the dimension of the brick - width and height.
     */
    public ClayBrick(Point point, Dimension size){
        super(point,size,DEF_BORDER,DEF_INNER,CLAY_STRENGTH);
    }

    /**
     * Method to make the Clay Brick's Face (Rectangle Shape)
     * @param pos The top left position (coordinates) of the brick.
     * @param size The width and height of the brick (Dimension).
     * @return Returns a rectangular Shape representing shape of a brick.
     */
    @Override
    protected Shape makeBrickFace(Point pos, Dimension size) {
        return new Rectangle(pos,size);
    }

    /**
     * Method to get the brick Face of Clay Brick.
     * @return Returns the brickFace of the Clay Brick.
     */
    @Override
    public Shape getBrick() {
        return super.getBrickFace();
    }

}
