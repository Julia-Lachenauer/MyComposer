package mycomposer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.swing.filechooser.FileSystemView;
import mycomposer.model.Song;
import mycomposer.model.SongImpl;

/**
 * Contains methods for managing files to be used with the composer. These include opening, saving,
 * exporting to .midi, and keeping track of the 5 most recently opened songs.
 */
public final class FileManager {

  public static Song OPEN_SONG = new SongImpl();
  public static String OPEN_FILE_PATH = null;

  public static final StringProperty OPEN_FILE_NAME_PROP = new SimpleStringProperty("Untitled");
  public static final BooleanProperty UNSAVED_CHANGES_PROP = new SimpleBooleanProperty();

  public static final String FOLDER_PATH = getFolderPath();
  private static final String RECENTS_PATH = FOLDER_PATH + "recents.txt";

  public static final FixedList<String> recentFiles = new FixedList<>(5);

  /**
   * Sets the current song to be a new song and sets the open file path to {@code null}.
   */
  public static void newFile() {
    OPEN_FILE_PATH = null;
    OPEN_FILE_NAME_PROP.set("Untitled");
    OPEN_SONG = new SongImpl();
    UNSAVED_CHANGES_PROP.set(false);
  }

  /**
   * Loads the recently opened files and attempts to open the most recent file, putting that file at
   * the top of the recent files list. If no such file is defined, opens a new song instead.
   *
   * @throws FileNotFoundException    if the most recent file does not exist
   * @throws IllegalArgumentException if the most recent file is not a valid {@code .mycomposer}
   *                                  file
   */
  public static void openLastFile() throws FileNotFoundException, IllegalArgumentException {
    loadRecentFiles();
    UNSAVED_CHANGES_PROP.set(false);

    if (recentFiles.size() > 0) {
      openFile(new File(recentFiles.get(0)));
    } else {
      newFile();
    }
  }

  /**
   * Attempts to open the given file as a {@code .mycomposer} file. Updates the recently opened file
   * list to put the given file at the top of the list and sets the open file path to the given file
   * path.
   *
   * @param file the file to open
   * @throws FileNotFoundException    if the given file does not exist
   * @throws IllegalArgumentException if the given file is not a valid {@code .mycomposer} file
   */
  public static void openFile(File file) throws FileNotFoundException, IllegalArgumentException {
    OPEN_SONG = SongReader.readSongFile(new FileReader(file));
    OPEN_FILE_PATH = file.getPath();
    OPEN_FILE_NAME_PROP.set(file.getName());

    recentFiles.addToList(OPEN_FILE_PATH);
    UNSAVED_CHANGES_PROP.set(false);
  }

  /**
   * Sets the currently open file path to be the given file and saves the current song data to the
   * given file path.
   *
   * @param file the file path to save the current song to
   * @throws IllegalArgumentException if the given file is {@code null}
   * @throws IllegalStateException    if the file write fails for any reason
   */
  public static void saveFile(File file) throws IllegalArgumentException, IllegalStateException {
    OPEN_FILE_PATH = file.getPath();
    OPEN_FILE_NAME_PROP.set(file.getName());
    saveCurrentFile();
  }

  /**
   * Attempts to save the currently opened file.
   *
   * @throws IllegalArgumentException if there is no currently open file path
   * @throws IllegalStateException    if the file write fails for any reason
   */
  public static void saveCurrentFile() throws IllegalArgumentException, IllegalStateException {
    if (OPEN_FILE_PATH == null) {
      throw new IllegalArgumentException("No currently open file path.");
    }

    try {
      FileWriter writer = new FileWriter(OPEN_FILE_PATH);
      writer.append(OPEN_SONG.songInfo());
      UNSAVED_CHANGES_PROP.set(false);
      writer.close();

      recentFiles.addToList(OPEN_FILE_PATH);
    } catch (IOException ioe) {
      throw new IllegalStateException("File write failed");
    }
  }

  /**
   * Exports the currently open song as a {@code .midi} file at the given file path.
   *
   * @param file the file to save the current song in as a MIDI file
   * @throws IllegalStateException if the file write fails for any reason
   */
  public static void exportSong(File file) throws IllegalStateException {
    MIDI.writeMIDIFile(OPEN_SONG, file);
  }

  /**
   * Attempts to open the file containing the 5 most recent files and set the recent file stack with
   * the paths in the file. If the file does not exist, it is created.
   */
  private static void loadRecentFiles() {
    try {
      Path path = Path.of(RECENTS_PATH);

      if (!Files.exists(path)) {
        Files.createFile(path);
      } else {
        Scanner scanner = new Scanner(path);

        String[] recents = new String[5];

        int index = 0;
        while (scanner.hasNextLine()) {
          recents[index] = scanner.nextLine();
          index++;
        }

        for (int i = index - 1; i >= 0; i--) {
          recentFiles.addToList(recents[i]);
        }
      }

    } catch (IOException ioe) {
      throw new IllegalStateException("File creation failed");
    }
  }

  /**
   * Iterates through the stack of recent file paths and writes them to the file containing the
   * recent files, clearing anything previously in the file. Typically, this will only need to be
   * called upon exiting the program.
   *
   * @throws IllegalStateException if the file write fails for any reason
   */
  public static void saveRecentFiles() throws IllegalStateException {
    try {
      FileWriter writer = new FileWriter(RECENTS_PATH);

      StringBuilder recentString = new StringBuilder();
      for (int i = 0; i < recentFiles.size(); i++) {
        recentString.append(recentFiles.get(i));
        if (i < recentFiles.size() - 1) {
          recentString.append("\n");
        }
      }

      writer.append(recentString.toString());
      writer.close();
    } catch (IOException ioe) {
      throw new IllegalStateException("File write failed");
    }
  }


  /**
   * Removes the given paths from the recents list if they are present.
   *
   * @param pathsToRemove the paths to remove from the recents list if present
   */
  public static void removeFromRecents(String... pathsToRemove) {
    for (String path : pathsToRemove) {
      recentFiles.remove(path);
    }
  }

  /**
   * Gets the folder path to the {@code MyComposer} directory in the Documents folder. If this
   * directory does not exist, it is created. This folder is where the file keeping track of
   * recently opened files is stored. Although it is encouraged that {@code .mycomposer} files are
   * also stored in the {@code MyComposer} directory, this is not a requirement.
   *
   * @return the folder path to the {@code MyComposer} directory
   * @throws IllegalStateException if directory creation fails for any reason
   */
  private static String getFolderPath() throws IllegalStateException {
    String folderPath =
        FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\MyComposer\\";
    try {
      if (!Files.isDirectory(Paths.get(folderPath))) {
        Files.createDirectory(Paths.get(folderPath));
      }
    } catch (IOException ioe) {
      throw new IllegalStateException("Directory creation failed");
    }

    return folderPath;
  }
}
