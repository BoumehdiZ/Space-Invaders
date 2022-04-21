// 
// Decompiled by Procyon v0.5.36
// 

package jeu;

import java.awt.image.ImageObserver;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import ressources.Chrono;
import java.awt.event.KeyListener;
import ressources.Clavier;
import java.awt.Image;
import javax.swing.ImageIcon;
import ressources.Fichier;
import ressources.Audio;
import java.awt.Font;
import entites.Soucoupe;
import entites.TirAlien;
import entites.Chateau;
import entites.TirVaisseau;
import entites.GroupeAliens;
import entites.Vaisseau;
import javax.swing.JPanel;

public class Scene extends JPanel
{
    public Vaisseau vaisseau;
    public GroupeAliens groupeAliens;
    public TirVaisseau tirVaisseau;
    public Chateau[] tabChateaux;
    public TirAlien tirAlien1;
    public TirAlien tirAlien2;
    public TirAlien tirAlien3;
    public Soucoupe soucoupe;
    private Font afficheScore;
    private Font afficheTexte;
    private Font afficheTexteContinue;
    private Font afficheTexteEchap;
    public Audio musiqueGameOver;
    public Audio musiqueIntro;
    public Fichier f1;
    public int score;
    public int vie;
    public int highscore;
    public int vague;
    public boolean start;
    public boolean gameover;
    public boolean scoreModifier;
    String strImgSpaceInvaders;
    ImageIcon icoSpaceInvaders;
    Image imgSpaceInvaders;
    String strImgVaisseau;
    ImageIcon icoVaisseau;
    Image imgVaisseau;
    
