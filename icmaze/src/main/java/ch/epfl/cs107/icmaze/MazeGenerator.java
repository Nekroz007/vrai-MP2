package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;
    private static final int PASSAGE = 0;

    private MazeGenerator(){}
    public static int[][] createMaze(int width, int height, int difficulty) {

        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("width/height must be >= 3");
        }
        int[][] grid = new int[height][width];


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = PASSAGE;
            }
        }


        for (int x = 0; x < width; x++) {
            grid[0][x] = WALL;
            grid[height - 1][x] = WALL;
        }
        for (int y = 0; y < height; y++) {
            grid[y][0] = WALL;
            grid[y][width - 1] = WALL;
        }


        divide(grid, 1, 1, width - 2, height - 2, difficulty);
        return grid;
    }

    /**
     * Recursive division
     * x,y = top-left of subregion (inclusive) in grid coordinates
     * width,height = subregion size (>=1)
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


            for (int i = x; i < x + width; i++) {
                grid[wallY][i] = WALL;
            }

            int passageRange = width - 1;
            int passageOffset = randomEven(passageRange);
            int passageX = x + passageOffset;
            grid[wallY][passageX] = PASSAGE;


            int topHeight = wallY - y;
            int bottomHeight = y + height - (wallY + 1);
            divide(grid, x, y, width, topHeight, difficulty);
            divide(grid, x, wallY + 1, width, bottomHeight, difficulty);
        } else {

            int possibleRange = width - 2;
            if (possibleRange <= 0) return;
            int wallOffset = randomOdd(possibleRange);
            int wallX = x + wallOffset;

            for (int i = y; i < y + height; i++) {
                grid[i][wallX] = WALL;
            }

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


    /**
     * Print the maze
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Print top border
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        // Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (assuming max > 0).
     */
    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }
}

