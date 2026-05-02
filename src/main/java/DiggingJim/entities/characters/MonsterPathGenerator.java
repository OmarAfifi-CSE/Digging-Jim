package DiggingJim.entities.characters;

import java.util.ArrayList;
import java.util.List;

import DiggingJim.config.GameConfig;
import javafx.animation.PathTransition;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MonsterPathGenerator {
    private List<Monster> monsters;
    private List<PathTransition> pathTransitions;
    private double monsterSpeed;

    public MonsterPathGenerator(List<Monster> monsters, double monsterSpeed) {
        this.monsters = monsters;
        this.monsterSpeed = monsterSpeed;
        this.pathTransitions = new ArrayList<>();
    }

    public void createPathTransitions() {
        // Make monsters visible
        for (Monster monster : monsters) {
            monster.show();
        }

        // First stage paths
        Line hPath1 = new Line(180, GameConfig.SCENE_HEIGHT / 6, GameConfig.SCENE_WIDTH - 360, GameConfig.SCENE_HEIGHT / 6);
        Line vPath1 = new Line(GameConfig.SCENE_WIDTH / 3, 140, GameConfig.SCENE_WIDTH / 3, 1000);
        Line vPath2 = new Line(GameConfig.SCENE_WIDTH * 2 / 3, 1000, GameConfig.SCENE_WIDTH * 2 / 3, 140);
        Line vPath3 = new Line(GameConfig.SCENE_WIDTH - 200, 300, GameConfig.SCENE_WIDTH - 200, 2000);
        Rectangle path1 = new Rectangle(300, 300, 3000, 700);

        // Second stage paths
        Line hPath2 = new Line(280, GameConfig.SCENE_HEIGHT * 5 / 12, GameConfig.SCENE_WIDTH - 160, GameConfig.SCENE_HEIGHT * 5 / 12);
        Line hPath3 = new Line(GameConfig.SCENE_WIDTH - 160, GameConfig.SCENE_HEIGHT * 7 / 12, 280, GameConfig.SCENE_HEIGHT * 7 / 12);
        Line vPath4 = new Line(GameConfig.SCENE_WIDTH / 3, 1280, GameConfig.SCENE_WIDTH / 3, 2080);
        Line vPath5 = new Line(GameConfig.SCENE_WIDTH * 2 / 3, 2080, GameConfig.SCENE_WIDTH * 2 / 3, 1280);
        Line vPath6 = new Line(140, 1300, 140, 3000);

        // Third stage paths
        Line hPath4 = new Line(300, 2550, GameConfig.SCENE_WIDTH - 100, 2550);
        Line hPath5 = new Line(GameConfig.SCENE_WIDTH - 100, 2900, 300, 2900);
        Line vPath7 = new Line(1000, 2394, 1000, GameConfig.SCENE_HEIGHT - 120); // Using bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + 80
        Line vPath8 = new Line(2000, GameConfig.SCENE_HEIGHT - 120, 2000, 2394);
        Line vPath9 = new Line(3000, 2394, 3000, GameConfig.SCENE_HEIGHT - 120);

        // Create path transitions
        PathTransition[] transitions = new PathTransition[] {
                createPathTransition(hPath1, monsters.get(0), 5500, true),
                createPathTransition(vPath1, monsters.get(1), 2000, true),
                createPathTransition(vPath2, monsters.get(2), 2000, true),
                createPathTransition(vPath3, monsters.get(3), 3500, true),
                createPathTransition(path1, monsters.get(4), 12000, false),
                createPathTransition(hPath2, monsters.get(5), 5500, true),
                createPathTransition(hPath3, monsters.get(6), 5500, true),
                createPathTransition(vPath4, monsters.get(7), 1850, true),
                createPathTransition(vPath5, monsters.get(8), 1850, true),
                createPathTransition(vPath6, monsters.get(9), 3400, true),
                createPathTransition(hPath4, monsters.get(10), 5100, true),
                createPathTransition(hPath5, monsters.get(11), 5100, true),
                createPathTransition(vPath7, monsters.get(12), 1700, true),
                createPathTransition(vPath8, monsters.get(13), 1700, true),
                createPathTransition(vPath9, monsters.get(14), 1700, true)
        };

        for (PathTransition transition : transitions) {
            transition.setRate(monsterSpeed);
            pathTransitions.add(transition);
        }
    }

    private PathTransition createPathTransition(javafx.scene.shape.Shape path, Monster monster, double duration, boolean autoReverse) {
        PathTransition transition = new PathTransition(Duration.millis(duration), path, monster);
        transition.setCycleCount(autoReverse ? PathTransition.INDEFINITE : PathTransition.INDEFINITE);
        if (autoReverse) {
            transition.setAutoReverse(true);
        }
        return transition;
    }

    public void startAllPaths() {
        for (PathTransition path : pathTransitions) {
            path.play();
        }
    }

    public void stopAllPaths() {
        for (PathTransition path : pathTransitions) {
            path.stop();
        }
    }

    public List<PathTransition> getPathTransitions() {
        return pathTransitions;
    }
}