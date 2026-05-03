package hr.adhd.igrica.util;

public class TtsManager {

    public static void speak(String text) {
        String safe = text.replace("'", "").replace("\"", "").replace("`", "");
        String cmd = "Add-Type -AssemblyName System.Speech; " +
                     "$s = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                     "$s.Rate = -2; " +
                     "$s.Speak('" + safe + "');";
        try {
            new ProcessBuilder("powershell", "-NonInteractive", "-Command", cmd)
                    .redirectErrorStream(true)
                    .start();
        } catch (Exception ignored) {
            // TTS unavailable on this platform
        }
    }
}
