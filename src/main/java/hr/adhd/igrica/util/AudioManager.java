package hr.adhd.igrica.util;

import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioManager {

    private static final AudioManager INSTANCE = new AudioManager();

    private AudioClip correctClip;
    private AudioClip wrongClip;
    private AudioClip clickClip;
    private AudioClip victoryClip;

    private AudioManager() {}

    public static AudioManager getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        try {
            correctClip = buildClip(generateSine(880f, 200), "correct.wav");
            wrongClip   = buildClip(generateDescend(440f, 280f, 350), "wrong.wav");
            clickClip   = buildClip(generateSine(660f, 90), "click.wav");
            victoryClip = buildClip(generateArpeggio(new float[]{523f, 659f, 784f, 1047f}, 110), "victory.wav");
        } catch (Exception ignored) {
            // audio not critical; game still works silently
        }
    }

    public void playCorrect() { if (correctClip != null) correctClip.play(); }
    public void playWrong()   { if (wrongClip   != null) wrongClip.play();   }
    public void playClick()   { if (clickClip   != null) clickClip.play();   }
    public void playVictory() { if (victoryClip != null) victoryClip.play(); }

    private short[] generateSine(float hz, int ms) {
        int rate = 44100;
        int n = (int)(rate * ms / 1000.0);
        short[] s = new short[n];
        for (int i = 0; i < n; i++) {
            s[i] = (short)(env(i, n) * 30000 * Math.sin(2 * Math.PI * hz * i / rate));
        }
        return s;
    }

    private short[] generateDescend(float startHz, float endHz, int ms) {
        int rate = 44100;
        int n = (int)(rate * ms / 1000.0);
        short[] s = new short[n];
        for (int i = 0; i < n; i++) {
            float hz = startHz + (endHz - startHz) * (float) i / n;
            s[i] = (short)(env(i, n) * 30000 * Math.sin(2 * Math.PI * hz * i / rate));
        }
        return s;
    }

    private short[] generateArpeggio(float[] freqs, int msEach) {
        int rate = 44100;
        int each = (int)(rate * msEach / 1000.0);
        short[] all = new short[each * freqs.length];
        for (int fi = 0; fi < freqs.length; fi++) {
            short[] seg = generateSine(freqs[fi], msEach);
            System.arraycopy(seg, 0, all, fi * each, seg.length);
        }
        return all;
    }

    private float env(int i, int total) {
        int fadeIn  = (int)(44100 * 0.01);
        int fadeOut = (int)(44100 * 0.02);
        if (i < fadeIn)            return (float) i / fadeIn;
        if (i > total - fadeOut)   return (float)(total - i) / fadeOut;
        return 1.0f;
    }

    private byte[] toWav(short[] samples) {
        int rate = 44100;
        int dataSize = samples.length * 2;
        ByteBuffer buf = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN);
        buf.put(new byte[]{'R','I','F','F'});
        buf.putInt(36 + dataSize);
        buf.put(new byte[]{'W','A','V','E'});
        buf.put(new byte[]{'f','m','t',' '});
        buf.putInt(16);
        buf.putShort((short) 1);      // PCM
        buf.putShort((short) 1);      // mono
        buf.putInt(rate);
        buf.putInt(rate * 2);         // byte rate
        buf.putShort((short) 2);      // block align
        buf.putShort((short) 16);     // bits per sample
        buf.put(new byte[]{'d','a','t','a'});
        buf.putInt(dataSize);
        for (short sample : samples) buf.putShort(sample);
        return buf.array();
    }

    private AudioClip buildClip(short[] samples, String name) throws IOException {
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), "adhd_" + name);
        Files.write(tmp, toWav(samples));
        return new AudioClip(tmp.toUri().toString());
    }
}
