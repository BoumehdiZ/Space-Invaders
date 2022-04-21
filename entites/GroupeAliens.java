// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import ressources.Audio;
import jeu.Main;
import java.awt.image.ImageObserver;
import ressources.Chrono;
import java.awt.Graphics;
import java.util.Random;

public class GroupeAliens
{
    private Alien[][] tabAlien;
    private boolean vaADroite;
    private boolean pos1;
    private int vitesse;
    private int[] tabAlienMort;
    Random hasard;
    private int nombreAliens;
    private int compteurSonAlien;
    
    public GroupeAliens() {
        this.tabAlien = new Alien[5][10];
        this.tabAlienMort = new int[] { -1, 1 };
        this.hasard = new Random();
        this.nombreAliens = 50;
        this.compteurSonAlien = 0;
        this.initTableauAliens();
        this.vaADroite = true;
        this.pos1 = true;
        this.vitesse = 20;
    }
    
    public void initTableauAliens() {
        for (int colonne = 0; colonne < 10; ++colonne) {
            this.tabAlien[0][colonne] = new Alien(79 + 43 * colonne, 120, "/images/alienHaut1.png", "/images/alienHaut2.png");
            for (int ligne = 1; ligne < 3; ++ligne) {
                this.tabAlien[ligne][colonne] = new Alien(79 + 43 * colonne, 120 + 40 * ligne, "/images/alienMilieu1.png", "/images/alienMilieu2.png");
            }
            for (int ligne = 3; ligne < 5; ++ligne) {
                this.tabAlien[ligne][colonne] = new Alien(79 + 43 * colonne, 120 + 40 * ligne, "/images/alienBas1.png", "/images/alienBas2.png");
            }
        }
    }
    
    public void dessinAliens(final Graphics g) {
        if (Chrono.compteTours % (100 - 10 * this.vitesse) == 0) {
            this.deplacementAliens();
        }
        for (int colonne = 0; colonne < 10; ++colonne) {
            for (int ligne = 0; ligne < 5; ++ligne) {
                if (this.tabAlien[ligne][colonne] != null) {
                    this.tabAlien[ligne][colonne].choixImage(this.pos1);
                    g.drawImage(this.tabAlien[ligne][colonne].getImg(), this.tabAlien[ligne][colonne].getxPos(), this.tabAlien[ligne][colonne].getyPos(), null);
                }
            }
        }
    }
    
    private boolean toucheBordGauche() {
        boolean reponse = false;
        for (int colonne = 0; colonne < 10; ++colonne) {
            for (int ligne = 0; ligne < 5; ++ligne) {
                if (this.tabAlien[ligne][colonne] != null && this.tabAlien[ligne][colonne].getxPos() < 50) {
                    reponse = true;
                    break;
                }
            }
        }
        return reponse;
    }
    
    private boolean toucheBordDroit() {
        boolean reponse = false;
        for (int colonne = 0; colonne < 10; ++colonne) {
            for (int ligne = 0; ligne < 5; ++ligne) {
                if (this.tabAlien[ligne][colonne] != null && this.tabAlien[ligne][colonne].getxPos() > 515) {
                    reponse = true;
                    break;
                }
            }
        }
        return reponse;
    }
    
    public void alienTourneEtDescend() {
        if (this.toucheBordDroit()) {
            for (int colonne = 0; colonne < 10; ++colonne) {
                for (int ligne = 0; ligne < 5; ++ligne) {
                    if (this.tabAlien[ligne][colonne] != null) {
                        this.tabAlien[ligne][colonne].setyPos(this.tabAlien[ligne][colonne].getyPos() + 20);
                    }
                }
            }
            this.vaADroite = false;
            if (this.vitesse < 9) {
                ++this.vitesse;
            }
        }
        else if (this.toucheBordGauche()) {
            for (int colonne = 0; colonne < 10; ++colonne) {
                for (int ligne = 0; ligne < 5; ++ligne) {
                    if (this.tabAlien[ligne][colonne] != null) {
                        this.tabAlien[ligne][colonne].setyPos(this.tabAlien[ligne][colonne].getyPos() + 20);
                    }
                }
            }
            this.vaADroite = true;
            if (this.vitesse < 9) {
                ++this.vitesse;
            }
        }
    }
    
