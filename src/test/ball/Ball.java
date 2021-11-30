package test.ball;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Random;

/**
 * This is the class used to represent the ball object in the Brick Game.
 *
 * Refactoring -
 *
 * The original code consisted of passing in two radii (radiusA and radiusB) for the makeBall method, however this is redundant and makes the
 * code harder to understand. I have refactored the code by creating one single parameter called 'radius'. This performs
 * the same functionality as the the ball will be having one radius only.
 *
 * A new method "getSpeedsXY" has been added so that this method can be called whenever the ball speed has to re-allocated randomly in any Class
 * rather than repeating the same lines of code as we saw in the original.
 *
 * The ball class now implements the BallInterface.
 *
 * The makeBall() method has been moved to BallInterface. -> XXXX Explain why later
 */

abstract public class Ball implements BallInterface {

    private Shape ballFace;

    private Point2D center;

    private Point2D up;
    private Point2D down;
    private Point2D left;
    private Point2D right;

    private Color border;
    private Color inner;

    private int speedX;
    private int speedY;

    /**
     * This is the constructor for the Ball Object to represent the ball.
     * The 4 coordinates of the edges of the ball are assigned here (up, down, left and right).
     * The face of the ball is set here.
     * The color (inner and border) of the ball is set here.
     *
     *
     * @param center Point2D type, center holds the x and y coordinate of the ball's center.
     * @param radius Integer type, radiusA holds the radius of the ball.
     * @param inner Color type, inner holds the ball's inside color.
     * @param border Color type, border holds the ball's outside color.
     */

    public Ball(Point2D center,int radius, Color inner,Color border){
        this.center = center;

        //declare variables for edges of ball
        up = new Point2D.Double();
        down = new Point2D.Double();
        left = new Point2D.Double();
        right = new Point2D.Double();

        //set locations to edges of ball - using center of ball
        up.setLocation(center.getX(),center.getY()-(radius / 2));
        down.setLocation(center.getX(),center.getY()+(radius / 2));

        left.setLocation(center.getX()-(radius /2),center.getY());
        right.setLocation(center.getX()+(radius /2),center.getY());

        //set the appearance and speed of ball
        ballFace = makeBall(center,radius);
        this.border = border;
        this.inner  = inner;

        speedX = 0;
        speedY = 0;

        //Set speed of the ball for the initial time
        int[] speedsXY =  getSpeedsXY();

        speedX = speedsXY[0];
        speedY = speedsXY[1];
    }

    /**
     * This method is focused on having the ball move freely as the game begins.
     */
    public void move(){
        RectangularShape tmp = (RectangularShape) ballFace;
        center.setLocation((center.getX() + speedX),(center.getY() + speedY));
        double w = tmp.getWidth(); //get width of rectangle/ball
        double h = tmp.getHeight(); //get height

        tmp.setFrame((center.getX() -(w / 2)),(center.getY() - (h / 2)),w,h); //to set frame of square (rect)
        setPoints(w,h); //use this to set the coordinates of the ball with found width and height

        ballFace = tmp;
    }

    /**
     * This method is used to set the speed of the ball along both axes.
     * @param x The speed at which the ball moves along the x-axis.
     * @param y The speed at which the ball moves along the y-axis.
     */
    public void setSpeed(int x,int y){ //set speed of ball
        speedX = x; //the rate at which you move pixels (pos - is for top left)
        speedY = y;
    }

    /**
     * This method is used to set the speed of the ball along the x axis only.
     * @param s The speed at which the ball moves along the x-axis.
     */
    public void setXSpeed(int s){
        speedX = s;
    }

    /**
     * This method is used to set the speed of the ball along the y axis only.
     * @param s The speed at which the ball moves along the y-axis.
     */
    public void setYSpeed(int s){
        speedY = s;
    }

    /**
     * Reverses the direction of the ball along the x-axis.
     */
    public void reverseX(){
        speedX *= -1;
    }

    /**
     * Reverses the direction of the ball along the y-axis.
     */
    public void reverseY(){
        speedY *= -1;
    }

    /**
     * Used to get the Color of the border of the ball.
     * @return Returns the Color of the border of the ball.
     */
    public Color getBorderColor(){
        return border;
    }

    /**
     * Used to get the Color of the inside of the ball.
     * @return Returns the Color of the inside of the ball.
     */
    public Color getInnerColor(){
        return inner;
    }

    /**
     * Used to get the center coordinates of the ball.
     * @return Returns the Point2D for the centre of the ball.
     */
    public Point2D getPosition(){
        return center;
    }

    /**
     * Used to get the face of the ball.
     * @return Returns the circle of the ball Face.
     */
    public Shape getBallFace(){
        return ballFace;
    }

    /**
     * This method is used to move the ball back to the spawn point after ball is lost or when new level is begun.
     * @param p The Point is the location we want the ball to be spawned from on reset.
     */
    public void moveTo(Point p){
        center.setLocation(p); //this is when you lose ball to spawn center again

        RectangularShape tmp = (RectangularShape) ballFace;
        double w = tmp.getWidth();
        double h = tmp.getHeight();

        tmp.setFrame((center.getX() -(w / 2)),(center.getY() - (h / 2)),w,h); //set frame around rectangle in ball spawn point again
        ballFace = tmp;
    }

    /**
     * This method is used to set the up, down, left, right edges of the ball with the passed in width and height.
     * @param width The width of the rectangle frame of the ball.
     * @param height The height of the rectangle frame of the ball.
     */
    private void setPoints(double width,double height){  //use this to set the coordinates of the ball with found width and height
        //width and height are the same
        up.setLocation(center.getX(),center.getY()-(height / 2));
        down.setLocation(center.getX(),center.getY()+(height / 2));

        left.setLocation(center.getX()-(width / 2),center.getY());
        right.setLocation(center.getX()+(width / 2),center.getY());
    }

    /**
     * Getter for x-axis speed of ball.
     * @return Returns the speed of the ball in x-axis.
     */
    public int getSpeedX(){
        return speedX;
    }

    /**
     * Getter for y-axis speed of ball.
     * @return Returns the speed of the ball in y-axis.
     */
    public int getSpeedY(){
        return speedY;
    }

    /**
     * This method is used to generate two speeds for X and Y axes respectively.
     * @return Returns an Int array where the zeroth index refers to speedX and first index refers to speedY.
     */
    public int[] getSpeedsXY(){
        Random rnd = new Random();

        do{
            this.speedX = rnd.nextInt(5) -2;
        } while (this.speedX == 0);
        do{
            this.speedY = -rnd.nextInt(3);
        } while (this.speedY == 0);

        return new int[] {this.speedX, this.speedY};
    }

    //Added encapuslation
    public Point2D getCenter() {
        return center;
    }

    public Point2D getUp() {
        return up;
    }

    public Point2D getDown() {
        return down;
    }

    public Point2D getLeft() {
        return left;
    }

    public Point2D getRight() {
        return right;
    }
}