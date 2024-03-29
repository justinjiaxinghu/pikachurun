package TheGame;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //create pikachu
        Image player = new Image(new FileInputStream("resources/pikachu.gif"));
        ImageView pikachu = new ImageView(player);
        pikachu.setPreserveRatio(true);
        pikachu.setFitHeight(50);
        pikachu.setY(450);

        //create gloom enemy
        Image obstacle = new Image(new FileInputStream("resources/gloom.gif"));
        final ImageView[] gloom = {new ImageView(obstacle)};

        //create background
        Image backdrop = new Image(new FileInputStream("resources/background.jpg"));
        ImageView background = new ImageView(backdrop);
        background.setPreserveRatio(true);
        background.setFitWidth(1000);

        //create health bar
        Rectangle healthBar = new Rectangle(200, 20, Color.LIGHTGREEN);
        Rectangle border = new Rectangle(200, 20, Color.TRANSPARENT);
        healthBar.setX(15);
        healthBar.setY(15);
        border.setX(15);
        border.setY(15);
        border.setStrokeWidth(2);
        border.setStroke(Color.WHITE);

        //create canvas
        Pane canvas = new Pane(background);
        canvas.getChildren().addAll(pikachu, border, healthBar);
        Scene s = new Scene(canvas);

        primaryStage.setScene(s);
        primaryStage.show();


        Random spawnTime = new Random();

        ArrayList<ImageView> enemies = new ArrayList<>();

        Timeline spawnEnemies = new Timeline(new KeyFrame(Duration.seconds(4), ev -> {
            gloom[0] = new ImageView(obstacle);
            gloom[0].setPreserveRatio(true);
            gloom[0].setFitHeight(50);
            gloom[0].setX(1100);
            gloom[0].setY(450);
            canvas.getChildren().add(gloom[0]);
            enemies.add(gloom[0]);
        }));
        spawnEnemies.setCycleCount(Animation.INDEFINITE);
        spawnEnemies.play();

        final int[] gravity = {0};
        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            double randomSpawnTime = spawnTime.nextInt(5) + 1;
            @Override
            public void handle(KeyEvent keyEvent) {
                double yPos = pikachu.getY();
                    if (keyEvent.getCode() == KeyCode.RIGHT) {
                        double xPos = pikachu.getX();
                        pikachu.setX(xPos + 20);
                    } else if (keyEvent.getCode() == KeyCode.LEFT) {
                        double xPos = pikachu.getX();
                        pikachu.setX(xPos - 20);
                    } else if (keyEvent.getCode() == KeyCode.SPACE && gravity[0] == 0) {
                        AnimationTimer jump = new AnimationTimer() {
                            @Override
                            public void handle(long l) {
                                pikachu.setY(pikachu.getY() - 15 + gravity[0]);
                                gravity[0]++;
                                if (pikachu.getY() == yPos) {
                                    this.stop();
                                    gravity[0] = 0;
                                }
                            }
                        };
                        spawnEnemies.setRate(randomSpawnTime);
                        randomSpawnTime = spawnTime.nextInt(8) + 2;
                        jump.start();
                    }
                }
        });

        //spawn enemies and handle collisions
        AnimationTimer spawnEnemy = new AnimationTimer() {
            @Override
            public void handle(long l) {
                for (ImageView v : enemies) {
                    v.setX(v.getX() - 10);

                    if (v.intersects(pikachu.getLayoutBounds())) {
                        FadeTransition damageTaken = new FadeTransition(Duration.seconds(0.1), pikachu);
                        damageTaken.setFromValue(1.0);
                        damageTaken.setToValue(0.0);
                        damageTaken.setCycleCount(Animation.INDEFINITE);
                        damageTaken.play();
                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        pikachu.setOpacity(1.0);
                                        damageTaken.stop();
                                    }
                                }, 500
                        );
                        v.setY(100000);
                        canvas.getChildren().remove(v);
                        healthBar.setWidth(healthBar.getWidth() - 50);
                    }
                }
            }
        };
        spawnEnemy.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
