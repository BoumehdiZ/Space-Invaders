// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import java.awt.Image;
import javax.swing.ImageIcon;

public abstract class Entite
{
    protected int largeur;
    protected int hauteur;
    protected int xPos;
    protected int yPos;
    protected int dx;
    protected int dy;
    protected boolean vivant;
    protected String strImg1;
    protected String strImg2;
    protected String strImg3;
    protected ImageIcon ico;
    protected Image img;
    
    public int getLargeur() {
        return this.largeur;
    }
    
    public void setLargeur(final int largeur) {
        this.largeur = largeur;
    }
    
    public int getHauteur() {
        return this.hauteur;
    }
    
    public void setHauteur(final int hauteur) {
        this.hauteur = hauteur;
    }
    
    public int getxPos() {
        return this.xPos;
    }
    
    public void setxPos(final int xPos) {
        this.xPos = xPos;
    }
    
    public int getyPos() {
        return this.yPos;
    }
    
    public void setyPos(final int yPos) {
        this.yPos = yPos;
    }
    
    public int getDx() {
        return this.dx;
    }
    
    public void setDx(final int dx) {
        this.dx = dx;
    }
    
    public int getDy() {
        return this.dy;
    }
    
    public void setDy(final int dy) {
        this.dy = dy;
    }
    
    public boolean isVivant() {
        return this.vivant;
    }
    
    public void setVivant(final boolean vivant) {
        this.vivant = vivant;
    }
    
    public String getStrImg1() {
        return this.strImg1;
    }
    
    public void setStrImg1(final String strImg1) {
        this.strImg1 = strImg1;
    }
    
    public String getStrImg2() {
        return this.strImg2;
    }
    
    public void setStrImg2(final String strImg2) {
        this.strImg2 = strImg2;
    }
    
    public String getStrImg3() {
        return this.strImg3;
    }
    
    public void setStrImg3(final String strImg3) {
        this.strImg3 = strImg3;
    }
    
    public ImageIcon getIco() {
        return this.ico;
    }
    
    public void setIco(final ImageIcon ico) {
        this.ico = ico;
    }
    
    public Image getImg() {
        return this.img;
    }
    
    public void setImg(final Image img) {
        this.img = img;
    }
}
