package mycomposer.model.unit;

/**
 * Represents the 12 pitches in an octave (with enharmonic equivalency support).
 */
public enum Pitch {
  C(0, "C"), Cs(1, "C#"), Db(1, "D♭"), D(2, "D"), Ds(3, "D#"), Eb(3, "E♭"), E(4, "E"), F(5, "F"),
  Fs(6, "F#"), Gb(6, "G♭"), G(7, "G"), Gs(8, "G#"), Ab(8, "A♭"), A(9, "A"), As(10, "A#"),
  Bb(10, "B♭"), B(11, "B");

  private final int pitch;
  private final String name;

  /**
   * Constructs a tone using the given pitch with 0 corresponding to C and 11 corresponding to B.
   *
   * @param pitch the pitch of the tone
   */
  Pitch(int pitch, String name) {
    this.pitch = pitch;
    this.name = name;
  }

  /**
   * Gets the number corresponding to this pitch, where C is 0 and B is 11.
   *
   * @return the number corresponding to this pitch
   */
  public int getPitchNumber() {
    return this.pitch;
  }

  /**
   * Gets the formatted name of this pitch.
   *
   * @return the formatted name of this pitch
   */
  public String getName() {
    return this.name;
  }
}
