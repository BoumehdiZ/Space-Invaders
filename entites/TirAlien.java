// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import ressources.Audio;
import jeu.Main;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import ressources.Chrono;
import javax.swing.ImageIcon;
import java.util.Random;

public class TirAlien extends Entite
{
    Random hasard;
    
    public TirAlien(final int[] tabPositionAlien) {
        this.hasard = new Random();
        super.xPos = tabPositionAlien[0] + 16 - 1;
        super.yPos = tabPositionAlien[1] - 25;
        super.largeur = 5;
        super.hauteur = 15;
        super.dx = 0;
        super.dy = 3;
        super.strImg1 = "/images/tirAlien1.png";
        super.strImg2 = "/images/tirAlien2.png";
        super.strImg3 = "";
        if (this.hasard.nextInt(2) == 0) {
            super.ico = new ImageIcon(this.getClass().getResource(super.strImg1));
        }
        else {
            super.ico = new ImageIcon(this.getClass().getResource(super.strImg2));
        }
        super.img = this.ico.getImage();
    }
    
    public int deplacementTirAlien() {
        if (Chrono.compteTours % 4 == 0 && this.yPos < 600) {
            this.yPos += 3;
        }
        return this.yPos;
    }
    
    public void dessinTirAlien(final Graphics g) {
        g.drawImage(this.img, this.xPos, this.deplacementTirAlien(), null);
    }
    
    private boolean tirAlienAHauteurDeChateau() {
        return this.yPos < 454 && this.yPos + this.hauteur > 400;
    }
    
    private int chateauProche() {
        int numeroChateau = -1;
        for (int colonne = -1; numeroChateau == -1 && colonne < 4; numeroChateau = colonne) {
            ++colonne;
            if (this.xPos + this.largeur - 1 > 89 + colonne * 114 && this.xPos + 1 < 161 + colonne * 114) {}
        }
        return numeroChateau;
    }
    
    private int abscisseContactTirAlienChateau(final Chateau chateau) {
        int xPosTirVaisseau = -1;
        if (this.xPos + this.largeur > chateau.getxPos() && this.xPos < chateau.getxPos() + 72) {
            xPosTirVaisseau = this.xPos;
        }
        return xPosTirVaisseau;
    }
    
    public int[] tirAlienToucheChateau() {
        final int[] tabRep = { -1, -1 };
        if (this.tirAlienAHauteurDeChateau()) {
            tabRep[0] = this.chateauProche();
            if (tabRep[0] != -1) {
                tabRep[1] = this.abscisseContactTirAlienChateau(Main.scene.tabChateaux[tabRep[0]]);
            }
        }
        return tabRep;
    }
    
    public void tirAlienDetruitChateau(final Chateau[] tabChateaux) {
        final int[] tab = this.tirAlienToucheChateau();
        if (tab[0] != -1 && tabChateaux[tab[0]].trouveBriqueHaut(tabChateaux[tab[0]].trouveColonneChateau(tab[1])) != -1 && tabChateaux[tab[0]].trouveBriqueHaut(tabChateaux[tab[0]].trouveColonneChateau(tab[1])) != 27) {
            tabChateaux[tab[0]].casseBriquesHaut(tab[1]);
            this.yPos = 700;
        }
    }
    
    public boolean toucheVaisseau(final Vaisseau vaisseau) {
        if (this.yPos < vaisseau.getyPos() + vaisseau.getHauteur() && this.yPos + this.hauteur > vaisseau.getyPos() && this.xPos + this.largeur > vaisseau.getxPos() && this.xPos < vaisseau.getxPos() + vaisseau.getLargeur()) {
            this.yPos = 700;
            Audio.playSound("/sons/sonDestructionVaisseau.wav");
            return true;
        }
        return false;
    }
}
