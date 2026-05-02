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
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class CollisionHandler {
    private Pane root;
    private GameEngine gameEngine;
    private GameCharacter character;
    private List<Rock> rocks;
    private List<Diamond> diamonds;
    private List<Monster> monsters;
    private List<Sand> sandTiles;
    private HBox bricksFirstMiddle;
    private HBox bricksSecondMiddle;

    private AnimationTimer characterSandTimer;
    private AnimationTimer characterRockTimer;
    private AnimationTimer characterMonsterTimer;
    private AnimationTimer characterDiamondTimer;
    private AnimationTimer rockSandTimer;
    private List<AnimationTimer> rockTimers = new ArrayList<>();

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

        setupCollisionHandlers();
    }

    private void setupCollisionHandlers() {
        setupCharacterSandCollision();
        setupCharacterRockCollision();
        setupCharacterMonsterCollision();
        setupCharacterDiamondCollision();
        setupRockSandCollision();
    }

    private void setupCharacterSandCollision() {
        characterSandTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds characterBounds = character.getBoundsInParent();
                List<Sand> toRemove = new ArrayList<>();

                for (Sand sand : new ArrayList<>(sandTiles)) {
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

                sandTiles.removeAll(toRemove);
            }
        };
        characterSandTimer.start();
    }

    private void checkRockSupport(Rock rock) {
        // Check if there's any sand directly below the rock
        boolean hasSupport = false;
        Bounds rockBounds = rock.getBoundsInParent();

        // Define an area below the rock to check for support
        double supportCheckX = rockBounds.getMinX() + 5;
        double supportCheckY = rockBounds.getMaxY() + 1; // Just below the rock
        double supportCheckWidth = rockBounds.getWidth() - 10;
        double supportCheckHeight = 5; // Small height to check just below

        // Check for sand support
        for (Sand sand : sandTiles) {
            Bounds sandBounds = sand.getBoundsInParent();
            if (sandBounds.intersects(supportCheckX, supportCheckY, supportCheckWidth, supportCheckHeight)) {
                hasSupport = true;
                break;
            }
        }

        // Check for brick support
        if (!hasSupport) {
            // Check if rock is sitting on first middle brick platform
            if (Math.abs(rockBounds.getMaxY() - bricksFirstMiddle.getLayoutY()) < 5) {
                hasSupport = true;
            }
            // Check if rock is sitting on second middle brick platform
            else if (Math.abs(rockBounds.getMaxY() - bricksSecondMiddle.getLayoutY()) < 5) {
                hasSupport = true;
            }
            // Check if rock is sitting on bottom frame
            else if (Math.abs(rockBounds.getMaxY() - (GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE)) < 5) {
                hasSupport = true;
            }
        }

        // If no support is found, start the rock falling
        if (!hasSupport) {
            rock.startFalling();
        }
    }

    private void setupCharacterRockCollision() {
        characterRockTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Rock rock : rocks) {
                    Bounds rockBounds = rock.getBoundsInLocal();
                    Bounds characterBounds = character.getBoundsInLocal();

                    if (characterBounds.intersects(rockBounds.getMinX() - 30, rockBounds.getMinY() - 30,
                            rockBounds.getWidth() + 60, rockBounds.getHeight() + 60)) {
                        // Right collision
                        if (characterBounds.intersects(rockBounds.getMinX() - 5, rockBounds.getMinY() + 43,
                                rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2)) {
                            character.setRightSpeed(0);
                        } else {
                            character.setRightSpeed(GameConfig.CHARACTER_SPEED);
                        }

                        // Left collision
                        if (characterBounds.intersects(rockBounds.getMinX() + 89, rockBounds.getMinY() + 43,
                                rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2)) {
                            character.setLeftSpeed(0);
                        } else {
                            character.setLeftSpeed(GameConfig.CHARACTER_SPEED);
                        }

                        // Up collision
                        if (characterBounds.intersects(rockBounds.getMinX() + 43, rockBounds.getMinY() + 89,
                                rockBounds.getWidth() - 43 * 2, rockBounds.getHeight() - 84)) {
                            character.setUpSpeed(0);
                        } else {
                            character.setUpSpeed(GameConfig.CHARACTER_SPEED);
                        }

                        // Down collision
                        if (characterBounds.intersects(rockBounds.getMinX() + 43, rockBounds.getMinY() - 5,
                                rockBounds.getWidth() - 43 * 2, rockBounds.getHeight() - 84)) {
                            character.setDownSpeed(0);
                        } else {
                            character.setDownSpeed(GameConfig.CHARACTER_SPEED);
                        }
                    }

                    // Rock pushing
                    if (characterBounds.intersects(rockBounds.getMinX() - 5, rockBounds.getMinY() + 43,
                            rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2) &&
                            character.isMovingRight()) {
                        if (!isSandInRockPath(rockBounds.getMinX() + 1, rockBounds.getMinY() + 20,
                                rockBounds.getWidth(), rockBounds.getHeight() - 40)) {
                            rock.setX(rock.getX() + 1);
                            // Check support after pushing
                            checkRockSupport(rock);
                        }
                    }

                    if (characterBounds.intersects(rockBounds.getMinX() + 89, rockBounds.getMinY() + 43,
                            rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2) &&
                            character.isMovingLeft()) {
                        if (!isSandInRockPath(rockBounds.getMinX() - 1, rockBounds.getMinY() + 20,
                                rockBounds.getWidth() + 1, rockBounds.getHeight() - 40)) {
                            rock.setX(rock.getX() - 1);
                            // Check support after pushing
                            checkRockSupport(rock);
                        }
                    }
                }
            }
        };
        characterRockTimer.start();
    }

    private boolean isSandInRockPath(double x, double y, double width, double height) {
        for (Sand sand : sandTiles) {
            if (sand.getBoundsInParent().intersects(x, y, width, height)) {
                return true;
            }
        }
        return false;
    }

    private void setupCharacterMonsterCollision() {
        characterMonsterTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds characterBounds = character.getBoundsInParent();
                for (Monster monster : monsters) {
                    if (monster.getBoundsInParent().intersects(characterBounds)) {
                        gameEngine.characterDeath();
                    }
                }
            }
        };
        characterMonsterTimer.start();
    }

    private void setupCharacterDiamondCollision() {
        characterDiamondTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds characterBounds = character.getBoundsInParent();
                List<Diamond> diamondsToRemove = new ArrayList<>();

                for (Diamond diamond : new ArrayList<>(diamonds)) {
                    Bounds diamondBounds = diamond.getBoundsInParent();
                    if (diamondBounds.intersects(
                            characterBounds.getMinX() + 40, characterBounds.getMinY() + 40, 0,
                            characterBounds.getWidth() - 75, characterBounds.getHeight() - 75, 0)) {
                        diamondsToRemove.add(diamond);
                        root.getChildren().remove(diamond);
                        gameEngine.collectDiamond();
                    }
                }

                diamonds.removeAll(diamondsToRemove);
            }
        };
        characterDiamondTimer.start();
    }

    private void setupRockSandCollision() {
        rockSandTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Rock rock : rocks) {
                    if (rock.isFalling()) {
                        rock.applyGravity();

                        Bounds rockBounds = rock.getBoundsInLocal();
                        Bounds characterBounds = character.getBoundsInLocal();

                        // Check if rock hits character and causes death
                        if (characterBounds.intersects(rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0,
                                rockBounds.getWidth() - 10, rockBounds.getHeight() - 45, 0) &&
                                rock.getGravity() >= 6) {
                            gameEngine.characterDeath();
                        }

                        // Check for collision with sand or character (to stop falling)
                        boolean shouldStop = false;

                        // Check for sand below
                        for (Sand sand : sandTiles) {
                            if (sand.getBoundsInLocal().intersects(
                                    rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0,
                                    rockBounds.getWidth() - 10, rockBounds.getHeight() - 50, 0)) {
                                shouldStop = true;
                                break;
                            }
                        }

                        // Check for character below
                        if (characterBounds.intersects(rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0,
                                rockBounds.getWidth() - 10, rockBounds.getHeight() - 50, 0)) {
                            shouldStop = true;
                        }

                        // Check for brick platforms below
                        double rockBottom = rock.getY() + rock.getFitHeight();
                        if (Math.abs(rockBottom - bricksFirstMiddle.getLayoutY()) < 5 ||
                                Math.abs(rockBottom - bricksSecondMiddle.getLayoutY()) < 5 ||
                                Math.abs(rockBottom - (GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE)) < 5) {
                            shouldStop = true;
                        }

                        // Check for other rocks below
                        for (Rock otherRock : rocks) {
                            if (rock != otherRock) {
                                Bounds otherRockBounds = otherRock.getBoundsInParent();
                                if (Math.abs(rockBottom - otherRockBounds.getMinY()) < 5 &&
                                        rockBounds.getMinX() < otherRockBounds.getMaxX() &&
                                        rockBounds.getMaxX() > otherRockBounds.getMinX()) {
                                    shouldStop = true;
                                    break;
                                }
                            }
                        }

                        // Stop falling if something is underneath
                        if (shouldStop) {
                            rock.stopFalling();
                        }
                    }
                }
            }
        };
        rockSandTimer.start();
    }

    public double handleBricksCollisionX(double characterX) {
        if (character.getY() > bricksFirstMiddle.getLayoutY() - 80 &&
                character.getY() < bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
            characterX = Math.max(GameConfig.SCENE_WIDTH - 280,
                    Math.min(characterX, GameConfig.SCENE_WIDTH - GameConfig.CHARACTER_SIZE - GameConfig.BRICKS_FRAME_SIZE));
            if (character.getX() == GameConfig.SCENE_WIDTH - 280) {
                character.setMovingLeft(false);
            }
        }

        if (character.getY() > bricksSecondMiddle.getLayoutY() - 80 &&
                character.getY() < bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
            characterX = Math.max(GameConfig.BRICKS_FRAME_SIZE, Math.min(characterX, 280 - GameConfig.CHARACTER_SIZE));
            if (character.getX() == bricksSecondMiddle.getLayoutX()) {
                character.setMovingRight(false);
            }
        }

        return characterX;
    }

    public double handleBricksCollisionY(double characterY) {
        if (character.getY() < bricksFirstMiddle.getLayoutY() && character.getX() < GameConfig.SCENE_WIDTH - 280) {
            characterY = Math.max(GameConfig.BRICKS_FRAME_SIZE,
                    Math.min(characterY, bricksFirstMiddle.getLayoutY() - GameConfig.CHARACTER_SIZE));

            if (character.getY() + GameConfig.CHARACTER_SIZE == bricksFirstMiddle.getLayoutY()) {
                character.setMovingDown(false);
            }
        }
        else if (character.getY() >= bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE &&
                character.getY() < bricksSecondMiddle.getLayoutY() &&
                character.getX() > bricksSecondMiddle.getLayoutX() - GameConfig.CHARACTER_SIZE &&
                character.getX() < GameConfig.SCENE_WIDTH - 280) {
            characterY = Math.max(bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE,
                    Math.min(characterY, bricksSecondMiddle.getLayoutY() - GameConfig.CHARACTER_SIZE));

            if (character.getY() + GameConfig.CHARACTER_SIZE == bricksSecondMiddle.getLayoutY()) {
                character.setMovingDown(false);
            }

            if (character.getY() == bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
                character.setMovingUp(false);
            }
        }
        else if (character.getY() >= bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE &&
                character.getY() < GameConfig.SCENE_HEIGHT &&
                character.getX() > bricksSecondMiddle.getLayoutX() - GameConfig.CHARACTER_SIZE) {
            characterY = Math.max(bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE,
                    Math.min(characterY, GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.CHARACTER_SIZE));

            if (character.getY() == bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
                character.setMovingUp(false);
            }
        }

        if (character.getY() > bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE &&
                character.getY() < bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE &&
                character.getX() > GameConfig.SCENE_WIDTH - 280) {
            characterY = Math.min(characterY, bricksSecondMiddle.getLayoutY() - GameConfig.CHARACTER_SIZE);

            if (character.getY() + GameConfig.CHARACTER_SIZE == bricksSecondMiddle.getLayoutY()) {
                character.setMovingDown(false);
            }
        }

        if (character.getY() < GameConfig.SCENE_HEIGHT / 2 &&
                character.getY() > bricksFirstMiddle.getLayoutY() &&
                character.getX() < 280) {
            characterY = Math.max(characterY, bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE);

            if (character.getY() == bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE) {
                character.setMovingUp(false);
            }
        }

        return characterY;
    }

    public void clearSandUnderObject(GameObject object) {
        Bounds objBounds = object.getBoundsInParent();
        List<Sand> toRemove = new ArrayList<>();

        for (Sand sand : new ArrayList<>(sandTiles)) {
            if (sand.getBoundsInParent().intersects(
                    objBounds.getMinX() + 3, objBounds.getMinY() + 3, 0,
                    objBounds.getWidth() - 6, objBounds.getHeight() - 6, 0)) {
                toRemove.add(sand);
                root.getChildren().remove(sand);
            }
        }

        sandTiles.removeAll(toRemove);
    }

    public void stopAll() {
        if (characterSandTimer != null) characterSandTimer.stop();
        if (characterRockTimer != null) characterRockTimer.stop();
        if (characterMonsterTimer != null) characterMonsterTimer.stop();
        if (characterDiamondTimer != null) characterDiamondTimer.stop();
        if (rockSandTimer != null) rockSandTimer.stop();
    }
}