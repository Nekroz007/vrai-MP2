package ch.epfl.cs107.play.areagame.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;

import java.util.List;

/**
 * MovableAreaEntity represent AreaEntity which can move on the grid
 */
public abstract class MovableAreaEntity extends AreaEntity {

    /// Indicate if a displacement occurs right now
    private boolean displacementOccurs;
    /// Indicate how many frames the current move is supposed to take
    private int framesForCurrentMove;
    /// Indicate how many remaining frames the current move has
    private int remainingFramesForCurrentMove;
    /// Indicate if the entity entered the new cells and left the unused ones
    private boolean cellsSwapped;
    private List<DiscreteCoordinates> originCells;
    private List<DiscreteCoordinates> targetCells;

    private Vector targetPosition;
    private Vector originPosition;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     */
    public MovableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        resetMotion();
    }

    /**
     * Initialize or reset (if some) the current motion information
     */
    protected void resetMotion() {
        this.displacementOccurs = false;
        this.framesForCurrentMove = 0;
        this.remainingFramesForCurrentMove = 0;
        this.cellsSwapped = false;
        originCells = null;
        targetCells = null;
    }

    /**
     * Final move method
     * If no displacement occurs or if the displacement just ends now,
     * start movement of one Cell in the current Orientation direction
     * Note the movement is possible only if this MovableAreaEntity can:
     * - leave the cells this motion implies to leave
     * - enter the cells this motion implies to enter
     *
     * @param frameForMove (int): the frame. This value will be cropped to 1 if smaller
     * @return (boolean): indicate if the move is initiated
     */
    protected final boolean move(int frameForMove) {
        return move(frameForMove, 0);
    }

    /**
     * Final move method
     * If no displacement occurs or if the displacement just ends now,
     * start movement of one Cell in the current Orientation direction
     * Note the movement is possible only if this MovableAreaEntity can:
     * - leave the cells this motion implies to leave
     * - enter the cells this motion implies to enter
     *
     * @param frameForMove  (int): the frame. This value will be cropped to 1 if smaller
     * @param startingFrame (int): start the movement directly from this frame
     * @return (boolean): indicate if the move is initiated
     */
    protected final boolean move(int frameForMove, int startingFrame) {
        if (!displacementOccurs || isTargetReached()) {
            List<DiscreteCoordinates> originCells = getCurrentCells();
            List<DiscreteCoordinates> targetCells = getNextCells(getPosition().add(getOrientation().toVector()));

            List<DiscreteCoordinates> enteringCells = targetCells.stream().filter(coords -> !originCells.contains(coords)).toList();
            List<DiscreteCoordinates> leavingCells = originCells.stream().filter(coords -> !targetCells.contains(coords)).toList();
            if (getOwnerArea().canEnterAreaCells(this, enteringCells) && getOwnerArea().canLeaveAreaCells(this, leavingCells)) {
                displacementOccurs = true;
                this.originCells = originCells;
                this.targetCells = targetCells;
                this.cellsSwapped = false;

                this.framesForCurrentMove = Math.max(1, frameForMove);
                startingFrame = Math.min(startingFrame, frameForMove);
                remainingFramesForCurrentMove = framesForCurrentMove - startingFrame;

                originPosition = getPosition();
                targetPosition = getPosition().add(getOrientation().toVector());
                increasePositionOf(startingFrame);
                return true;
            }
        }
        return false;
    }

    private List<DiscreteCoordinates> getNextCells(Vector nextPosition) {
        final Vector oldPosition = getPosition();
        setCurrentPosition(nextPosition);
        final List<DiscreteCoordinates> result = getCurrentCells();
        setCurrentPosition(oldPosition);
        return result;
    }

    /**
     * Change the unit position to the one specified
     *
     * @param newPosition new unit's position
     * @return true if the move was successful, false otherwise
     */
    public boolean changePosition(DiscreteCoordinates newPosition) {
        if (newPosition.equals(getCurrentMainCellCoordinates())) {
            return true;
        }
        List<DiscreteCoordinates> currentCells = getCurrentCells();
        List<DiscreteCoordinates> nextCells = getNextCells(newPosition.toVector());

        final List<DiscreteCoordinates> enteringCells = nextCells.stream().filter(coords -> !currentCells.contains(coords)).toList();
        final List<DiscreteCoordinates> leavingCells = currentCells.stream().filter(coords -> !nextCells.contains(coords)).toList();

        if (getOwnerArea().canEnterAreaCells(this, enteringCells) && getOwnerArea().leaveAreaCells(this, leavingCells)) {
            getOwnerArea().enterAreaCells(this, enteringCells);

            setCurrentPosition(newPosition.toVector());
            resetMotion();
            return true;
        }
        System.out.println("Position change impossible. Destination is most likely occupied");
        return false;
    }


    /**
     * Final abortCurrentMove method
     * If a displacement occurs and if the displacement is not end,
     * abort the current move, returning to the previous cell
     * Note the abort is possible only if this MovableAreaEntity can:
     * - return to the cells it left
     * - leave the cells it entered
     *
     * @return (boolean): indicate if the abort is initiated
     */
    protected final boolean abortCurrentMove() {
        if (!displacementOccurs) {
            return false;
        }
        if (cellsSwapped) {
            final List<DiscreteCoordinates> enteringCells = targetCells.stream().filter(coords -> !originCells.contains(coords)).toList();
            final List<DiscreteCoordinates> leavingCells = originCells.stream().filter(coords -> !targetCells.contains(coords)).toList();

            if (getOwnerArea().canEnterAreaCells(this, leavingCells) && getOwnerArea().canLeaveAreaCells(this, enteringCells)) {
                cellsSwapped = false;
            } else {
                return false;
            }
        } else {
            cellsSwapped = true;
        }
        remainingFramesForCurrentMove = framesForCurrentMove - remainingFramesForCurrentMove;

        Vector tempPos = originPosition;
        originPosition = targetPosition;
        targetPosition = tempPos;

        List<DiscreteCoordinates> tempCells = originCells;
        originCells = targetCells;
        targetCells = tempCells;
        return true;
    }


    /**
     * Indicate if a displacement is occurring
     *
     * @return (boolean)
     */
    protected boolean isDisplacementOccurs() {
        return displacementOccurs;
    }

    /**
     * @return (boolean): true when the target cell is just reaching now
     */
    protected boolean isTargetReached() {
        return remainingFramesForCurrentMove == 0;
    }

    /**
     * Increase the position of a certain amount of frame
     *
     * @param frame
     */
    private void increasePositionOf(int frame) {
        final DiscreteCoordinates before = getCurrentMainCellCoordinates();
        setCurrentPosition(getPosition().add(getOrientation().toVector().mul(frame / (float) framesForCurrentMove)));
        final DiscreteCoordinates after = getCurrentMainCellCoordinates();
        if (!before.equals(after)) {
            final List<DiscreteCoordinates> enteringCells = targetCells.stream().filter(coords -> !originCells.contains(coords)).toList();
            final List<DiscreteCoordinates> leavingCells = originCells.stream().filter(coords -> !targetCells.contains(coords)).toList();

            if (getOwnerArea().canEnterAreaCells(this, enteringCells) && getOwnerArea().leaveAreaCells(this, leavingCells)) {
                getOwnerArea().enterAreaCells(this, enteringCells);
                cellsSwapped = true;
            } else {
                orientate(getOrientation().opposite());
            }
        }
    }

    /// MovableAreaEntity extends AreaEntity
    @Override
    protected boolean orientate(Orientation orientation) {
        // Allow reorientation only if no displacement is occurring or if abort current move (opposite orientation)
        if (getOrientation().opposite().equals(orientation)) {
            if (abortCurrentMove())
                return super.orientate(orientation);
        }
        return !displacementOccurs && super.orientate(orientation);
    }

    /// MovableAreaEntity implements Actor

    @Override
    public void update(float deltaTime) {
        if (displacementOccurs) {
            if (!isTargetReached()) {
                increasePositionOf(1);
            } else {
                setCurrentPosition(targetPosition);
                resetMotion();
            }
        }
        remainingFramesForCurrentMove = Math.max(remainingFramesForCurrentMove - 1, 0);
    }

    /// Implements Positionable

    @Override
    public Vector getVelocity() {
        return getOrientation().toVector().mul(framesForCurrentMove);
    }
}
