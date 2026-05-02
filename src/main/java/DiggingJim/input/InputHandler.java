package DiggingJim.input;

import DiggingJim.core.GameEngine;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private final GameEngine gameEngine;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void handleKeyPress(KeyCode code) {
        if (!gameEngine.isGameFinished() && gameEngine.isGameReady()) {
            switch (code) {
                case UP, W -> gameEngine.getCharacter().setMovingUp(true);
                case DOWN, S -> gameEngine.getCharacter().setMovingDown(true);
                case LEFT, A -> gameEngine.getCharacter().setMovingLeft(true);
                case RIGHT, D -> gameEngine.getCharacter().setMovingRight(true);
                default -> {}
            }
        }
    }

    public void handleKeyRelease(KeyCode code) {
        switch (code) {
            case UP, W -> gameEngine.getCharacter().setMovingUp(false);
            case DOWN, S -> gameEngine.getCharacter().setMovingDown(false);
            case LEFT, A -> gameEngine.getCharacter().setMovingLeft(false);
            case RIGHT, D -> gameEngine.getCharacter().setMovingRight(false);
            default -> {}
        }
    }
}