    public void deplacementAliens() {
        if (this.tabAlienMort[0] != -1) {
            this.elimineAlienMort(this.tabAlienMort);
            this.tabAlienMort[0] = -1;
        }
        if (this.vaADroite) {
            for (int colonne = 0; colonne < 10; ++colonne) {
                for (int ligne = 0; ligne < 5; ++ligne) {
                    if (this.tabAlien[ligne][colonne] != null) {
                        this.tabAlien[ligne][colonne].setxPos(this.tabAlien[ligne][colonne].getxPos() + 2);
                    }
                }
            }
        }
        else {
            for (int colonne = 0; colonne < 10; ++colonne) {
                for (int ligne = 0; ligne < 5; ++ligne) {
                    if (this.tabAlien[ligne][colonne] != null) {
                        this.tabAlien[ligne][colonne].setxPos(this.tabAlien[ligne][colonne].getxPos() - 2);
                    }
                }
            }
        }
        this.joueSonAlien();
        ++this.compteurSonAlien;
        if (this.pos1) {
            this.pos1 = false;
        }
        else {
            this.pos1 = true;
        }
        this.alienTourneEtDescend();
    }
    
    public void tirVaisseauToucheAlien(final TirVaisseau tirVaisseau) {
        for (int colonne = 0; colonne < 10; ++colonne) {
            int ligne = 0;
            while (ligne < 5) {
                if (this.tabAlien[ligne][colonne] != null && tirVaisseau.tueAlien(this.tabAlien[ligne][colonne])) {
                    this.tabAlien[ligne][colonne].vivant = false;
                    tirVaisseau.yPos = -1;
                    this.tabAlienMort[0] = ligne;
                    this.tabAlienMort[1] = colonne;
                    if (ligne == 0) {
                        Main.scene.score += 50;
                        break;
                    }
                    if (ligne < 3 && ligne > 0) {
                        Main.scene.score += 40;
                        break;
                    }
                    Main.scene.score += 20;
                    break;
                }
                else {
                    ++ligne;
                }
            }
        }
    }
    
    private void elimineAlienMort(final int[] tabAlienMort) {
        this.tabAlien[this.tabAlienMort[0]][this.tabAlienMort[1]] = null;
        --this.nombreAliens;
    }
    
    public int[] choixAlienQuiTire() {
        final int[] positionAlien = { -1, -1 };
        if (this.nombreAliens != 0) {
            do {
                final int colonne = this.hasard.nextInt(10);
                for (int ligne = 4; ligne >= 0; --ligne) {
                    if (this.tabAlien[ligne][colonne] != null) {
                        positionAlien[0] = this.tabAlien[ligne][colonne].getxPos();
                        positionAlien[1] = this.tabAlien[ligne][colonne].getyPos();
                        break;
                    }
                }
            } while (positionAlien[0] == -1);
        }
        return positionAlien;
    }
    
    private void joueSonAlien() {
        final int compteur = this.compteurSonAlien % 4;
        if (compteur == 0) {
            Audio.playSound("/sons/sonAlien1.wav");
        }
        else if (compteur == 1) {
            Audio.playSound("/sons/sonAlien2.wav");
        }
        else if (compteur == 2) {
            Audio.playSound("/sons/sonAlien3.wav");
        }
        else {
            Audio.playSound("/sons/sonAlien4.wav");
        }
    }
    
    public int getNombreAliens() {
        return this.nombreAliens;
    }
    
    public int positionAlienLePlusBas() {
        int posBas = 0;
        int posBasFinal = 0;
        for (int colonne = 1; colonne < 10; ++colonne) {
            for (int ligne = 4; ligne >= 0; --ligne) {
                if (this.tabAlien[ligne][colonne] != null) {
                    posBas = this.tabAlien[ligne][colonne].yPos + this.tabAlien[ligne][colonne].hauteur;
                    break;
                }
            }
            if (posBas > posBasFinal) {
                posBasFinal = posBas;
            }
        }
        return posBasFinal;
    }
}
