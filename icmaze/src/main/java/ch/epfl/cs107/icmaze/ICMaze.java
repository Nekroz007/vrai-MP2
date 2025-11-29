package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {
    private final String[] areas = {"icmaze/Spawn", "icmaze/Boss"};
    private ICMazePlayer player;
    private int areaIndex;

    /**
     * Add all the Tuto2 areas
     */
    private void createAreas() {
        addArea(new Spawn());
        addArea(new BossArea());
    }

    /**
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
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

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
            switchArea();
        super.update(deltaTime);
    }

    @Override
    public void end() {

    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }

    /**
     * sets the area named `areaKey` as current area in the game Tuto2
     * @param areaKey (String) title of an area
     */
    private void initArea(String areaKey) {
        ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnPosition();
        player = new ICMazePlayer(area, Orientation.DOWN, coords);
        player.enterArea(area, coords);
        player.centerCamera();
    }

    /**
     * switches from one area to the other
     * the player is healed when moving to a new area
     */
    private void switchArea() {
        /*player.leaveArea();
        areaIndex = (areaIndex == 0) ? 1 : 0;
        ICMazeArea currentArea = (ICMazeArea) setCurrentArea(areas[areaIndex], false);
        player.enterArea(currentArea, currentArea.getPlayerSpawnPosition());
        player.strengthen();

         */
    }
}