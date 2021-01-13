package mycomposer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mycomposer.MIDI;
import mycomposer.model.unit.Unit;

/**
 * Represents a track in the song which stores units (notes and drum beats). Although each layer can
 * only store and play one note at a time, multiple layers can play at the same time to create
 * chords.
 */
public final class Layer {

  private final List<Unit> units;
  private int finalBeat;

  private String name;
  private int volume;
  private LayerColor color;
  private boolean mute;

  /**
   * Creates a new layer with the given name, color, and mute setting.
   *
   * @param name   the name of the layer
   * @param volume the volume of the layer
   * @param color  the color of the layer
   * @param mute   whether or not the layer should be muted
   * @throws IllegalArgumentException if the given name is empty or the given volume is less than 0
   *                                  or greater than 100
   */
  public Layer(String name, int volume, LayerColor color, boolean mute)
      throws IllegalArgumentException {
    if (name.length() == 0) {
      throw new IllegalArgumentException("Layer name cannot be empty.");
    }

    if (volume < 0 || volume > 100) {
      throw new IllegalArgumentException("Volume must be between 0 and 100 inclusive.");
    }

    this.name = name;
    this.color = color;
    this.volume = volume;

    this.mute = mute;

    this.units = new ArrayList<>();
    this.finalBeat = 0;
  }

  /**
   * Gets the name of this layer.
   *
   * @return the name of this layer
   */
  public String getName() {
    return this.name;
  }

  /**
   * Renames this layer to the given name.
   *
   * @param name the new name for this layer
   * @throws IllegalArgumentException if the given name is empty
   */
  public void setName(String name) throws IllegalArgumentException {
    if (name.length() == 0) {
      throw new IllegalArgumentException("Layer name cannot be empty.");
    }
    this.name = name;
  }

  /**
   * Gets the color of this layer.
   *
   * @return the color of this layer
   */
  public LayerColor getColor() {
    return this.color;
  }

  /**
   * Sets the color of this layer to the given color.
   *
   * @param color the color to set this layer to
   */
  public void setColor(LayerColor color) {
    this.color = color;
  }

  /**
   * Gets the volume of this layer.
   *
   * @return the volume of this layer
   */
  public int getVolume() {
    return this.volume;
  }

  /**
   * Sets the volume of this layer to the given value.
   *
   * @param volume the volume to set this layer to
   * @throws IllegalArgumentException if the given volume is less than 0 or greater than 100
   */
  public void setVolume(int volume) throws IllegalArgumentException {
    if (volume < 0 || volume > 100) {
      throw new IllegalArgumentException("Volume must be between 0 and 100 inclusive.");
    }

    this.volume = volume;
  }

  /**
   * Return whether or not this layer is currently muted.
   *
   * @return whether or not this layer is currently muted
   */
  public boolean isMute() {
    return this.mute;
  }

  /**
   * Mutes this layer if it is not muted, unmutes this layer if it is muted.
   */
  public void toggleMute() {
    this.mute = !this.mute;
  }

  /**
   * Returns the final beat number in this layer.
   *
   * @return the final beat in this layer
   */
  public int getFinalBeat() {
    return this.finalBeat;
  }

  /**
   * Returns the list of units in this layer.
   *
   * @return the list of units in this layer
   */
  public List<Unit> getUnits() {
    return new ArrayList<>(this.units);
  }

  /**
   * Adds the given unit to this layer.
   *
   * @param unit the unit to add
   * @throws IllegalArgumentException if the new unit overlaps with any existing units in this
   *                                  layer
   */
  public void addUnit(Unit unit) throws IllegalArgumentException {
    for (int i = unit.getStartBeat(); i <= unit.getEndBeat(); i++) {
      if (this.beatOverlapsUnit(i)) {
        throw new IllegalArgumentException("New unit cannot overlap existing units.");
      }
    }

    if (unit.getEndBeat() > this.finalBeat) {
      this.finalBeat = unit.getEndBeat();
    }

    this.units.add(unit);
  }

  /**
   * Removes the given unit from this layer if it is present.
   *
   * @param unit the unit to remove
   */
  public void removeUnit(Unit unit) {
    Collections.sort(this.units);
    this.units.remove(unit);

    if (unit.getEndBeat() == this.finalBeat) {
      int numUnits = this.units.size();

      if (numUnits == 0) {
        this.finalBeat = 0;
      } else {
        this.finalBeat = this.units.get(numUnits - 1).getEndBeat();
      }
    }
  }

  /**
   * Updates the final beat before the given unit is edited.
   *
   * @param unit the unit that is being edited
   */
  public void checkEditUnit(Unit unit, int newEndBeat) {
    if (unit.getEndBeat() == this.finalBeat) {
      this.finalBeat = newEndBeat;
    }
  }

