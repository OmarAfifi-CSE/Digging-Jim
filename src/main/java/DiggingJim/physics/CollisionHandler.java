package DiggingJim.physics;

import java.util.ArrayList;
import java.util.List;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameEngine;
import DiggingJim.core.GameObject;
import DiggingJim.entities.characters.GameCharacter;
import DiggingJim.entities.characters.Monster;
import DiggingJim.entities.environment.Diamond;
import DiggingJim.entities.environment.Rock;
import DiggingJim.entities.environment.Sand;
import javafx.geometry.Bounds;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class CollisionHandler {
    private final Pane root;
    private final GameEngine gameEngine;
    private final GameCharacter character;
    private final List<Rock> rocks;
    private final List<Diamond> diamonds;
    private final List<Monster> monsters;
    private final List<Sand> sandTiles;
    private final HBox bricksFirstMiddle;
    private final HBox bricksSecondMiddle;

    public CollisionHandler(Pane root, GameEngine gameEngine, GameCharacter character,
                            List<Rock> rocks, List<Diamond> diamonds, List<Monster> monsters,
                            List<Sand> sandTiles, HBox bricksFirstMiddle, HBox bricksSecondMiddle) {
        this.root = root;
        this.gameEngine = gameEngine;
        this.character = character;
        this.rocks = rocks;
        this.diamonds = diamonds;
        this.monsters = monsters;
        this.sandTiles = sandTiles;
        this.bricksFirstMiddle = bricksFirstMiddle;
        this.bricksSecondMiddle = bricksSecondMiddle;
    }

    /**
     * Main collision update loop, called from GameEngine
     */
    public void handleCollisions(double deltaTime) {
        handleCharacterSandCollision();
        handleCharacterBrickCollision();
        handleCharacterRockCollision(deltaTime);
        handleCharacterMonsterCollision();
        handleCharacterDiamondCollision();
        handleRockPhysics(deltaTime);
    }

    private void handleCharacterBrickCollision() {
        // Broad boundaries (Bricks frame)
        if (character.getX() < GameConfig.BRICKS_FRAME_SIZE) {
            character.setX(GameConfig.BRICKS_FRAME_SIZE);
        } else if (character.getX() > GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE - GameConfig.CHARACTER_SIZE) {
            character.setX(GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE - GameConfig.CHARACTER_SIZE);
        }

        if (character.getY() < GameConfig.BRICKS_FRAME_SIZE) {
            character.setY(GameConfig.BRICKS_FRAME_SIZE);
        } else if (character.getY() > GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.CHARACTER_SIZE) {
            character.setY(GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.CHARACTER_SIZE);
        }

        // Platform collisions (AABB)
        resolveBrickPlatformCollision(bricksFirstMiddle);
        resolveBrickPlatformCollision(bricksSecondMiddle);
    }

    private void resolveBrickPlatformCollision(HBox platform) {
        Bounds charBounds = character.getBoundsInParent();
        Bounds platBounds = platform.getBoundsInParent();

        if (charBounds.intersects(platBounds)) {
            double charCenterX = charBounds.getCenterX();
            double charCenterY = charBounds.getCenterY();
            double platCenterX = platBounds.getCenterX();
            double platCenterY = platBounds.getCenterY();

            double dx = charCenterX - platCenterX;
            double dy = charCenterY - platCenterY;
            double combinedHalfWidth = (charBounds.getWidth() + platBounds.getWidth()) / 2;
            double combinedHalfHeight = (charBounds.getHeight() + platBounds.getHeight()) / 2;

            double overlapX = combinedHalfWidth - Math.abs(dx);
            double overlapY = combinedHalfHeight - Math.abs(dy);

            if (overlapX < overlapY) {
                if (dx > 0) character.setX(character.getX() + overlapX);
                else character.setX(character.getX() - overlapX);
            } else {
                if (dy > 0) character.setY(character.getY() + overlapY);
                else character.setY(character.getY() - overlapY);
            }
        }
    }

    private void handleCharacterSandCollision() {
        Bounds characterBounds = character.getBoundsInParent();
        List<Sand> toRemove = new ArrayList<>();

        // Optimization: Only check sand tiles near the character
        for (Sand sand : getSandTilesInProximity(characterBounds, 50)) {
            if (sand.getBoundsInParent().intersects(characterBounds)) {
                toRemove.add(sand);
                root.getChildren().remove(sand);

                // Check if any rocks above need to start falling
                for (Rock rock : rocks) {
                    if (!rock.isFalling()) {
                        checkRockSupport(rock);
                    }
                }
            }
        }

        if (!toRemove.isEmpty()) {
            sandTiles.removeAll(toRemove);
        }
    }

    private List<Sand> getSandTilesInProximity(Bounds bounds, double margin) {
        List<Sand> proximityTiles = new ArrayList<>();
        double minX = bounds.getMinX() - margin;
        double maxX = bounds.getMaxX() + margin;
        double minY = bounds.getMinY() - margin;
        double maxY = bounds.getMaxY() + margin;

        for (Sand sand : sandTiles) {
            double sx = sand.getX();
            double sy = sand.getY();
            if (sx >= minX && sx <= maxX && sy >= minY && sy <= maxY) {
                proximityTiles.add(sand);
            }
        }
        return proximityTiles;
    }

    private void handleCharacterRockCollision(double deltaTime) {
        Bounds charBounds = character.getBoundsInParent();
        
        for (Rock rock : rocks) {
            Bounds rockBounds = rock.getBoundsInParent();
            
            // Broad phase check
            if (!charBounds.intersects(rockBounds)) continue;

            // Determine collision side and resolve (AABB Resolution)
            double charCenterX = charBounds.getCenterX();
            double charCenterY = charBounds.getCenterY();
            double rockCenterX = rockBounds.getCenterX();
            double rockCenterY = rockBounds.getCenterY();
            
            double dx = charCenterX - rockCenterX;
            double dy = charCenterY - rockCenterY;
            double combinedHalfWidth = (charBounds.getWidth() + rockBounds.getWidth()) / 2;
            double combinedHalfHeight = (charBounds.getHeight() + rockBounds.getHeight()) / 2;

            double overlapX = combinedHalfWidth - Math.abs(dx);
            double overlapY = combinedHalfHeight - Math.abs(dy);

            if (overlapX > 0 && overlapY > 0) {
                if (overlapX < overlapY) {
                    // X-axis collision
                    if (dx > 0) {
                        // Character is on the right of the rock, attempt pushing
                        if (character.isMovingLeft() && !rock.isFalling()) {
                            attemptPushRock(rock, overlapX, false);
                        } else {
                            character.setX(character.getX() + overlapX);
                        }
                    } else {
                        // Character is on the left of the rock, attempt pushing
                        if (character.isMovingRight() && !rock.isFalling()) {
                            attemptPushRock(rock, overlapX, true);
                        } else {
                            character.setX(character.getX() - overlapX);
                        }
                    }
                } else {
                    // Y-axis collision
                    if (dy > 0) {
                        character.setY(character.getY() + overlapY);
                    } else {
                        character.setY(character.getY() - overlapY);
                    }
                }
            }
        }
    }

    private void attemptPushRock(Rock rock, double overlapX, boolean moveRight) {
        // Pushing should be consistent with character movement speed to avoid jitter
        double pushSpeed = 2.0; // Moderate push speed
        double pushDistance = moveRight ? pushSpeed : -pushSpeed;
        // Check slightly ahead to ensure the rock has space to move
        double checkOffset = moveRight ? 5 : -5;
        
        if (!isPathObstructed(rock, rock.getX() + checkOffset, rock.getY())) {
            rock.setX(rock.getX() + pushDistance);
            // Resolve overlap and move character with the rock
            // If moveRight (on left), resolution is -overlapX
            // If !moveRight (on right), resolution is +overlapX
            double resolution = moveRight ? -overlapX : overlapX;
            character.setX(character.getX() + resolution + pushDistance);
            checkRockSupport(rock);
        } else {
            // Path blocked, just resolve overlap normally
            double resolution = moveRight ? -overlapX : overlapX;
            character.setX(character.getX() + resolution);
        }
    }

    private boolean isPathObstructed(Rock rock, double targetX, double targetY) {
        // Check for sand
        for (Sand sand : getSandTilesInProximity(rock.getBoundsInParent(), 10)) {
            if (sand.getBoundsInParent().intersects(targetX + 5, targetY + 5, GameConfig.ROCK_WIDTH - 10, GameConfig.ROCK_HEIGHT - 10)) {
                return true;
            }
        }
        
        // Check for other rocks
        for (Rock other : rocks) {
            if (other == rock) continue;
            if (other.getBoundsInParent().intersects(targetX + 5, targetY + 5, GameConfig.ROCK_WIDTH - 10, GameConfig.ROCK_HEIGHT - 10)) {
                return true;
            }
        }
        
        // Bricks check (simplified)
        if (targetY + GameConfig.ROCK_HEIGHT > bricksFirstMiddle.getLayoutY() && targetY < bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
             if (targetX + GameConfig.ROCK_WIDTH > bricksFirstMiddle.getLayoutX() && targetX < bricksFirstMiddle.getLayoutX() + bricksFirstMiddle.getWidth()) return true;
        }
        
        return false;
    }

    private void handleRockPhysics(double deltaTime) {
        for (Rock rock : rocks) {
            if (rock.isFalling()) {
                rock.applyGravity();
                checkRockLanding(rock);
                checkRockCharacterKilling(rock);
            } else {
                checkRockSupport(rock);
            }
        }
    }

    private void checkRockLanding(Rock rock) {
        Bounds rockBounds = rock.getBoundsInParent();
        double rockBottom = rockBounds.getMaxY();
        boolean shouldStop = false;

        // Check for sand below
        for (Sand sand : getSandTilesInProximity(rockBounds, 20)) {
            Bounds sandBounds = sand.getBoundsInParent();
            if (rockBottom >= sandBounds.getMinY() && rockBottom <= sandBounds.getMaxY() &&
                rockBounds.getMinX() < sandBounds.getMaxX() && rockBounds.getMaxX() > sandBounds.getMinX()) {
                rock.setY(sandBounds.getMinY() - GameConfig.ROCK_HEIGHT);
                shouldStop = true;
                break;
            }
        }

        // Check for bricks
        if (!shouldStop) {
            if (Math.abs(rockBottom - bricksFirstMiddle.getLayoutY()) < 10) {
                rock.setY(bricksFirstMiddle.getLayoutY() - GameConfig.ROCK_HEIGHT);
                shouldStop = true;
            } else if (Math.abs(rockBottom - bricksSecondMiddle.getLayoutY()) < 10) {
                rock.setY(bricksSecondMiddle.getLayoutY() - GameConfig.ROCK_HEIGHT);
                shouldStop = true;
            } else if (Math.abs(rockBottom - (GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE)) < 10) {
                rock.setY(GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.ROCK_HEIGHT);
                shouldStop = true;
            }
        }

        // Check for other rocks (Stacking)
        if (!shouldStop) {
            for (Rock other : rocks) {
                if (other == rock) continue;
                Bounds otherBounds = other.getBoundsInParent();
                if (Math.abs(rockBottom - otherBounds.getMinY()) < 10 &&
                    rockBounds.getMinX() < otherBounds.getMaxX() &&
                    rockBounds.getMaxX() > otherBounds.getMinX()) {
                    rock.setY(otherBounds.getMinY() - GameConfig.ROCK_HEIGHT);
                    shouldStop = true;
                    break;
                }
            }
        }

        if (shouldStop) {
            rock.stopFalling();
        }
    }

    private void checkRockCharacterKilling(Rock rock) {
        // Only kill if rock is falling with significant downward momentum
        // Higher threshold (15.0) ensures it doesn't kill if it just started falling
        if (rock.isFalling() && rock.getGravity() > 15.0) {
            Bounds rockBounds = rock.getBoundsInParent();
            Bounds charBounds = character.getBoundsInParent();
            
            // Check if the rock's bottom area is intersecting the character's top area
            boolean isAbove = rockBounds.getMaxY() > charBounds.getMinY() && 
                             rockBounds.getMinY() < charBounds.getMinY();
            boolean isHorizontalAligned = rockBounds.getMinX() < charBounds.getMaxX() - 20 &&
                                         rockBounds.getMaxX() > charBounds.getMinX() + 20;

            if (isAbove && isHorizontalAligned) {
                gameEngine.characterDeath();
            }
        }
    }

    private void checkRockSupport(Rock rock) {
        Bounds rockBounds = rock.getBoundsInParent();
        double supportY = rockBounds.getMaxY() + 2;
        boolean hasSupport = false;

        // Check sand support
        for (Sand sand : getSandTilesInProximity(rockBounds, 10)) {
            Bounds sandBounds = sand.getBoundsInParent();
            if (sandBounds.contains(rockBounds.getCenterX(), supportY)) {
                hasSupport = true;
                break;
            }
        }

        // Check brick support
        if (!hasSupport) {
            if (Math.abs(rockBounds.getMaxY() - bricksFirstMiddle.getLayoutY()) < 5 ||
                Math.abs(rockBounds.getMaxY() - bricksSecondMiddle.getLayoutY()) < 5 ||
                Math.abs(rockBounds.getMaxY() - (GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE)) < 5) {
                hasSupport = true;
            }
        }

        // Check other rock support
        if (!hasSupport) {
            for (Rock other : rocks) {
                if (other == rock) continue;
                Bounds otherBounds = other.getBoundsInParent();
                if (Math.abs(rockBounds.getMaxY() - otherBounds.getMinY()) < 5 &&
                    rockBounds.getMinX() < otherBounds.getMaxX() &&
                    rockBounds.getMaxX() > otherBounds.getMinX()) {
                    hasSupport = true;
                    break;
                }
            }
        }

        // Check character support
        if (!hasSupport) {
            Bounds charBounds = character.getBoundsInParent();
            if (charBounds.getMinY() <= supportY && charBounds.getMaxY() >= supportY &&
                charBounds.getMinX() < rockBounds.getMaxX() - 10 &&
                charBounds.getMaxX() > rockBounds.getMinX() + 10) {
                hasSupport = true;
            }
        }

        if (!hasSupport) {
            rock.startFalling();
        }
    }

    private void handleCharacterMonsterCollision() {
        Bounds charBounds = character.getBoundsInParent();
        for (Monster monster : monsters) {
            if (monster.getBoundsInParent().intersects(charBounds)) {
                gameEngine.characterDeath();
                break;
            }
        }
    }

    private void handleCharacterDiamondCollision() {
        Bounds charBounds = character.getBoundsInParent();
        List<Diamond> toRemove = new ArrayList<>();
        for (Diamond diamond : diamonds) {
            if (diamond.getBoundsInParent().intersects(charBounds)) {
                toRemove.add(diamond);
                root.getChildren().remove(diamond);
                gameEngine.collectDiamond();
            }
        }
        if (!toRemove.isEmpty()) {
            diamonds.removeAll(toRemove);
        }
    }



    public void clearSandUnderObject(GameObject object) {
        Bounds objBounds = object.getBoundsInParent();
        List<Sand> toRemove = new ArrayList<>();
        for (Sand sand : getSandTilesInProximity(objBounds, 10)) {
            if (sand.getBoundsInParent().intersects(objBounds)) {
                toRemove.add(sand);
                root.getChildren().remove(sand);
            }
        }
        sandTiles.removeAll(toRemove);
    }


}