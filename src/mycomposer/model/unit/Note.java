package mycomposer.model.unit;

import java.util.Objects;

/**
 * Represents a note.
 */
public final class Note extends UnitImpl {

  private Pitch pitch;
  private int octave;

  /**
   * Constructs a note with the given start beat, end beat,, instrument, velocity, pitch, and
   * octave.
   *
   * @param startBeat  the first beat of the note
   * @param endBeat    the last beat of the note
   * @param instrument the instrument of the note (MIDI index from 0-127, inclusive)
   * @param pitch      the pitch of the note
   * @param octave     the octave of the note
   * @throws IllegalArgumentException if the start beat is after the end beat, the instrument index
   *                                  is less than 0 or greater than 127, or the octave is less than
   *                                  -1 or greater than 8
   */
  public Note(int startBeat, int endBeat, int instrument, Pitch pitch, int octave)
      throws IllegalArgumentException {
    super(startBeat, endBeat, instrument);

    if (instrument < 0 || instrument > 127) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 0-127 inclusive.");
    }

    if (octave < -1 || octave > 8) {
      throw new IllegalArgumentException("Octave must be from -1 through 8 inclusive.");
    }

    this.pitch = pitch;
    this.octave = octave;
  }

  @Override
  public void setInstrument(int instrument) throws IllegalArgumentException {
    if (instrument < 0 || instrument > 127) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 0-127 inclusive.");
    }

    super.setInstrument(instrument);
  }

  @Override
  public Pitch getPitch() {
    return this.pitch;
  }

  @Override
  public int getOctave() {
    return this.octave;
  }

  @Override
  public int getMIDINumber() {
    return this.pitch.getPitchNumber() + 12 * (this.octave + 1);
  }

  @Override
  public String getPitchFormat() {
    return this.pitch.getName() + this.octave;
  }

  @Override
  public void setPitch(Pitch pitch) {
    this.pitch = pitch;
  }

  @Override
  public void setOctave(int octave) throws IllegalArgumentException {
    if (octave < -1 || octave > 8) {
      throw new IllegalArgumentException("Octave must be from -1 through 8 inclusive.");
    }

    this.octave = octave;
  }

  @Override
  public String toString() {
    return "note " + super.toString() + " " + this.pitch.toString() + " " + this.octave;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.startBeat, this.endBeat, this.instrument, this.pitch, this.octave);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Note)) {
      return false;
    }

    Note that = (Note) obj;

    return (this.startBeat == that.startBeat && this.endBeat == that.endBeat
        && this.instrument == that.instrument && this.pitch == that.pitch
        && this.octave == that.octave);
  }
}
