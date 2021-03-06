package brickdestroy.levels;

import brickdestroy.brick.*;

import java.awt.*;

/**
 * This class is responsible for setting up the different brick formations (Wall setup) and types of bricks for each Level in the game.
 *
 * Extensions  -
 * Added new type of brick (ColorBrick) for the "makeBrick" method.
 * Added new level for the "makeLevels" method which uses Color Bricks and Steel Bricks.
 */
public class WallSetup {

    private static final int LEVELS_COUNT = 5; //number of levels in the game
    private static final int CLAY = 1;
    private static final int STEEL = 2;
    private static final int CEMENT = 3;
    private static final int COLOR = 4;

    private Brick[][] levelsMade;

    /**
     * This is the constructor for WallSetup class.
     * @param drawArea The area in which the game will be played.
     * @param brickCount The number of bricks on the wall.
     * @param lineCount The number of lines of bricks in the game.
     * @param brickDimensionRatio The dimension ratio of a single brick.
     */
    public WallSetup(Rectangle drawArea, int brickCount, int lineCount, double brickDimensionRatio){
        levelsMade = makeLevels(drawArea, brickCount, lineCount, brickDimensionRatio); //returns Brick[][]
    }

    /**
     * This method is used to form the Brick GamePlay for a particular level where only one type of Brick is used.
     * @param drawArea drawArea is the Rectangle in which the level is being setup.
     * @param brickCnt this represents the total number of bricks on the brick wall.
     * @param lineCnt this represents the number of brick lines on the wall.
     * @param brickSizeRatio this represents the height to width ratio of a singular brick.
     * @param type this Integer represents the corresponding level we want to set up.
     * @return Returns an array of type Brick to setup the wall.
     */
    private Brick[] makeSingleTypeLevel(Rectangle drawArea, int brickCnt, int lineCnt, double brickSizeRatio, int type){

        brickCnt -= brickCnt % lineCnt; //see how many bricks we can use
        int brickOnLine = brickCnt / lineCnt; //how many bricks on one line
        double brickLen = drawArea.getWidth() / brickOnLine; //length of one brick
        double brickHgt = brickLen / brickSizeRatio; //height of one brick
        brickCnt += lineCnt / 2; //30 += 3/2 => 31
        Brick[] tmp  = new Brick[brickCnt]; //make array to store brick objects, stores 31 bricks in array

        Dimension brickSize = new Dimension((int) brickLen,(int) brickHgt); //width and height
        //dimension encapsulates width and height of object in one object
        Point p = new Point();
        int i;
        for(i = 0; i < tmp.length; i++){
            int line = i / brickOnLine;
            if(line == lineCnt)
                break;
            double x = (i % brickOnLine) * brickLen;
            x =(line % 2 == 0) ? x : (x - (brickLen / 2));
            double y = (line) * brickHgt;
            p.setLocation(x,y);
            tmp[i] = makeBrick(p,brickSize,type);
        }

        for(double y = brickHgt;i < tmp.length;i++, y += 2*brickHgt){
            double x = (brickOnLine * brickLen) - (brickLen / 2);
            p.setLocation(x,y);
            if(type == 1){
                tmp[i] = new ClayBrick(p,brickSize); //set this brick to clay
            }else if(type == 4){
                tmp[i] = new ColorBrick(p,brickSize); //set this brick to color brick
            }

        }
        return tmp;
    }

