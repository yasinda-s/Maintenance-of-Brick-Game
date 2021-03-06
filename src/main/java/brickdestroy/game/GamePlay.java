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
package brickdestroy.game;

import brickdestroy.levels.HalvePlayerDrop;
import brickdestroy.levels.IncreaseSpeedDrop;
import brickdestroy.levels.WallSetup;
import brickdestroy.ball.Ball;
import brickdestroy.ball.RubberBall;
import brickdestroy.brick.Brick;
import brickdestroy.brick.Crack;
import brickdestroy.player.Player;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This class is responsible for the general game play of the Brick Game (interactions between components and it keeps track of in game scores).
 *
 * Maintenance and Refactoring activities -
 *
 * Changed name from "Wall" to "GamePlay" as this is not solely responsible for the Wall above in the game, its more about the game play of the game.
 * The methods that formed the formation of bricks in the wall for each level has been removed from this class and created in WallSetup Class so that the only responsibility of WallSetup is to create the formation of bricks on the wall.
 * Instead of setting the speed of the ball (for both axes) in the GamePlay constructor, it has been moved to the Ball class's constructor so that the initial speed is randomly assigned from the parent class.
 * The original code had the same lines of code repeating when the ball was to be reset in the "ballReset" method, I have refactored the code by simplifying the method.
 * Change method from nextLevel() to skipLevel() and added more resets for the game as changing the wall structure does not resemble the start of a new level. The method now calls balls, score and timer to reset to 0.
 * Added getters for the scores so that other classes can access them, this improves encapsulation and hides data that do not need to be seen from other classes.
 * Added getters for the ball object created to promote encapsulation.
 * "impactWall" method stores the values taken from Brick class so that its easier to read the values we use in the if-condition.
 *
 * Extensions -
 * Added a method called "calculateScore" which is used to calculate the user's level score and total score for the ongoing game.
 * This method consists of a reward penalty system so that the user's performance matters.
 * For each level, if the user takes a longer time to break the bricks, he/she will receive lesser points for each brick broken. This is done to reward users that break bricks faster and to penalize when they take too long to break bricks.
 * The method which keeps track of the points lost with increasing time is called "checkScoreDenominator".
 * The "findImpacts" method is extended to take into account the ball movement when it impacts with one of the power drops that appear in level 5.
 * The "playerReset" is used to reset the length of player bar.
 * The "resetLevelScores" is added to reset the scores of all levels played by user.
 */

public class GamePlay {
    private Rectangle area;
    private Point ballPos;

    Brick[] bricks; //array of bricks
    private Ball ball;
    Player player;
    private IncreaseSpeedDrop increaseSpeedDrop;
    private HalvePlayerDrop halvePlayerDrop;

    private Brick[][] levels;
    private int level;

    private Point startPoint;
    private int brickCount;
    private int ballCount;
    private boolean ballLost;

    private int score;
    private int scoreToAdd;
    private int scoreLvlOne;
    private int scoreLvlTwo;
    private int scoreLvlThree;
    private int scoreLvlFour;
    private int scoreLvlFive;
    private int scoreLvlSix;
    private int levelTimePlayed;

    /**
     * This is the constructor for the GamePlay class.
     * @param drawArea drawArea is the Rectangle in which the whole game is being setup.
     * @param brickCount this represents the total number of bricks on the brick wall.
     * @param lineCount this represents the number of brick lines on the wall.
     * @param brickDimensionRatio this represents the height to width ratio of a singular brick.
     * @param increaseSpeedDrop this represents the additional power drop in level 5.
     * @param halvePlayerDrop this represents the additional power drop in level 5.
     */
    public GamePlay(Rectangle drawArea, int brickCount, int lineCount, double brickDimensionRatio, IncreaseSpeedDrop increaseSpeedDrop, HalvePlayerDrop halvePlayerDrop){
        this.increaseSpeedDrop = increaseSpeedDrop;
        this.halvePlayerDrop = halvePlayerDrop;

        WallSetup wallsetup = new WallSetup(drawArea,brickCount,lineCount,brickDimensionRatio);
        //returns a brick[][] object which can be indexed to get one of the 4 levels

        levels = wallsetup.getLevelsMade();
        level = 0; //original level is 0

        score = 0;
        scoreToAdd = 0;
        scoreLvlOne = 0;
        scoreLvlTwo = 0;
        scoreLvlThree = 0;
        scoreLvlFour = 0;
        scoreLvlFive = 0;
        scoreLvlSix = 0;

        ballCount = 3; //we get 3 lives
        ballLost = false; //originally no balls are lost

        area = drawArea;
    }

    /**
     * This method is used to make the ball and player object for the first time.
     * @param ballPos Represents the Point2D position of the ball.
     */
    public void makeComponents(Point2D ballPos){
        this.startPoint = new Point((Point) ballPos); //takes in the position of the ball to begin with (the player bar is based on this too)
        this.ballPos = (Point) ballPos;
        //use the position of the ball to make a rubber ball object

        player = new Player((Point) ballPos.clone(),150,10, area);
        ball = new RubberBall(ballPos);
    }

