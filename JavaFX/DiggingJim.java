package diggingjim;

import java.io.File;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DiggingJim extends Application {

    private MediaPlayer startPlay;
    private MediaPlayer btnClick;
    private MediaPlayer pickDiamond;
    private MediaPlayer gameOverSound;
    private MediaPlayer victorySound;
    private double CHARACTER_Down_SPEED = 5;
    private double CHARACTER_Up_SPEED = 5;
    private double CHARACTER_Right_SPEED = 5;
    private double CHARACTER_Left_SPEED = 5;// Ad
    private static final int NUM_OF_ROCKS = 120;
    private static final double GRAVITY = .7;
    private static final double[] NUM_OF_ROCKS_SPEED = new double[NUM_OF_ROCKS];
    Rocks[] rock = new Rocks[NUM_OF_ROCKS];
    private final double SCENE_WIDTH = 2 * 1920;
    private final double SCENE_HEIGHT = 3 * 1080;
    private final double CHARACTER_SPEED = 5;
    private final double FRAME_DURATION = 7.0;
    private final double CHARACTER_SIZE = 89;
    private final int SAND_SIZE = 45;
    private final int NUM_OF_DIAMONDS = 60;
    private final int ROCK_WIDTH = 90;
    private final int ROCK_HEIGHT = 90;
    private final int DIAMOND_WIDTH = 90;
    private final int DIAMOND_HEIGHT = 90;
    private final int BRICKS_SIZE = 120;
    private final int BRICKS_FRAME_SIZE = 80;
    private final double MONSTER_SIZE = 89;
    private final int NUM_MONSTERS = 15;
    private final double heartSize = 60;
    private final double characterStartX = 90;
    private final double characterStartY = 90;
    private final double Door_Size = 120;
    private int numOfHearts = 3;
    private int collectedDiamonds = 0;
    private boolean Animationstopped = false;
    private boolean isGameFinished = false;
    private int index = 0;
    private ArrayList<ImageView> monsterView = new ArrayList<>(NUM_MONSTERS);
    private ImageView monster;
    private Image monsterImage;
    private Image openDoor;
    private Image closedDoor;
    private Image Brick;
    private ImageView enterDoor;
    private ImageView exitDoor;
    private ImageView characterView;
    private ImageView heart;
    private ImageView[] emptyHeart = new ImageView[numOfHearts];
    private Timeline animation;
    private Timeline timeline;
    private ImageView bricks;
    private Image characterFrontView;
    private Image characterLeftView;
    private Image characterRightView;
    private Label SCORE;
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private Diamonds[] diamonds = new Diamonds[NUM_OF_DIAMONDS];
    private HBox bricksFirstMiddle, bricksSecondMiddle, heartBox, diamondCounterBox;
    private Button easybtn, normalbtn, hardbtn, extremebtn;
    private VBox initialBox;
    boolean isready = false;
    int score;
    int monsterspeedLevel;
    private Pane root = new Pane();
    private int diamondCounter = 0;
    int i = 0;

    @Override
    public void start(Stage primaryStage) {
        gameLayout(primaryStage);
        startRootMovementAnimation();
        sounds();
        createSand();
        createDoor();
        createCharacter();
        timelineAnimation();
        createBricks();
        createDiamonds();
        setRockSpeed();
        createRocks();
        createMonsters();
        createOptionButtons();
        characterDiamondsCollision();
        characterRockCollision();
        characterMonsterIntersection();
        createHearts();
        createDiamondCounter();
        callCheckSand();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

    }

    private void gameLayout(Stage primaryStage) {

        Pane pane = new Pane(root);
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> handleKeyRelease(event.getCode()));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Digging Jim");
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(false);
    }

    private void callCheckSand() {
        for (int i = 0; i < NUM_OF_ROCKS; i++) {
            rockSandCollision(rock[i], i);
            removeSandAroundRock(rock[i]);

        }
    }

    private void createOptionButtons() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Vampire Wars.ttf"), 100);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Midnight Moon.ttf"), 100);
        Label selectlabel = new Label("Select Level");
        selectlabel.setFont(new Font("Vampire Wars", 100));

        easybtn = new Button("Easy");
        easybtn.setFocusTraversable(false);
        easybtn.setFont(new Font("Midnight Moon", 40));
        easybtn.setMaxWidth(Double.MAX_VALUE);
        easybtn.setMaxHeight(5);

        normalbtn = new Button("Normal");
        normalbtn.setFocusTraversable(false);
        normalbtn.setFont(new Font("Midnight Moon", 40));
        normalbtn.setMaxWidth(Double.MAX_VALUE);

        hardbtn = new Button("Hard");
        hardbtn.setFocusTraversable(false);
        hardbtn.setFont(new Font("Midnight Moon", 40));
        hardbtn.setMaxWidth(Double.MAX_VALUE);

        extremebtn = new Button("Extreme");
        extremebtn.setFocusTraversable(false);
        extremebtn.setFont(new Font("Midnight Moon", 40));
        extremebtn.setMaxWidth(Double.MAX_VALUE);

        initialBox = new VBox(20, selectlabel, easybtn, normalbtn, hardbtn, extremebtn);
        initialBox.setLayoutX(SCENE_WIDTH / 4 - 300);
        initialBox.setLayoutY(SCENE_HEIGHT / 6 - 300);
        initialBox.setStyle("-fx-background-image: url('/images/InitialBoxBackground.png');" + "-fx-background-size: cover;");
        initialBox.setPadding(new Insets(20, 40, 40, 40));
        Timeline wait = new Timeline(new KeyFrame(Duration.seconds(4.5), e -> {
            root.getChildren().add(initialBox);
        }));
        wait.play();

        easybtn.setOnAction(e -> {
            btnClick.play();
            score = 30;
            monsterspeedLevel = 1;
            root.getChildren().remove(initialBox);
            createPathsTransition();
            isready = true;
        });
        normalbtn.setOnAction(e -> {
            btnClick.play();
            score = 40;
            monsterspeedLevel = 2;
            root.getChildren().remove(initialBox);
            createPathsTransition();
            isready = true;
        });
        hardbtn.setOnAction(e -> {
            btnClick.play();
            score = 50;
            monsterspeedLevel = 3;
            root.getChildren().remove(initialBox);
            createPathsTransition();
            isready = true;
        });
        extremebtn.setOnAction(e -> {
            btnClick.play();
            score = 60;
            monsterspeedLevel = 4;
            root.getChildren().remove(initialBox);
            createPathsTransition();
            isready = true;
        });
        easybtn.setOnMouseEntered(e -> {
            easybtn.setStyle("-fx-background-color:Black");
            easybtn.setTextFill(Color.WHITE);
        });
        normalbtn.setOnMouseEntered(e -> {
            normalbtn.setStyle("-fx-background-color:Black");
            normalbtn.setTextFill(Color.WHITE);
        });
        hardbtn.setOnMouseEntered(e -> {
            hardbtn.setStyle("-fx-background-color:Black");
            hardbtn.setTextFill(Color.WHITE);
        });
        extremebtn.setOnMouseEntered(e -> {
            extremebtn.setStyle("-fx-background-color:Black");
            extremebtn.setTextFill(Color.WHITE);
        });
        easybtn.setOnMouseExited(e -> {
            easybtn.setStyle("-fx-background-color:white");
            easybtn.setTextFill(Color.BLACK);
        });

        easybtn.setOnMouseExited(e -> {
            easybtn.setStyle("-fx-background-color:white");
            easybtn.setTextFill(Color.BLACK);
        });
        normalbtn.setOnMouseExited(e -> {
            normalbtn.setStyle("-fx-background-color:white");
            normalbtn.setTextFill(Color.BLACK);
        });
        hardbtn.setOnMouseExited(e -> {
            hardbtn.setStyle("-fx-background-color:white");
            hardbtn.setTextFill(Color.BLACK);
        });
        extremebtn.setOnMouseExited(e -> {
            extremebtn.setStyle("-fx-background-color:white");
            extremebtn.setTextFill(Color.BLACK);
        });

    }

    private void createDoor() {
        openDoor = new Image("images/Open Door.png");
        closedDoor = new Image("images/Door.png");

        enterDoor = new ImageView(openDoor);
        enterDoor.setX(80);
        enterDoor.setY(60);
        enterDoor.setFitWidth(Door_Size);
        enterDoor.setFitHeight(Door_Size);

        exitDoor = new ImageView(closedDoor);
        exitDoor.setX(SCENE_WIDTH - BRICKS_FRAME_SIZE - Door_Size);
        exitDoor.setY(SCENE_HEIGHT - BRICKS_FRAME_SIZE - Door_Size);
        exitDoor.setFitWidth(Door_Size);
        exitDoor.setFitHeight(Door_Size);

        root.getChildren().addAll(enterDoor, exitDoor);
    }

    private void createCharacter() {
        characterFrontView = new Image("images/Character front face.png");
        characterLeftView = new Image("images/Character left face.png");
        characterRightView = new Image("images/Character right face.png");
        characterView = new ImageView(characterFrontView);
        characterView.setFitWidth(CHARACTER_SIZE);
        characterView.setFitHeight(CHARACTER_SIZE);
        characterView.setX(characterStartX);
        characterView.setY(characterStartY);
        root.getChildren().add(characterView);
    }

    private void createHearts() {
        heartBox = new HBox(10);
        for (int i = 0; i < 3; i++) {
            heart = new ImageView(new Image("images/Heart.png"));
            heart.setFitWidth(heartSize);
            heart.setFitHeight(heartSize);
            heartBox.getChildren().add(heart);

            emptyHeart[i] = new ImageView(new Image("images/Empty Heart.png"));
            emptyHeart[i].setFitWidth(heartSize);
            emptyHeart[i].setFitHeight(heartSize);
        }

        heartBox.setLayoutX(3560);
        heartBox.setLayoutY(2180);
        root.getChildren().add(heartBox);
    }

    private void createMonsters() {
        monsterImage = new Image("images/Monster.png");
        for (int i = 0; i < NUM_MONSTERS; i++) {
            monster = new ImageView(monsterImage);
            monster.setFitWidth(MONSTER_SIZE);
            monster.setFitHeight(MONSTER_SIZE);

            monster.setOpacity(0);
            monsterView.add(monster);
            root.getChildren().add(monsterView.get(i));
        }

    }

    private void createPathsTransition() {//create pathes for the Ghosts and path transition
        //first stage pathes
        for (ImageView Monster : monsterView) {
            Monster.setOpacity(1);
        }

        Line Hpath1 = new Line(180, SCENE_HEIGHT / 6, SCENE_WIDTH - 360, SCENE_HEIGHT / 6);//3660
        Line Vpath1 = new Line(SCENE_WIDTH / 3, 140, SCENE_WIDTH / 3, 1000); //860
        Line Vpath2 = new Line(SCENE_WIDTH * 2 / 3, 1000, SCENE_WIDTH * 2 / 3, 140);//860
        Line Vpath3 = new Line(SCENE_WIDTH - 200, 300, SCENE_WIDTH - 200, 2000);//1700
        Rectangle path1 = new Rectangle(300, 300, 3000, 700);//7400
        //second stage pathes
        Line Hpath2 = new Line(280, SCENE_HEIGHT * 5 / 12, SCENE_WIDTH - 160, SCENE_HEIGHT * 5 / 12);//3850
        Line Hpath3 = new Line(SCENE_WIDTH - 160, SCENE_HEIGHT * 7 / 12, 280, SCENE_HEIGHT * 7 / 12);//3850
        Line Vpath4 = new Line(SCENE_WIDTH / 3, 1280, SCENE_WIDTH / 3, 2080);//800
        Line Vpath5 = new Line(SCENE_WIDTH * 2 / 3, 2080, SCENE_WIDTH * 2 / 3, 1280);//800
        Line Vpath6 = new Line(140, 1300, 140, 3000);//1700
        //third stage pathes
        Line Hpath4 = new Line(300, 2550, SCENE_WIDTH - 100, 2550);//3540
        Line Hpath5 = new Line(SCENE_WIDTH - 100, 2900, 300, 2900);//3540
        Line Vpath7 = new Line(1000, bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + 80, 1000, SCENE_HEIGHT - 120);//760
        Line Vpath8 = new Line(2000, SCENE_HEIGHT - 120, 2000, bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + 80);//760
        Line Vpath9 = new Line(3000, bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + 80, 3000, SCENE_HEIGHT - 120);//760

        PathTransition pathTransition1 = new PathTransition(Duration.millis(5500), Hpath1, monsterView.get(0));
        pathTransition1.setCycleCount(Timeline.INDEFINITE);
        pathTransition1.setAutoReverse(true);

        PathTransition pathTransition2 = new PathTransition(Duration.millis(2000), Vpath1, monsterView.get(1));
        pathTransition2.setCycleCount(-1);
        pathTransition2.setAutoReverse(true);

        PathTransition pathTransition3 = new PathTransition(Duration.millis(2000), Vpath2, monsterView.get(2));
        pathTransition3.setCycleCount(-1);
        pathTransition3.setAutoReverse(true);

        PathTransition pathTransition4 = new PathTransition(Duration.millis(3500), Vpath3, monsterView.get(3));
        pathTransition4.setCycleCount(-1);
        pathTransition4.setAutoReverse(true);

        PathTransition pathTransition5 = new PathTransition(Duration.seconds(12), path1, monsterView.get(4));
        pathTransition5.setCycleCount(-1);

        PathTransition pathTransition6 = new PathTransition(Duration.millis(5500), Hpath2, monsterView.get(5));
        pathTransition6.setCycleCount(-1);
        pathTransition6.setAutoReverse(true);

        PathTransition pathTransition7 = new PathTransition(Duration.millis(5500), Hpath3, monsterView.get(6));
        pathTransition7.setCycleCount(-1);
        pathTransition7.setAutoReverse(true);

        PathTransition pathTransition8 = new PathTransition(Duration.millis(1850), Vpath4, monsterView.get(7));
        pathTransition8.setCycleCount(-1);
        pathTransition8.setAutoReverse(true);

        PathTransition pathTransition9 = new PathTransition(Duration.millis(1850), Vpath5, monsterView.get(8));
        pathTransition9.setCycleCount(-1);
        pathTransition9.setAutoReverse(true);

        PathTransition pathTransition10 = new PathTransition(Duration.millis(3400), Vpath6, monsterView.get(9));
        pathTransition10.setCycleCount(-1);
        pathTransition10.setAutoReverse(true);

        PathTransition pathTransition11 = new PathTransition(Duration.millis(5100), Hpath4, monsterView.get(10));
        pathTransition11.setCycleCount(-1);
        pathTransition11.setAutoReverse(true);

        PathTransition pathTransition12 = new PathTransition(Duration.millis(5100), Hpath5, monsterView.get(11));
        pathTransition12.setCycleCount(-1);
        pathTransition12.setAutoReverse(true);

        PathTransition pathTransition13 = new PathTransition(Duration.millis(1700), Vpath7, monsterView.get(12));
        pathTransition13.setCycleCount(-1);
        pathTransition13.setAutoReverse(true);

        PathTransition pathTransition14 = new PathTransition(Duration.millis(1700), Vpath8, monsterView.get(13));
        pathTransition14.setCycleCount(-1);
        pathTransition14.setAutoReverse(true);

        PathTransition pathTransition15 = new PathTransition(Duration.millis(1700), Vpath9, monsterView.get(14));
        pathTransition15.setCycleCount(-1);
        pathTransition15.setAutoReverse(true);

        pathTransition1.play();
        pathTransition2.play();
        pathTransition3.play();
        pathTransition4.play();
        pathTransition5.play();
        pathTransition6.play();
        pathTransition7.play();
        pathTransition8.play();
        pathTransition9.play();
        pathTransition10.play();
        pathTransition11.play();
        pathTransition12.play();
        pathTransition13.play();
        pathTransition14.play();
        pathTransition15.play();

        double easyRate = .5, normalRate = .75, HardRate = 1, extreamRate = 1.25;

        switch (monsterspeedLevel) {
            case 1:
                pathTransition1.setRate(easyRate);
                pathTransition2.setRate(easyRate);
                pathTransition3.setRate(easyRate);
                pathTransition4.setRate(easyRate);
                pathTransition5.setRate(easyRate);
                pathTransition6.setRate(easyRate);
                pathTransition7.setRate(easyRate);
                pathTransition8.setRate(easyRate);
                pathTransition9.setRate(easyRate);
                pathTransition10.setRate(easyRate);
                pathTransition11.setRate(easyRate);
                pathTransition12.setRate(easyRate);
                pathTransition13.setRate(easyRate);
                pathTransition14.setRate(easyRate);
                pathTransition15.setRate(easyRate);

                break;
            case 2:
                pathTransition1.setRate(normalRate);
                pathTransition2.setRate(normalRate);
                pathTransition3.setRate(normalRate);
                pathTransition4.setRate(normalRate);
                pathTransition5.setRate(normalRate);
                pathTransition6.setRate(normalRate);
                pathTransition7.setRate(normalRate);
                pathTransition8.setRate(normalRate);
                pathTransition9.setRate(normalRate);
                pathTransition10.setRate(normalRate);
                pathTransition11.setRate(normalRate);
                pathTransition12.setRate(normalRate);
                pathTransition13.setRate(normalRate);
                pathTransition14.setRate(normalRate);
                pathTransition15.setRate(normalRate);

                break;
            case 3:
                pathTransition1.setRate(HardRate);
                pathTransition2.setRate(HardRate);
                pathTransition3.setRate(HardRate);
                pathTransition4.setRate(HardRate);
                pathTransition5.setRate(HardRate);
                pathTransition6.setRate(HardRate);
                pathTransition7.setRate(HardRate);
                pathTransition8.setRate(HardRate);
                pathTransition9.setRate(HardRate);
                pathTransition10.setRate(HardRate);
                pathTransition11.setRate(HardRate);
                pathTransition12.setRate(HardRate);
                pathTransition13.setRate(HardRate);
                pathTransition14.setRate(HardRate);
                pathTransition15.setRate(HardRate);

                break;
            case 4:
                pathTransition1.setRate(extreamRate);
                pathTransition2.setRate(extreamRate);
                pathTransition3.setRate(extreamRate);
                pathTransition4.setRate(extreamRate);
                pathTransition5.setRate(extreamRate);
                pathTransition6.setRate(extreamRate);
                pathTransition7.setRate(extreamRate);
                pathTransition8.setRate(extreamRate);
                pathTransition9.setRate(extreamRate);
                pathTransition10.setRate(extreamRate);
                pathTransition11.setRate(extreamRate);
                pathTransition12.setRate(extreamRate);
                pathTransition13.setRate(extreamRate);
                pathTransition14.setRate(extreamRate);
                pathTransition15.setRate(extreamRate);

                break;

        }

    }

    private void createSand() {
        for (int x = 0; x < SCENE_WIDTH; x += SAND_SIZE) {
            for (int y = 0; y < SCENE_HEIGHT; y += SAND_SIZE) {
                Sand sand = new Sand();
                sand.setFitWidth(SAND_SIZE);
                sand.setFitHeight(SAND_SIZE);
                sand.setX(x);
                sand.setY(y);
                root.getChildren().add(sand);
            }
        }
    }

    private void timelineAnimation() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(FRAME_DURATION), event -> {
            moveCharacter();
            characterSandIntersection();
            trackCharacterMovement();
            characterRockPush();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void createBricks() {
        Brick = new Image("images/Bricks.png");
        bricksFirstMiddle = new HBox();     //for the first middle bricks
        bricksSecondMiddle = new HBox();    //for the second middle bricks
        HBox bricksUp = new HBox();         //for the upper bricks frame
        HBox bricksDown = new HBox();       //for the bottom bricks frame
        VBox bricksLeft = new VBox();       //for the left bricks frame
        VBox bricksRight = new VBox();      //for the right bricks frame

        for (int num = 0; num < 29; num++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE);
            bricks.setFitHeight(BRICKS_SIZE);
            bricksFirstMiddle.getChildren().add(bricks);
        }
        for (int num = 0; num < 29; num++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE);
            bricks.setFitHeight(BRICKS_SIZE);
            bricksSecondMiddle.getChildren().add(bricks);

        }
        for (int i = 0; i < 50; i++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE - 40);
            bricks.setFitHeight(BRICKS_SIZE - 40);
            bricksUp.getChildren().add(bricks);
        }
        for (int i = 0; i < 50; i++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE - 40);
            bricks.setFitHeight(BRICKS_SIZE - 40);
            bricksDown.getChildren().add(bricks);
        }

        for (int i = 0; i < 40; i++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE - 40);
            bricks.setFitHeight(BRICKS_SIZE - 40);
            bricksRight.getChildren().add(bricks);
        }
        for (int i = 0; i < 40; i++) {
            bricks = new ImageView(Brick);
            bricks.setFitWidth(BRICKS_SIZE - 40);
            bricks.setFitHeight(BRICKS_SIZE - 40);
            bricksLeft.getChildren().add(bricks);
        }

        bricksFirstMiddle.setLayoutX(BRICKS_FRAME_SIZE);
        bricksFirstMiddle.setLayoutY(SCENE_HEIGHT / 3 - 46);
        bricksSecondMiddle.setLayoutX(280);//200 px is the empty reigion
        bricksSecondMiddle.setLayoutY(SCENE_HEIGHT * 2 / 3 - 46);
        bricksUp.setLayoutX(0);
        bricksUp.setLayoutY(0);
        bricksDown.setLayoutX(0);
        bricksDown.setLayoutY(SCENE_HEIGHT - BRICKS_FRAME_SIZE);
        bricksLeft.setLayoutX(0);
        bricksLeft.setLayoutY(BRICKS_FRAME_SIZE);
        bricksRight.setLayoutX(SCENE_WIDTH - BRICKS_FRAME_SIZE);
        bricksRight.setLayoutY(BRICKS_FRAME_SIZE);

        root.getChildren().addAll(bricksUp, bricksDown, bricksRight, bricksLeft, bricksFirstMiddle, bricksSecondMiddle);
    }

    private void startRootMovementAnimation() {

        root.setLayoutX(-SCENE_WIDTH / 2);
        root.setLayoutY(-SCENE_HEIGHT * 2 / 3);
        Timeline wait = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            animation = new Timeline(new KeyFrame(Duration.millis(FRAME_DURATION), event1 -> {
                if (root.getLayoutX() < 0) {
                    root.setLayoutX(root.getLayoutX() + CHARACTER_SPEED);
                    heartBox.setLayoutX(heartBox.getLayoutX() - CHARACTER_SPEED);
                    diamondCounterBox.setLayoutX(diamondCounterBox.getLayoutX() - CHARACTER_SPEED);
                }
                if (root.getLayoutY() < 0) {
                    root.setLayoutY(root.getLayoutY() + CHARACTER_SPEED);
                    heartBox.setLayoutY(heartBox.getLayoutY() - CHARACTER_SPEED);
                    diamondCounterBox.setLayoutY(diamondCounterBox.getLayoutY() - CHARACTER_SPEED);
                }
            }));
            animation.setCycleCount(432);
            animation.play();
            startPlay.play();
        }));
        wait.play();
    }

    private void deathAnimation(int cycleCount) {    //after death
        numOfHearts--;
        heartBox.getChildren().set(i, emptyHeart[i]);

        if (numOfHearts > 0) {
            characterView.setX(characterStartX);
            characterView.setY(characterStartY);
            CHARACTER_Up_SPEED = CHARACTER_SPEED;
            CHARACTER_Down_SPEED = CHARACTER_SPEED;
            CHARACTER_Right_SPEED = CHARACTER_SPEED;
            CHARACTER_Left_SPEED = CHARACTER_SPEED;
            i++;

            FadeTransition fade = new FadeTransition();
            fade.setDuration(Duration.seconds(2));
            fade.setFromValue(.1);
            fade.setToValue(1);
            fade.setAutoReverse(true);
            fade.setNode(characterView);
            fade.play();

            animation = new Timeline(new KeyFrame(Duration.millis(FRAME_DURATION / 2), e -> {
                if (root.getLayoutX() < 0) {
                    root.setLayoutX(root.getLayoutX() + CHARACTER_SPEED);
                    heartBox.setLayoutX(heartBox.getLayoutX() - CHARACTER_SPEED);
                    diamondCounterBox.setLayoutX(diamondCounterBox.getLayoutX() - CHARACTER_SPEED);
                }
                if (root.getLayoutY() < 0) {
                    root.setLayoutY(root.getLayoutY() + CHARACTER_SPEED);
                    heartBox.setLayoutY(heartBox.getLayoutY() - CHARACTER_SPEED);
                    diamondCounterBox.setLayoutY(diamondCounterBox.getLayoutY() - CHARACTER_SPEED);
                }
            }));
            animation.setCycleCount(cycleCount);
            animation.play();

        } else {
            GameOver();
        }

    }

    private int getCycleCount() {//to get the number of cyclecount we need to return back the camera to the orign

        double maxRootLayout = root.getLayoutX() < root.getLayoutY() ? root.getLayoutX() : root.getLayoutY(); //maximum in negative
        int cyclecount = (int) Math.round(maxRootLayout / 5) * -1;
        return cyclecount;
    }

    private void GameOver() {
        ImageView GameOver = new ImageView(new Image("images/You Lose.png"));
        GameOver.setFitWidth(1600);
        GameOver.setFitHeight(1000);
        GameOver.setLayoutX(SCENE_WIDTH / 4 - 800);
        GameOver.setLayoutY(SCENE_HEIGHT / 6 - 500);
        GameOver.setOpacity(0);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(GameOver);
        fadeTransition.setDuration(Duration.millis(2000)); // Duration of the fade effect
        fadeTransition.setFromValue(0.0); // Start fully transparent
        fadeTransition.setToValue(1.0); // End fully opaque

        // Play the transition
        fadeTransition.play();
        root.getChildren().add(GameOver);

        isGameFinished = true;
        root.setLayoutX(0);
        root.setLayoutY(0);
        heartBox.setLayoutX(1670);
        heartBox.setLayoutY(20);
        diamondCounterBox.setLayoutX(1480);
        diamondCounterBox.setLayoutY(20);
        root.getChildren().removeAll(monsterView);
        characterView.setX(characterStartX);
        characterView.setY(characterStartY);
        gameOverSound.play();

    }

    private void handleKeyPress(KeyCode code) {
        if (!isGameFinished && isready) {
            switch (code) {
                case UP:
                case W:
                    movingUp = true;
                    characterView.setImage(characterFrontView);
                    break;
                case DOWN:
                case S:
                    movingDown = true;
                    characterView.setImage(characterFrontView);
                    break;
                case LEFT:
                case A:
                    movingLeft = true;
                    characterView.setImage(characterLeftView);
                    break;
                case RIGHT:
                case D:
                    movingRight = true;
                    characterView.setImage(characterRightView);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleKeyRelease(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                movingUp = false;
                break;
            case DOWN:
            case S:
                movingDown = false;
                break;
            case LEFT:
            case A:
                movingLeft = false;
                break;
            case RIGHT:
            case D:
                movingRight = false;
                break;
            default:
                break;
        }

        if (!movingUp && !movingDown && !movingLeft && !movingRight) {
            characterView.setImage(characterFrontView);
        }
    }

    private void moveCharacter() {

        double dx = 0, dy = 0;

        if (movingUp && !movingDown && !movingLeft && !movingRight) {
            dy -= CHARACTER_Up_SPEED;
        }
        if (movingDown && !movingUp && !movingLeft && !movingRight) {
            dy += CHARACTER_Down_SPEED;
        }
        if (movingLeft && !movingUp && !movingDown && !movingRight) {
            dx -= CHARACTER_Left_SPEED;
        }
        if (movingRight && !movingUp && !movingDown && !movingLeft) {
            dx += CHARACTER_Right_SPEED;
        }

        double characterX = characterView.getX() + dx;
        double characterY = characterView.getY() + dy;
        //Prevent character from going out of scene
        characterX = Math.max(BRICKS_FRAME_SIZE, Math.min(characterX, SCENE_WIDTH - CHARACTER_SIZE - BRICKS_FRAME_SIZE));
        characterY = Math.max(BRICKS_FRAME_SIZE, Math.min(characterY, SCENE_HEIGHT - CHARACTER_SIZE - BRICKS_FRAME_SIZE));
        //Prevent character from going through middle bricks

        characterX = middleBricksIntersectionX(characterX);
        characterY = middleBricksIntersectionY(characterY);

        characterView.setX(characterX);
        characterView.setY(characterY);

    }

    private double middleBricksIntersectionY(double characterY) {

        if (characterView.getY() < bricksFirstMiddle.getLayoutY() && characterView.getX() < SCENE_WIDTH - 280) {//280 for the right empty reigion
            characterY = Math.max(BRICKS_FRAME_SIZE, Math.min(characterY, bricksFirstMiddle.getLayoutY() - CHARACTER_SIZE));

            if (characterView.getY() + CHARACTER_SIZE == bricksFirstMiddle.getLayoutY()) {
                movingDown = false;
            }
        } else if (characterView.getY() >= bricksFirstMiddle.getLayoutY() + BRICKS_SIZE && characterView.getY() < bricksSecondMiddle.getLayoutY()
                && characterView.getX() > bricksSecondMiddle.getLayoutX() - CHARACTER_SIZE && characterView.getX() < SCENE_WIDTH - 280) {
            characterY = Math.max(bricksFirstMiddle.getLayoutY() + BRICKS_SIZE, Math.min(characterY, bricksSecondMiddle.getLayoutY() - CHARACTER_SIZE));

            if (characterView.getY() + CHARACTER_SIZE == bricksSecondMiddle.getLayoutY()) {
                movingDown = false;
            }
            if (characterView.getY() == bricksFirstMiddle.getLayoutY() + BRICKS_SIZE) {
                movingUp = false;
            }
        } else if (characterView.getY() >= bricksSecondMiddle.getLayoutY() + BRICKS_SIZE && characterView.getY() < SCENE_HEIGHT
                && characterView.getX() > bricksSecondMiddle.getLayoutX() - CHARACTER_SIZE) {
            characterY = Math.max(bricksSecondMiddle.getLayoutY() + BRICKS_SIZE, Math.min(characterY, SCENE_HEIGHT - BRICKS_FRAME_SIZE - CHARACTER_SIZE));
            if (characterView.getY() == bricksSecondMiddle.getLayoutY() + BRICKS_SIZE) {
                movingUp = false;
            }
        }
        if (characterView.getY() > bricksFirstMiddle.getLayoutY() + BRICKS_SIZE && characterView.getY() < bricksSecondMiddle.getLayoutY() + BRICKS_SIZE && characterView.getX() > SCENE_WIDTH - 280) {
            characterY = Math.min(characterY, bricksSecondMiddle.getLayoutY() - CHARACTER_SIZE);
            if (characterView.getY() + CHARACTER_SIZE == bricksSecondMiddle.getLayoutY()) {
                movingDown = false;
            }
        }
        if (characterView.getY() < SCENE_HEIGHT / 2 && characterView.getY() > bricksFirstMiddle.getLayoutY() && characterView.getX() < 280) {
            characterY = Math.max(characterY, bricksFirstMiddle.getLayoutY() + BRICKS_SIZE);
            if (characterView.getY() == bricksFirstMiddle.getLayoutY() + BRICKS_SIZE) {
                movingUp = false;
            }
        }
        return characterY;
    }

    private double middleBricksIntersectionX(double characterX) {
        if (characterView.getY() > bricksFirstMiddle.getLayoutY() - 80 && characterView.getY() < bricksFirstMiddle.getLayoutY() + BRICKS_SIZE) {
            characterX = Math.max(SCENE_WIDTH - 280, Math.min(characterX, SCENE_WIDTH - CHARACTER_SIZE - BRICKS_FRAME_SIZE));
            if (characterView.getX() == SCENE_WIDTH - 280) {
                movingLeft = false;
            }
        }
        if (characterView.getY() > bricksSecondMiddle.getLayoutY() - 80 && characterView.getY() < bricksSecondMiddle.getLayoutY() + BRICKS_SIZE) {
            characterX = Math.max(BRICKS_FRAME_SIZE, Math.min(characterX, 280 - CHARACTER_SIZE));
            if (characterView.getX() == bricksSecondMiddle.getLayoutX()) {
                movingRight = false;
            }
        }
        return characterX;
    }

    private void characterSandIntersection() {
        Bounds characterBounds = characterView.getBoundsInParent();
        root.getChildren().removeIf(node -> {
            if (node instanceof Sand && ((Sand) node).getImage() != null) {
                Bounds sandBounds = ((ImageView) node).getBoundsInParent();
                return characterBounds.intersects(sandBounds);
            }
            return false;
        });
    }

    private void characterMonsterIntersection() {
        AnimationTimer Timer1 = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < NUM_MONSTERS; i++) {
                    Bounds charBounds = characterView.getBoundsInParent();
                    Bounds monsterBounds = monsterView.get(i).getBoundsInParent();
                    if (charBounds.intersects(monsterBounds)) {
                        deathAnimation(getCycleCount());
                    }
                }
            }
        };

        Timer1.start();

    }

    private void trackCharacterMovement() {

        if (movingRight && !movingLeft && !movingUp && !movingDown && CHARACTER_Right_SPEED == 5) {
            if (characterView.getX() > SCENE_WIDTH / 4 && characterView.getX() < SCENE_WIDTH * 3 / 4 && root.getLayoutX() > -SCENE_WIDTH / 2) {
                root.setLayoutX(root.getLayoutX() - CHARACTER_SPEED);
                heartBox.setLayoutX(heartBox.getLayoutX() + CHARACTER_SPEED);
                diamondCounterBox.setLayoutX(diamondCounterBox.getLayoutX() + CHARACTER_SPEED);
            }
        } else if (movingLeft && !movingRight && !movingUp && !movingDown && CHARACTER_Left_SPEED == 5) {
            if (characterView.getX() > SCENE_WIDTH / 4 && characterView.getX() < SCENE_WIDTH * 3 / 4 && root.getLayoutX() < 0) {
                root.setLayoutX(root.getLayoutX() + CHARACTER_SPEED);
                heartBox.setLayoutX(heartBox.getLayoutX() - CHARACTER_SPEED);
                diamondCounterBox.setLayoutX(diamondCounterBox.getLayoutX() - CHARACTER_SPEED);
            }
        } else if (movingDown && !movingUp && !movingRight && !movingLeft && CHARACTER_Down_SPEED == 5) {
            if (characterView.getY() > SCENE_HEIGHT / 6 && characterView.getY() < SCENE_HEIGHT * 5 / 6 && root.getLayoutY() > -SCENE_HEIGHT / 1.5) {
                root.setLayoutY(root.getLayoutY() - CHARACTER_SPEED);
                heartBox.setLayoutY(heartBox.getLayoutY() + CHARACTER_SPEED);
                diamondCounterBox.setLayoutY(diamondCounterBox.getLayoutY() + CHARACTER_SPEED);
            }
        } else if (movingUp && !movingDown && !movingRight && !movingLeft && CHARACTER_Up_SPEED == 5) {
            if (characterView.getY() > SCENE_HEIGHT / 6 && characterView.getY() < SCENE_HEIGHT * 5 / 6 && root.getLayoutY() < 0) {
                root.setLayoutY(root.getLayoutY() + CHARACTER_SPEED);
                heartBox.setLayoutY(heartBox.getLayoutY() - CHARACTER_SPEED);
                diamondCounterBox.setLayoutY(diamondCounterBox.getLayoutY() - CHARACTER_SPEED);
            }
        }
    }

    private void createDiamondCounter() {
        diamondCounterBox = new HBox();
        ImageView diamondbox = new ImageView(new Image("images/Diamond Blue.png"));
        diamondbox.setFitWidth(70);
        diamondbox.setFitHeight(70);
        SCORE = new Label();
        SCORE.setTextFill(Color.GOLD);
        SCORE.setFont(Font.font(30));
        diamondCounterBox.getChildren().addAll(diamondbox, SCORE);
        diamondCounterBox.setLayoutX(3380);
        diamondCounterBox.setLayoutY(2180);
        root.getChildren().add(diamondCounterBox);
    }

    private void finishedGame(int score) {
        if (collectedDiamonds >= score) {
            exitDoor.setImage(openDoor);
            if (characterView.getX() >= exitDoor.getX() && characterView.getY() >= exitDoor.getY()) {

                ImageView victory = new ImageView(new Image("images/Victory.png"));
                victory.setFitWidth(1000);
                victory.setFitHeight(600);
                victory.setX(SCENE_WIDTH * 3 / 4 - 500);
                victory.setY(SCENE_HEIGHT * 5 / 6 - 300);

                victory.setOpacity(0);
                FadeTransition fadeTransition = new FadeTransition();
                fadeTransition.setNode(victory);
                fadeTransition.setDuration(Duration.millis(2000)); // Duration of the fade effect
                fadeTransition.setFromValue(0.0); // Start fully transparent
                fadeTransition.setToValue(1.0); // End fully opaque
                // Play the transition
                fadeTransition.play();
                root.getChildren().add(victory);
                isGameFinished = true;
                root.getChildren().removeAll(monsterView);
                victorySound.play();
            }
        }
    }

    private void createDiamonds() {
        for (index = 0; index < NUM_OF_DIAMONDS; index++) {
            diamonds[index] = new Diamonds();
            diamonds[index].setFitWidth(DIAMOND_WIDTH);
            diamonds[index].setFitHeight(DIAMOND_HEIGHT);
            if (index < NUM_OF_DIAMONDS / 3 && index > 0) {
                double randX = (BRICKS_FRAME_SIZE + DIAMOND_WIDTH / 2) + index * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (BRICKS_FRAME_SIZE + DIAMOND_HEIGHT / 2) + (Math.random() * ((bricksFirstMiddle.getLayoutY() - DIAMOND_HEIGHT) - BRICKS_FRAME_SIZE));
                double finalY = randY - randY % SAND_SIZE;
                diamonds[index].setX(finalX);
                diamonds[index].setY(finalY);
                root.getChildren().add(diamonds[index]);

            } else if (index < NUM_OF_DIAMONDS * 2 / 3) {
                double randX = (BRICKS_FRAME_SIZE + DIAMOND_WIDTH / 2) + (index - NUM_OF_DIAMONDS / 3) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksFirstMiddle.getLayoutY() + BRICKS_SIZE + DIAMOND_HEIGHT / 2) + (Math.random() * ((bricksSecondMiddle.getLayoutY() - DIAMOND_HEIGHT) - (bricksFirstMiddle.getLayoutY() + BRICKS_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                diamonds[index].setX(finalX);
                diamonds[index].setY(finalY);
                root.getChildren().add(diamonds[index]);

            } else {
                double randX = (BRICKS_FRAME_SIZE + DIAMOND_WIDTH / 2) + (index - NUM_OF_DIAMONDS * 2 / 3) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + DIAMOND_HEIGHT / 2) + (Math.random() * ((SCENE_HEIGHT - BRICKS_FRAME_SIZE - DIAMOND_HEIGHT) - (bricksSecondMiddle.getLayoutY() + BRICKS_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                diamonds[index].setX(finalX);
                diamonds[index].setY(finalY);
                root.getChildren().add(diamonds[index]);

            }

            root.getChildren().removeIf(node -> {
                Bounds diamondbound = diamonds[index].getBoundsInParent();
                if (node instanceof Sand) {
                    Bounds sandBounds = node.getBoundsInParent();

                    if (diamondbound.contains(sandBounds)) {
                        return true;
                    }

                }
                return false;

            });

            characterDiamondsCollision();

        }
        Diamonds diamond = new Diamonds();
        diamond.setFitWidth(90);
        diamond.setFitHeight(90);
        double randX = (SCENE_WIDTH - BRICKS_FRAME_SIZE - DIAMOND_WIDTH - SAND_SIZE);
        double finalX = randX - randX % SAND_SIZE;
        double randY = (SCENE_HEIGHT - BRICKS_FRAME_SIZE - SAND_SIZE * 10);
        double finalY = randY - randY % SAND_SIZE;
        diamond.setX(finalX);
        diamond.setY(finalY);
        root.getChildren().add(diamond);

        root.getChildren().removeIf(node -> {
            Bounds diamondbound = diamond.getBoundsInParent();
            if (node instanceof Sand) {
                Bounds sandBounds = node.getBoundsInParent();

                if (diamondbound.contains(sandBounds)) {
                    return true;
                }

            }
            return false;

        });

    }

    private void characterDiamondsCollision() {

        AnimationTimer time = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds bn_character = characterView.getBoundsInParent();

                finishedGame(score);

                root.getChildren().removeIf(node -> {

                    if (node instanceof Diamonds) {

                        Bounds bn_diamond = node.getBoundsInParent();

                        if (bn_diamond.intersects(bn_character.getMinX() + 40, bn_character.getMinY() + 40, 0, bn_character.getWidth() - 75, bn_character.getHeight() - 75, 0)) {

                            collectedDiamonds++;
                            pickDiamond.play();
                            // Schedule a task to stop the audio after .5 second
                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    pickDiamond.stop();
                                }
                            },
                                    500 // .5 second
                            );
                            SCORE.setText("X " + collectedDiamonds + "/" + score);

                            return true;
                        }
                    }
                    return false;

                });

            }
        };
        time.start();
    }

    private void removeSandAroundRock(Rocks rock) {
        Bounds rockBound = rock.getBoundsInParent();

        root.getChildren().removeIf(node -> {
            if (node instanceof Sand) {
                Bounds sandBounds = ((Sand) node).getBoundsInParent();

                // Check if the rock fully contains the sand tile
                boolean fullyContains = sandBounds.intersects(rockBound.getMinX() + 3, rockBound.getMinY() + 3, 0, rockBound.getWidth() - 6, rockBound.getHeight() - 6, 0);

                if (fullyContains) {
                    // Remove the sand tile if the rock fully contains it
                    return true;
                }
            }
            return false;
        });

    }

    private void rockSandCollision(Rocks rock, int l) {

        AnimationTimer Timer3 = new AnimationTimer() {
            @Override
            public void handle(long now) {
                NUM_OF_ROCKS_SPEED[l] += GRAVITY;
                // start to check every node to control all sand node in  pane
                for (int i = 0; i < root.getChildren().size(); i++) {

                    if (root.getChildren().get(i) instanceof Sand) {
                        Bounds rockBounds = rock.getBoundsInLocal();
                        if ((characterView.getBoundsInLocal().intersects(rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0, rockBounds.getWidth() - 10, rockBounds.getHeight() - 45, 0)) && (NUM_OF_ROCKS_SPEED[l] >= 6)) {//make the bounds smaller because intersects return true if they just touch
                            deathAnimation(getCycleCount());
                        }

                        if ((root.getChildren().get(i).getBoundsInLocal().intersects(rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0, rockBounds.getWidth() - 10, rockBounds.getHeight() - 50, 0)) || (characterView.getBoundsInLocal().intersects(rockBounds.getMinX() + 5, rockBounds.getMinY() + 50, 0, rockBounds.getWidth() - 10, rockBounds.getHeight() - 50, 0))) {//make the bounds smaller because intersects return true if they just touch

                            NUM_OF_ROCKS_SPEED[l] = 0;

                        }
                    }
                }
                //set y of rock
                //and return again to make speed=1 and if it intersected it will stop
                rock.setY(rock.getY() + NUM_OF_ROCKS_SPEED[l]);
            }
        };
        Timer3.start();

    }

    public void createRocks() {
        for (index = 0; index < (NUM_OF_ROCKS); index++) {
            rock[index] = new Rocks();
            rock[index].setFitWidth(ROCK_WIDTH);
            rock[index].setFitHeight(ROCK_HEIGHT);

            if (index < NUM_OF_ROCKS / 6 && index > 0) {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + index * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (BRICKS_FRAME_SIZE + ROCK_HEIGHT / 2 + SAND_SIZE) + (Math.random() * ((bricksFirstMiddle.getLayoutY() / 2 - ROCK_HEIGHT) - (BRICKS_FRAME_SIZE + ROCK_HEIGHT / 2 + SAND_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            } else if (index < NUM_OF_ROCKS * 2 / 6) {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + (index - NUM_OF_ROCKS / 6) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksFirstMiddle.getLayoutY() / 2 - ROCK_HEIGHT) + (Math.random() * ((bricksFirstMiddle.getLayoutY() - ROCK_HEIGHT) - (bricksFirstMiddle.getLayoutY() / 2 - ROCK_HEIGHT)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            } else if (index < NUM_OF_ROCKS * 3 / 6) {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + (index - NUM_OF_ROCKS * 2 / 6) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksFirstMiddle.getLayoutY() + BRICKS_SIZE + SAND_SIZE) + (Math.random() * ((bricksSecondMiddle.getLayoutY() * 3 / 4) - (bricksFirstMiddle.getLayoutY() + BRICKS_SIZE + SAND_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            } else if (index < NUM_OF_ROCKS * 4 / 6) {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + (index - NUM_OF_ROCKS * 3 / 6) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksSecondMiddle.getLayoutY() * 3 / 4) + (Math.random() * ((bricksSecondMiddle.getLayoutY() - ROCK_HEIGHT) - (bricksSecondMiddle.getLayoutY() * 3 / 4)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            } else if (index < NUM_OF_ROCKS * 5 / 6) {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + (index - NUM_OF_ROCKS * 4 / 6) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + SAND_SIZE) + (Math.random() * ((SCENE_HEIGHT * 5 / 6) - (bricksSecondMiddle.getLayoutY() + BRICKS_SIZE + SAND_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            } else {
                double randX = (BRICKS_FRAME_SIZE + ROCK_WIDTH / 2) + (index - NUM_OF_ROCKS * 5 / 6) * (SAND_SIZE + 135);
                double finalX = randX - randX % SAND_SIZE;
                double randY = (SCENE_HEIGHT * 5 / 6) + (Math.random() * ((SCENE_HEIGHT - BRICKS_FRAME_SIZE - ROCK_HEIGHT - SAND_SIZE) - (SCENE_HEIGHT * 5 / 6 - BRICKS_FRAME_SIZE)));
                double finalY = randY - randY % SAND_SIZE;
                rock[index].setX(finalX);
                rock[index].setY(finalY);
                root.getChildren().add(rock[index]);

            }

            root.getChildren().removeIf(node -> {
                Bounds diamondbound = rock[index].getBoundsInParent();
                if (node instanceof Sand) {
                    Bounds sandBounds = node.getBoundsInParent();

                    if (diamondbound.contains(sandBounds)) {
                        return true;
                    }

                }
                return false;

            });

        }

    }

    private void setRockSpeed() {
        for (int i = 0; i < NUM_OF_ROCKS_SPEED.length; i++) {

            NUM_OF_ROCKS_SPEED[i] = 5;

        }
    }

    private void characterRockCollision() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < root.getChildren().size(); i++) {

                    Bounds b = root.getChildren().get(i).getBoundsInLocal();
                    if (root.getChildren().get(i) instanceof Rocks) {

                        if (characterView.getBoundsInLocal().intersects(b.getMinX() - 30, b.getMinY() - 30, 0, b.getWidth() + 60, b.getHeight() + 60, 0)) {

                            if (characterView.getBoundsInLocal().intersects(b.getMinX() - 5, b.getMinY() + 43, 0, b.getWidth() - 84, b.getHeight() - 43 * 2, 0)) {

                                CHARACTER_Right_SPEED = 0;

                            } else {

                                CHARACTER_Right_SPEED = 5;

                            }
                            if (characterView.getBoundsInLocal().intersects(b.getMinX() + 89, b.getMinY() + 43, 0, b.getWidth() - 84, b.getHeight() - 43 * 2, 0)) {

                                CHARACTER_Left_SPEED = 0;

                            } else {

                                CHARACTER_Left_SPEED = 5;
                            }
                            if (characterView.getBoundsInLocal().intersects(b.getMinX() + 43, b.getMinY() + 89, 0, b.getWidth() - 43 * 2, b.getHeight() - 84, 0)) {

                                CHARACTER_Up_SPEED = 0;

                            } else {

                                CHARACTER_Up_SPEED = 5;
                            }
                            if (characterView.getBoundsInLocal().intersects(b.getMinX() + 43, b.getMinY() - 5, 0, b.getWidth() - 43 * 2, b.getHeight() - 84, 0)) {

                                CHARACTER_Down_SPEED = 0;

                            } else {

                                CHARACTER_Down_SPEED = 5;
                            }

                        }

                    }
                }

            }
        };
        timer.start();

    }

    private void characterRockPush() {
        for (int i = 0; i < root.getChildren().size(); i++) {
            Node node = root.getChildren().get(i);
            if (node instanceof Rocks) {
                Rocks rock = (Rocks) node;
                Bounds rockBounds = rock.getBoundsInLocal();
                Bounds characterBounds = characterView.getBoundsInLocal();

                if (characterBounds.intersects(rockBounds.getMinX() - 5, rockBounds.getMinY() + 43, 0, rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2, 0)) {

                    if ((!isSandInRockPath(rockBounds.getMinX() + 1, rockBounds.getMinY() + 20, rockBounds.getWidth(), rockBounds.getHeight() - 40)) && (movingRight == true)) {
                        rock.setX(rock.getX() + 1);
                    }
                }

                if (characterBounds.intersects(rockBounds.getMinX() + 89, rockBounds.getMinY() + 43, 0, rockBounds.getWidth() - 84, rockBounds.getHeight() - 43 * 2, 0)) {

                    if ((!isSandInRockPath(rockBounds.getMinX() - 1, rockBounds.getMinY() + 20, rockBounds.getWidth() + 1, rockBounds.getHeight() - 40)) && (movingLeft == true)) {
                        rock.setX(rock.getX() - 1);
                    }
                }
            }
        }
    }

    private boolean isSandInRockPath(double x, double y, double width, double height) {
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i) instanceof Sand) {
                Bounds sandBounds = root.getChildren().get(i).getBoundsInParent();
                if (sandBounds.intersects(x, y, width, height)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sounds() {
        String start = "build/classes/sounds/Start Sound.MP3";
        String btn = "build/classes/sounds/Button Sound.MP3";
        String diamond = "build/classes/sounds/Pick Diamond.MP3";
        String gameOver = "build/classes/sounds/Game Over.MP3";
        String victory = "build/classes/sounds/Victory.MP3";

        try {
            // Create a Media object
            Media startSound = new Media(new File(start).toURI().toString());
            Media btnSound = new Media(new File(btn).toURI().toString());
            Media diamondSound = new Media(new File(diamond).toURI().toString());
            Media loseSound = new Media(new File(gameOver).toURI().toString());
            Media winSound = new Media(new File(victory).toURI().toString());

            // Create a MediaPlayer object
            startPlay = new MediaPlayer(startSound);
            startPlay.setCycleCount(1);

            btnClick = new MediaPlayer(btnSound);
            btnClick.setCycleCount(1);

            pickDiamond = new MediaPlayer(diamondSound);
            pickDiamond.setCycleCount(MediaPlayer.INDEFINITE);

            gameOverSound = new MediaPlayer(loseSound);
            gameOverSound.setCycleCount(1);

            victorySound = new MediaPlayer(winSound);
            victorySound.setCycleCount(1);

        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
