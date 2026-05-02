package DiggingJim;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameEngine;
import DiggingJim.assets.AssetManager;
import DiggingJim.input.InputHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DiggingJim extends Application {

    private GameEngine gameEngine;
    private InputHandler inputHandler;

    @Override
    public void start(Stage primaryStage) {
        // Create the world pane (where the level is built)
        Pane gamePane = new Pane();
        gamePane.setPrefSize(GameConfig.SCENE_WIDTH, GameConfig.SCENE_HEIGHT);
        gamePane.setStyle("-fx-background-color: black;");

        // Create the UI layer (fixed on top of the viewport)
        Pane uiLayer = new Pane();
        uiLayer.setPrefSize(1920, 1080);
        uiLayer.setPickOnBounds(false); // Allow clicks to pass through to the game if not on UI

        // Create the viewport (the 1920x1080 visible area)
        Pane viewport = new Pane();
        viewport.setPrefSize(1920, 1080);
        viewport.setMinSize(1920, 1080);
        viewport.setMaxSize(1920, 1080);
        viewport.setClip(new Rectangle(1920, 1080));
        viewport.getChildren().addAll(gamePane, uiLayer);

        // Wrap viewport in a Group to allow scaling without affecting parent layout
        Group scaledGroup = new Group(viewport);

        // Root container that centers the scaled game
        StackPane rootContainer = new StackPane(scaledGroup);
        rootContainer.setStyle("-fx-background-color: black;");

        // Initialize game engine with both world and UI layers
        gameEngine = new GameEngine(gamePane, uiLayer);

        // Initialize input handler
        inputHandler = new InputHandler(gameEngine);

        // Set up the scene
        Scene scene = new Scene(rootContainer, 1920, 1080);
        scene.setOnKeyPressed(event -> inputHandler.handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> inputHandler.handleKeyRelease(event.getCode()));

        // Responsive Scaling Logic
        DoubleProperty scale = new SimpleDoubleProperty(1);
        scaledGroup.scaleXProperty().bind(scale);
        scaledGroup.scaleYProperty().bind(scale);

        Runnable updateScale = () -> {
            double scaleX = scene.getWidth() / 1920;
            double scaleY = scene.getHeight() / 1080;
            scale.set(Math.min(scaleX, scaleY));
        };

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateScale.run());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateScale.run());

        // Configure stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Digging Jim");
        primaryStage.getIcons().add(AssetManager.getInstance().getImage("icon"));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);

        // Handle close request
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Start the game
        gameEngine.initializeGame();

        primaryStage.show();
        
        // Initial scale update after show
        updateScale.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}