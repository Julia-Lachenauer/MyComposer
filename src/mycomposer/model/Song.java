package mycomposer.model;

import java.util.List;

/**
 * Represents a song for the composer program. Songs have a tempo and are comprised of layers, each
 * of which contain the units (notes and drum beats) which make up the song. Although layers can
 * only play and store one note at a time, layers all play simultaneously, thus creating chords if
 * notes are placed in the same place within multiple layers.
 */
public interface Song {

  /**
   * Gets the tempo of this song.
   *
   * @return the tempo of this song
   */
  int getTempo();

  /**
   * Sets the tempo of this song to the given tempo.
   *
   * @param tempo the new tempo of this song
   * @throws IllegalArgumentException if the given tempo is less than 10 or greater than 500
   */
  void setTempo(int tempo) throws IllegalArgumentException;

  /**
   * Returns a list of the layers in this song.
   *
   * @return a list of the layers in this song
   */
  List<Layer> getLayers();

  /**
   * Returns the final beat of this song.
   *
   * @return the final beat of this song
   */
  int getFinalBeat();

  /**
   * Adds the given layer to this song.
   *
   * @param layer the layer to add
   */
  void addLayer(Layer layer);

  /**
   * Removes the layer with the given name from this song if such a layer is present.
   *
   * @param layer the layer to remove
   */
  void removeLayer(Layer layer);

  /**
   * Outputs a formatted string containing all data needed to represent this song, including the
   * colors and mute settings of all layers. The string returned by this method can be read into the
   * composer program using the {@code SongReader} class.
   *
   * @return a formatted string representation of this song
   */
  String songInfo();
}
