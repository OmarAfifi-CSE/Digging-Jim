package DiggingJim.world;

import java.util.ArrayList;
import java.util.List;

import DiggingJim.config.GameConfig;
import DiggingJim.entities.characters.Monster;
import DiggingJim.entities.environment.Diamond;
import DiggingJim.entities.environment.Door;
import DiggingJim.entities.environment.Rock;
import DiggingJim.entities.environment.Sand;
import DiggingJim.assets.AssetManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LevelBuilder {
    private final Pane root;
    private final List<Sand> sandTiles = new ArrayList<>();
    private final List<Rock> rocks = new ArrayList<>();
    private final List<Diamond> diamonds = new ArrayList<>();
    private final List<Monster> monsters = new ArrayList<>();

    private Door exitDoor;

    private HBox bricksFirstMiddle;
    private HBox bricksSecondMiddle;

    public LevelBuilder(Pane root) {
        this.root = root;
    }

    public void buildLevel() {
        createSand();
        createDoors();
        createBricks();
        createRocks();
        createDiamonds();
        createMonsters();
    }

    private void createSand() {
        for (int x = 0; x < GameConfig.SCENE_WIDTH; x += GameConfig.SAND_SIZE) {
            for (int y = 0; y < GameConfig.SCENE_HEIGHT; y += GameConfig.SAND_SIZE) {
                Sand sand = new Sand(x, y);
                sandTiles.add(sand);
                root.getChildren().add(sand);
            }
        }
    }

    private void createDoors() {
        Door entranceDoor = new Door(80, 60, true);
        exitDoor = new Door(
                GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE - GameConfig.DOOR_SIZE,
                GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.DOOR_SIZE,
                false
        );

        root.getChildren().addAll(entranceDoor, exitDoor);
    }

    private void createBricks() {
        // Create brick layouts
        bricksFirstMiddle = createHorizontalBricks(29, GameConfig.BRICKS_SIZE);
        bricksSecondMiddle = createHorizontalBricks(29, GameConfig.BRICKS_SIZE);
        HBox bricksUp = createHorizontalBricks(50, GameConfig.BRICKS_SIZE - 40);
        HBox bricksDown = createHorizontalBricks(50, GameConfig.BRICKS_SIZE - 40);
        VBox bricksLeft = createVerticalBricks(40, GameConfig.BRICKS_SIZE - 40);
        VBox bricksRight = createVerticalBricks(40, GameConfig.BRICKS_SIZE - 40);

        // Position brick layouts
        bricksFirstMiddle.setLayoutX(GameConfig.BRICKS_FRAME_SIZE);
        bricksFirstMiddle.setLayoutY(GameConfig.SCENE_HEIGHT / 3 - 46);

        bricksSecondMiddle.setLayoutX(280);
        bricksSecondMiddle.setLayoutY(GameConfig.SCENE_HEIGHT * 2 / 3 - 46);

        bricksUp.setLayoutX(0);
        bricksUp.setLayoutY(0);

        bricksDown.setLayoutX(0);
        bricksDown.setLayoutY(GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE);

        bricksLeft.setLayoutX(0);
        bricksLeft.setLayoutY(GameConfig.BRICKS_FRAME_SIZE);

        bricksRight.setLayoutX(GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE);
        bricksRight.setLayoutY(GameConfig.BRICKS_FRAME_SIZE);

        root.getChildren().addAll(bricksUp, bricksDown, bricksRight, bricksLeft, bricksFirstMiddle, bricksSecondMiddle);
    }

    private HBox createHorizontalBricks(int count, double size) {
        HBox box = new HBox();
        for (int i = 0; i < count; i++) {
            ImageView brick = new ImageView(AssetManager.getInstance().getImage("bricks"));
            brick.setFitWidth(size);
            brick.setFitHeight(size);
            box.getChildren().add(brick);
        }
        return box;
    }

    private VBox createVerticalBricks(int count, double size) {
        VBox box = new VBox();
        for (int i = 0; i < count; i++) {
            ImageView brick = new ImageView(AssetManager.getInstance().getImage("bricks"));
            brick.setFitWidth(size);
            brick.setFitHeight(size);
            box.getChildren().add(brick);
        }
        return box;
    }

    private void createRocks() {
        for (int i = 0; i < GameConfig.NUM_OF_ROCKS; i++) {
            Rock rock = new Rock(0, 0);
            rocks.add(rock);

            // Different rock placement based on the index
            if (i < GameConfig.NUM_OF_ROCKS / 6 && i > 0) {
                placeRockInSection(rock, i,
                        GameConfig.BRICKS_FRAME_SIZE, GameConfig.BRICKS_FRAME_SIZE + GameConfig.ROCK_HEIGHT / 2 + GameConfig.SAND_SIZE,
                        GameConfig.SCENE_WIDTH, bricksFirstMiddle.getLayoutY() / 2 - GameConfig.ROCK_HEIGHT);
            } else if (i < GameConfig.NUM_OF_ROCKS * 2 / 6) {
                placeRockInSection(rock, i - GameConfig.NUM_OF_ROCKS / 6,
                        GameConfig.BRICKS_FRAME_SIZE, bricksFirstMiddle.getLayoutY() / 2 - GameConfig.ROCK_HEIGHT,
                        GameConfig.SCENE_WIDTH, bricksFirstMiddle.getLayoutY() - GameConfig.ROCK_HEIGHT);
            } else if (i < GameConfig.NUM_OF_ROCKS * 3 / 6) {
                placeRockInSection(rock, i - GameConfig.NUM_OF_ROCKS * 2 / 6,
                        GameConfig.BRICKS_FRAME_SIZE, bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE + GameConfig.SAND_SIZE,
                        GameConfig.SCENE_WIDTH, bricksSecondMiddle.getLayoutY() * 3 / 4);
            } else if (i < GameConfig.NUM_OF_ROCKS * 4 / 6) {
                placeRockInSection(rock, i - GameConfig.NUM_OF_ROCKS * 3 / 6,
                        GameConfig.BRICKS_FRAME_SIZE, bricksSecondMiddle.getLayoutY() * 3 / 4,
                        GameConfig.SCENE_WIDTH, bricksSecondMiddle.getLayoutY() - GameConfig.ROCK_HEIGHT);
            } else if (i < GameConfig.NUM_OF_ROCKS * 5 / 6) {
                placeRockInSection(rock, i - GameConfig.NUM_OF_ROCKS * 4 / 6,
                        GameConfig.BRICKS_FRAME_SIZE, bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE + GameConfig.SAND_SIZE,
                        GameConfig.SCENE_WIDTH, GameConfig.SCENE_HEIGHT * 5 / 6);
            } else {
                placeRockInSection(rock, i - GameConfig.NUM_OF_ROCKS * 5 / 6,
                        GameConfig.BRICKS_FRAME_SIZE, GameConfig.SCENE_HEIGHT * 5 / 6,
                        GameConfig.SCENE_WIDTH, GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.ROCK_HEIGHT - GameConfig.SAND_SIZE);
            }

            // Clear sand under the rock using a new CollisionHandler method
            clearSandUnderRock(rock);
            root.getChildren().add(rock);
        }
    }

    private void placeRockInSection(Rock rock, int offset, double minX, double minY, double maxX, double maxY) {
        double x = (minX + GameConfig.ROCK_WIDTH / 2) + offset * (GameConfig.SAND_SIZE + 135);
        double finalX = x - x % GameConfig.SAND_SIZE;
        double y = minY + (Math.random() * (maxY - minY));
        double finalY = y - y % GameConfig.SAND_SIZE;
        rock.setX(finalX);
        rock.setY(finalY);
    }

    private void clearSandUnderRock(Rock rock) {
        List<Sand> toRemove = new ArrayList<>();

        for (Sand sand : new ArrayList<>(sandTiles)) {
            if (rock.getBoundsInParent().intersects(sand.getBoundsInParent())) {
                toRemove.add(sand);
                root.getChildren().remove(sand);
            }
        }

        sandTiles.removeAll(toRemove);
    }

    private void createDiamonds() {
        for (int i = 0; i < GameConfig.NUM_OF_DIAMONDS; i++) {
            Diamond diamond = new Diamond(0, 0);

            // Different diamond placement based on the index
            if (i < GameConfig.NUM_OF_DIAMONDS / 3 && i > 0) {
                placeDiamondInSection(diamond, i,
                        GameConfig.BRICKS_FRAME_SIZE, GameConfig.BRICKS_FRAME_SIZE,
                        GameConfig.SCENE_WIDTH, bricksFirstMiddle.getLayoutY() - GameConfig.DIAMOND_HEIGHT);
            } else if (i < GameConfig.NUM_OF_DIAMONDS * 2 / 3) {
                placeDiamondInSection(diamond, i - GameConfig.NUM_OF_DIAMONDS / 3,
                        GameConfig.BRICKS_FRAME_SIZE, bricksFirstMiddle.getLayoutY() + GameConfig.BRICKS_SIZE,
                        GameConfig.SCENE_WIDTH, bricksSecondMiddle.getLayoutY() - GameConfig.DIAMOND_HEIGHT);
            } else {
                placeDiamondInSection(diamond, i - GameConfig.NUM_OF_DIAMONDS * 2 / 3,
                        GameConfig.BRICKS_FRAME_SIZE, bricksSecondMiddle.getLayoutY() + GameConfig.BRICKS_SIZE,
                        GameConfig.SCENE_WIDTH, GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.DIAMOND_HEIGHT);
            }

            // Clear sand under the diamond
            clearSandUnderDiamond(diamond);
            diamonds.add(diamond);
            root.getChildren().add(diamond);
        }

        // Add one extra diamond near the exit door
        Diamond extraDiamond = new Diamond(
                GameConfig.SCENE_WIDTH - GameConfig.BRICKS_FRAME_SIZE - GameConfig.DIAMOND_WIDTH - GameConfig.SAND_SIZE,
                GameConfig.SCENE_HEIGHT - GameConfig.BRICKS_FRAME_SIZE - GameConfig.SAND_SIZE * 10
        );
        clearSandUnderDiamond(extraDiamond);
        diamonds.add(extraDiamond);
        root.getChildren().add(extraDiamond);
    }

    private void placeDiamondInSection(Diamond diamond, int offset, double minX, double minY, double maxX, double maxY) {
        double x = (minX + GameConfig.DIAMOND_WIDTH / 2) + offset * (GameConfig.SAND_SIZE + 135);
        double finalX = x - x % GameConfig.SAND_SIZE;
        double y = minY + (Math.random() * (maxY - minY));
        double finalY = y - y % GameConfig.SAND_SIZE;
        diamond.setX(finalX);
        diamond.setY(finalY);
    }

    private void clearSandUnderDiamond(Diamond diamond) {
        List<Sand> toRemove = new ArrayList<>();

        for (Sand sand : new ArrayList<>(sandTiles)) {
            if (diamond.getBoundsInParent().intersects(sand.getBoundsInParent())) {
                toRemove.add(sand);
                root.getChildren().remove(sand);
            }
        }

        sandTiles.removeAll(toRemove);
    }

    private void createMonsters() {
        for (int i = 0; i < GameConfig.NUM_MONSTERS; i++) {
            Monster monster = new Monster();
            monsters.add(monster);
            root.getChildren().add(monster);
        }
    }

    public List<Sand> getSandTiles() {
        return sandTiles;
    }

    public List<Rock> getRocks() {
        return rocks;
    }

    public List<Diamond> getDiamonds() {
        return diamonds;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }



    public Door getExitDoor() {
        return exitDoor;
    }

    public HBox getBricksFirstMiddle() {
        return bricksFirstMiddle;
    }

    public HBox getBricksSecondMiddle() {
        return bricksSecondMiddle;
    }
}