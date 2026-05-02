package DiggingJim.ui;

import DiggingJim.assets.AssetManager;
import DiggingJim.config.GameConfig;
import DiggingJim.core.GameEngine;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private final Pane root; // This now refers to the uiLayer
    private final GameEngine gameEngine;

    private VBox initialBox;
    private final HBox heartBox;
    private final HBox diamondCounterBox;
    private final Label scoreLabel;
    private final ImageView[] hearts;
    private final ImageView[] emptyHearts;

    public UIManager(Pane uiLayer, GameEngine gameEngine) {
        this.root = uiLayer;
        this.gameEngine = gameEngine;

        this.heartBox = new HBox(10);
        this.diamondCounterBox = new HBox();
        this.scoreLabel = new Label();
        this.hearts = new ImageView[GameConfig.INITIAL_HEARTS];
        this.emptyHearts = new ImageView[GameConfig.INITIAL_HEARTS];
    }

    public void createLevelSelectionUI() {
        Label selectLabel = new Label("Select Level");
        selectLabel.setFont(new Font("Vampire Wars", 100));
        selectLabel.setTextFill(Color.WHITE);

        Button easyBtn = createButton("Easy");
        Button normalBtn = createButton("Normal");
        Button hardBtn = createButton("Hard");
        Button extremeBtn = createButton("Extreme");

        initialBox = new VBox(20, selectLabel, easyBtn, normalBtn, hardBtn, extremeBtn);
        initialBox.setAlignment(Pos.CENTER);
        initialBox.setPrefSize(800, 600);
        initialBox.setLayoutX(960 - 400); // Center horizontally
        initialBox.setLayoutY(540 - 300); // Center vertically
        
        initialBox.setStyle("-fx-background-image: url('/images/initialBoxBackground.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;");
        initialBox.setPadding(new Insets(40));

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
        button.setMaxWidth(400);
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
        for (int i = 0; i < GameConfig.INITIAL_HEARTS; i++) {
            hearts[i] = new ImageView(AssetManager.getInstance().getImage("heart"));
            hearts[i].setFitWidth(GameConfig.HEART_SIZE);
            hearts[i].setFitHeight(GameConfig.HEART_SIZE);
            heartBox.getChildren().add(hearts[i]);

            emptyHearts[i] = new ImageView(AssetManager.getInstance().getImage("emptyHeart"));
            emptyHearts[i].setFitWidth(GameConfig.HEART_SIZE);
            emptyHearts[i].setFitHeight(GameConfig.HEART_SIZE);
        }

        // Fixed position in top-right corner of the viewport
        heartBox.setLayoutX(1670);
        heartBox.setLayoutY(20);
        root.getChildren().add(heartBox);
    }

    public void createDiamondCounter() {
        ImageView diamondIcon = new ImageView(AssetManager.getInstance().getImage("diamond"));
        diamondIcon.setFitWidth(70);
        diamondIcon.setFitHeight(70);

        scoreLabel.setTextFill(Color.GOLD);
        scoreLabel.setFont(Font.font(30));

        diamondCounterBox.getChildren().addAll(diamondIcon, scoreLabel);
        
        // Fixed position in top-right corner, to the left of hearts
        diamondCounterBox.setLayoutX(1480);
        diamondCounterBox.setLayoutY(20);
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
        // Center on 1920x1080 viewport
        gameOverImage.setLayoutX(160);
        gameOverImage.setLayoutY(40);
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
        // Center on 1920x1080 viewport
        victoryImage.setLayoutX(460);
        victoryImage.setLayoutY(240);
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