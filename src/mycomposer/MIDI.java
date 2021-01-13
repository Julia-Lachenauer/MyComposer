package mycomposer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import mycomposer.model.Layer;
import mycomposer.model.Song;
import mycomposer.model.unit.Unit;

/**
 * Contains methods for managing the MIDI files and songs. These methods allow the user to save a
 * given song as a {@code .midi} file and allow the user to load a given song as a MIDI sequence.
 */
public final class MIDI {

  public static final int MAX_BEATS = 10000;
  public static final int EXTRA_BEATS = 32;

  /**
   * Writes and saves a .midi file representing the given song at the given file path. If the given
   * song is empty, nothing is written or saved.
   *
   * @param song the song to write and save
   * @param file the file to write the MIDI data to
   * @throws IllegalStateException if the MIDI file write fails for any reason
   */
  public static void writeMIDIFile(Song song, File file) throws IllegalStateException {
    Sequence sequence = loadSong(song);

    if (song.getFinalBeat() > 0) {
      try {
        MidiSystem.write(sequence, 1, file);
      } catch (IOException ioException) {
        throw new IllegalStateException("MIDI file write failed.");
      }
    }
  }

  /**
   * Generates a MIDI sequence representing the given song.
   *
   * @param song the song to create a MIDI sequence for
   * @return a MIDI sequence representing the given song
   * @throws IllegalArgumentException if invalid MIDI data is encountered
   * @throws IllegalStateException    if the MIDI component is unavailable
   */
  public static Sequence loadSong(Song song)
      throws IllegalArgumentException, IllegalStateException {
    try {
      Sequencer sequencer = MidiSystem.getSequencer();
      sequencer.setTempoInBPM(song.getTempo());
      sequencer.open();

      try {
        Sequence sequence = new Sequence(Sequence.PPQ, 8);

        List<Layer> layers = song.getLayers();

        for (Layer layer : layers) {
          if (!layer.isMute() && !layer.getUnits().isEmpty()) {
            Track track = sequence.createTrack();
            List<Unit> units = layer.getUnits();

            for (Unit unit : units) {
              ShortMessage startMessage = new ShortMessage();
              ShortMessage endMessage = new ShortMessage();

              if (unit.isDrum()) {
                startMessage.setMessage(144, 9, unit.getInstrument() + 1, layer.getVolume());
                endMessage.setMessage(128, 9, unit.getInstrument() + 1, layer.getVolume());
              } else {
                ShortMessage instrumentChange = new ShortMessage();
                instrumentChange.setMessage(192, 1, unit.getInstrument(), 0);
                track.add(new MidiEvent(instrumentChange, unit.getStartBeat()));

                startMessage.setMessage(144, 1, unit.getMIDINumber(), layer.getVolume());
                endMessage.setMessage(128, 1, unit.getMIDINumber(), layer.getVolume());
              }

              track.add(new MidiEvent(startMessage, unit.getStartBeat()));
              track.add(new MidiEvent(endMessage, (unit.getEndBeat()) + 1));
            }
          }
        }

        sequencer.close();

        return sequence;

      } catch (InvalidMidiDataException imde) {
        throw new IllegalArgumentException("Invalid MIDI data encountered");
      }

    } catch (MidiUnavailableException mue) {
      throw new IllegalStateException("MIDI component is unavailable");
    }
  }
}
