package games;

import games.ACGame;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * This is the main program, it is basically boilerplate to create
 * an animated scene.
 * 
 * @author Anirudh Jonnavithula, Carolyn Yao, Robert Duvall
 */
public class ACGameMain extends Application {
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 60;
    private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    private ACGame myGame;


    /**
     * Set things up at the beginning.
     */
    @Override
    public void start (Stage s) {
        // create your own game here
        myGame = new ACGame();
        s.setTitle(myGame.getTitle());

        Scene scene = myGame.init(SIZE, SIZE);
        s.setScene(scene);
        s.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                                      e -> myGame.step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
