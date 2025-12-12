package ch.epfl.cs107.icmaze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;
    private static final int EMPTY = 0;

    private MazeGenerator(){}

    public static int[][] createMaze(int width, int height, int difficulty) {
        // 1. Initialisation de la grille vide (tout à 0 par défaut en Java) [cite: 333]
        int[][] maze = new int[height][width];

        // 2. Lancement de la division récursive sur toute la zone
        divide(maze, 0, 0, width, height, difficulty);

        return maze;
    }

    /**
     * Méthode de division récursive.
     * * @param maze       La grille à remplir
     * @param x          Coordonnée X du coin haut-gauche de la sous-région
     * @param y          Coordonnée Y du coin haut-gauche de la sous-région
     * @param w          Largeur de la sous-région
     * @param h          Hauteur de la sous-région
     * @param difficulty Taille minimale pour continuer la division
     */
    private static void divide(int[][] maze, int x, int y, int w, int h, int difficulty) {
        // 1. Cas de base : si la sous-région est trop petite, on arrête
        if (w <= difficulty || h <= difficulty) {
            return;
        }

        // 2. Choix de l'orientation (Horizontal ou Vertical)
        // On privilégie la direction la plus longue pour un aspect homogène
        boolean horizontal;
        if (w > h) {
            horizontal = false; // Couper verticalement (mur vertical)
        } else if (h > w) {
            horizontal = true;  // Couper horizontalement (mur horizontal)
        } else {
            horizontal = random.nextBoolean(); // Aléatoire si carré
        }

        if (horizontal) {
            // --- AJOUT D'UN MUR HORIZONTAL ---

            // 3. Choisir un emplacement pour le mur (Y) à une position IMPAIRE
            // On cherche un index valide dans [y, y + h - 1] qui soit impair.
            int wallY = getRandomIndex(y, h, true);

            // Si aucun emplacement valide n'est trouvé (ex: zone trop petite pour contenir un impair), on arrête.
            if (wallY == -1) return;

            // 4. Choisir un emplacement pour le passage (X) à une position PAIRE
            int holeX = getRandomIndex(x, w, false);

            // Dessiner le mur et le trou
            for (int i = x; i < x + w; i++) {
                if (i == holeX) {
                    maze[wallY][i] = EMPTY; // Ouverture
                } else {
                    maze[wallY][i] = WALL;  // Mur
                }
            }

            // 5. Appels récursifs sur les deux nouvelles sous-régions (Haut et Bas)
            // Zone du haut (de y à wallY)
            divide(maze, x, y, w, wallY - y, difficulty);
            // Zone du bas (de wallY + 1 à la fin)
            divide(maze, x, wallY + 1, w, (y + h) - (wallY + 1), difficulty);

        } else {
            // --- AJOUT D'UN MUR VERTICAL ---

            // 3. Choisir un emplacement pour le mur (X) à une position IMPAIRE
            int wallX = getRandomIndex(x, w, true);

            if (wallX == -1) return;

            // 4. Choisir un emplacement pour le passage (Y) à une position PAIRE
            int holeY = getRandomIndex(y, h, false);

            // Dessiner le mur et le trou
            for (int j = y; j < y + h; j++) {
                if (j == holeY) {
                    maze[j][wallX] = EMPTY;
                } else {
                    maze[j][wallX] = WALL;
                }
            }

            // 5. Appels récursifs (Gauche et Droite)
            // Zone de gauche
            divide(maze, x, y, wallX - x, h, difficulty);
            // Zone de droite
            divide(maze, wallX + 1, y, (x + w) - (wallX + 1), h, difficulty);
        }
    }

    /**
     * Helper pour trouver un index aléatoire pair ou impair dans une plage donnée.
     * * @param start L'index de départ (offset global)
     * @param length La taille de la zone
     * @param mustBeOdd true si on veut un nombre impair, false pour pair
     * @return Un index valide ou -1 si impossible
     */
    private static int getRandomIndex(int start, int length, boolean mustBeOdd) {
        // On liste les candidats valides dans la plage [start, start + length - 1]
        // Note: une approche plus optimisée mathématiquement est possible,
        // mais une boucle est plus simple à lire et robuste pour les petites tailles de labyrinthe.

        // On compte d'abord combien il y a de candidats
        int count = 0;
        for (int i = 0; i < length; i++) {
            int val = start + i;
            if (mustBeOdd) {
                if (val % 2 != 0) count++;
            } else {
                if (val % 2 == 0) count++;
            }
        }

        if (count == 0) return -1;

        // On choisit le k-ième candidat
        int targetIndex = random.nextInt(count);

        // On retrouve la valeur correspondante
        int currentCount = 0;
        for (int i = 0; i < length; i++) {
            int val = start + i;
            boolean isMatch = mustBeOdd ? (val % 2 != 0) : (val % 2 == 0);

            if (isMatch) {
                if (currentCount == targetIndex) {
                    return val;
                }
                currentCount++;
            }
        }
        return -1; // Ne devrait pas arriver
    }





    /** Visual print for debugging */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int width = grid.length;
        int height = grid[0].length;

        System.out.print("┌");
        for (int x = 0; x < width; x++) System.out.print("───");
        System.out.println("┐");

        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[x][y] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        System.out.print("└");
        for (int x = 0; x < width; x++) System.out.print("───");
        System.out.println("┘");
    }

    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }
}
