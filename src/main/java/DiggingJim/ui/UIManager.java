package DiggingJim.ui;

import DiggingJim.assets.AssetManager;
import DiggingJim.config.GameConfig;
import DiggingJim.core.GameEngine;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class UIManager {
    private Pane root;
    private GameEngine gameEngine;

    private VBox initialBox;
    private HBox heartBox;
    private HBox diamondCounterBox;
    private Label scoreLabel;
    private ImageView[] hearts;
    private ImageView[] emptyHearts;

    public UIManager(Pane root, GameEngine gameEngine) {
        this.root = root;
        this.gameEngine = gameEngine;

        hearts = new ImageView[GameConfig.INITIAL_HEARTS];
        emptyHearts = new ImageView[GameConfig.INITIAL_HEARTS];
    }

    public void createLevelSelectionUI() {
        Label selectLabel = new Label("Select Level");
        selectLabel.setFont(new Font("Vampire Wars", 100));

        Button easyBtn = createButton("Easy");
        Button normalBtn = createButton("Normal");
        Button hardBtn = createButton("Hard");
        Button extremeBtn = createButton("Extreme");

        initialBox = new VBox(20, selectLabel, easyBtn, normalBtn, hardBtn, extremeBtn);
        initialBox.setLayoutX(GameConfig.SCENE_WIDTH / 4 - 300);
        initialBox.setLayoutY(GameConfig.SCENE_HEIGHT / 6 - 300);
        initialBox.setStyle("-fx-background-image: url('/images/initialBoxBackground.png');" +
                "-fx-background-size: cover;");
        initialBox.setPadding(new Insets(20, 40, 40, 40));

        // Add button event handlers
        easyBtn.setOnAction(e -> {
            AssetManager.getInstance().playSound("buttonClick");
            gameEngine.startGame(GameConfig.EASY_SCORE_REQUIREMENT, 1);
            root.getChildren().remove(initialBox);
        });

        normalBtn.setOnAction(e -> {
            AssetManager.getInstance().playSound("buttonClick");
            gameEngine.startGame(GameConfig.NORMAL_SCORE_REQUIREMENT, 2);
            root.getChildren().remove(initialBox);
        });

        hardBtn.setOnAction(e -> {
            AssetManager.getInstance().playSound("buttonClick");
            gameEngine.startGame(GameConfig.HARD_SCORE_REQUIREMENT, 3);
            root.getChildren().remove(initialBox);
        });

        extremeBtn.setOnAction(e -> {
            AssetManager.getInstance().playSound("buttonClick");
            gameEngine.startGame(GameConfig.EXTREME_SCORE_REQUIREMENT, 4);
            root.getChildren().remove(initialBox);
        });

        setupButtonStyles(easyBtn);
        setupButtonStyles(normalBtn);
        setupButtonStyles(hardBtn);
        setupButtonStyles(extremeBtn);

        // Delay adding the selection UI to give the intro animation time to play
        gameEngine.scheduleTask(() -> root.getChildren().add(initialBox), 4.5);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setFont(new Font("Midnight Moon", 40));
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void setupButtonStyles(Button button) {
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color:Black");
            button.setTextFill(Color.WHITE);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color:white");
            button.setTextFill(Color.BLACK);
        });
    }

    public void createHearts() {
        heartBox = new HBox(10);

        for (int i = 0; i < GameConfig.INITIAL_HEARTS; i++) {
            hearts[i] = new ImageView(AssetManager.getInstance().getImage("heart"));
            hearts[i].setFitWidth(GameConfig.HEART_SIZE);
            hearts[i].setFitHeight(GameConfig.HEART_SIZE);
            heartBox.getChildren().add(hearts[i]);

            emptyHearts[i] = new ImageView(AssetManager.getInstance().getImage("emptyHeart"));
            emptyHearts[i].setFitWidth(GameConfig.HEART_SIZE);
            emptyHearts[i].setFitHeight(GameConfig.HEART_SIZE);
        }

        heartBox.setLayoutX(3560);
        heartBox.setLayoutY(2180);
        root.getChildren().add(heartBox);
    }

    public void createDiamondCounter() {
        diamondCounterBox = new HBox();
        ImageView diamondIcon = new ImageView(AssetManager.getInstance().getImage("diamond"));
        diamondIcon.setFitWidth(70);
        diamondIcon.setFitHeight(70);

        scoreLabel = new Label();
        scoreLabel.setTextFill(Color.GOLD);
        scoreLabel.setFont(Font.font(30));

        diamondCounterBox.getChildren().addAll(diamondIcon, scoreLabel);
        diamondCounterBox.setLayoutX(3380);
        diamondCounterBox.setLayoutY(2180);
        root.getChildren().add(diamondCounterBox);
    }

    public void updateDiamondCounter(int collected, int required) {
        scoreLabel.setText("X " + collected + "/" + required);
    }

    public void reduceHeart(int heartIndex) {
        heartBox.getChildren().set(heartIndex, emptyHearts[heartIndex]);
    }

    public void showGameOverScreen() {
        ImageView gameOverImage = new ImageView(AssetManager.getInstance().getImage("gameOver"));
        gameOverImage.setFitWidth(1600);
        gameOverImage.setFitHeight(1000);
        gameOverImage.setLayoutX(GameConfig.SCENE_WIDTH / 4 - 800);
        gameOverImage.setLayoutY(GameConfig.SCENE_HEIGHT / 6 - 500);
        gameOverImage.setOpacity(0);

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(gameOverImage);
        fadeTransition.setDuration(Duration.millis(2000));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        root.getChildren().add(gameOverImage);
        AssetManager.getInstance().playSound("gameOver");
    }

    public void showVictoryScreen() {
        ImageView victoryImage = new ImageView(AssetManager.getInstance().getImage("victory"));
        victoryImage.setFitWidth(1000);
        victoryImage.setFitHeight(600);
        victoryImage.setX(GameConfig.SCENE_WIDTH * 3 / 4 - 500);
        victoryImage.setY(GameConfig.SCENE_HEIGHT * 5 / 6 - 300);
        victoryImage.setOpacity(0);

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(victoryImage);
        fadeTransition.setDuration(Duration.millis(2000));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        root.getChildren().add(victoryImage);
        AssetManager.getInstance().playSound("victory");
    }

    public HBox getHeartBox() {
        return heartBox;
    }

    public HBox getDiamondCounterBox() {
        return diamondCounterBox;
    }
}