  /**
   * Determines if the given beat overlaps with any existing units in this layer.
   *
   * @param beat the beat to check
   * @return whether or not the given beat overlaps with any existing units in this layer
   */
  public boolean beatOverlapsUnit(int beat) {
    for (Unit unit : this.units) {
      if (unit.beatOverlapsUnit(beat)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Determines if the given beat overlaps with any existing units in this layer, ignoring the given
   * unit to ignore.
   *
   * @param beat         the beat to check
   * @param unitToIgnore the unit to ignore when checking for overlaps
   * @return whether or not the given beat overlaps with any existing units in this layer aside from
   * the given unit
   */
  public boolean beatOverlapsUnit(int beat, Unit unitToIgnore) {
    for (Unit unit : this.units) {
      if (!unit.equals(unitToIgnore)) {
        if (unit.beatOverlapsUnit(beat)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Determines if the given range overlaps any existing units in this layer, ignoring the given
   * unit to ignore.
   *
   * @param start        the start beat of the range
   * @param end          the end beat of the range
   * @param unitToIgnore the unit to ignore when checking for overlaps
   * @return whether or not the given range overlaps with any existing units in this layer aside
   * from the given unit
   */
  public boolean rangeOverlapsUnit(int start, int end, Unit unitToIgnore) {
    for (int i = start; i <= end; i++) {
      if (this.beatOverlapsUnit(i, unitToIgnore)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the beat farthest to the left of the given beat such that all beats between (and
   * including) that beat and the given beat are empty. If the beat directly to the left of the
   * given beat is not empty, then the given beat is returned.
   *
   * @param beat the beat to check
   * @return the furthest free beat to the left of the given beat
   * @throws IllegalArgumentException if the given beat is less than 0
   */
  public int leftFreeBeat(int beat) throws IllegalArgumentException {
    if (beat < 0) {
      throw new IllegalArgumentException("Beat cannot be less than 0.");
    }

    if (this.units.size() == 0) {
      return 0;
    }

    Collections.sort(this.units);

    for (int i = 0; i < this.units.size(); i++) {

      Unit unit = this.units.get(i);

      if (unit.getEndBeat() >= beat) {
        if (i == 0) {
          return 0;
        } else {
          return this.units.get(i - 1).getEndBeat() + 1;
        }
      }
    }

    return this.units.get(this.units.size() - 1).getEndBeat() + 1;
  }

  /**
   * Gets the beat farthest to the left of the start beat of the given unit such that all beats
   * between (and including) that beat and the start beat are empty. If the beat directly to the
   * left of the start beat is not empty, then the start beat is returned.
   *
   * @param unit the unit to check
   * @return the furthest free beat to the left of the start beat of the given beat
   */
  public int leftFreeBeat(Unit unit) {
    return this.leftFreeBeat(unit.getStartBeat());
  }

  /**
   * Gets the beat farthest to the right of the given beat such that all beats between (and
   * including) that beat and the given beat are empty. If the beat directly to the right of the
   * given beat is not empty, then the given beat is returned. If there are no notes to the right of
   * the given beat, then the maximum song length is returned.
   *
   * @param beat the beat to check
   * @return the furthest free beat to the right of the given beat
   * @throws IllegalArgumentException if the given beat is less than 0
   */
  public int rightFreeBeat(int beat) throws IllegalArgumentException {
    if (beat < 0) {
      throw new IllegalArgumentException("Beat cannot be less than 0.");
    }

    if (this.units.size() == 0) {
      return MIDI.MAX_BEATS;
    }

    Collections.sort(this.units);

    int numUnits = this.units.size();

    for (int i = numUnits - 1; i >= 0; i--) {

      Unit unit = this.units.get(i);

      if (unit.getStartBeat() <= beat) {
        if (i == numUnits - 1) {
          return MIDI.MAX_BEATS;
        } else {
          return this.units.get(i + 1).getStartBeat() - 1;
        }
      }
    }

    return this.units.get(0).getStartBeat() - 1;
  }

  /**
   * Gets the beat farthest to the right of the end beat of the given unit such that all beats
   * between (and including) that beat and the end beat are empty. If the beat directly to the right
   * of the end beat is not empty, then the end beat is returned. If there are no notes to the right
   * of the end beat, then the maximum song length is returned.
   *
   * @param unit the unit to check
   * @return the furthest free beat to the right of the end beat of the given beat
   */
  public int rightFreeBeat(Unit unit) {
    return this.rightFreeBeat(unit.getEndBeat());
  }

  /**
   * Sorts this layer's units in order of increasing end beat. Note that units never overlap.
   */
  public void sortUnits() {
    Collections.sort(this.units);
  }
}
