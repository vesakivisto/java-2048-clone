/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameProject;

import javax.swing.JFrame;

/**
 *
 * @author Vesa
 */
public class Play extends JFrame {
    // Runs the game
    public static void main(String argc[]) {
        JFrame game = new JFrame();
        game.setTitle("2048");
        game.setSize(400, 460);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setResizable(false);
        game.setContentPane(new Game());
        game.setVisible(true);
    }
}