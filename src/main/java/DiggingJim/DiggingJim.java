package DiggingJim;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameEngine;
import DiggingJim.assets.AssetManager;
import DiggingJim.input.InputHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DiggingJim extends Application {

    private GameEngine gameEngine;
    private InputHandler inputHandler;

    @Override
    public void start(Stage primaryStage) {
        // Create the root pane
        Pane gamePane = new Pane();

        gamePane.setStyle("-fx-background-color: black;");


        // Initialize game engine
        gameEngine = new GameEngine(gamePane);

        // Initialize input handler
        inputHandler = new InputHandler(gameEngine);

        // Set up the scene
        Scene scene = new Scene(gamePane);
        scene.setOnKeyPressed(event -> inputHandler.handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> inputHandler.handleKeyRelease(event.getCode()));

        // Configure stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Digging Jim");
        primaryStage.getIcons().add(AssetManager.getInstance().getImage("icon"));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(false);

        // Handle close request
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Start the game
        gameEngine.initializeGame();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}