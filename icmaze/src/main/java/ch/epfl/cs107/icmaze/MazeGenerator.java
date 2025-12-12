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
        // defensive
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("width/height must be >= 3");
        }

        int[][] grid = new int[width][height]; // INVERSION ICI

        // fill with passages
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = PASSAGE;
            }
        }

        // put outer walls
        for (int x = 0; x < width; x++) {
            grid[x][0] = WALL;
            grid[x][height - 1] = WALL;
        }
        for (int y = 0; y < height; y++) {
            grid[0][y] = WALL;
            grid[width - 1][y] = WALL;
        }

        // divide interior region (1,1) .. (width-2,height-2)
        divide(grid, 1, 1, width - 2, height - 2, difficulty);
        return grid;
    }

    /**
     * Recursive division
     * x,y = top-left of subregion (inclusive)
     * width,height = subregion size
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
            int possibleRange = height - 2;
            if (possibleRange <= 0) return;

            int wallOffset = randomOdd(possibleRange);
            int wallY = y + wallOffset;

            // horizontal wall
            for (int i = x; i < x + width; i++) {
                grid[i][wallY] = WALL;
            }

            // passage
            int passageRange = width - 1;
            int passageOffset = randomEven(passageRange);
            int passageX = x + passageOffset;
            grid[passageX][wallY] = PASSAGE;

            int topHeight = wallY - y;
            int bottomHeight = y + height - (wallY + 1);

            divide(grid, x, y, width, topHeight, difficulty);
            divide(grid, x, wallY + 1, width, bottomHeight, difficulty);

        } else {

            int possibleRange = width - 2;
            if (possibleRange <= 0) return;

            int wallOffset = randomOdd(possibleRange);
            int wallX = x + wallOffset;

            // vertical wall
            for (int i = y; i < y + height; i++) {
                grid[wallX][i] = WALL;
            }

            // passage
            int passageRange = height - 1;
            int passageOffset = randomEven(passageRange);
            int passageY = y + passageOffset;
            grid[wallX][passageY] = PASSAGE;

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
