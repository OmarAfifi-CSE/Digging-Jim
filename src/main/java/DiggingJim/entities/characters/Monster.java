package DiggingJim.entities.characters;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class Monster extends GameObject {
    public Monster() {
        super("monster", GameConfig.MONSTER_SIZE, GameConfig.MONSTER_SIZE);
        setOpacity(0);
    }

    public void show() {
        setOpacity(1);
    }
}