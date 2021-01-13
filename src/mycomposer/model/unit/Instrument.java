package mycomposer.model.unit;

/**
 * Represents the 128 MIDI instruments supported by this program.
 * <p>https://www.midi.org/specifications-old/item/gm-level-1-sound-set
 */
public enum Instrument implements Sound {
  AcousticGrandPiano("Acoustic Grand Piano"), BrightAcousticPiano(
      "Bright Acoustic Piano"), ElectricGrandPiano("Electric Grand Piano"), HonkytonkPiano(
      "Honky-tonk Piano"), EPiano1("Electric Piano 1"), EPiano2("Electric Piano 2"), Harpsichord(
      "Harpsichord"), Clavi("Clavi"), Celesta("Celesta"), Glockenspiel("Glockenspiel"), MusicBox(
      "Music Box"), Vibraphone("Vibraphone"), Marimba("Marimba"), Xylophone(
      "Xylophone"), TubularBells("Tubular Bells"), Dulcimer("Dulcimer"), DrawbarOrgan(
      "Drawbar Organ"), PercussiveOrgan("Percussive Organ"), RockOrgan("Rock Organ"), ChurchOrgan(
      "Church Organ"), ReedOrgan("Reed Organ"), Accordion("Accordion"), Harmonica(
      "Harmonica"), TangoAccordion("Tango Accordion"), AcousticGuitarNylon(
      "Acoustic Guitar (Nylon)"), AcousticGuitarSteel("Acoustic Guitar (Steel)"), EGuitarJazz(
      "Electric Guitar (Jazz)"), EGuitarClean("Electric Guitar (Clean)"), EGuitarMuted(
      "Electric Guitar (Muted)"), OverdrivenGuitar("Overdriven Guitar"), DistortionGuitar(
      "Distortion Guitar"), GuitarHarmonics("Guitar Harmonics"), AcousticBass(
      "Acoustic Bass"), EBassFinger("Electric Bass (Finger)"), EBassPick(
      "Electric Bass (Pick)"), FretlessBass("Fretless Bass"), SlapBass1("Slap Bass 1"), SlapBass2(
      "Slap Bass 2"), SynthBass1("Synth Bass 1"), SynthBass2("Synth Bass 2"), Violin(
      "Violin"), Viola("Viola"), Cello("Cello"), Contabass("Contrabass"), TremoloStrings(
      "Tremolo Strings"), PizzicatoStrings("Pizzicato Strings"), OrchestralHarp(
      "Orchestral Harp"), Timpani("Timpani"), StringEnsemble1("String Ensemble 1"), StringEnsemble2(
      "String Ensemble 2"), SynthStrings1("Synth Strings 1"), SynthStrings2(
      "Synth Strings 2"), ChoirAahs("Choir Aahs"), ChoirOohs("Choir Oohs"), SynthVoice(
      "Synth Voice"), OrchestraHit("Orchestra Hit"), Trumpet("Trumpet"), Trombone("Trombone"), Tuba(
      "Tuba"), MutedTrumpet("Muted Trumpet"), FrenchHorn("French Horn"), BrassSection(
      "Brass Section"), SynthBrass1("Synth Brass 1"), SynthBrass2("Synth Brass 2"), SopranoSax(
      "Soprano Sax"), AltoSax("Alto Sax"), TenorSax("Tenor Sax"), BaritoneSax("Baritone Sax"), Oboe(
      "Oboe"), EnglishHorn("English Horn"), Bassoon("Bassoon"), Clarinet("Clarinet"), Piccolo(
      "Piccolo"), Flute("Flute"), Recorder("Recorder"), PanFlute("Pan Flute"), BlownBottle(
      "Blown Bottle"), Shakuhachi("Shakuhachi"), Whistle("Whistle"), Ocarina(
      "Ocarina"), Lead1Square("Lead 1 (Square)"), Lead1Sawtooth("Lead 2 (Sawtooth)"), Lead3Square(
      "Lead 3 (Calliope)"), Lead4Chiff("Lead 4 (Chiff)"), Lead5Charang(
      "Lead 5 (Charang)"), Lead6Voice("Lead 6 (Voice)"), Lead7Fifths(
      "Lead 7 (Fifths)"), Lead8BassLead("Lead 8 (Bass + Lead)"), Pad1NewAge(
      "Pad 1 (New Age"), Pad2Warm("Pad 2 (Warm)"), Pad3Polysynth("Pad 3 (Polysynth)"), Pad4Choir(
      "Pad 4 (Choir)"), Pad5Bowed("Pad 5 (Bowed)"), Pad6Metallic("Pad 6 (Metallic)"), Pad7Halo(
      "Pad 7 (Halo)"), Pad8Sweep("Pad 8 (Sweep)"), FX1Rain("FX 1 (Rain)"), FX2Soundtrack(
      "FX 2 (Soundtrack)"), FX3Crystal("FX 3 (Crystal)"), FX4Atmosphere(
      "FX 4 (Atmosphere)"), FX5Brightness("FX 5 (Brightness)"), FX6Goblins(
      "FX 6 (Goblins)"), FX7Echoes("FX 7 (Echoes)"), FX8SciFi("FX 8 (Sci-Fi)"), Sitar(
      "Sitar"), Banjo("Banjo"), Shamisen("Shamisen"), Koto("Koto"), Kalimba("Kalimba"), BagPipe(
      "Bag Pipe"), Fiddle("Fiddle"), Shanai("Shanai"), TinkleBell("Tinkle Bell"), Agogo(
      "Agogo"), SteelDrums("Steel Drums"), Woodblock("Woodblock"), TaikoDrum(
      "Taiko Drum"), MelodicTom("Melodic Tom"), SynthDrum("Synth Drum"), ReverseCymbal(
      "Reverse Cymbal"), GuitarFretNoise("Guitar Fret Noise"), BreathNoise(
      "Breath Noise"), Seashore("Seashore"), BirdTweet("Bird Tweet"), TelephoneRing(
      "Telephone Ring"), Helicopter("Helicopter"), Applause("Applause"), Gunshot("Gunshot");

  private final String name;

  /**
   * Constructs an instrument with the given name.
   *
   * @param name the name of the instrument
   */
  Instrument(String name) {
    this.name = name;
  }

  @Override
  public int getIndex() {
    return this.ordinal();
  }

  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Gets the sound at the given MIDI index.
   *
   * @param soundIndex the MIDI index to get the sound for
   * @return the sound at the given MIDI index (MIDI index from 0-127, inclusive)
   * @throws IllegalArgumentException if the given MIDI index is less than 0 or greater than 127
   */
  public static Instrument getSound(int soundIndex) throws IllegalArgumentException {
    if (soundIndex < 0 || soundIndex > 127) {
      throw new IllegalArgumentException("Instrument MIDI index must be from 0-127 inclusive.");
    }

    return Instrument.values()[soundIndex];
  }
}
