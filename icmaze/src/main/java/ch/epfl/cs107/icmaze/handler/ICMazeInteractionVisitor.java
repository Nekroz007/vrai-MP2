package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeBehavior;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
/**
 * InteractionVisitor for the ICMaze entities
 */

public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {
  /// Add Interaction method with all non Abstract Interactable
    default void interactWith(ICMazeBehavior.ICMazeCell cell, boolean isCellInteraction) { ; }
    default void interactWith(ICMazePlayer player, boolean isCellInteraction) { ; }
    default void interactWith(Pickaxe pickaxe, boolean isCellInteraction) { ; }
    default void interactWith(Heart heart, boolean isCellInteraction) { ; }
    default void interactWith(Key key, boolean isCellInteraction) { ; }
    default void interactWith(Portal portal, boolean isCellInteraction) { ; }
    default void interactWith(Rock rock, boolean isCellInteraction) { ; }
    default void interactWith(LogMonster logMonster, boolean isCellInteraction) { ; }
}
