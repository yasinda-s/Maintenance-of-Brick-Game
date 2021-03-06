/*
 *  Brick Destroy - A simple Arcade video game
 *   Copyright (C) 2017  Filippo Ranza
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package brickdestroy.brick;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * The Steel Brick refers to bricks that are grey in color and break on probability. It inherits from the Brick Class directly.
 */
public class SteelBrick extends Brick {
    //properties of steel brick shown below - has probability in breaking
    private static final Color DEF_INNER = new Color(203, 203, 201);
    private static final Color DEF_BORDER = Color.BLACK;
    private static final int STEEL_STRENGTH = 1;
    private static final double STEEL_PROBABILITY = 0.4;

    private Random rnd;
    private Shape brickFace;

    /**
     * This is the constructor for the SteelBrick, it inherits most from the super constructor.
     * @param point Refers to the top left coordinates of the Steel Brick.
     * @param size Refers to the dimension of the brick - width and height.
     */
    public SteelBrick(Point point, Dimension size){
        super(point,size,DEF_BORDER,DEF_INNER,STEEL_STRENGTH);
        rnd = new Random();
        brickFace = super.getBrickFace();
    }

    /**
     * Method to make the Steel Brick's Face (Rectangle Shape)
     * @param pos The top left position (coordinates) of the brick.
     * @param size The width and height of the brick (Dimension).
     * @return Returns a Rectangular Shape of the brick face.
     */
    @Override
    protected Shape makeBrickFace(Point pos, Dimension size) {
        return new Rectangle(pos,size);
    }

    /**
     * Getter for a singular Steel Brick.
     * @return Returns the Shape of the brick.
     */
    @Override
    public Shape getBrick() {
        return brickFace;
    }

    /**
     * This method is used to set the impact based on point of impact and direction of hit.
     * @param point Refers to the edge of the ball making contact with the brick.
     * @param dir Refers to the direction (edge) of the brick that is making contact with the ball.
     * @return Returns true if the brick is broken.
     */
    public boolean setImpact(Point2D point , int dir){
        if(super.isBroken())
            return false;
        impact();
        return super.isBroken();
    }

    /**
     * This method compares the random number generated to the probability of Steel breaking, then calls impact if the brick breaks.
     */
    public void impact(){ //impact is dependant on the rnd variable
        if(rnd.nextDouble() < STEEL_PROBABILITY){
            super.impact();
        }
    }
}
