package mycomposer.model.unit;

/**
 * Represents the 47 MIDI percussion instruments supported by this program.
 * <p>https://www.midi.org/specifications-old/item/gm-level-1-sound-set
 */
public enum Percussion implements Sound {

  AcousticBassDrum("Acoustic Bass Drum"), BassDrum1("Bass Drum 1"), SideStick(
      "Side Stick"), AcousticSnare("Acoustic Snare"), HandClap("Hand Clap"), ElectricSnare(
      "Electric Snare"), LowFloorTom("Low Floor Tom"), ClosedHiHat("Closed Hi Hat"), HighFloorTom(
      "High Floor Tom"), PedalHiHat("Pedal Hi-Hat"), LowTom("Low Tom"), OpenHiHat(
      "Open Hi-Hat"), LowMidTom("Low-Mid Tom"), HiMidTom("Hi-Mid Tom"), CrashCymbal1(
      "Crash Cymbal 1"), HighTom("High Tom"), RideCymbal1("Ride Cymbal 1"), ChineseCymbal(
      "Chinese Cymbal"), RideBell("Ride Bell"), Tambourine("Tambourine"), SplashCymbal(
      "Splash Cymbal"), Cowbell("Cowbell"), CrashCymbal2("Crash Cymbal 2"), Vibraslap(
      "Vibraslap"), RideCymbal2("Ride Cymbal 2"), HiBongo("Hi Bongo"), LowBongo(
      "Low Bongo"), MuteHiConga("Mute Hi Conga"), OpenHiConga("Open Hi Conga"), LowConga(
      "Low Conga"), HighTimbale("High Timbale"), LowTimbale("Low Timbale"), HighAgogo(
      "High Agogo"), LowAgogo("Low Agogo"), Cabasa("Cabasa"), Maracas("Maracas"), ShortWhistle(
      "Short Whistle"), LongWhistle("Long Whistle"), ShortGuiro("Short Guiro"), LongGuiro(
      "Long Guiro"), Claves("Claves"), HiWoodBlock("Hi Wood Block"), LowWoodBlock(
      "Low Wood Block"), MuteCuica("Mute Cuica"), OpenCuica("Open Cuica"), MuteTriangle(
      "Mute Triangle"), OpenTriangle("Open Triangle");

  private final String name;

  /**
   * Constructs a percussion instrument with the given name.
   *
   * @param name the name of the percussion instrument
   */
  Percussion(String name) {
    this.name = name;
  }

  @Override
  public int getIndex() {
    return this.ordinal() + 34;
  }

  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Gets the sound at the given MIDI index.
   *
   * @param soundIndex the MIDI index to get the sound for
   * @return the sound at the given MIDI index (MIDI index from 34-80, inclusive)
   * @throws IllegalArgumentException if the given MIDI index is less than 34 or greater than 80
   */
  public static Percussion getSound(int soundIndex) throws IllegalArgumentException {
    if (soundIndex < 34 || soundIndex > 80) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 34-80 inclusive.");
    }

    return Percussion.values()[soundIndex - 34];
  }
}
