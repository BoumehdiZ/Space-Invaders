// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import ressources.Chrono;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Vaisseau extends Entite
{
    private int compteur;
    
    public Vaisseau() {
        this.compteur = 0;
        super.xPos = 280;
        super.yPos = 490;
        super.largeur = 39;
        super.hauteur = 24;
        super.dx = 0;
        super.dy = 0;
        super.vivant = true;
        super.strImg1 = "/images/vaisseau.png";
        super.strImg2 = "/images/vaisseauDetruit1.png";
        super.strImg3 = "/images/vaisseauDetruit2.png";
        super.ico = new ImageIcon(this.getClass().getResource(super.strImg1));
        super.img = this.ico.getImage();
    }
    
    public int deplacementVaisseau() {
        if (this.dx < 0) {
            if (this.xPos > 60) {
                this.xPos += this.dx;
            }
        }
        else if (this.dx > 0 && this.xPos + this.dx < 500) {
            this.xPos += this.dx;
        }
        return this.xPos;
    }
    
    public void dessinVaisseau(final Graphics g) {
        if (!this.vivant) {
            this.destructionVaisseau();
        }
        g.drawImage(this.img, this.deplacementVaisseau(), this.yPos, null);
    }
    
    public void destructionVaisseau() {
        if (this.compteur < 300) {
            if (Chrono.compteTours % 2 == 0) {
                super.ico = new ImageIcon(this.getClass().getResource(this.strImg2));
            }
            else {
                super.ico = new ImageIcon(this.getClass().getResource(this.strImg3));
            }
            ++this.compteur;
        }
        super.img = this.ico.getImage();
    }
}
