package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
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

    private void createAreas() {
        addArea(new Spawn());
        addArea(new BossArea());
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
        switchArea(); // Gère les téléportations si besoin
        super.update(deltaTime);
    }

    @Override
    public void end() {}

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
            // Récupère la destination
            String destArea = player.getCurrentPortal().getDestinationAreaName();
            DiscreteCoordinates destCoords = player.getCurrentPortal().getDestinationCoords();

            if (destArea != null && destCoords != null) {
                player.leaveArea();
                ICMazeArea currentArea = (ICMazeArea) setCurrentArea(destArea, false);
                player.enterArea(currentArea, destCoords);
                player.centerCamera();
            }
            player.resetPortal(); // On réinitialise le portail
        }
    }
}