    /**
     * This method enables both the ball and player bar to move around.
     */
    public void move(){
        player.move();
        ball.move();
    }

    /**
     * This method is used to identify where the ball makes an impact (The player bar, wall, power drops and Frame Borders) and how the ball's movement is affected.
     */
    public void findImpacts(){
        if(player.impact(ball)){
            ball.reverseY(); //change direction of ball when it hits player bar to go back up -reflection along Y axis
        }
        else if(impactWall()){
            brickCount--;
            calculateScore(level);
            player.increaseWidth();
        }
        else if(impactBorder()) { //if the ball hits the corner walls (left and right)
            ball.reverseX();
        }
        else if(ball.getPosition().getY() < area.getY()){ //if ball hits top frame
            ball.reverseY();
        }
        else if(ball.getPosition().getY() > area.getY() + area.getHeight()){ //if ball goes out (below)
            ballCount--;
            ballLost = true;
        }else if(level==5 && increaseSpeedDrop.impact(ball) && !increaseSpeedDrop.isImpactOnce()){
            increaseSpeedDrop.setImpactOnce(true);
            ball.setSpeed(5, -5);
        }else if(level==5 && halvePlayerDrop.impact(ball) && !halvePlayerDrop.isImpactOnce()) {
            halvePlayerDrop.setImpactOnce(true);
            player.halveWidth();
        }
    }

    /**
     * This method is used to calculate the ongoing final score and level score of the player for one game.
     * @param level Takes in the level which the users is playing currently.
     */
    public void calculateScore(int level){
        switch (level){
            case 1:
                scoreToAdd = 100 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlOne += scoreToAdd; //based on how soon you break the bricks in that level as sooner means higher points
                score+=100; //based on how many solid points you gain for each brick in a level (cumulative)
                break;
            case 2:
                scoreToAdd = 200 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlTwo += scoreToAdd;
                score+=200;
                break;
            case 3:
                scoreToAdd = 300 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlThree += scoreToAdd;
                score+=300;
                break;
            case 4:
                scoreToAdd = 400 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlFour += scoreToAdd;
                score+=400;
                break;
            case 5:
                scoreToAdd = 500 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlFive += scoreToAdd;
                score+=500;
                break;
            case 6:
                scoreToAdd = 600 / checkScoreDenominator(getLevelTimePlayed());
                scoreLvlSix += scoreToAdd;
                score += 600;
                break;
        }
    }

    /**
     * This method determines the denominator in which users level score will be divided by.
     * @param timePlayed The current ongoing timer for the gameplay.
     * @return Returns an integer which is the denominator.
     */
    private int checkScoreDenominator(int timePlayed){
        if(timePlayed>0 && timePlayed<30){
            return 1;
        }else if(timePlayed>=30 && timePlayed<60){
            return 2;
        }else if(timePlayed>=60 && timePlayed<100){
            return 3;
        }else{
            return 4;
        }
    }

    /**
     * This method is used to understand the ball movement and how it causes cracks on the bricks on impact.
     * @return Returns a boolean value depending on whether the ball made impact with the wall or not (true if impact, else false).
     */
    private boolean impactWall(){ //to handle how ball moves and causes cracks on impact with brick wall
        for(Brick b : bricks){
            int UP_IMPACT = Brick.getUpImpact();
            int DOWN_IMPACT = Brick.getDownImpact();
            int LEFT_IMPACT = Brick.getLeftImpact();
            int RIGHT_IMPACT = Brick.getRightImpact();

            if (b.findImpact(ball)==UP_IMPACT){
                ball.reverseY(); //reverse Y movement
                return b.setImpact(ball.getDown(), Crack.getUP());
            }else if(b.findImpact(ball)==DOWN_IMPACT){
                ball.reverseY(); //reverse Y movement
                return b.setImpact(ball.getUp(), Crack.getDOWN());
            }else if(b.findImpact(ball)==LEFT_IMPACT){
                ball.reverseX(); //fireball must have this but not being reversed X or Y for all cases
                return b.setImpact(ball.getRight(), Crack.getRIGHT());
            }else if(b.findImpact(ball)==RIGHT_IMPACT){
                ball.reverseX();
                return b.setImpact(ball.getLeft(), Crack.getLEFT());
            }
        }
        return false;
    }

    /**
     * This is to see if the ball had gone beyond the range of the border.
     * @return Returns true if the ball had gone beyond the range of the border.
     */
    private boolean impactBorder(){
        Point2D p = ball.getPosition();
        return ((p.getX() < area.getX()) ||(p.getX() > (area.getX() + area.getWidth())));
    }

    /**
     * Getter for the number of bricks available on the Wall above.
     * @return Returns the number of bricks available on the wall above.
     */
    public int getBrickCount(){
        return brickCount;
    }

    /**
     * Getter for the number of balls available for the level.
     * @return Returns Int, the number of balls available for the level.
     */
    public int getBallCount(){
        return ballCount;
    }

