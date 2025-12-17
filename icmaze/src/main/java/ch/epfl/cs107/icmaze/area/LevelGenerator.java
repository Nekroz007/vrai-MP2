package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.*;

public class LevelGenerator {

    public static ICMazeArea[] generateLine(ICMaze game, int length) {;
        List<ICMazeArea> levels = new ArrayList<>();
        Map<DiscreteCoordinates, ICMazeArea> map = new HashMap<>();

        // spawn
        DiscreteCoordinates currentPos = new DiscreteCoordinates(0, 0);
        Spawn spawn = new Spawn();
        levels.add(spawn);
        map.put(currentPos, spawn);

        ICMazeArea prevArea = spawn;

        List<Orientation> dirs = Arrays.asList(Orientation.UP, Orientation.RIGHT, Orientation.DOWN);

        // salle de labyrinthe
        for (int i = 0; i < length; ++i) {
            double progress = (double) (i + 1) / length;
            int difficulty;
            int step = (int) (progress * 4);
            switch (step) {
                case 0: difficulty = Difficulty.EASY; break;
                case 1: difficulty = Difficulty.MEDIUM; break;
                case 2: difficulty = Difficulty.HARD; break;
                default: difficulty = Difficulty.HARDEST; break;
            }

            int keyId = RandomGenerator.rng.nextInt(10000) + 1;
            ICMazeArea newArea;
            double r = RandomGenerator.rng.nextDouble();

            if (r < progress * progress) newArea = new LargeArea(difficulty, keyId);
            else if (r < progress) newArea = new MediumArea(difficulty, keyId);
            else newArea = new SmallArea(difficulty, keyId);

            Collections.shuffle(dirs, RandomGenerator.rng);
            DiscreteCoordinates nextPos = null;
            Orientation usedDir = null;

            for (Orientation d : dirs) {
                DiscreteCoordinates candidate = currentPos.jump(d.toVector());
                if (!map.containsKey(candidate)) {
                    nextPos = candidate;
                    usedDir = d;
                    break;
                }
            }
            if (nextPos == null) break;

            connect(prevArea, newArea, usedDir);
            levels.add(newArea);
            map.put(nextPos, newArea);
            prevArea = newArea;
            currentPos = nextPos;
        }

        // boss
        BossArea boss = new BossArea();
        Collections.shuffle(dirs, RandomGenerator.rng);
        for (Orientation d : dirs) {
            DiscreteCoordinates candidate = currentPos.jump(d.toVector());
            if (!map.containsKey(candidate)) {
                connect(prevArea, boss, d);
                levels.add(boss);
                break;
            }
        }

        return levels.toArray(new ICMazeArea[0]);
    }

    private static void connect(ICMazeArea from, ICMazeArea to, Orientation dir) {
        from.setExitOrientation(dir);
        to.setEntryOrientation(dir.opposite());

        int fromSize = getAreaSize(from);
        int toSize = getAreaSize(to);

        ICMazeArea.AreaPortals outPortal;
        ICMazeArea.AreaPortals inPortal;
        DiscreteCoordinates arrivalInTo;
        DiscreteCoordinates arrivalInFrom;

        switch (dir) {
            case UP:
                outPortal = ICMazeArea.AreaPortals.N;
                inPortal = ICMazeArea.AreaPortals.S;

                arrivalInTo = new DiscreteCoordinates(toSize / 2, 1);
                arrivalInFrom = new DiscreteCoordinates(fromSize / 2, fromSize - 1);
                break;

            case RIGHT:
                outPortal = ICMazeArea.AreaPortals.E;
                inPortal = ICMazeArea.AreaPortals.W;

                arrivalInTo = new DiscreteCoordinates(1, toSize / 2);
                arrivalInFrom = new DiscreteCoordinates(fromSize - 1, fromSize / 2);
                break;

            case DOWN:
                outPortal = ICMazeArea.AreaPortals.S;
                inPortal = ICMazeArea.AreaPortals.N;

                arrivalInTo = new DiscreteCoordinates(toSize / 2, toSize - 1);
                arrivalInFrom = new DiscreteCoordinates(fromSize / 2, 1);
                break;

            default:
                outPortal = ICMazeArea.AreaPortals.W;
                inPortal = ICMazeArea.AreaPortals.E;

                arrivalInTo = new DiscreteCoordinates(toSize - 1, toSize / 2);
                arrivalInFrom = new DiscreteCoordinates(1, fromSize / 2);
                break;
        }

        from.registerPortal(outPortal, to.getTitle(), arrivalInTo, Portal.State.LOCKED, from.getKeyId());
        to.registerPortal(inPortal, from.getTitle(), arrivalInFrom, Portal.State.OPEN, Portal.NO_KEY_ID);
    }

    private static int getAreaSize(ICMazeArea area) {
        return area.getSize();
    }
}