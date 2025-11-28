package ch.epfl.cs107.play.tuto1;

import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.tuto1.actor.SimpleGhost;
import ch.epfl.cs107.play.tuto1.area.maps.Ferme;
import ch.epfl.cs107.play.tuto1.area.maps.Village;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

/**
 * A grid-based game with a simple ghost
 */
public final class Tuto1 extends AreaGame {
    private final static float STEP = .05f;
    private final String[] areas = {"zelda/Ferme", "zelda/Village"};
    private SimpleGhost player;
    private int areaIndex;

    /**
     * Add all the Tuto1 areas
     */
    private void createAreas() {
        addArea(new Ferme());
        addArea(new Village());
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            Area area = setCurrentArea(areas[areaIndex], true);
            player = new SimpleGhost(new Vector(18, 7), "ghost.1");
            area.registerActor(player);
            area.setViewCandidate(player);
            return true;
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        if (player.isWeak())
            switchArea();
        Keyboard keyboard = getWindow().getKeyboard();
        Button key = keyboard.get(Keyboard.UP);
        if (key.isDown())
            player.moveUp(STEP);
        key = keyboard.get(Keyboard.DOWN);
        if (key.isDown())
            player.moveDown(STEP);
        key = keyboard.get(Keyboard.LEFT);
        if (key.isDown())
            player.moveLeft(STEP);
        key = keyboard.get(Keyboard.RIGHT);
        if (key.isDown())
            player.moveRight(STEP);
        super.update(deltaTime);
    }

    @Override
    public void end() {

    }

    @Override
    public String getTitle() {
        return "Tuto1";
    }

    /**
     * switches from one area to the other
     */
    private void switchArea() {
        Area currentArea = getCurrentArea();
        currentArea.unregisterActor(player);
        areaIndex = (areaIndex == 0) ? 1 : 0;
        currentArea = setCurrentArea(areas[areaIndex], false);
        currentArea.registerActor(player);
        currentArea.setViewCandidate(player);
        player.strengthen();
    }

}