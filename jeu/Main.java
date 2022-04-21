// 
// Decompiled by Procyon v0.5.36
// 

package jeu;

import java.awt.Container;
import java.awt.Component;
import javax.swing.JFrame;

public class Main
{
    public static Scene scene;
    
    public static void main(final String[] args) {
        final JFrame fenetre = new JFrame("Space Invaders");
        fenetre.setSize(600, 600);
        fenetre.setResizable(false);
        fenetre.setLocationRelativeTo(null);
        fenetre.setDefaultCloseOperation(3);
        fenetre.setAlwaysOnTop(true);
        fenetre.setContentPane(Main.scene = new Scene());
        fenetre.setVisible(true);
    }
}
