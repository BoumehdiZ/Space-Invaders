// 
// Decompiled by Procyon v0.5.36
// 

package ressources;

import java.io.IOException;
import entites.Chateau;
import entites.TirVaisseau;
import entites.Vaisseau;
import entites.GroupeAliens;
import jeu.Main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Clavier implements KeyListener
{
    public Fichier f1;
    
    public Clavier() {
        this.f1 = new Fichier();
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        if (Main.scene.start) {
            if (Main.scene.vaisseau.isVivant()) {
                if (e.getKeyCode() == 39) {
                    Main.scene.vaisseau.setDx(1);
                }
                else if (e.getKeyCode() == 37) {
                    Main.scene.vaisseau.setDx(-1);
                }
                else if (e.getKeyCode() == 32 && !Main.scene.tirVaisseau.isVaisseauTire()) {
                    Audio.playSound("/sons/sonTirVaisseau.wav");
                    Main.scene.tirVaisseau.setyPos(477);
                    Main.scene.tirVaisseau.setxPos(Main.scene.vaisseau.getxPos() + 19 - 1);
                    Main.scene.tirVaisseau.setVaisseauTire(true);
                }
            }
            else if (e.getKeyCode() == 10 && Main.scene.gameover) {
                Main.scene.musiqueGameOver.stop();
                Main.scene.start = false;
                Main.scene.gameover = false;
            }
        }
        else {
            if (e.getKeyCode() == 10 && !Main.scene.start) {
                Main.scene.musiqueIntro.stop();
                Main.scene.start = true;
                Main.scene.scoreModifier = false;
                Main.scene.groupeAliens = new GroupeAliens();
                Main.scene.vaisseau = new Vaisseau();
                Main.scene.tirVaisseau = new TirVaisseau();
                Main.scene.soucoupe = null;
                Main.scene.musiqueIntro = new Audio("/sons/musiqueIntro.wav");
                Main.scene.score = 0;
                Main.scene.vague = 1;
                Main.scene.vie = 3;
                Chrono.compteTours = 0;
                for (int colonne = 0; colonne < 4; ++colonne) {
                    Main.scene.tabChateaux[colonne] = new Chateau(89 + colonne * 114);
                }
            }
            if (e.getKeyCode() == 32) {
                Main.scene.scoreModifier = true;
                try {
                    this.f1.ouvrir("Score.txt", "ecriture");
                    this.f1.ecriture(0);
                    this.f1.fermer();
                }
                catch (IOException e2) {
                    System.out.println(e2);
                }
            }
        }
    }
    
    @Override
    public void keyReleased(final KeyEvent e) {
        Main.scene.vaisseau.setDx(0);
    }
    
    @Override
    public void keyTyped(final KeyEvent arg0) {
    }
}
