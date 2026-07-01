import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private static Clip activeTimerSound;

    // הפעלת סאונד הטיימר בלולאה (Loop)
    public static void playTimerSoundLoop() {
        try {
            stopTimerSound(); // עצירת סאונד קודם ליתר ביטחון
            File soundFile = new File("src/timer.wav");
            if (soundFile.exists()) {
                activeTimerSound = AudioSystem.getClip();
                activeTimerSound.open(AudioSystem.getAudioInputStream(soundFile));
                activeTimerSound.loop(Clip.LOOP_CONTINUOUSLY); // לולאה אינסופית
                activeTimerSound.start();
            }
        } catch (Exception ex) {
            System.out.println("Error playing timer sound: " + ex.getMessage());
        }
    }

    // עצירת סאונד הטיימר באופן מיידי
    public static void stopTimerSound() {
        if (activeTimerSound != null && activeTimerSound.isRunning()) {
            activeTimerSound.stop();
            activeTimerSound.close();
        }
    }

    // השמעת אפקט קול חד-פעמי (למשל start, win, drow)
    public static void playEffect(String soundName) {
        try {
            File soundFile = new File("src/" + soundName + ".wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start();
            }
        } catch (Exception ex) {
            System.out.println("Error playing effect sound: " + ex.getMessage());
        }
    }
}