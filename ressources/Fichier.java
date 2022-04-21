// 
// Decompiled by Procyon v0.5.36
// 

package ressources;

import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Fichier
{
    private BufferedWriter fw;
    private BufferedReader fr;
    private char mode;
    
    public void ouvrir(final String nomFichier, final String modeOuverture) throws IOException {
        this.mode = modeOuverture.charAt(0);
        final File f = new File(nomFichier);
        if (this.mode == 'r' || this.mode == 'l') {
            this.fr = new BufferedReader(new FileReader(f));
        }
        if (this.mode == 'w' || this.mode == 'e') {
            this.fw = new BufferedWriter(new FileWriter(f));
        }
    }
    
    public void ecriture(final int tmp) throws IOException {
        final String nb = String.valueOf(tmp);
        this.fw.write(nb, 0, nb.length());
    }
    
    public String lecture() throws IOException {
        final String chaine = this.fr.readLine();
        return chaine;
    }
    
    public void fermer() throws IOException {
        if (this.mode == 'r' || this.mode == 'l') {
            this.fr.close();
        }
        if (this.mode == 'w' || this.mode == 'e') {
            this.fw.close();
        }
    }
}
