package brickdestroy.levels;

import brickdestroy.game.GameBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * This class is responsible for the handling and displaying of the permanent high scores to the game frame.
 * It also displays the ending total high score of the user (even if it is not in the high score leader board).
 *
 * Extensions - Added a background image for the leader board.
 */

public class HighScore {
    private Font menuFont;
    private Font headFont;
    private Font buttonFont;
    private static final String HIGH_SCORE_TEXT = "HIGH SCORE BOARD";
    private static final String SCORE_EXIT_TEXT = "Exit Game";
    private static final int TEXT_SIZE = 30;
    private static final int TOP_SCORES = 5;

    Writer writer;
    private java.util.List<Integer> scoresFromFile;
    private GameBoard gameBoard;
    private Rectangle scoreExitButtonRect;
    private Image backGroundImage;

    /**
     * This is the constructor for the HighScore class.
     * @param gameBoard The GameBoard object where the game is setup,
     * @throws IOException In case the highscore.txt file cannot be found.
     */
    public HighScore(GameBoard gameBoard) throws IOException {
        headFont = new Font("Noto Mono", Font.BOLD, 40);
        menuFont = new Font("Monospaced",Font.PLAIN,TEXT_SIZE);
        buttonFont = new Font("Noto Mono", Font.BOLD, 20);
        scoresFromFile = new ArrayList<Integer>();
        writer = new BufferedWriter(new FileWriter("src/main/resources/highscore.txt", true));
        this.gameBoard = gameBoard;
        Dimension btnDim = new Dimension(gameBoard.getWidth()/3, gameBoard.getHeight()/12);
        scoreExitButtonRect = new Rectangle(btnDim);
        backGroundImage = new ImageIcon("src/main/resources/backGroundImage.jpg").getImage();
    }

    /**
     * This method is responsible for drawing the High Score Screen that is shown when the user either completes or loses the game.
     * @param g2d The Graphics 2d frame in which we want to draw the game components.
     */
    public void drawHighScoreScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        FontRenderContext frc = g2d.getFontRenderContext();
        g2d.setFont(buttonFont);

        g2d.drawImage(backGroundImage, 0, 0, null);

        Rectangle2D headingRect = menuFont.getStringBounds(HIGH_SCORE_TEXT,frc);

        int sX,sY;
        sX = (int)(gameBoard.getWidth() - headingRect.getWidth()) / 2;
        sY = gameBoard.getHeight() / 6;

        g2d.drawString("Your high score was " + gameBoard.getScoreFromGameplay() + "!", sX+30, sY-40);

        g2d.setFont(headFont);

        g2d.drawString(HIGH_SCORE_TEXT,sX-50,sY+20); //draw the string "High Score"

        //displaying high scores
        int score_x, score_y;

        score_x = sX + 110;
        score_y = sY + 70;

        g2d.setColor(new Color(141, 50, 5));
        g2d.setFont(menuFont);

        if (scoresFromFile.size()>TOP_SCORES){
            for(int i=0;i<TOP_SCORES;i++){
                String scoreString = String.valueOf(scoresFromFile.get(i));
                g2d.drawString(scoreString, score_x, score_y);
                score_y += 50;
            }
        } else{
            for (Integer integer : scoresFromFile) {
                String scoreString = String.valueOf(integer);
                g2d.drawString(scoreString, score_x, score_y);
                score_y += 30;
            }
        }

        //exit button
        g2d.setColor(Color.BLACK);
        g2d.setFont(buttonFont); //set the font

        Rectangle2D menuTxtRect = headFont.getStringBounds(SCORE_EXIT_TEXT,frc);

        Dimension btnDim = new Dimension(gameBoard.getWidth()/3, gameBoard.getHeight()/12);
        scoreExitButtonRect = new Rectangle(btnDim); //button draws when it is inside method

        //coordinates for exit button
        int x = (gameBoard.getWidth() - scoreExitButtonRect.width) / 2;
        int y =(int) ((gameBoard.getHeight() - scoreExitButtonRect.height) * 0.8);

        scoreExitButtonRect.setLocation(x, y + 40);

        //get the location of the string for start button
        x = (int)(scoreExitButtonRect.getWidth() - menuTxtRect.getWidth()) / 2;
        y = (int)(scoreExitButtonRect.getHeight() - menuTxtRect.getHeight() - 40) / 2;

        x += scoreExitButtonRect.x;
        y += scoreExitButtonRect.y + (scoreExitButtonRect.height * 0.9);

        g2d.draw(scoreExitButtonRect); //leave as it is
        g2d.drawString(SCORE_EXIT_TEXT,x+53,y+20); //with normal coordinate found above

        if(gameBoard.isScoreExitClicked()){ //change color, redraw button and more...
            System.out.println("Thank you for playing Brick Game!");
            System.exit(0);
        }
    }

    /**
     * This method is used to find the top 5 permanent high scores for the game.
     * @throws FileNotFoundException This is in case the method is unable to access the file highscore.txt which records the scores.
     */
    public void findHighScore() throws FileNotFoundException {
        Scanner inputScore = new Scanner(new File("src/main/resources/highscore.txt"));

        while(inputScore.hasNext()){
            scoresFromFile.add(Integer.parseInt(inputScore.next()));
        }
        scoresFromFile.sort(Collections.reverseOrder());

        if(scoresFromFile.size()>TOP_SCORES){
            scoresFromFile = scoresFromFile.subList(0,6);
        }
    }

    /**
     * This method returns the exit button rectangle used in the high score screen.
     * @return Returns the exit button in high score screen.
     */
    public Rectangle getScoreExitButtonRect() {
        return scoreExitButtonRect;
    }

    /**
     * This method is used to return the writer which writes data into the highscore.txt file.
     * @return Returns the writer.
     */
    public Writer getWriter() {
        return writer;
    }
}