    /**
     * Method to identify if a ball was lost in a level.
     * @return Returns true if ball was lost, else false.
     */
    public boolean isBallLost(){
        return ballLost;
    }

    /**
     * This method is used to respawn a ball and set their speed when a ball is lost.
     */
    public void ballReset(){
        player.moveTo(startPoint);
        ball.moveTo(startPoint);
        if(level==5){
            if(ballCount==2){
                ball.setSpeed(3,-3);
            }else if(ballCount==1){
                ball.setSpeed(4,-4);
            }
        }else{
            ball.setSpeed(2,-2);
        }
        ballLost = false;
    }

    /**
     * This method is used to Reset the wall structure and form the bricks again, it also resets the number of balls to its original value.
     */
    public void wallReset(){ //to bring back wall formation
        for(Brick b : bricks)
            b.repair();
        brickCount = bricks.length;
        ballCount = 3;
    }

    /**
     * This method is used to ensure that the length of the player bar is being reset on method call.
     */
    public void playerReset(){
        player.resetWidth();
    }

    /**
     * This method is used to check if all the balls are lost.
     * @return Returns true if all balls are lost, else returns false.
     */
    public boolean ballEnd(){
        return ballCount == 0;
    } //returns true if no more balls left

    /**
     * This method is used to check if all the bricks are broken.
     * @return Returns true if all bricks are broken, else returns false.
     */
    public boolean isDone(){
        return brickCount == 0;
    } //returns true if all bricks broken

    /**
     * This method is used to advance to the next level.
     */
    public void nextLevel(){
        bricks = levels[level++]; //advance in level (increase index)
        this.brickCount = bricks.length;
    }

    /**
     * This method is used to check if there are more levels left to advance.
     * @return Returns true if there are more levels available, else returns false.
     */
    public boolean hasLevel(){ //see if user has more levels to complete
        return level < levels.length;
    }

    /**
     * Setter to set the speed of the ball in X-axis.
     * @param s refers to the speed of ball we want to set in X-axis.
     */
    public void setBallXSpeed(int s){
        ball.setXSpeed(s);
    }

    /**
     * Setter to set the speed of the ball in Y-axis.
     * @param s refers to the speed of ball we want to set in Y-axis.
     */
    public void setBallYSpeed(int s){
        ball.setYSpeed(s);
    }

    /**
     * This method is used to reset the ball count for a given level back to its original value.
     */
    public void resetBallCount(){
        ballCount = 3;
    }

    /**
     * This method is used to increment the time the user had played the game.
     */
    public void incrementTime(){
        levelTimePlayed++;
    }

    /**
     * This method is used to access the user's time played from any class.
     * @return Returns the time in seconds.
     */
    public int getLevelTimePlayed() {
        int seconds = levelTimePlayed/100;
        return seconds;
    }

    /**
     * This method is used as a setter to set the time the user had played the game.
     * @param timePlayed The time the user had played the game.
     */
    public void setLevelTimePlayed(int timePlayed) {
        this.levelTimePlayed = timePlayed;
    }

    /**
     * This method is used to get the user's current score.
     * @return Int type score of user.
     */
    public int getScore() {
        return score;
    }

    /**
     * This method is used to set the user's score.
     * @param score New score to set score to.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * This method is called to reset the scores of all levels played by user.
     */
    public void resetLevelScores(){
        this.scoreLvlOne = 0;
        this.scoreLvlTwo = 0;
        this.scoreLvlThree = 0;
        this.scoreLvlFour = 0;
        this.scoreLvlFive = 0;
        this.scoreLvlSix = 0;
    }

    /**
     * This method ensures that all the in-game components reset when the level is skipped.
     */
    public void skipLevel(){
        ballReset();
        wallReset();
        nextLevel();
        setScore(0);
        resetLevelScores();
        player.resetWidth();
        setLevelTimePlayed(0);
        System.out.println("Current Level - " + level);
    }

    /**
     * Method to access current level number from other classes.
     * @return return level number.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Method to return score for level one.
     * @return return level one score.
     */
    public int getScoreLvlOne() {
        return scoreLvlOne;
    }

    /**
     * Method to return score for level two.
     * @return return level two score.
     */
    public int getScoreLvlTwo() {
        return scoreLvlTwo;
    }

    /**
     * Method to return score for level three.
     * @return return level three score.
     */
    public int getScoreLvlThree() {
        return scoreLvlThree;
    }

    /**
     * Method to return score for level four.
     * @return return level four score.
     */
    public int getScoreLvlFour() {
        return scoreLvlFour;
    }

    /**
     * Method to return score for level five.
     * @return return level five score.
     */
    public int getScoreLvlFive() {
        return scoreLvlFive;
    }

    /**
     * Method to return score for level six.
     * @return return level six score.
     */
    public int getScoreLvlSix() {return scoreLvlSix;}

    /**
     * Method to get the ball object from other classes.
     * @return returns the ball object.
     */
    public Ball getBall() {
        return ball;
    }

}