package DiggingJim.config;

public class GameConfig {
    // Scene dimensions
    public static final double SCENE_WIDTH = 2 * 1920;
    public static final double SCENE_HEIGHT = 3 * 1080;
    public static final double VIEWPORT_WIDTH = 1920;
    public static final double VIEWPORT_HEIGHT = 1080;

    // Game entity sizes
    public static final double CHARACTER_SIZE = 89;
    public static final int SAND_SIZE = 45;
    public static final int ROCK_WIDTH = 90;
    public static final int ROCK_HEIGHT = 90;
    public static final int DIAMOND_WIDTH = 90;
    public static final int DIAMOND_HEIGHT = 90;
    public static final int BRICKS_SIZE = 120;
    public static final int BRICKS_FRAME_SIZE = 80;
    public static final double MONSTER_SIZE = 89;
    public static final double HEART_SIZE = 60;
    public static final double DOOR_SIZE = 120;

    // Game character position and movement
    public static final double CHARACTER_START_X = 90;
    public static final double CHARACTER_START_Y = 90;
    public static final double CHARACTER_SPEED = 20;
    public static final double GRAVITY = 0.7;
    public static final double FRAME_DURATION = 7.0;
    public static final double TARGET_FPS = 60.0;

    // Game entity counts
    public static final int NUM_OF_ROCKS = 120;
    public static final int NUM_OF_DIAMONDS = 60;
    public static final int NUM_MONSTERS = 15;
    public static final int INITIAL_HEARTS = 3;

    // Difficulty levels
    public static final int EASY_SCORE_REQUIREMENT = 30;
    public static final int NORMAL_SCORE_REQUIREMENT = 40;
    public static final int HARD_SCORE_REQUIREMENT = 50;
    public static final int EXTREME_SCORE_REQUIREMENT = 60;

    public static final double EASY_MONSTER_SPEED = 0.5;
    public static final double NORMAL_MONSTER_SPEED = 0.75;
    public static final double HARD_MONSTER_SPEED = 1.0;
    public static final double EXTREME_MONSTER_SPEED = 1.25;
}