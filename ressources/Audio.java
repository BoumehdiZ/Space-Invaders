// 
// Decompiled by Procyon v0.5.36
// 

package ressources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio
{
    private Clip clip;
    
    public Audio(final String son) {
        try {
            final AudioInputStream audio = AudioSystem.getAudioInputStream(this.getClass().getResource(son));
            (this.clip = AudioSystem.getClip()).open(audio);
        }
        catch (Exception ex) {}
    }
    
    public Clip getClip() {
        return this.clip;
    }
    
    public void play() {
        this.clip.start();
    }
    
    public void stop() {
        this.clip.stop();
    }
    
    public static void playSound(final String son) {
        final Audio s = new Audio(son);
        s.play();
    }
}