    public Scene() {
        this.vaisseau = new Vaisseau();
        this.groupeAliens = new GroupeAliens();
        this.tirVaisseau = new TirVaisseau();
        this.tabChateaux = new Chateau[4];
        this.afficheScore = new Font("Arial", 0, 20);
        this.afficheTexte = new Font("Arial", 0, 80);
        this.afficheTexteContinue = new Font("Arial", 0, 20);
        this.afficheTexteEchap = new Font("Arial", 0, 15);
        this.musiqueGameOver = new Audio("/sons/GameOver.wav");
        this.musiqueIntro = new Audio("/sons/musiqueIntro.wav");
        this.f1 = new Fichier();
        this.score = 0;
        this.vie = 3;
        this.highscore = 0;
        this.vague = 1;
        this.start = false;
        this.gameover = false;
        this.scoreModifier = false;
        this.strImgSpaceInvaders = "/images/SpaceInvaders.png";
        this.icoSpaceInvaders = new ImageIcon(this.getClass().getResource(this.strImgSpaceInvaders));
        this.imgSpaceInvaders = this.icoSpaceInvaders.getImage();
        this.strImgVaisseau = "/images/vaisseau.png";
        this.icoVaisseau = new ImageIcon(this.getClass().getResource(this.strImgVaisseau));
        this.imgVaisseau = this.icoVaisseau.getImage();
        for (int colonne = 0; colonne < 4; ++colonne) {
            this.tabChateaux[colonne] = new Chateau(89 + colonne * 114);
        }
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(new Clavier());
        final Thread chronoEcran = new Thread(new Chrono());
        chronoEcran.start();
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics g2 = g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 600, 600);
        g2.setColor(Color.GREEN);
        g2.fillRect(30, 530, 535, 5);
        if (this.start) {
            this.vaisseau.dessinVaisseau(g2);
            if (this.vaisseau.isVivant()) {
                g.setFont(this.afficheScore);
                g.drawString("SCORE : " + this.score, 400, 25);
                try {
                    this.f1.ouvrir("Score.txt", "lecture");
                    final String ligne = this.f1.lecture();
                    this.highscore = Integer.parseInt(ligne);
                    this.f1.fermer();
                    if (this.highscore < this.score) {
                        this.f1.ouvrir("Score.txt", "ecriture");
                        this.f1.ecriture(this.score);
                        this.f1.fermer();
                    }
                }
                catch (IOException e) {
                    System.out.println(e);
                }
                g.setFont(this.afficheScore);
                g.drawString("HIGHSCORE : " + this.highscore, 200, 25);
                g.setFont(this.afficheScore);
                g.drawString("VAGUE : " + this.vague, 50, 25);
                this.groupeAliens.dessinAliens(g2);
                this.tirVaisseau.dessinVaisseau(g2);
                if (this.vie != 0) {
                    int largeurVaisseau = 0;
                    for (int i = 0; i < this.vie; ++i) {
                        g.drawImage(this.imgVaisseau, 50 + largeurVaisseau * 2, 540, null);
                        largeurVaisseau += 39;
                    }
                }
                this.groupeAliens.tirVaisseauToucheAlien(this.tirVaisseau);
                for (int colonne = 0; colonne < 4; ++colonne) {
                    this.tabChateaux[colonne].dessinChateau(g2);
                }
                this.tirVaisseau.tirVaisseauDetruitChateau(this.tabChateaux);
                if (Chrono.compteTours % 500 == 0) {
                    this.tirAlien1 = new TirAlien(this.groupeAliens.choixAlienQuiTire());
                }
                if (this.tirAlien1 != null) {
                    this.tirAlien1.dessinTirAlien(g2);
                    this.tirAlien1.tirAlienDetruitChateau(this.tabChateaux);
                    if (this.tirAlien1.toucheVaisseau(this.vaisseau)) {
                        --this.vie;
                        this.vaisseau.setxPos(280);
                    }
                }
                if (Chrono.compteTours % 750 == 0) {
                    this.tirAlien2 = new TirAlien(this.groupeAliens.choixAlienQuiTire());
                }
                if (this.tirAlien2 != null) {
                    this.tirAlien2.dessinTirAlien(g2);
                    this.tirAlien2.tirAlienDetruitChateau(this.tabChateaux);
                    if (this.tirAlien2.toucheVaisseau(this.vaisseau)) {
                        --this.vie;
                        this.vaisseau.setxPos(280);
                    }
                }
                if (Chrono.compteTours % 900 == 0) {
                    this.tirAlien3 = new TirAlien(this.groupeAliens.choixAlienQuiTire());
                }
                if (this.tirAlien3 != null) {
                    this.tirAlien3.dessinTirAlien(g2);
                    this.tirAlien3.tirAlienDetruitChateau(this.tabChateaux);
                    if (this.tirAlien3.toucheVaisseau(this.vaisseau)) {
                        --this.vie;
                        this.vaisseau.setxPos(280);
                    }
                }
                if (Chrono.compteTours % 2500 == 0) {
                    this.soucoupe = new Soucoupe();
                }
                if (this.soucoupe != null) {
                    if (this.soucoupe.getxPos() > 0) {
                        if (this.tirVaisseau.detruitSoucoupe(this.soucoupe)) {
                            if (this.soucoupe.getDx() != 0) {
                                this.score += 100;
                            }
                            this.soucoupe.setDx(0);
                            this.soucoupe.setVivant(false);
                            this.soucoupe.musiqueSoucoupe.stop();
                            this.soucoupe.musiqueDestructionSoucoupe.play();
                        }
                        this.soucoupe.dessinSoucoupe(g2);
                    }
                    else {
                        this.soucoupe = null;
                    }
                }
                if (this.groupeAliens.getNombreAliens() == 0) {
                    this.groupeAliens = new GroupeAliens();
                    ++this.vague;
                }
            }
            if (this.groupeAliens.positionAlienLePlusBas() >= 490) {
                this.vaisseau.setVivant(false);
                this.vaisseau.destructionVaisseau();
            }
            if (this.vie == 0) {
                this.vaisseau.setVivant(false);
            }
            if (!this.vaisseau.isVivant()) {
                this.gameover = true;
                this.musiqueGameOver.play();
                g.setFont(this.afficheTexte);
                g.drawString("GAME OVER", 50, 250);
                g2.setColor(Color.WHITE);
                g.setFont(this.afficheTexteContinue);
                g.drawString("Appuyez sur entr\u00e9e pour continuer !", 150, 350);
                if (this.soucoupe != null) {
                    this.soucoupe.musiqueDestructionSoucoupe.stop();
                    this.soucoupe.musiqueSoucoupe.stop();
                }
            }
        }
        else {
            this.musiqueIntro.play();
            g2.setColor(Color.WHITE);
            g.drawImage(this.imgSpaceInvaders, 50, 0, null);
            g.setFont(this.afficheTexteContinue);
            g.drawString("Appuyez sur entr\u00e9e pour continuer !", 150, 380);
            if (!this.scoreModifier) {
                g.setFont(this.afficheTexteEchap);
                g.drawString("Appuyez sur espace pour r\u00e9initialiser le highscore", 150, 500);
            }
            else {
                g.setFont(this.afficheTexteEchap);
                g.drawString("Le highscore a \u00e9t\u00e9 r\u00e9initialis\u00e9 !", 200, 500);
            }
        }
    }
}
