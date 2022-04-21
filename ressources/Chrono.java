// 
// Decompiled by Procyon v0.5.36
// 

package ressources;

import jeu.Main;

public class Chrono implements Runnable
{
    private final int PAUSE = 5;
    public static int compteTours;
    
    static {
        Chrono.compteTours = 0;
    }
    
    @Override
    public void run() {
        while (true) {
            ++Chrono.compteTours;
            Main.scene.repaint();
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex) {}
        }
    }
}
