package mycomposer.controller;

import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mycomposer.FileManager;
import mycomposer.Player;
import mycomposer.model.Layer;
import mycomposer.model.Song;
import mycomposer.view.ComposerViewBase;
import mycomposer.view.SaveDialogue;

/**
 * A controller which allows for user interactions with the composer program.
 */
public class ComposerController implements Features {

  private Song song;
  private final ComposerViewBase view;

  /**
   * Constructs a controller for managing the composer, supplies the MIDI player with the given
   * song, and initializes the view.
   *
   * @param song the song to display
   * @param view the composer view
   * @throws IllegalArgumentException if the song or the view are null
   */
  public ComposerController(Song song, ComposerViewBase view) throws IllegalArgumentException {
    if (song == null || view == null) {
      throw new IllegalArgumentException("Song and view cannot be null.");
    }

    this.song = song;
    this.view = view;
    Player.setSong(song, true);

    this.view.setFeatures(this);
    this.view.setPlaybackControls();
    this.view.refresh(song.getLayers(), true);
  }

  @Override
  public void setSong() {
    this.song = FileManager.OPEN_SONG;
    Player.setSong(this.song, true);

    this.view.refresh(this.song.getLayers(), true);
  }

  @Override
  public void refresh() {
    this.refreshSong();
  }

  @Override
  public void setTempo(int tempo) throws IllegalArgumentException {
    if (tempo < 10 || tempo > 500) {
      throw new IllegalArgumentException("Tempo must be between 10 and 500 BPM inclusive.");
    }
    FileManager.UNSAVED_CHANGES_PROP.set(true);

    this.song.setTempo(tempo);
    Player.setTempo(tempo);
  }

  @Override
  public void addLayer(Layer layer) {
    FileManager.UNSAVED_CHANGES_PROP.set(true);

    this.song.addLayer(layer);
    this.refreshSong();
  }

  @Override
  public void removeLayer(Layer layer) {
    FileManager.UNSAVED_CHANGES_PROP.set(true);

    this.song.removeLayer(layer);
    this.refreshSong();
  }

  /**
   * Refreshes the view and supplies the MIDI player with the current song.
   */
  private void refreshSong() {
    Player.setSong(this.song, false);
    this.view.refresh(this.song.getLayers(), false);
  }

  /**
   * Sets up the application window and then runs the program.
   *
   * @param stage the stage to display
   */
  public void run(Stage stage) {
    Rectangle2D bounds = Screen.getPrimary().getBounds();

    double width = bounds.getWidth() * 0.9;
    double height = bounds.getHeight() * 0.9;

    Scene scene = new Scene(this.view, width, height);

    scene.getStylesheets().add("mycomposer/view/Stylesheet.css");
    stage.setScene(scene);
    stage.setResizable(true);
    stage.getIcons().add(new Image("/resources/icon.png"));

    Runnable saveAs = () -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save As");
      fileChooser.setInitialDirectory(new File(FileManager.FOLDER_PATH));
      fileChooser.getExtensionFilters()
          .add(new ExtensionFilter("MyComposer Files", "*.mycomposer"));

      File file = fileChooser.showSaveDialog(stage);
      if (file != null) {
        FileManager.saveFile(file);
      }
    };

    stage.setOnCloseRequest(e -> {
      this.view.pause();
      if (FileManager.UNSAVED_CHANGES_PROP.get()) {
        e.consume();
        SaveDialogue.showSaveDialogue(stage, saveAs, () -> {
          Player.closePlayer();
          FileManager.saveRecentFiles();
          stage.close();
        });
      } else {
        Player.closePlayer();
        FileManager.saveRecentFiles();
      }
    });

    stage.titleProperty().bind(
        Bindings.when(FileManager.UNSAVED_CHANGES_PROP).then("*").otherwise("")
            .concat(FileManager.OPEN_FILE_NAME_PROP).concat(" - MyComposer"));

    stage.show();

    this.view.setFileMenu();
  }
}
