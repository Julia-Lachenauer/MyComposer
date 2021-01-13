package mycomposer;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import mycomposer.model.Layer;
import mycomposer.model.Song;
import mycomposer.model.unit.Unit;

/**
 * Represents a MIDI player to play songs in the program.
 */
public final class Player {

  private static Synthesizer synth;
  private static Sequencer sequencer;
  private static MidiChannel[] channels;

  public static int BEAT = 0;
  public static int FINAL_BEAT = 0;

  public static final DoubleProperty TEMPO_PROP = new SimpleDoubleProperty();
  public static final BooleanProperty PLAY_PROP = new SimpleBooleanProperty();

  /**
   * Creates a new MIDI player with a synthesizer for playing individual notes and a sequencer for
   * playing whole songs. This method should only be called once during the runtime of this program
   * as it opens a MIDI synthesizer and sequencer which are not closed until {@link #closePlayer()}
   * is called.
   *
   * @throws IllegalStateException if the MIDI components (synthesizer or sequencer) cannot be
   *                               opened for any reason
   */
  public static void initPlayer() throws IllegalStateException {
    try {
      synth = MidiSystem.getSynthesizer();
      synth.open();
      synth.loadAllInstruments(synth.getDefaultSoundbank());

      channels = synth.getChannels();

      sequencer = MidiSystem.getSequencer();
      sequencer.open();

    } catch (MidiUnavailableException e) {
      throw new IllegalStateException("Error getting synthesizer");
    }
  }

  /**
   * Loads the given song into the MIDI sequence and sets this MIDI player's sequencer to use that
   * sequence. This does not actually play the given song.
   *
   * @param song      the song to set this player to use
   * @param resetBeat whether or not to reset the beat back to 0 upon loading the song
   * @throws IllegalArgumentException if invalid MIDI data is encountered
   */
  public static void setSong(Song song, boolean resetBeat) throws IllegalArgumentException {
    try {

      FINAL_BEAT = song.getFinalBeat();
      TEMPO_PROP.setValue(song.getTempo());

      if (resetBeat) {
        BEAT = 0;
      } else {
        if (BEAT > FINAL_BEAT) {
          BEAT = FINAL_BEAT;
        }
      }

      Sequence sequence = MIDI.loadSong(song);
      sequencer.setSequence(sequence);

      List<Layer> layerList = song.getLayers();

      for (int i = 0; i < layerList.size(); i++) {
        sequencer.setTrackMute(i, layerList.get(i).isMute());
      }

      sequencer.setTickPosition(BEAT);
      sequencer.setTempoInBPM(TEMPO_PROP.floatValue());

    } catch (InvalidMidiDataException imde) {
      throw new IllegalArgumentException("Invalid MIDI data encountered");
    }
  }

  /**
   * Sets the tick position of the player to the given beat. If the given beat is less than 0 or
   * greater than the number of beats in the song, the tick position is set to 0.
   *
   * @param beat the beat of the song to set the player to
   */
  public static void setBeat(int beat) {
    if (beat < 0) {
      BEAT = 0;
    } else {
      BEAT = Math.min(beat, FINAL_BEAT);
    }

    sequencer.setTickPosition(BEAT);
    sequencer.setTempoInBPM(TEMPO_PROP.floatValue());
  }

  /**
   * Sets the tempo of this song to the given tempo (in BPM).
   *
   * @param tempo the new tempo of this song
   * @throws IllegalArgumentException if the given tempo is less than 10 or greater than 500
   */
  public static void setTempo(int tempo) throws IllegalArgumentException {
    if (tempo < 10 || tempo > 500) {
      throw new IllegalArgumentException("Tempo must be between 10 and 500 BPM inclusive.");
    }

    TEMPO_PROP.setValue(tempo);

    sequencer.setTickPosition(BEAT);
    sequencer.setTempoInBPM(tempo);
  }

  /**
   * Plays the song loaded into the player if it is not already playing.
   */
  public static void play() {
    if (sequencer.getSequence() != null) {
      if (!sequencer.isRunning() && !PLAY_PROP.get()) {
        sequencer.start();
        sequencer.setTempoInBPM(TEMPO_PROP.floatValue());
        PLAY_PROP.setValue(true);
      }
    }
  }

  /**
   * If a song is currently playing, pauses the song.
   */
  public static void pause() {
    if (sequencer.isRunning() && PLAY_PROP.get()) {
      sequencer.stop();
      PLAY_PROP.setValue(false);
    }
  }

  /**
   * Resets the song back to the beginning. If the song was playing, it continues to play from the
   * beginning, otherwise it remains paused.
   */
  public static void reset() {
    BEAT = 0;
    sequencer.setTickPosition(0);
    sequencer.setTempoInBPM(TEMPO_PROP.floatValue());
  }

  /**
   * Returns whether or not the player is currently playing the song.
   *
   * @return whether or not the player is playing
   */
  public static boolean isPlaying() {
    return PLAY_PROP.get();
  }

  /**
   * Gets the tempo in BPM of the song.
   *
   * @return the tempo in BPM of the song
   */
  public static int getTempo() {
    return TEMPO_PROP.intValue();
  }

  /**
   * If the beat can be incremented without going past the final beat, increments the current beat
   * by 1 and returns {@code true}. This does not actually advance the MIDI player. This only
   * increments the representation of the current beat. Otherwise, does not increment the beat,
   * pauses the song, and returns {@code false}.
   *
   * @return true if the beat can be incremented without going past the final beat, false otherwise
   */
  public static boolean incrementBeat() {
    if (BEAT < FINAL_BEAT) {
      BEAT++;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Determines if the song is complete by checking if the current beat is greater than or equal to
   * the final beat.
   *
   * @return whether or not the song is complete
   */
  public static boolean songComplete() {
    return BEAT >= FINAL_BEAT;
  }

  /**
   * Toggles whether or not the given layer (indicated by the layer index) is muted.
   *
   * @param layerIndex the index of the given layer to toggle mute on
   */
  public static void setMuteLayer(int layerIndex, boolean mute) {
    sequencer.setTrackMute(layerIndex, mute);
  }

  /**
   * Plays the given unit (note or drum beat).
   *
   * @param unit the unit to play
   */
  public static void playUnit(Unit unit) {
    if (unit.isDrum()) {
      channels[9].noteOn(unit.getInstrument() + 1, 50);
    } else {
      channels[0].programChange(unit.getInstrument());
      channels[0].noteOn(unit.getMIDINumber(), 50);
    }
  }

  /**
   * Stops playing all units currently playing.
   */
  public static void stopAllUnits() {
    channels[0].allSoundOff();
    channels[9].allSoundOff();
  }

  /**
   * Closes the MIDI player by closing the synthesizer and the sequencer. This should be called
   * before exiting the program.
   */
  public static void closePlayer() {
    sequencer.close();
    synth.close();
  }
}
