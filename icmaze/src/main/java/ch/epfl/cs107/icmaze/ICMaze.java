package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.LevelGenerator;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.icmaze.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame implements DialogHandler {
    private final String[] areas = {"icmaze/Spawn", "icmaze/Boss"};
    private ICMazePlayer player;
    private int areaIndex;
    private Dialog activeDialog;

    private void generateHardCodedLevel() {
        ;
    }

    protected void createAreas() {
        int labyrinthCount = 0;

        ICMazeArea[] levels = LevelGenerator.generateLine(this, labyrinthCount);

        for (ICMazeArea area : levels) {
            if (area instanceof Spawn) {
                ((Spawn) area).setDialogHandler(this);
            }
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

        if (activeDialog != null) {
            if (getCurrentArea() != null) {
                getCurrentArea().draw(getWindow());
            }

            if (keyboard.get(KeyBindings.NEXT_DIALOG).isPressed()) {
                activeDialog.update(deltaTime);
            }
            activeDialog.draw(getWindow());

            if (activeDialog.isCompleted()) {
                activeDialog = null;
            }
            return;
        }

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

    @Override
    public void publish(Dialog dialog) {
        this.activeDialog = dialog;
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

    public void switchArea() {
        if (player.getCurrentPortal() != null) {
            String destinArea = player.getCurrentPortal().getDestinationAreaName();
            DiscreteCoordinates destCoords = player.getCurrentPortal().getDestinationCoords();

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