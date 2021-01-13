package mycomposer.model.unit;

import mycomposer.model.Layer;

/**
 * Represents a unit that makes sound (either a note or a drum beat).
 */
public class UnitImpl implements Unit {

  protected int startBeat;
  protected int endBeat;
  protected int instrument;

  /**
   * Constructs a sound unit with the given start beat, end beat, instrument, and velocity.
   *
   * @param startBeat  the first beat of the note
   * @param endBeat    the last beat of the note
   * @param instrument the instrument of the note (given by the valid MIDI index)
   * @throws IllegalArgumentException if the start beat is after the end beat, the instrument index
   *                                  is invalid
   */
  protected UnitImpl(int startBeat, int endBeat, int instrument)
      throws IllegalArgumentException {
    if (startBeat > endBeat) {
      throw new IllegalArgumentException(
          "Start beat must be before or at the same time as the end beat.");
    }

    this.startBeat = startBeat;
    this.endBeat = endBeat;
    this.instrument = instrument;
  }

  @Override
  public int getStartBeat() {
    return this.startBeat;
  }

  @Override
  public int getEndBeat() {
    return this.endBeat;
  }

  @Override
  public int getDuration() {
    return this.endBeat - this.startBeat + 1;
  }

  @Override
  public int getInstrument() {
    return this.instrument;
  }

  @Override
  public Pitch getPitch() {
    return null;
  }

  @Override
  public int getOctave() {
    return 0;
  }

  @Override
  public int getMIDINumber() {
    return 0;
  }

  @Override
  public String getPitchFormat() {
    return "";
  }

  @Override
  public boolean beatOverlapsUnit(int beat) {
    return this.startBeat <= beat && this.endBeat >= beat;
  }

  @Override
  public boolean isDrum() {
    return false;
  }

  @Override
  public void setStartEnd(int startBeat, int endBeat, Layer layer) throws IllegalArgumentException {
    if (endBeat < startBeat) {
      throw new IllegalArgumentException("New end beat cannot be before start beat.");
    }

    if (layer.rangeOverlapsUnit(startBeat, endBeat, this)) {
      throw new IllegalArgumentException("Unit cannot overlap existing units.");
    }

    layer.checkEditUnit(this, endBeat);

    this.startBeat = startBeat;
    this.endBeat = endBeat;
  }

  @Override
  public void setInstrument(int instrument) throws IllegalArgumentException {
    this.instrument = instrument;
  }

  @Override
  public void setPitch(Pitch pitch) {
  }

  @Override
  public void setOctave(int octave) throws IllegalArgumentException {
  }

  @Override
  public String toString() {
    return String.format("%d %d %d", this.startBeat, this.endBeat, this.instrument);
  }

  @Override
  public int compareTo(Unit unit) {
    return this.endBeat - unit.getEndBeat();
  }
}
