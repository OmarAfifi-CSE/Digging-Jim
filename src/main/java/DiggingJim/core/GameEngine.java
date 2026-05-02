package DiggingJim.core;

import java.util.ArrayList;
import java.util.List;

import DiggingJim.config.GameConfig;
import DiggingJim.entities.characters.GameCharacter;
import DiggingJim.entities.characters.Monster;
import DiggingJim.entities.environment.Diamond;
import DiggingJim.entities.environment.Door;
import DiggingJim.entities.environment.Rock;
import DiggingJim.entities.environment.Sand;
import DiggingJim.assets.AssetManager;
import DiggingJim.entities.characters.MonsterPathGenerator;
import DiggingJim.ui.UIManager;
import DiggingJim.physics.CollisionHandler;
import DiggingJim.world.LevelBuilder;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class GameEngine {
    private Pane root;
    private GameCharacter character;
    private List<Rock> rocks;
    private List<Diamond> diamonds;
    private List<Monster> monsters;
    private List<Sand> sandTiles;
    private Door entranceDoor;
    private Door exitDoor;

    private UIManager uiManager;
    private LevelBuilder levelBuilder;
    private CollisionHandler collisionHandler;
    private MonsterPathGenerator monsterPathGenerator;

    private int scoreRequirement;
    private int collectedDiamonds = 0;
    private int remainingHearts = GameConfig.INITIAL_HEARTS;
    private boolean isGameFinished = false;
    private boolean isGameReady = false;

    private AnimationTimer gameLoop;
    private Timeline cameraFollowTimeline;
    private int heartIndex = 0;
    private long lastTime = 0;

    public GameEngine(Pane root, Pane uiLayer) {
        this.root = root;
        this.uiManager = new UIManager(uiLayer, this);
        this.character = new GameCharacter();
        this.monsters = new ArrayList<>();
        this.rocks = new ArrayList<>();
        this.diamonds = new ArrayList<>();
        this.sandTiles = new ArrayList<>();
    }

    public void initializeGame() {
        // Build the level
        levelBuilder = new LevelBuilder(root);
        levelBuilder.buildLevel();

        // Get generated game objects
        rocks = levelBuilder.getRocks();
        diamonds = levelBuilder.getDiamonds();
        monsters = levelBuilder.getMonsters();
        sandTiles = levelBuilder.getSandTiles();
        entranceDoor = levelBuilder.getEntranceDoor();
        exitDoor = levelBuilder.getExitDoor();

        // Add character to the scene
        root.getChildren().add(character);

        // Create UI elements
        uiManager.createHearts();
        uiManager.createDiamondCounter();
        uiManager.createLevelSelectionUI();

        // Initialize collision handler
        collisionHandler = new CollisionHandler(
                root,
                this,
                character,
                rocks,
                diamonds,
                monsters,
                sandTiles,
                levelBuilder.getBricksFirstMiddle(),
                levelBuilder.getBricksSecondMiddle()
        );

        // Start initial camera movement animation
        startInitialCameraMovement();

        // Play startup sound
        AssetManager.getInstance().playSound("start");

        // Start the game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (isGameReady && !isGameFinished) {
                    updateCharacterPosition(deltaTime);
                    checkGameCompletion();
                }
            }
        };
        gameLoop.start();
    }

    public void startGame(int scoreRequirement, int monsterSpeedLevel) {
        this.scoreRequirement = scoreRequirement;

        // Set up monster paths based on difficulty
        double monsterSpeed;
        switch (monsterSpeedLevel) {
            case 1:
                monsterSpeed = GameConfig.EASY_MONSTER_SPEED;
                break;
            case 2:
                monsterSpeed = GameConfig.NORMAL_MONSTER_SPEED;
                break;
            case 3:
                monsterSpeed = GameConfig.HARD_MONSTER_SPEED;
                break;
            case 4:
                monsterSpeed = GameConfig.EXTREME_MONSTER_SPEED;
                break;
            default:
                monsterSpeed = GameConfig.NORMAL_MONSTER_SPEED;
        }

        monsterPathGenerator = new MonsterPathGenerator(monsters, monsterSpeed);
        monsterPathGenerator.createPathTransitions();
        monsterPathGenerator.startAllPaths();

        // Update the diamond counter
        uiManager.updateDiamondCounter(collectedDiamonds, scoreRequirement);

        isGameReady = true;
    }

    private void startInitialCameraMovement() {
        // Set initial camera position
        root.setLayoutX(-GameConfig.SCENE_WIDTH / 2);
        root.setLayoutY(-GameConfig.SCENE_HEIGHT * 2 / 3);

        // Create timeline for camera movement
        Timeline wait = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            cameraFollowTimeline = new Timeline(new KeyFrame(Duration.millis(GameConfig.FRAME_DURATION), event1 -> {
                // Move camera toward center
                if (root.getLayoutX() < 0) {
                    root.setLayoutX(root.getLayoutX() + GameConfig.CHARACTER_SPEED);
                }
                if (root.getLayoutY() < 0) {
                    root.setLayoutY(root.getLayoutY() + GameConfig.CHARACTER_SPEED);
                }
            }));
            cameraFollowTimeline.setCycleCount(432);
            cameraFollowTimeline.play();
        }));
        wait.play();
    }

    private void updateCharacterPosition(double deltaTime) {
        double characterX = character.getX();
        double characterY = character.getY();

        // Apply brick collision constraints
        characterX = collisionHandler.handleBricksCollisionX(characterX);
        characterY = collisionHandler.handleBricksCollisionY(characterY);

        character.setX(characterX);
        character.setY(characterY);

        // Update character movement within constraints
        character.move(
                GameConfig.BRICKS_FRAME_SIZE,
                GameConfig.BRICKS_FRAME_SIZE,
                GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE,
                GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE,
                deltaTime
        );

        // Handle camera tracking character
        trackCharacterMovement(deltaTime);
    }

    private void trackCharacterMovement(double deltaTime) {
        double speedMultiplier = deltaTime * 60.0;
        // Only track character when moving in a single direction and at full speed
        if (character.isMovingRight() && !character.isMovingLeft() &&
                !character.isMovingUp() && !character.isMovingDown() &&
                character.getRightSpeed() == GameConfig.CHARACTER_SPEED) {

            if (character.getX() > GameConfig.SCENE_WIDTH / 4 &&
                    character.getX() < GameConfig.SCENE_WIDTH * 3 / 4 &&
                    root.getLayoutX() > -GameConfig.SCENE_WIDTH / 2) {

                root.setLayoutX(root.getLayoutX() - GameConfig.CHARACTER_SPEED * speedMultiplier);
            }
        } else if (character.isMovingLeft() && !character.isMovingRight() &&
                !character.isMovingUp() && !character.isMovingDown() &&
                character.getLeftSpeed() == GameConfig.CHARACTER_SPEED) {

            if (character.getX() > GameConfig.SCENE_WIDTH / 4 &&
                    character.getX() < GameConfig.SCENE_WIDTH * 3 / 4 &&
                    root.getLayoutX() < 0) {

                root.setLayoutX(root.getLayoutX() + GameConfig.CHARACTER_SPEED * speedMultiplier);
            }
        } else if (character.isMovingDown() && !character.isMovingUp() &&
                !character.isMovingRight() && !character.isMovingLeft() &&
                character.getDownSpeed() == GameConfig.CHARACTER_SPEED) {

            if (character.getY() > GameConfig.SCENE_HEIGHT / 6 &&
                    character.getY() < GameConfig.SCENE_HEIGHT * 5 / 6 &&
                    root.getLayoutY() > -GameConfig.SCENE_HEIGHT / 1.5) {

                root.setLayoutY(root.getLayoutY() - GameConfig.CHARACTER_SPEED * speedMultiplier);
            }
        } else if (character.isMovingUp() && !character.isMovingDown() &&
                !character.isMovingRight() && !character.isMovingLeft() &&
                character.getUpSpeed() == GameConfig.CHARACTER_SPEED) {

            if (character.getY() > GameConfig.SCENE_HEIGHT / 6 &&
                    character.getY() < GameConfig.SCENE_HEIGHT * 5 / 6 &&
                    root.getLayoutY() < 0) {

                root.setLayoutY(root.getLayoutY() + GameConfig.CHARACTER_SPEED * speedMultiplier);
            }
        }
    }

    public void collectDiamond() {
        collectedDiamonds++;
        uiManager.updateDiamondCounter(collectedDiamonds, scoreRequirement);
        AssetManager.getInstance().playSound("pickDiamond");

        // Stop diamond sound after 0.5 seconds
        scheduleTask(() -> AssetManager.getInstance().stopSound("pickDiamond"), 0.5);

        // Check if exit door should be unlocked
        if (collectedDiamonds >= scoreRequirement) {
            exitDoor.setOpen(true);
        }
    }

    public void characterDeath() {
        if (!isGameFinished) {
            remainingHearts--;
            uiManager.reduceHeart(heartIndex);
            heartIndex++;

            if (remainingHearts > 0) {
                // Reset character position and speed
                character.resetPosition();

                // Return camera to starting position
                int cycleCount = getCyclesToReturnCamera();

                Timeline returnCamera = new Timeline(new KeyFrame(Duration.millis(GameConfig.FRAME_DURATION / 2), e -> {
                    if (root.getLayoutX() < 0) {
                        root.setLayoutX(root.getLayoutX() + GameConfig.CHARACTER_SPEED);
                    }
                    if (root.getLayoutY() < 0) {
                        root.setLayoutY(root.getLayoutY() + GameConfig.CHARACTER_SPEED);
                    }
                }));
                returnCamera.setCycleCount(cycleCount);
                returnCamera.play();
            } else {
                gameOver();
            }
        }
    }

    private int getCyclesToReturnCamera() {
        double maxRootLayout = root.getLayoutX() < root.getLayoutY() ? root.getLayoutX() : root.getLayoutY();
        return (int) Math.round(maxRootLayout / GameConfig.CHARACTER_SPEED) * -1;
    }

    private void gameOver() {
        isGameFinished = true;

        // Reset position
        root.setLayoutX(0);
        root.setLayoutY(0);

        // Remove monsters
        for (Monster monster : monsters) {
            root.getChildren().remove(monster);
        }

        // Reset character
        character.resetPosition();

        // Show game over screen
        uiManager.showGameOverScreen();
    }

    private void checkGameCompletion() {
        if (!isGameFinished && collectedDiamonds >= scoreRequirement) {
            // Check if character reached exit door
            if (character.getX() >= exitDoor.getX() && character.getY() >= exitDoor.getY()) {
                victory();
            }
        }
    }

    private void victory() {
        isGameFinished = true;

        // Remove monsters
        for (Monster monster : monsters) {
            root.getChildren().remove(monster);
        }

        // Show victory screen
        uiManager.showVictoryScreen();
    }

    public void scheduleTask(Runnable task, double seconds) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), event -> task.run()));
        timeline.play();
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public boolean isGameReady() {
        return isGameReady;
    }
}