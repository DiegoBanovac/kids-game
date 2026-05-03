module hr.adhd.igrica {
    requires javafx.controls;
    requires javafx.media;
    opens hr.adhd.igrica to javafx.graphics;
    opens hr.adhd.igrica.screens to javafx.graphics;
    opens hr.adhd.igrica.games to javafx.graphics;
    opens hr.adhd.igrica.util to javafx.graphics;
}
