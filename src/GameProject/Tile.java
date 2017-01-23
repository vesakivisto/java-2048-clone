/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameProject;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vesa
 */
public class Tile {
    // Tile value
    private int value;
    // Tile label
    private final JLabel label;
    // Font to draw the label in
    private final Font font = new Font("Arial", Font.BOLD, 30);
    // Font colors. Start color is for generated tiles (2 and 4), other is for tiles that have value higher than 4
    private final Color start = new Color(0x776E65);
    private final Color other = new Color(0xF9F6F2);
    // Constructor for tiles
    public Tile(int value) {
        this.value = value;
        this.label = new JLabel(Integer.toString(value));
        this.label.setSize(40, 40);
        this.label.setFont(font);
        this.label.setForeground(start);
    }
    // Returns the value of a tile
    public int getValue() {
        return this.value;
    }
    // Returns the label of a tile
    public JLabel getLabel() {
        return this.label;
    }
    // Returns the font of a tile
    public Font getFont() {
        return this.font;
    }
    // A method to return tile's background color.
    public Color getColor() {
        switch(this.value) {
            case 2: return new Color(0xEEE4DA);
            case 4: return new Color(0xEDE0C8);
            case 8: return new Color(0xF2B179);
            case 16: return new Color(0xF59563);
            case 32: return new Color(0xF67C5F);
            case 64: return new Color(0xF65E3B);
            case 128: return new Color(0xEDCF72);
            case 256: return new Color(0xEDCC61);
            case 512: return new Color(0xEDC850);
            case 1024: return new Color(0xEDC53F);
            case 2048: return new Color(0xEDC22E);
                
        }
        // Default color for null tiles
        return new Color(0xCDC1B4);
    }
    // A method to double the value of a tile. Also checks what font color should be used
    public void doubleTileValue() {
        value = value*2;
        label.setText(Integer.toString(value));
        if (value > 4) {
            label.setForeground(other);
        }  
    }
}