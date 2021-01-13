package mycomposer.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.animation.Timeline;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import mycomposer.FileManager;
import mycomposer.FixedList;
import mycomposer.Player;

/**
 * Represents a file menu allowing the user to create a new song, open a song, open a recent song,
 * save their song, and export their song to a .midi file.
 */
public class FileMenu extends Menu {

  private final Stage originStage;
  private final Runnable setSong;
  private Timeline timeline;

  private final Menu openRecent;

  /**
   * Constructs a file menu.
   *
   * @param originStage the stage where the menu is displayed
   * @param setSong     an operation to set the composer and the MIDI player with the song currently
   *                    open in the file manager
   * @param timeline    the timeline controlling the playback of the song in the composer
   */
  public FileMenu(Stage originStage, Runnable setSong, Timeline timeline) {
    this.originStage = originStage;
    this.setSong = setSong;
    this.timeline = timeline;

    this.setText("File");

    MenuItem newSong = new MenuItem("New Song");
    newSong.setOnAction(e -> this.newSong());
    newSong.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

    this.openRecent = new Menu("Open Recent");
    this.updateOpenRecent();

    MenuItem open = new MenuItem("Open");
    open.setOnAction(e -> this.openPopup());
    open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

    MenuItem save = new MenuItem("Save");
    save.setOnAction(e -> this.save());
    save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

    MenuItem saveAs = new MenuItem("Save As");
    saveAs.setOnAction(e -> this.saveAs());
    saveAs.setAccelerator(
        new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

    MenuItem export = new MenuItem("Export to .midi");
    export.setOnAction(e -> this.exportAs());

    this.getItems().addAll(newSong, open, this.openRecent, save, saveAs, export);
  }

  /**
   * Sets the timeline to the given timeline.
   *
   * @param timeline the timeline to set
   */
  public void setTimeline(Timeline timeline) {
    this.timeline = timeline;
  }

  /**
   * Pauses the song, then checks for any unsaved changes. Then, a new, empty song is created and
   * the composer and MIDI player are set to use that new song.
   */
  private void newSong() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
    }

    if (FileManager.UNSAVED_CHANGES_PROP.get()) {
      SaveDialogue.showSaveDialogue(this.originStage, this::openSaveAsWindow, () -> {
        FileManager.newFile();
        this.setSong.run();
      });
    } else {
      FileManager.newFile();
      this.setSong.run();
    }
  }

  /**
   * Updates the recently opened files menu.
   */
  private void updateOpenRecent() {
    this.openRecent.getItems().clear();

    FixedList<String> recents = FileManager.recentFiles;

    String[] recentsToRemove = new String[5];

    for (int i = 0; i < recents.size(); i++) {
      String recentFileName = recents.get(i);

      if (!Files.exists(Path.of(recentFileName))) {
        recentsToRemove[i] = recentFileName;
      } else {
        File recentFile = new File(recentFileName);

        CustomMenuItem item = new CustomMenuItem();
        item.setContent(new Label(recentFile.getName()));

        item.setOnAction(e -> {
          Player.pause();
          this.timeline.stop();

          Runnable openRecentFile = () -> this.openFile(recentFile);

          if (FileManager.UNSAVED_CHANGES_PROP.get()) {
            SaveDialogue.showSaveDialogue(this.originStage, this::openSaveAsWindow, openRecentFile);
          } else {
            openRecentFile.run();
          }
        });

        Tooltip eventToolTip = new Tooltip(recentFile.getPath());
        eventToolTip.setShowDelay(new Duration(200));
        Tooltip.install(item.getContent(), eventToolTip);

        this.openRecent.getItems().add(item);
      }
    }

    FileManager.removeFromRecents(recentsToRemove);
  }

  /**
   * Attempts to open the given file and set the composer and the MIDI player to use that song. If
   * the song does not exist or is an invalid file, a new, empty song is created and the recent
   * files are updated.
   *
   * @param file the file to attempt to open
   */
  private void openFile(File file) {
    try {
      FileManager.openFile(file);
      this.setSong.run();
      this.updateOpenRecent();
    } catch (FileNotFoundException fnfe) {
      FileManager.removeFromRecents(file.getPath());
      ErrorPopup.showError(this.originStage, "Error: This file does not exist.");

      FileManager.newFile();
      this.setSong.run();
      this.updateOpenRecent();
    } catch (IllegalArgumentException iae) {
      ErrorPopup.showError(this.originStage, "Error: Invalid .mycomposer file.");

      FileManager.newFile();
      this.setSong.run();
      this.updateOpenRecent();
    }
  }


  /**
   * Pauses the song and opens the open file window and allows the user to open a .mycomposer file.
   */
  private void openPopup() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
    }

    Runnable openSongPopup = () -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Song");
      fileChooser.setInitialDirectory(new File(FileManager.FOLDER_PATH));
      fileChooser.getExtensionFilters()
          .add(new ExtensionFilter("MyComposer Files", "*.mycomposer"));

      File file = fileChooser.showOpenDialog(this.originStage);
      if (file != null) {
        this.openFile(file);
        this.setSong.run();
        this.updateOpenRecent();
      }
    };

    if (FileManager.UNSAVED_CHANGES_PROP.get()) {
      SaveDialogue.showSaveDialogue(this.originStage, this::openSaveAsWindow, openSongPopup);
    } else {
      openSongPopup.run();
    }
  }

  /**
   * If the current song is new (meaning it has never been saved before), the "Save As" menu will
   * display. Otherwise, the current song data is saved in the currently open file path in the file
   * manager.
   */
  private void save() {
    if (FileManager.OPEN_FILE_PATH != null) {
      FileManager.saveCurrentFile();
    } else {
      this.saveAs();
    }
  }

  /**
   * Pauses the song and opens the "Save As" window.
   */
  private void saveAs() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
    }

    this.openSaveAsWindow();
  }

  /**
   * Opens the "Save As" window, which allows the user to save the current song with a chosen file
   * name at a chosen location.
   */
  private void openSaveAsWindow() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save As");
    fileChooser.setInitialDirectory(new File(FileManager.FOLDER_PATH));
    fileChooser.getExtensionFilters().add(new ExtensionFilter("MyComposer Files", "*.mycomposer"));

    File file = fileChooser.showSaveDialog(this.originStage);
    if (file != null) {
      FileManager.saveFile(file);
      this.updateOpenRecent();
    }
  }

  /**
   * Pauses the song and opens the window to allow the user to export the song as a .midi file. If
   * there are unsaved changes, the user is prompted to save their changes before exporting the
   * song.
   */
  private void exportAs() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
    }

    if (Player.FINAL_BEAT > 0) {
      if (FileManager.UNSAVED_CHANGES_PROP.get()) {
        SaveDialogue.showSaveDialogue(this.originStage, this::openSaveAsWindow, this::exportWindow);
      } else {
        this.exportWindow();
      }
    } else {
      ErrorPopup.showError(this.originStage, "Cannot export an empty song.");
    }
  }

  /**
   * Opens the window to allow the user to export their song as a .midi file.
   */
  private void exportWindow() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export As");
    fileChooser.setInitialDirectory(new File(FileManager.FOLDER_PATH));
    fileChooser.getExtensionFilters().add(new ExtensionFilter("MIDI Files", "*.midi", "*.mid"));

    File file = fileChooser.showSaveDialog(this.originStage);
    if (file != null) {
      FileManager.exportSong(file);

      this.openFile(file);
      this.setSong.run();
    }
  }
}
