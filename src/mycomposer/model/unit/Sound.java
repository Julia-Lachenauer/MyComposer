package mycomposer.model.unit;

/**
 * Represents an instrument capable of making sound, such as an instrument or a percussion
 * instrument. Instruments have names and MIDI indexes.
 */
public interface Sound {

  /**
   * Gets the index of this instrument, which corresponds to its MIDI index.
   *
   * @return the index of this instrument
   */
  int getIndex();

  /**
   * Gets the name of this instrument.
   *
   * @return the name of this instrument
   */
  String getName();
}
