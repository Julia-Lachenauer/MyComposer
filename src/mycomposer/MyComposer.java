package mycomposer;

import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.stage.Stage;
import mycomposer.controller.ComposerController;
import mycomposer.model.Song;
import mycomposer.view.ComposerViewBase;

/**
 * Allows the program to run.
 */
public class MyComposer extends Application {

  /**
   * Launches the program
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    Player.initPlayer();

    try {
      FileManager.openLastFile();
    } catch (FileNotFoundException | IllegalArgumentException fnfe) {
      FileManager.newFile();
    }

    Song song = FileManager.OPEN_SONG;
    ComposerViewBase root = new ComposerViewBase();
    ComposerController controller = new ComposerController(song, root);

    controller.run(stage);
  }
}
