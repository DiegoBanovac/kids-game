package hr.adhd.igrica.util;

public class Settings {
    private static boolean dyslexiaMode = false;

    public static boolean isDyslexiaMode() { return dyslexiaMode; }
    public static void toggleDyslexiaMode() { dyslexiaMode = !dyslexiaMode; }
}
