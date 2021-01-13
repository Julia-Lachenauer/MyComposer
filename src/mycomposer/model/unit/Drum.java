package mycomposer.model.unit;

import java.util.Objects;

/**
 * Represents a drum beat.
 */
public final class Drum extends UnitImpl {

  /**
   * Constructs a new drum beat with the given start beat, end beat, instrument, and velocity.
   *
   * @param startBeat  the first beat of the drum beat
   * @param endBeat    the last beat of the drum beat
   * @param instrument the instrument of the drum beat (MIDI index from 34-80, inclusive)
   * @throws IllegalArgumentException if the start beat is after the end beat or the instrument
   *                                  index is less than 34 or greater than 80
   */
  public Drum(int startBeat, int endBeat, int instrument) throws IllegalArgumentException {
    super(startBeat, endBeat, instrument);

    if (instrument < 34 || instrument > 80) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 34-80 inclusive.");
    }
  }

  @Override
  public boolean isDrum() {
    return true;
  }

  @Override
  public void setInstrument(int instrument) throws IllegalArgumentException {
    if (instrument < 34 || instrument > 80) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 34 to 80 inclusive.");
    }

    super.setInstrument(instrument);
  }

  @Override
  public String toString() {
    return "drum " + super.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.startBeat, this.endBeat, this.instrument);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Drum)) {
      return false;
    }

    Drum that = (Drum) obj;

    return (this.startBeat == that.startBeat && this.endBeat == that.endBeat
        && this.instrument == that.instrument);
  }
}
