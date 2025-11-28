package ch.epfl.cs107.icmaze;

import static ch.epfl.cs107.play.window.Keyboard.E;
import static ch.epfl.cs107.play.window.Keyboard.ENTER;
import static ch.epfl.cs107.play.window.Keyboard.P;
import static ch.epfl.cs107.play.window.Keyboard.R;
import static ch.epfl.cs107.play.window.Keyboard.B;
import static ch.epfl.cs107.play.window.Keyboard.SPACE;
import static ch.epfl.cs107.play.window.Keyboard.UP;
import static ch.epfl.cs107.play.window.Keyboard.DOWN;
import static ch.epfl.cs107.play.window.Keyboard.LEFT;
import static ch.epfl.cs107.play.window.Keyboard.RIGHT;

/**
 * Interface KeyboardConfig
 * Définition des touches de déplacement des deux joueurs ainsi que d'autres
 * actions globales dans le jeu.
 */
public final class KeyBindings {

    /**
     * Touches utilisées pour le joueur rouge.
     */
    public static final PlayerKeyBindings PLAYER_KEY_BINDINGS = new PlayerKeyBindings(UP, LEFT, DOWN, RIGHT, SPACE, E);

    /**
     * Touche pour passer au dialogue suivant.
     */
    public static final int NEXT_DIALOG = ENTER;
    /**
     * Touche pour réinitialiser le jeu.
     */
    public static final int RESET_GAME = R;
    /**
     * Touche pour réinitialiser la zone.
     */
    public static final int PAUSE_GAME = P;

    /**
     * Touche pour se téléporter à la salle du BOSS
     */
    public static final int BOSS_ROOM = B;

    private KeyBindings() {

    }

    /**
     * Touches utilisées pour un joueur
     *
     * @param up         Pour le déplacement vers le haut
     * @param left       Pour le déplacement vers la gauche
     * @param down       Pour le déplacement vers le bas
     * @param right      Pour le déplacement vers la droite
     * @param pickaxe    Pour utiliser la pioche
     * @param interact   Pour interagir à distance
     */
    public record PlayerKeyBindings(int up, int left, int down, int right, int pickaxe, int interact) {
    }
}
