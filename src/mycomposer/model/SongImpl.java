package mycomposer.model;

import java.util.ArrayList;
import java.util.List;
import mycomposer.model.unit.Unit;

/**
 * Represents an implementation of the song implementing all methods to allow the user to create and
 * modify songs.
 */
public class SongImpl implements Song {

  private final List<Layer> layers;
  private int tempo;

  /**
   * Constructs a new song with a default tempo of 60 BPM (beats per minute) and an empty list of
   * layers.
   */
  public SongImpl() {
    this.layers = new ArrayList<>();
    this.tempo = 60;
  }

  @Override
  public int getTempo() {
    return this.tempo;
  }

  @Override
  public void setTempo(int tempo) throws IllegalArgumentException {
    if (tempo < 10 || tempo > 500) {
      throw new IllegalArgumentException("Tempo must be between 10 and 500 BPM inclusive.");
    }

    this.tempo = tempo;
  }

  @Override
  public List<Layer> getLayers() {
    return new ArrayList<>(this.layers);
  }

  @Override
  public int getFinalBeat() {
    int longestTrack = 0;

    for (Layer layer : this.layers) {
      int beatsInTrack = layer.getFinalBeat();
      if (beatsInTrack > longestTrack) {
        longestTrack = beatsInTrack;
      }
    }

    return longestTrack;
  }

  @Override
  public void addLayer(Layer layer) {
    this.layers.add(layer);
  }

  @Override
  public void removeLayer(Layer layer) {
    this.layers.remove(layer);
  }

  @Override
  public String songInfo() {
    StringBuilder builder = new StringBuilder();

    builder.append("tempo ").append(this.tempo).append("\n\n");

    for (Layer layer : this.layers) {
      String layerName = layer.getName();

      if (layer.isMute()) {
        builder.append("*");
      }

      builder.append("layer ").append(layer.getColor()).append(" ").append(layer.getVolume())
          .append(" ").append(layerName).append("\n");
    }

    for (Layer layer : this.layers) {
      layer.sortUnits();

      String layerName = layer.getName();

      builder.append("--------------------------").append("\n\n").append(layerName).append("\n");

      for (Unit unit : layer.getUnits()) {
        builder.append("\n");
        builder.append(unit.toString());
      }

      builder.append("\n");
    }

    return builder.toString();
  }
}
