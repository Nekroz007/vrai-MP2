package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.LargeArea;
import ch.epfl.cs107.icmaze.area.maps.MediumArea;
import ch.epfl.cs107.icmaze.area.maps.SmallArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.*;

public class LevelGenerator {

    private final static int ROOM_DISTRIBUTION_CONSTANT = 4;

    /**
     * Génère un niveau linéaire de taille donnée.
     * @param game Le jeu (pour l'initialisation des aires)
     * @param length La longueur du niveau (nombre de salles entre Spawn et Boss)
     * @return Un tableau contenant toutes les aires générées dans l'ordre
     */
    public static ICMazeArea[] generateLine(ICMaze game, int length) {
        // 1. Préparation des structures
        ICMazeArea[] areas = new ICMazeArea[length + 2]; // +2 pour Spawn et Boss
        Set<DiscreteCoordinates> occupiedCoords = new HashSet<>();

        // Coordonnées fictives dans la grille de génération (x, y)
        DiscreteCoordinates currentMapCoord = new DiscreteCoordinates(0, 0);
        occupiedCoords.add(currentMapCoord);

        // --- 2. Création du Spawn (Début) ---
        Spawn spawn = new Spawn();
        // Initialisation standard du Spawn pour le jeu
        spawn.setBehavior(new ICMazeBehavior(game.getWindow(), "SmallArea"));
        // Note: createArea() sera appelé par le jeu plus tard, mais on peut configurer ici
        areas[0] = spawn;

        ICMazeArea prevArea = spawn;
        Orientation entryForNext = null; // Pour Spawn, pas d'entrée spécifique requise par le algo ici

        // --- 3. Boucle de génération des salles intermédiaires ---
        for (int i = 0; i < length; i++) {
            // A. Calcul de la progression et difficulté
            double progress = (double) (i + 1) / length;
            int difficulty = switch ((int) (progress * ROOM_DISTRIBUTION_CONSTANT)) {
                case 0 -> Difficulty.EASY;
                case 1 -> Difficulty.MEDIUM;
                case 2 -> Difficulty.HARD;
                default -> Difficulty.HARDEST;
            };

            // B. Choix d'une direction libre (Nord, Est, Sud)
            Orientation exitDir = chooseRandomDirection(currentMapCoord, occupiedCoords, prevArea);

            // Si cul-de-sac (ne devrait pas arriver avec cet algo simple, mais sécurité)
            if (exitDir == null) exitDir = Orientation.RIGHT;

            // C. Mise à jour coordonnées
            currentMapCoord = currentMapCoord.jump(exitDir.toVector());
            occupiedCoords.add(currentMapCoord);

            // D. Détermination du type de salle
            Orientation entryDir = exitDir.opposite(); // On entre par l'opposé de la sortie précédente
            // La sortie de la nouvelle salle sera déterminée au prochain tour,
            // MAIS pour les constructeurs de Small/Medium/Large, on doit souvent donner entrée/sortie.
            // Astuce : Ici on connecte "prev" à "current".
            // Le constructeur demande (entry, exit, keyId, difficulty).
            // On ne connait pas encore la sortie de la nouvelle salle 'i'.
            // C'est un problème classique. Solution: On pré-calcule tout le chemin OU on utilise des setters.
            // Pour simplifier, on va tricher légèrement : on décide de la sortie MAINTENANT pour la salle 'i'.
            // Mais attendez, la boucle construit la salle 'i'.

            // Correction algorithme : On doit choisir la direction de sortie de la salle 'i' MAINTENANT.
            // La direction choisie 'exitDir' est celle qui sort de 'prevArea' vers 'newArea'.
            // Donc 'newArea' est placée à 'exitDir'.
            // L'entrée de 'newArea' est 'exitDir.opposite()'.
            // Et quelle est la sortie de 'newArea' ? Elle sera choisie à l'itération i+1 ?
            // Non, il faut la choisir maintenant pour construire l'objet si le constructeur l'exige.

            // Simplification : On va générer la géométrie (liste de directions) AVANT de créer les objets.
        }

        // --- RESTART : Approche par pré-calcul du chemin ---
        // C'est beaucoup plus propre pour satisfaire les constructeurs.

        List<Orientation> path = new ArrayList<>();
        DiscreteCoordinates cursor = new DiscreteCoordinates(0,0);
        Set<DiscreteCoordinates> map = new HashSet<>();
        map.add(cursor);

        // On génère 'length' directions (sorties de Spawn, Salle 1, ..., Salle N-1)
        // La Salle N sortira vers le Boss.
        for (int i = 0; i < length; i++) {
            List<Orientation> candidates = new ArrayList<>(Arrays.asList(Orientation.UP, Orientation.RIGHT, Orientation.DOWN));
            Collections.shuffle(candidates, RandomGenerator.rng);

            Orientation selected = null;
            for (Orientation dir : candidates) {
                if (!map.contains(cursor.jump(dir.toVector()))) {
                    selected = dir;
                    break;
                }
            }
            // Fallback si bloqué (peu probable en lineaire simple vers l'avant)
            if (selected == null) selected = Orientation.RIGHT;

            path.add(selected);
            cursor = cursor.jump(selected.toVector());
            map.add(cursor);
        }
        // Dernière direction vers le Boss (toujours possible car Boss n'est pas sur la grille occupée)
        path.add(Orientation.RIGHT);

        // --- Instanciation des aires ---

        // 1. Spawn
        // Spawn doit avoir une sortie verrouillée (clé MAX_VALUE).
        // Sa direction de sortie est path.get(0).
        int keyId = 1; // On commence les clés à 1

        // Reset variables
        prevArea = spawn;
        Orientation dirFromPrev = path.get(0);

        // Configuration Spawn (Sortie)
        // On configure le portail de sortie de Spawn.
        // Note: Pour Spawn, le constructeur ne prend pas d'orientation, il faut utiliser registerPortal ou setDestination après.
        // Mais Spawn est "fixe". On va supposer qu'on utilise registerPortal pour connecter.

        areas[0] = spawn;

        for (int i = 0; i < length; i++) {
            // Création de l'aire i (qui est en fait la i+1eme aire du tableau, après Spawn)
            double progress = (double) (i + 1) / length;
            int difficulty = switch ((int) (progress * ROOM_DISTRIBUTION_CONSTANT)) {
                case 0 -> Difficulty.EASY;
                case 1 -> Difficulty.MEDIUM;
                case 2 -> Difficulty.HARD;
                default -> Difficulty.HARDEST;
            };

            Orientation entry = dirFromPrev.opposite();
            Orientation exit = path.get(i+1); // Vers la prochaine (ou Boss)
            int currentKeyId = keyId++;

            ICMazeArea newArea;
            double r = RandomGenerator.rng.nextDouble();
            if (r < progress * progress) {
                newArea = new LargeArea(entry, exit, currentKeyId, difficulty);
            } else if (r < progress) {
                newArea = new MediumArea(entry, exit, currentKeyId, difficulty);
            } else {
                newArea = new SmallArea(entry, exit, currentKeyId, difficulty);
            }
            areas[i+1] = newArea;

            // Connexion Bidirectionnelle : Prev -> New et New -> Prev
            connect(prevArea, newArea, dirFromPrev, entry, (i==0 ? Integer.MAX_VALUE : currentKeyId - 1));
            // Note sur les clés :
            // Spawn (idx 0) sort avec MAX_VALUE (selon enoncé 2.6.3/3.3).
            // Les autres sortent avec la clé qu'ils contiennent.

            prevArea = newArea;
            dirFromPrev = exit;
        }

        // --- Boss Area ---
        // Le boss est la dernière aire.
        // Entrée = opposé de la dernière direction.
        // Clé d'entrée = -1 (Piège fatal) selon 5.1, ou ouvert selon 2.6.3.
        // Le PDF 4.3 dit "Ajoute une BossArea... Connecte les deux".
        Orientation bossEntry = dirFromPrev.opposite();
        BossArea bossArea = new BossArea(bossEntry, -1); // -1 pour le piège
        areas[length + 1] = bossArea;

        // Connexion Finale : Dernière Salle -> Boss
        // La dernière salle a pour clé de sortie 'keyId - 1' (celui généré à la fin de la boucle)
        connect(prevArea, bossArea, dirFromPrev, bossEntry, keyId - 1);

        return areas;
    }

