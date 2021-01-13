package mycomposer.controller;

import mycomposer.model.Layer;

/**
 * Features that allow users to interact with the composer program.
 */
public interface Features {

  /**
   * Sets the composer with the song currently open in the file manager and supplies the MIDI player
   * with that song.
   */
  void setSong();

  /**
   * Refreshes the view and supplies the MIDI player with the current song.
   */
  void refresh();

  /**
   * Sets the tempo of the song and the tempo of the MIDI player to the given tempo.
   *
   * @param tempo the new tempo for the song
   * @throws IllegalArgumentException if the tempo is less than 10 or greater than 500
   */
  void setTempo(int tempo) throws IllegalArgumentException;

  /**
   * Adds the given layer to the song.
   *
   * @param layer the layer to add
   */
  void addLayer(Layer layer);

  /**
   * Removes the given layer from the song if that layer is present.
   *
   * @param layer the layer to remove
   */
  void removeLayer(Layer layer);
}
