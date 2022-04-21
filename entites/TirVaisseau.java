// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import jeu.Main;
import ressources.Audio;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class TirVaisseau extends Entite
{
    private boolean vaisseauTire;
    
    public TirVaisseau() {
        this.vaisseauTire = false;
        super.xPos = 0;
        super.yPos = 477;
        super.largeur = 3;
        super.hauteur = 13;
        super.dx = 0;
        super.dy = 2;
        super.strImg1 = "/images/tirVaisseau.png";
        super.strImg2 = "";
        super.strImg3 = "";
        super.ico = new ImageIcon(this.getClass().getResource(super.strImg1));
        super.img = this.ico.getImage();
    }
    
    public boolean isVaisseauTire() {
        return this.vaisseauTire;
    }
    
    public void setVaisseauTire(final boolean vaisseauTire) {
        this.vaisseauTire = vaisseauTire;
    }
    
    public int deplacementTirVaisseau() {
        if (this.vaisseauTire) {
            if (this.yPos > 0) {
                this.yPos -= 2;
            }
            else {
                this.vaisseauTire = false;
            }
        }
        return this.yPos;
    }
    
    public void dessinVaisseau(final Graphics g) {
        if (this.vaisseauTire) {
            g.drawImage(this.img, this.xPos, this.deplacementTirVaisseau(), null);
        }
    }
    
    public boolean tueAlien(final Alien alien) {
        if (this.yPos < alien.getyPos() + alien.getHauteur() && this.yPos + this.hauteur > alien.getyPos() && this.xPos + this.largeur > alien.getxPos() && this.xPos < alien.getxPos() + alien.getLargeur()) {
            Audio.playSound("/sons/sonAlienMeurt.wav");
            return true;
        }
        return false;
    }
    
    private boolean tirVaisseauAHauteurDeChateau() {
        return this.yPos < 454 && this.yPos + this.hauteur > 400;
    }
    
    private int chateauProche() {
        int numeroChateau = -1;
        for (int colonne = -1; numeroChateau == -1 && colonne < 4; numeroChateau = colonne) {
            ++colonne;
            if (this.xPos + this.largeur > 89 + colonne * 114 && this.xPos < 161 + colonne * 114) {}
        }
        return numeroChateau;
    }
    
    private int abscisseContactTirChateau(final Chateau chateau) {
        int xPosTirVaisseau = -1;
        if (this.xPos + this.largeur > chateau.getxPos() && this.xPos < chateau.getxPos() + 72) {
            xPosTirVaisseau = this.xPos;
        }
        return xPosTirVaisseau;
    }
    
    public int[] tirVaisseauToucheChateau() {
        final int[] tabRep = { -1, -1 };
        if (this.tirVaisseauAHauteurDeChateau()) {
            tabRep[0] = this.chateauProche();
            if (tabRep[0] != -1) {
                tabRep[1] = this.abscisseContactTirChateau(Main.scene.tabChateaux[tabRep[0]]);
            }
        }
        return tabRep;
    }
    
    public void tirVaisseauDetruitChateau(final Chateau[] tabChateaux) {
        final int[] tab = this.tirVaisseauToucheChateau();
        if (tab[0] != -1 && tabChateaux[tab[0]].trouveBrique(tabChateaux[tab[0]].trouveColonneChateau(tab[1])) != -1) {
            tabChateaux[tab[0]].casseBriques(tab[1]);
            this.yPos = -1;
        }
    }
    
    public boolean detruitSoucoupe(final Soucoupe soucoupe) {
        if (this.yPos < soucoupe.getyPos() + soucoupe.getHauteur() && this.yPos + this.hauteur > soucoupe.getyPos() && this.xPos + this.largeur > soucoupe.getxPos() && this.xPos < soucoupe.getxPos() + soucoupe.getLargeur()) {
            this.vaisseauTire = false;
            return true;
        }
        return false;
    }
}