    /**
     * This method is used to form the Brick Wall for a particular level where two types of Bricks are being used.
     * @param drawArea drawArea is the Rectangle in which the level is being setup.
     * @param brickCnt this represents the total number of bricks on the brick wall.
     * @param lineCnt this represents the number of brick lines on the wall.
     * @param brickSizeRatio this represents the height to width ratio of a singular brick.
     * @param typeA this represents the first type of Brick being used.
     * @param typeB this represents the second type of Brick being used.
     * @return Returns an array of Bricks with the different types of bricks to be set up as the Wall above.
     */
    private Brick[] makeChessboardLevel(Rectangle drawArea, int brickCnt, int lineCnt, double brickSizeRatio, int typeA, int typeB){
        brickCnt -= brickCnt % lineCnt;

        int brickOnLine = brickCnt / lineCnt;
        int centerLeft = brickOnLine / 2 - 1;
        int centerRight = brickOnLine / 2 + 1;
        double brickLen = drawArea.getWidth() / brickOnLine;
        double brickHgt = brickLen / brickSizeRatio;

        brickCnt += lineCnt / 2;
        Brick[] tmp  = new Brick[brickCnt];
        Dimension brickSize = new Dimension((int) brickLen,(int) brickHgt);
        Point p = new Point();

        int i;
        for(i = 0; i < tmp.length; i++){
            int line = i / brickOnLine;
            if(line == lineCnt)
                break;
            int posX = i % brickOnLine;
            double x = posX * brickLen;
            x =(line % 2 == 0) ? x : (x - (brickLen / 2));
            double y = (line) * brickHgt;
            p.setLocation(x,y);

            boolean b = ((line % 2 == 0 && i % 2 == 0) || (line % 2 != 0 && posX > centerLeft && posX <= centerRight));
            tmp[i] = b ?  makeBrick(p,brickSize,typeA) : makeBrick(p,brickSize,typeB);
        }

        for(double y = brickHgt;i < tmp.length;i++, y += 2*brickHgt){
            double x = (brickOnLine * brickLen) - (brickLen / 2);
            p.setLocation(x,y);
            tmp[i] = makeBrick(p,brickSize,typeA);
        }
        return tmp;
    }

    /**
     * This method initializes all of the different levels we have in the game.
     * @param drawArea drawArea is the Rectangle in which the level is being setup.
     * @param brickCount this represents the total number of bricks on the brick wall.
     * @param lineCount this represents the number of brick lines on the wall.
     * @param brickDimensionRatio this represents the height to width ratio of a singular brick.
     * @return Returns Brick[][] type which consists of all the different levels we have in the game which can be accessed by indexing.
     */
    private Brick[][] makeLevels(Rectangle drawArea,int brickCount,int lineCount,double brickDimensionRatio){
        Brick[][] tmp = new Brick[LEVELS_COUNT][];
        tmp[0] = makeSingleTypeLevel(drawArea,brickCount,lineCount,brickDimensionRatio,CLAY);
        tmp[1] = makeChessboardLevel(drawArea,brickCount,lineCount,brickDimensionRatio,CLAY,CEMENT);
        tmp[2] = makeChessboardLevel(drawArea,brickCount,lineCount,brickDimensionRatio,CLAY,STEEL);
        tmp[3] = makeChessboardLevel(drawArea,brickCount,lineCount,brickDimensionRatio,STEEL,CEMENT);
        tmp[4] = makeChessboardLevel(drawArea,brickCount,lineCount,brickDimensionRatio,STEEL,COLOR);
        return tmp;
    }

    /**
     * This method is used to make any type of brick for the levels.
     * @param point Represents the top left coordinate of the rectangle (Brick).
     * @param size Represents the width and height of the Brick.
     * @param type Represents the type of brick being used (Clay, Cement, SteeL, Color).
     * @return Returns a Brick type to be created.
     */
    private Brick makeBrick(Point point, Dimension size, int type){ //for making new bricks/brick types
        Brick out;
        switch(type){
            case CLAY:
                out = new ClayBrick(point,size);
                break;
            case STEEL:
                out = new SteelBrick(point,size);
                break;
            case CEMENT:
                out = new CementBrick(point, size);
                break;
            case COLOR:
                out = new ColorBrick(point, size);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown Type:%d\n",type));
        }
        return out;
    }

    /**
     * This method is used to get the levels created in the form of Brick[][]
     * @return returns Brick[][] of the levels created.
     */
    public Brick[][] getLevelsMade() { //encapsulation
        return levelsMade;
    }
}
