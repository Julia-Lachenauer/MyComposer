package mycomposer.model.unit;

import mycomposer.model.Layer;

/**
 * Represents a note or drum beat in a song. Units are sortable, and sort by increasing end beat.
 */
public interface Unit extends Comparable<Unit> {

  /**
   * Gets the start beat of this unit.
   *
   * @return the start beat of this unit
   */
  int getStartBeat();

  /**
   * Gets the end beat of this unit.
   *
   * @return the end beat of this unit
   */
  int getEndBeat();

  /**
   * Gets the duration of this unit.
   *
   * @return the duration of this unit
   */
  int getDuration();

  /**
   * Gets the MIDI number corresponding to the instrument of this unit.
   *
   * @return the MIDI number of the instrument of this unit
   */
  int getInstrument();

  /**
   * Gets the pitch of this unit. Returns {@code null} for drum beats.
   *
   * @return the pitch of this unit
   */
  Pitch getPitch();

  /**
   * Gets the octave of this unit. Returns 0 for drum beats.
   *
   * @return the octave of this unit
   */
  int getOctave();

  /**
   * Gets the MIDI pitch of this unit. Returns 0 for drum beats.
   *
   * @return the MIDI pitch of this unit
   */
  int getMIDINumber();

  /**
   * Outputs the tone and octave in the form {@code C6} for notes and "" (empty string) for drum
   * beats.
   *
   * @return a formatted string representation of the pitch of the unit
   */
  String getPitchFormat();

  /**
   * Checks if the given beat is between (inclusive) this unit's start and end beats.
   *
   * @param beat the beat to check
   * @return whether or not the given beat is within this unit
   */
  boolean beatOverlapsUnit(int beat);

  /**
   * Returns whether or not this unit is a drum beat.
   *
   * @return true if this unit is a drum beat, false otherwise
   */
  boolean isDrum();

  /**
   * Sets both the start and end bats of this unit.
   *
   * @param startBeat the new start beat
   * @param endBeat   the new end beat
   * @param layer     the layer that this unit is in
   * @throws IllegalArgumentException if the end beat is before the start beat or if the new start
   *                                  and end beats will cause this unit to overlap any other
   *                                  existing units in the given layer
   */
  void setStartEnd(int startBeat, int endBeat, Layer layer) throws IllegalArgumentException;

  /**
   * Sets the instrument of this unit to the given instrument indicated by the given MIDI instrument
   * index.
   *
   * @param instrument the MIDI index of the new instrument
   * @throws IllegalArgumentException if the MIDI index for the new instrument is invalid
   */
  void setInstrument(int instrument) throws IllegalArgumentException;

  /**
   * Sets the pitch of this unit to the given pitch. Does nothing for drum beats.
   *
   * @param pitch the new pitch for this unit
   */
  void setPitch(Pitch pitch);

  /**
   * Sets the octave of this unit to the given octave. Does nothing for drum beats.
   *
   * @param octave the new octave for this unit
   * @throws IllegalArgumentException if the new octave is less than -1 or greater than 8
   */
  void setOctave(int octave) throws IllegalArgumentException;

  /**
   * Outputs a formatted string representation of this unit in one of the two formats based on the
   * type of unit.
   *
   * <p>{@code note s e i t o}
   * <p>{@code drum s e i}
   *
   * <p>where:
   *
   * <ul>
   *   <li>{@code s} represents the start beat.</li>
   *   <li>{@code e} represents the end beat.</li>
   *   <li>{@code i} represents the instrument.</li>
   *   <li>{@code t} represents the tone.</li>
   *   <li>{@code o} represents the octave.</li>
   * </ul>
   *
   * @return a formatted string representation of this unit
   */
  String toString();
}
