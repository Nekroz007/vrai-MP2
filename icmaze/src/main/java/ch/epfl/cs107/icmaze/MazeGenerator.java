package ch.epfl.cs107.icmaze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;
    private static final int PASSAGE = 0;

    private MazeGenerator(){}

    public static int[][] createMaze(int width, int height, int difficulty) {
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("width/height must be >= 3");
        }

        // Initialisation de la grille
        int[][] grid = new int[height][width];

        // remplir avec des passages
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = PASSAGE;
            }
        }

        // murs extérieurs
        for (int x = 0; x < width; x++) {
            grid[0][x] = WALL;
            grid[height - 1][x] = WALL;
        }
        for (int y = 0; y < height; y++) {
            grid[y][0] = WALL;
            grid[y][width - 1] = WALL;
        }

        // diviser la région intérieure
        divide(grid, 1, 1, width - 2, height - 2, difficulty);

        return grid;
    }

    /**
     * Division récursive
     * x,y = coin supérieur gauche de la sous-région (inclus)
     * width,height = taille de la sous-région
     */
    private static void divide(int[][] grid, int x, int y, int width, int height, int difficulty) {
        if (width <= difficulty || height <= difficulty) {
            return;
        }

        boolean horizontal;
        if (width > height) horizontal = false;
        else if (height > width) horizontal = true;
        else horizontal = random.nextBoolean();

        if (horizontal) {
            int possibleRange = height;
            if (possibleRange <= 0) return;

            int wallOffset = randomOdd(possibleRange);
            int wallY = y + wallOffset;

            // mur horizontal
            for (int i = x; i < x + width; i++) {
                grid[wallY][i] = WALL;
            }

            // passage
            int passageRange = width - 1;
            int passageOffset = randomEven(passageRange);
            int passageX = x + passageOffset;
            grid[wallY][passageX] = PASSAGE;

            int topHeight = wallY - y;
            int bottomHeight = y + height - (wallY + 1);

            divide(grid, x, y, width, topHeight, difficulty);
            divide(grid, x, wallY + 1, width, bottomHeight, difficulty);

        } else {
            int possibleRange = width;
            if (possibleRange <= 0) return;

            int wallOffset = randomOdd(possibleRange);
            int wallX = x + wallOffset;

            // mur vertical
            for (int i = y; i < y + height; i++) {
                grid[i][wallX] = WALL;
            }

            // passage
            int passageRange = height - 1;
            int passageOffset = randomEven(passageRange);
            int passageY = y + passageOffset;
            grid[passageY][wallX] = PASSAGE;

            int leftWidth = wallX - x;
            int rightWidth = x + width - (wallX + 1);

            divide(grid, x, y, leftWidth, height, difficulty);
            divide(grid, wallX + 1, y, rightWidth, height, difficulty);
        }
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