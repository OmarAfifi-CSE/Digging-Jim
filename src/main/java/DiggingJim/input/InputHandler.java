package DiggingJim.input;

import DiggingJim.core.GameEngine;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private GameEngine gameEngine;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void handleKeyPress(KeyCode code) {
        if (!gameEngine.isGameFinished() && gameEngine.isGameReady()) {
            switch (code) {
                case UP:
                case W:
                    gameEngine.getCharacter().setMovingUp(true);
                    break;
                case DOWN:
                case S:
                    gameEngine.getCharacter().setMovingDown(true);
                    break;
                case LEFT:
                case A:
                    gameEngine.getCharacter().setMovingLeft(true);
                    break;
                case RIGHT:
                case D:
                    gameEngine.getCharacter().setMovingRight(true);
                    break;
                default:
                    break;
            }
        }
    }

    public void handleKeyRelease(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                gameEngine.getCharacter().setMovingUp(false);
                break;
            case DOWN:
            case S:
                gameEngine.getCharacter().setMovingDown(false);
                break;
            case LEFT:
            case A:
                gameEngine.getCharacter().setMovingLeft(false);
                break;
            case RIGHT:
            case D:
                gameEngine.getCharacter().setMovingRight(false);
                break;
            default:
                break;
        }
    }
}