    // Méthode utilitaire pour connecter deux aires
    private static void connect(ICMazeArea from, ICMazeArea to, Orientation dirFrom, Orientation dirTo, int keyId) {
        // Calcul des coordonnées d'arrivée
        DiscreteCoordinates coordsInTo = getArrivalCoordinates(to.getWidth(), dirTo);
        DiscreteCoordinates coordsInFrom = getArrivalCoordinates(from.getWidth(), dirFrom.opposite()); // Pour le retour

        // Enregistrement portail ALLER (From -> To)
        // Verrouillé si c'est une sortie de labyrinthe (keyId valide)
        Portal.State state = (keyId != Portal.NO_KEY_ID) ? Portal.State.LOCKED : Portal.State.OPEN;
        from.registerPortal(getPortalType(dirFrom), to.getTitle(), coordsInTo, state, keyId);

        // Enregistrement portail RETOUR (To -> From)
        // Toujours ouvert (sauf piège Boss, mais ici c'est le retour standard)
        // Note: Le piège du Boss est géré dans le constructeur/update du BossArea normalement
        to.registerPortal(getPortalType(dirTo), from.getTitle(), coordsInFrom, Portal.State.OPEN, Portal.NO_KEY_ID);
    }

    private static ICMazeArea.AreaPortals getPortalType(Orientation dir) {
        return switch (dir) {
            case UP -> ICMazeArea.AreaPortals.N;
            case DOWN -> ICMazeArea.AreaPortals.S;
            case LEFT -> ICMazeArea.AreaPortals.W;
            case RIGHT -> ICMazeArea.AreaPortals.E;
        };
    }

    private static DiscreteCoordinates getArrivalCoordinates(int size, Orientation entryDir) {
        int center = size / 2;
        return switch (entryDir) {
            case UP -> new DiscreteCoordinates(center, size - 2); // Arrive en haut (descend)
            case DOWN -> new DiscreteCoordinates(center, 1);      // Arrive en bas (monte)
            case LEFT -> new DiscreteCoordinates(1, center);      // Arrive à gauche (va à droite)
            case RIGHT -> new DiscreteCoordinates(size - 2, center); // Arrive à droite (va à gauche)
        };
    }

    // Helper pour la direction random (utilisé dans la v1 du code, gardé si besoin)
    private static Orientation chooseRandomDirection(DiscreteCoordinates current, Set<DiscreteCoordinates> occupied, ICMazeArea prev) {
        List<Orientation> dirs = new ArrayList<>(Arrays.asList(Orientation.UP, Orientation.RIGHT, Orientation.DOWN));
        Collections.shuffle(dirs, RandomGenerator.rng);
        for(Orientation d : dirs) {
            if(!occupied.contains(current.jump(d.toVector()))) return d;
        }
        return null;
    }
}