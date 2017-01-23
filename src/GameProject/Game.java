/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameProject;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.util.Random;

/**
 *
 * @author Vesa
 */
public class Game extends JPanel {
    // A variable for the gameboard
    private Tile tiles[][];
    // How many rows and cols there is
    private final int rowsCols = 4;
    // Margin for the tiles. Gets multiplied when specifying tile offsets
    private final int margin = 10;
    // Tile size and center, used for tile drawing
    private final int tileSize = 86;
    private final int tileCenter = tileSize/2;
    // Variables for scores and tile monitoring
    private int score = 0;
    private int bestScore = 0;
    private int numberOfTiles = 0;
    // Tile offsets. Determines where to draw the tiles
    private final int offset[] = {margin, (tileSize+margin*2), (tileSize*2+margin*3), (tileSize*3+margin*4)};
    // Different booleans to check things
    private boolean isWon = false;
    private boolean isLost = false;
    private boolean isMoved = false;
    private boolean isChecked = false;
    // A method to start the game. Reads best score from file, adds keytracker, sets window size etc.
    public Game() {   
        setBackground(new Color(0xBBADA0));
        setSize(400, 460);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyTracker());
        tiles = new Tile[rowsCols][rowsCols];
        resetGame();
        readFile();
    }
    // A method to reset the game. Empties tiles and sets needed variables to true/false/0. Also adds two tiles at random position
    public final void resetGame() {
        for (int row = 0; row < rowsCols; row++) {
            for (int col = 0; col < rowsCols; col++) {
                if (tiles[row][col] != null) {
                    remove(tiles[row][col].getLabel());
                }
            }
        }
        
        tiles = new Tile[rowsCols][rowsCols];
        numberOfTiles = 0;
        score = 0;
        isWon = false;
        isLost = false;
        isMoved = true;
        isChecked = false;
        addRandom(isMoved);
        addRandom(isMoved);
    }
    // A method to add two tiles at random position
    public void addRandom(boolean isMoved) {
        if (isMoved == true) {
            if (numberOfTiles < 16) {
                Random random = new Random();
                int row = random.nextInt(rowsCols);
                int col = random.nextInt(rowsCols);

                if (tiles[row][col] == null) {
                    if (Math.random() < 0.9) {
                        tiles[row][col] = new Tile(2);
                        numberOfTiles++;
                    }
                    else {
                        tiles[row][col] = new Tile(4);
                        numberOfTiles++;
                    }
                }
                else {
                    addRandom(isMoved);
                }
            }
        }
    }
    // Paints the game
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawTiles(g2d);
        check();
        drawEndScreen(g2d); 
    }
    // A method used to draw tiles
    public void drawTiles(Graphics g2d) {
        // FontMetrics is used to get the width and height of the label of tile for better drawing
        FontMetrics fm = getFontMetrics(new Font("Arial", Font.BOLD, 30));
        int labelWidth;
        int labelHeight;
        JLabel label;
        
        for (int row = 0; row < rowsCols; row++) {
            int yOffset = offset[row];
            
            for (int col = 0; col < rowsCols; col++) {
                int xOffset = offset[col];
                
                if (tiles[row][col] == null) {
                    g2d.setColor(new Color(0xCDC1B4));
                    g2d.fillRoundRect(xOffset, yOffset, tileSize, tileSize, 10, 10);
                }
                else {
                    // Gets the label and sets it's location
                    label = tiles[row][col].getLabel();
                    labelWidth = (int) fm.stringWidth(String.valueOf(tiles[row][col].getValue()));
                    labelHeight = (int) fm.getHeight();
                    label.setLocation(xOffset+tileCenter-(labelWidth/2), yOffset+tileCenter-(labelHeight/2));
                    // Draws tiles and adds label to it
                    g2d.setColor(tiles[row][col].getColor());
                    g2d.fillRoundRect(xOffset, yOffset, tileSize, tileSize, 10, 10);
                    add(label);
                    // Draws scores to bottom of the window
                    g2d.setColor(new Color(0xFFFFFF));
                    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                    g2d.drawString("Score: " + score, 25, 415);
                    g2d.drawString("Best: " + bestScore, 250, 415);
                }
            }
        }
    }
    // A method used for end screen drawing
    public void drawEndScreen(Graphics g2d) {
        if (isWon || isLost) {         
            g2d.setColor(new Color(255, 255, 255, 110));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2d.setColor(new Color(0x776E65));
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Press ESC to restart.", 104, (this.getHeight()-this.getHeight()/3-10));
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
        }   
        if (isWon) {
            g2d.drawString("You won!", 115, this.getHeight()/2);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Press Enter to continue.", 89, (this.getHeight()-this.getHeight()/3+20));
        }
        if (isLost) {
            g2d.drawString("You lost!", 115, this.getHeight()/2);
        }
    }
    // Tracks user inputs. Also adds one new random tile and checks if current score is better than best score.
    // Game resetting and score writing to file also happens here
    // Doesn't add any new tile, if any movement didn't happen
    class KeyTracker extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            isMoved = false;
            
            if (isWon || isLost) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    writeFile();
                    resetGame();
                }
            }
            if (isWon) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    isWon = false;
                    isChecked = true;
                    repaint();
                }
            }
            if (!isWon && !isLost) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        isMoved = moveLeftRight(0, 4, 1);
                        mergeLeftRight(0, 3, 1);
                        moveLeftRight(0, 4, 1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        isMoved = moveLeftRight(3, -1, -1);
                        mergeLeftRight(3, 0, -1);
                        moveLeftRight(3, -1, -1);
                        break;
                    case KeyEvent.VK_UP:
                        isMoved = moveUpDown(0, 4, 1);
                        mergeUpDown(0, 3, 1);
                        moveUpDown(0, 4, 1);
                        break;
                    case KeyEvent.VK_DOWN:
                        isMoved = moveUpDown(3, -1, -1);
                        mergeUpDown(3, 0, -1);
                        moveUpDown(3, -1, -1);
                        break;
                }
            }
            if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                addRandom(isMoved);
            } 
            repaint();
            if (score > bestScore) {
                bestScore = score;
            }
        }
    }
    // A method used to move tiles left or right
    public boolean moveLeftRight(int start, int end, int increment) {
        ArrayList<Tile> tempRow;
        int row;
        int col;
        int next;
        
        for (row=0; row<rowsCols; row++) {
            tempRow = new ArrayList<>();
            
            for (col = start; col != end; col += increment) {
                if (tiles[row][col] != null){
                    tempRow.add(tiles[row][col]);
                }
            }

            next = 0;

            for (col = start; col != end; col += increment) {
                try {
                    if (tempRow.get(next) != tiles[row][col]) {
                        isMoved = true;
                        tiles[row][col] = tempRow.get(next);
                    }
                } catch (IndexOutOfBoundsException e) {
                    tiles[row][col] = null;
                }
                next++;
            }
        } 
        return isMoved;
    }
    // A method used to move tiles up or down
    public boolean moveUpDown(int start, int end, int increment) {
        ArrayList<Tile> tempRow;
        int row;
        int col;
        int next;
        
        for (col=0; col<rowsCols; col++) {
            
            tempRow = new ArrayList<>();
            
            for (row = start; row != end; row += increment) {
                if (tiles[row][col] != null) {
                    tempRow.add(tiles[row][col]);
                }
            }

            next = 0;

            for (row = start; row != end; row += increment) {
                try {
                    if (tempRow.get(next) != tiles[row][col]) {
                        isMoved = true;
                        tiles[row][col] = tempRow.get(next);
                    }
                } catch (IndexOutOfBoundsException e) {
                    tiles[row][col] = null;
                }
                next++;
            }
        } 
        return isMoved;
    }
    // A method used to merge tiles from right to left or left to right
    public void mergeLeftRight(int start, int end, int increment) {
        for (int row = 0; row < rowsCols; row++) {
            int col = start;
            try {
                while (col != end) {
                    if (tiles[row][col] != null && tiles[row][col+increment] != null && tiles[row][col].getValue() == tiles[row][col+increment].getValue()) {
                        tiles[row][col].doubleTileValue();
                        score = score + tiles[row][col].getValue();
                        remove(tiles[row][col+increment].getLabel());
                        tiles[row][col+increment] = null;
                        numberOfTiles = numberOfTiles-1;
                        isMoved = true;
                        col = col+(2*increment);
                    }
                    else {
                        col = col+increment;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {      
            }
        }
    }
    // A method used to merge tiles from down to up or up to down
    public void mergeUpDown(int start, int end, int increment) {
        for (int col = 0; col < rowsCols; col++) {
            int row = start;
            try {
                while (row != end) {
                    if (tiles[row][col] != null && tiles[row+increment][col] != null && tiles[row][col].getValue() == tiles[row+increment][col].getValue()) {
                        tiles[row][col].doubleTileValue();
                        score = score + tiles[row][col].getValue();
                        remove(tiles[row+increment][col].getLabel());
                        tiles[row+increment][col] = null;
                        numberOfTiles = numberOfTiles-1;
                        isMoved = true;
                        row = row+(2*increment);
                    }
                    else {
                        row = row+increment;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {      
            }
        }
    }
    // A method that goes through a few checks. Sets different booleans to different values depending on if the game is won or lost. Doesn't check for victory,
    // if the game was won once and player wanted to continue
    public void check() {
        if (!isChecked) {
            for (int row = 0; row < rowsCols; row++) {
                for (int col = 0; col < rowsCols; col++) {
                    if (tiles[row][col] != null && tiles[row][col].getValue() == 2048) {
                        isWon = true;
                    }
                }
            }
        }
        if (!isWon) {
            if (numberOfTiles == 16) {
                isLost = true;
                for (int row = 0; row < rowsCols; row++) {
                    try {
                        for (int col = 0; col < rowsCols; col++) {
                            if (row < 3) {
                                if (tiles[row][col].getValue() == tiles[row+1][col].getValue() || tiles[row][col].getValue() == tiles[row][col+1].getValue()) {
                                    isLost = false;
                                }
                            }
                            // This was needed to get the last row and column to work
                            else {
                                if (tiles[row][col].getValue() == tiles[row][col+1].getValue()) {
                                    isLost = false;
                                }
                            }
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e){      
                    }
                }
            }
        }
    }
    // Writes the best score to file
    public final void writeFile() {
        FileWriter fw;
        BufferedWriter bw;
        
        try {
            fw = new FileWriter("score.txt");
            bw = new BufferedWriter(fw);
            bw.write(String.valueOf(bestScore));
            bw.close();
        } catch(IOException e){
            System.out.println("Can't create file.");
        }
    }
    // Reads the best score from file
    public final void readFile() {
        FileReader fr;
        BufferedReader br;
        
        try {
            fr = new FileReader("score.txt");
            br = new BufferedReader(fr);
            bestScore = Integer.parseInt(br.readLine());
            br.close();
        } catch(IOException e){
            System.out.println("File doesn't exist.");
        }
    }
}