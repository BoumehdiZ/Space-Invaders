// 
// Decompiled by Procyon v0.5.36
// 

package entites;

import ressources.Audio;
import java.awt.Color;
import java.awt.Graphics;

public class Chateau extends Entite
{
    private final int NBRE_LIGNES = 27;
    private final int NBRE_COLONNES = 36;
    boolean[][] tabChateau;
    
    public Chateau(final int xPos) {
        this.tabChateau = new boolean[27][36];
        super.xPos = xPos;
        super.yPos = 400;
        this.initTabChateau();
    }
    
    public void initTabChateau() {
        for (int ligne = 0; ligne < 27; ++ligne) {
            for (int colonne = 0; colonne < 36; ++colonne) {
                this.tabChateau[ligne][colonne] = true;
            }
        }
        for (int colonne2 = 0; colonne2 < 6; ++colonne2) {
            for (int ligne2 = 0; ligne2 < 2; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
        for (int colonne2 = 0; colonne2 < 4; ++colonne2) {
            for (int ligne2 = 2; ligne2 < 4; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
        for (int colonne2 = 0; colonne2 < 2; ++colonne2) {
            for (int ligne2 = 4; ligne2 < 6; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
        for (int ligne = 18; ligne < 27; ++ligne) {
            for (int colonne = 10; colonne < 26; ++colonne) {
                this.tabChateau[ligne][colonne] = false;
            }
        }
        for (int colonne2 = 12; colonne2 < 24; ++colonne2) {
            for (int ligne2 = 16; ligne2 < 18; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
        for (int colonne2 = 14; colonne2 < 22; ++colonne2) {
            for (int ligne2 = 14; ligne2 < 16; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
        for (int colonne2 = 0; colonne2 < 2; ++colonne2) {
            for (int ligne2 = 4; ligne2 < 6; ++ligne2) {
                this.tabChateau[ligne2][colonne2] = false;
                this.tabChateau[ligne2][36 - colonne2 - 1] = false;
            }
        }
    }
    
    public void dessinChateau(final Graphics g2) {
        for (int ligne = 0; ligne < 27; ++ligne) {
            for (int colonne = 0; colonne < 36; ++colonne) {
                if (this.tabChateau[ligne][colonne]) {
                    g2.setColor(Color.GREEN);
                }
                else {
                    g2.setColor(Color.BLACK);
                }
                g2.fillRect(this.xPos + 2 * colonne, this.yPos + 2 * ligne, 2, 2);
            }
        }
    }
    
    public int trouveColonneChateau(final int xMissile) {
        int colonne = -1;
        colonne = (xMissile - this.xPos) / 2;
        return colonne;
    }
    
    public int trouveBrique(final int colonne) {
        int ligne;
        for (ligne = 26; ligne >= 0 && !this.tabChateau[ligne][colonne]; --ligne) {}
        return ligne;
    }
    
    private void enleveBriques(final int ligne, final int colonne) {
        for (int compteur = 0; compteur < 6; ++compteur) {
            if (ligne - compteur >= 0) {
                this.tabChateau[ligne - compteur][colonne] = false;
                if (colonne < 35) {
                    this.tabChateau[ligne - compteur][colonne + 1] = false;
                }
            }
        }
    }
    
    public void casseBriques(final int xMissile) {
        final int colonne = this.trouveColonneChateau(xMissile);
        this.enleveBriques(this.trouveBrique(colonne), colonne);
        Audio.playSound("/sons/sonCasseBrique.wav");
    }
    
    public int trouveBriqueHaut(final int colonne) {
        int ligne = 0;
        if (colonne != -1) {
            while (ligne < 27 && !this.tabChateau[ligne][colonne]) {
                ++ligne;
            }
        }
        return ligne;
    }
    
    private void enleveBriquesHaut(final int ligne, final int colonne) {
        for (int compteur = 0; compteur < 6; ++compteur) {
            if (ligne + compteur < 27 && colonne != -1) {
                this.tabChateau[ligne + compteur][colonne] = false;
                if (colonne < 35) {
                    this.tabChateau[ligne + compteur][colonne + 1] = false;
                }
            }
        }
    }
    
    public void casseBriquesHaut(final int xMissile) {
        final int colonne = this.trouveColonneChateau(xMissile);
        this.enleveBriquesHaut(this.trouveBriqueHaut(colonne), colonne);
        Audio.playSound("/sons/sonCasseBrique.wav");
    }
}
