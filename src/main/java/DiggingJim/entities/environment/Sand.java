package DiggingJim.entities.environment;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class Sand extends GameObject {
    public Sand(double x, double y) {
        super("sand", GameConfig.SAND_SIZE, GameConfig.SAND_SIZE);
        setX(x);
        setY(y);
    }
}