// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import javax.swing.ImageIcon;

public class Alien extends Entite
{
    public Alien(final int xPos, final int yPos, final String strImg1, final String strImg2) {
        super.xPos = xPos;
        super.yPos = yPos;
        super.largeur = 33;
        super.hauteur = 25;
        super.dx = 0;
        super.dy = 0;
        super.vivant = true;
        super.strImg1 = strImg1;
        super.strImg2 = strImg2;
        super.strImg3 = "/images/alienMeurt.png";
        super.ico = new ImageIcon(this.getClass().getResource(super.strImg1));
        super.img = this.ico.getImage();
    }
    
    public void choixImage(final boolean pos1) {
        if (this.vivant) {
            if (pos1) {
                super.ico = new ImageIcon(this.getClass().getResource(this.strImg1));
            }
            else {
                super.ico = new ImageIcon(this.getClass().getResource(this.strImg2));
            }
        }
        else {
            super.ico = new ImageIcon(this.getClass().getResource(this.strImg3));
        }
        super.img = this.ico.getImage();
    }
}
