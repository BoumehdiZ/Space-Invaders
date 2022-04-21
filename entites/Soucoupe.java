// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import java.awt.image.ImageObserver;
import java.awt.Graphics;
import ressources.Chrono;
import javax.swing.ImageIcon;
import ressources.Audio;

public class Soucoupe extends Entite
{
    public Audio musiqueSoucoupe;
    public Audio musiqueDestructionSoucoupe;
    private int compteur;
    
    public Soucoupe() {
        this.musiqueSoucoupe = new Audio("/sons/sonSoucoupePasse.wav");
        this.musiqueDestructionSoucoupe = new Audio("/sons/sonDestructionSoucoupe.wav");
        this.compteur = 0;
        super.xPos = 600;
        super.yPos = 50;
        super.largeur = 42;
        super.hauteur = 22;
        super.dx = 1;
        super.dy = 0;
        super.vivant = true;
        super.strImg1 = "/images/soucoupe.png";
        super.strImg2 = "/images/soucoupe100.png";
        super.strImg3 = "";
        super.ico = new ImageIcon(this.getClass().getResource(super.strImg1));
        super.img = this.ico.getImage();
        this.musiqueSoucoupe.play();
        this.musiqueDestructionSoucoupe.stop();
        this.compteur = 0;
    }
    
    public int deplacementSoucoupe() {
        if (this.vivant && Chrono.compteTours % 2 == 0) {
            if (this.xPos > 0) {
                this.xPos -= this.dx;
            }
            else {
                this.xPos = 600;
            }
        }
        return this.xPos;
    }
    
    public void dessinSoucoupe(final Graphics g) {
        if (!this.vivant) {
            this.destructionSoucoupe();
        }
        g.drawImage(this.img, this.deplacementSoucoupe(), this.yPos, null);
    }
    
    public void destructionSoucoupe() {
        if (this.compteur < 300) {
            super.ico = new ImageIcon(this.getClass().getResource(this.strImg2));
            super.img = this.ico.getImage();
            ++this.compteur;
        }
        else {
            this.xPos = 600;
        }
    }
}
