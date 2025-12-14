package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
        private final Sprite sprite;
        private int hp;
        private Animation deathAnimation;
        private boolean isDying = false;
        private final Cooldown damageCooldown;
        private boolean isInvulnerable = false;

        public Rock(Area area, Orientation orientation, DiscreteCoordinates position) {
            super(area, orientation, position);
            this.sprite = new Sprite("rock.2", 1, 1, this);
            this.hp = 3;
            this.damageCooldown = new Cooldown(0.5f);
        }

        /**
         * Applique des dommages au rocher
         *
         * @param damage Quantité de dégâts
         */
        public void takeDamage(int damage) {
            if (!isDying && !isInvulnerable) {
                hp -= damage;
                isInvulnerable = true;
                damageCooldown.reset();

                if (hp <= 0) {
                    isDying = true;
                    this.deathAnimation = new Animation("icmaze/vanish", 7, 2, 2, this,32
                            , 32, new Vector(-0.5f, 0.0f), 24/7, false);
                }
            }
        }

        @Override
        public void update(float deltaTime) {
            super.update(deltaTime);

            if (isInvulnerable) {
                if (damageCooldown.ready(deltaTime)) {
                    isInvulnerable = false;
                }
            }

            if (isDying) {
                deathAnimation.update(deltaTime);
                if (deathAnimation.isCompleted()) {
                    getOwnerArea().unregisterActor(this);

                    if (RandomGenerator.rng.nextDouble() < 0.5) {
                        getOwnerArea().registerActor(new Heart(getOwnerArea(), Orientation.DOWN, getCurrentMainCellCoordinates()));
                    }
                }
            }
        }

        @Override
        public void draw(Canvas canvas) {
            if (isDying) {
                deathAnimation.draw(canvas);
            } else {
                sprite.draw(canvas);
            }
        }

        @Override
        public boolean takeCellSpace(){
            return !isDying;
        }

        @Override
        public boolean isCellInteractable() {
            return !isDying;
        }

        @Override
        public boolean isViewInteractable() {
            // On peut interagir à distance (attaquer) tant qu'il n'est pas mort
            return !isDying;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
        }

        @Override
        public List<DiscreteCoordinates> getCurrentCells() {
            return Collections.singletonList(getCurrentMainCellCoordinates());
        }
    }

