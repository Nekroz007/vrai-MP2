package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.LevelGenerator;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {
    private final String[] areas = {"icmaze/Spawn", "icmaze/Boss"};
    private ICMazePlayer player;
    private int areaIndex;

    private void generateHardCodedLevel() {
        ;
    }

    protected void createAreas() {
        int labyrinthCount = 0;

        ICMazeArea[] levels = LevelGenerator.generateLine(this, labyrinthCount);

        for (ICMazeArea area : levels) {
            addArea(area);
        }
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getWindow().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_GAME).isPressed()){
            resetGame();
            return;
        }
        switchArea();
        super.update(deltaTime);
        if (player != null && player.isDead()){
            resetGame();
        }
    }
    private void resetGame() {
        createAreas();
        areaIndex = 0;
        initArea(areas[areaIndex]);
        player.resetHealth();
    }

    @Override
    public void end() {}

    @Override
    public int getFrameRate() {
        return 60;
    }


    @Override
    public String getTitle() {
        return "ICMaze";
    }

    private void initArea(String areaKey) {
        ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnPosition();
        player = new ICMazePlayer(area, Orientation.DOWN, coords);
        player.enterArea(area, coords);
        player.centerCamera();
    }

    /**
     * Change de zone si nécessaire
     */
    public void switchArea() {
        if (player.getCurrentPortal() != null) {
            String destinArea = player.getCurrentPortal().getDestinationAreaName();
            DiscreteCoordinates destCoords = player.getCurrentPortal().getDestinationCoords();
            System.out.print("coordonnées de la destination: ");
            System.out.println(destCoords);
            System.out.println("destination: " + destinArea);

            if (destinArea != null && destCoords != null) {
                player.leaveArea();
                ICMazeArea currentArea = (ICMazeArea) setCurrentArea(destinArea, false);
                player.enterArea(currentArea, destCoords);
                player.centerCamera();
            }
            player.resetPortal();
        }
    }
}
