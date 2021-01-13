package mycomposer.view;

import java.util.List;
import mycomposer.controller.Features;
import mycomposer.model.Layer;

/**
 * Contains all functions needed to set up the user interface for the composer.
 */
public interface ComposerView {

  /**
   * Supplies the GUI with the given set of features in order to add functionality to the view.
   *
   * @param features an implementation of the Features interface defining methods to add
   *                 functionality to the program
   */
  void setFeatures(Features features);

  /**
   * Adds a bar at the bottom of the interface to provide playback controls to the users.
   */
  void setPlaybackControls();

  /**
   * Adds a file menu to the top of the view to allow the user to manage their composer files. This
   * must be called after the stage is shown.
   */
  void setFileMenu();

  /**
   * Pauses the song.
   */
  void pause();

  /**
   * Refreshes the GUI.
   *
   * @param layers      a map pairing the name of each layer in the song to that layer
   * @param resetScroll whether or not to reset the scroll back to the beginning of the song
   */
  void refresh(List<Layer> layers, boolean resetScroll);
}
