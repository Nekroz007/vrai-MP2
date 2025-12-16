package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public class LargeArea extends ICMazeArea {
    private final int difficulty;
    public static final int KEY_ID = 3;


    public LargeArea() {
        super("LargeArea", 32, KEY_ID);
        this.difficulty = Difficulty.HARDEST;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() { return new DiscreteCoordinates(1, size / 2 + 1); }


    protected void createArea() {
        registerActor(new Background(this, "LargeArea"));
        generateMazeAndPlaceRocks(difficulty);

        createPortals();

        Portal westPortal = portals.get(AreaPortals.W);
        westPortal.setState(Portal.State.OPEN);
        westPortal.setDestination("icmaze/MediumArea", new DiscreteCoordinates(15, 8));

        Portal eastPortal = portals.get(AreaPortals.E);
        eastPortal.setState(Portal.State.LOCKED);
        eastPortal.setKeyId(KEY_ID);
        eastPortal.setDestination("icmaze/Boss", new DiscreteCoordinates(1, 4));

        for (Portal p : portals.values()) registerActor(p);

        int maxEnemies = 3;
        int enemyCount = 0;

        double diffRatio = Math.min(
                1.0,
                (double) Difficulty.HARDEST / difficulty
        );

        List<DiscreteCoordinates> coords = graph.keySet();
        Collections.shuffle(coords, RandomGenerator.rng);

        for (DiscreteCoordinates pos : coords) {

            if (enemyCount >= maxEnemies) break;

            if (RandomGenerator.rng.nextDouble() < 0.25 + 0.60 * diffRatio) {

                double pTarget  = 0.10 + 0.70 * diffRatio;
                double pRandom  = 0.20;
                double pSleeping = 1.0 - pTarget - pRandom;

                double roll = RandomGenerator.rng.nextDouble();
                LogMonster.State state;

                if (roll < pSleeping) {
                    state = LogMonster.State.SLEEPING;
                } else if (roll < pSleeping + pRandom) {
                    state = LogMonster.State.RANDOM;
                } else {
                    state = LogMonster.State.CHASING;
                }

                registerActor(new LogMonster(this, Orientation.DOWN, pos, state));

                enemyCount++;
            }
        }

    }

    @Override
    public String getTitle() { return "icmaze/LargeArea"; }
}
