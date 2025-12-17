package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.actor.collectable.Diamond;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeBehavior;
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
    default void interactWith(FireProjectile fireProjectile, boolean isCellInteraction) { ; }
    default void interactWith(Boss boss, boolean isCellInteraction) { ; }
    default void interactWith(Diamond diamond, boolean isCellInteraction) { ; }
}
