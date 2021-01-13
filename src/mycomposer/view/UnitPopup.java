package mycomposer.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mycomposer.FileManager;
import mycomposer.Player;
import mycomposer.model.Layer;
import mycomposer.model.unit.Drum;
import mycomposer.model.unit.Instrument;
import mycomposer.model.unit.Note;
import mycomposer.model.unit.Percussion;
import mycomposer.model.unit.Pitch;
import mycomposer.model.unit.Sound;
import mycomposer.model.unit.Unit;

/**
 * A pop-up window for adding or editing a new note or drum beat.
 */
public class UnitPopup extends TabPane {

  private final Layer layer;
  private final Unit unitToEdit;
  private final int clickedBeat;
  private final Runnable refresh;
  private final Runnable unpause;

  /**
   * Constructs a unit popup and sets the unit to {@code null}. This constructor should be used when
   * creating a popup to add a new unit.
   *
   * @param layer       the layer the unit will be added to
   * @param clickedBeat the beat that was clicked to trigger this window opening
   * @param refresh     an operation to save and refresh the song
   * @param unpause     an operation to unpause the song when an add/edit unit popup is closed
   */
  public UnitPopup(Layer layer, int clickedBeat, Runnable refresh, Runnable unpause) {
    this(null, layer, clickedBeat, refresh, unpause);
  }

  /**
   * Constructs a unit popup with the given unit. This constructor should be used when creating a
   * popup to edit an existing unit.
   *
   * @param unit        {@code null} if creating new unit, otherwise is the unit to be edited or
   *                    deleted
   * @param layer       the layer the unit is in
   * @param clickedBeat the beat that was clicked to trigger this window opening
   * @param refresh     an operation to save and refresh the song
   * @param unpause     an operation to unpause the song when an add/edit unit popup is closed
   */
  public UnitPopup(Unit unit, Layer layer, int clickedBeat, Runnable refresh, Runnable unpause) {
    this.layer = layer;
    this.unitToEdit = unit;
    this.clickedBeat = clickedBeat;
    this.refresh = refresh;
    this.unpause = unpause;

    this.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.ESCAPE) {
        this.unpause.run();

        Stage stage = (Stage) this.getScene().getWindow();
        stage.close();
      }
    });

    Tab instrumentTab;
    Tab percussionTab;

    if (unit == null) {
      instrumentTab = new Tab("Add Note", this.addNotePopup());
      percussionTab = new Tab("Add Drum Beat", this.addPercussionPopup());
    } else {
      instrumentTab = new Tab("Edit Note", this.addNotePopup());
      percussionTab = new Tab("Edit Drum Beat", this.addPercussionPopup());

      if (unit.isDrum()) {
        this.getSelectionModel().select(1);
      }
    }

    instrumentTab.setClosable(false);
    percussionTab.setClosable(false);

    this.getTabs().addAll(instrumentTab, percussionTab);
  }

  /**
   * Creates the menu options to add a new note.
   *
   * @return a box with the menu options to add a new note
   */
  private VBox addNotePopup() {
    ComboBox<Pitch> pitchPicker = new ComboBox<>();
    pitchPicker.getItems().setAll(Pitch.values());

    pitchPicker.setCellFactory(pitchListView -> new ListCell<>() {
      @Override
      protected void updateItem(Pitch pitch, boolean b) {
        super.updateItem(pitch, b);

        if (pitch != null) {
          this.setText(pitch.getName());
        }
      }
    });

    pitchPicker.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Pitch pitch, boolean b) {
        super.updateItem(pitch, b);

        if (pitch != null) {
          this.setText(pitch.getName());
        }
      }
    });

    Integer[] octaves = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7};
    ComboBox<Integer> octavePicker = new ComboBox<>();
    octavePicker.getItems().setAll(octaves);

    if (this.unitToEdit == null) {
      pitchPicker.setValue(Pitch.C);
      octavePicker.setValue(3);
    } else {
      if (this.unitToEdit.isDrum()) {
        pitchPicker.setValue(Pitch.C);
        octavePicker.setValue(3);
      } else {
        octavePicker.setValue(this.unitToEdit.getOctave());
        pitchPicker.setValue(this.unitToEdit.getPitch());
      }
    }

    GridPane pitchOctave = new GridPane();
    pitchOctave.setHgap(4);

    pitchOctave.add(pitchPicker, 0, 0);
    pitchOctave.add(octavePicker, 1, 0);
    pitchOctave.add(new Label("Pitch"), 0, 1);
    pitchOctave.add(new Label("Octave"), 1, 1);

    Button confirm;

    if (this.unitToEdit == null) {
      confirm = new Button("Add note");
    } else {
      confirm = new Button("Edit note");
    }

    CommonFields commonFields = new CommonFields(this.unitToEdit, this.layer, this.clickedBeat,
        false);
    commonFields.setPadding(new Insets(10, 0, 0, 0));

    confirm.setOnAction(e -> {
      try {
        int startBeat = commonFields.getStartBeat();
        int endBeat = commonFields.getEndBeat();
        int instrument = commonFields.getItem().getIndex();
        Pitch pitch = pitchPicker.getValue();
        int octave = octavePicker.getValue();

        if (this.unitToEdit == null) {
          try {
            Note note = new Note(startBeat, endBeat, instrument, pitch, octave);
            this.layer.addUnit(note);

            FileManager.UNSAVED_CHANGES_PROP.set(true);
            this.close();
          } catch (IllegalArgumentException iae) {
            ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
          }
        } else {
          if (this.unitToEdit.isDrum()) {
            try {
              this.layer.removeUnit(this.unitToEdit);
              Note note = new Note(startBeat, endBeat, instrument, pitch, octave);
              this.layer.addUnit(note);

              FileManager.UNSAVED_CHANGES_PROP.set(true);
              this.close();
            } catch (IllegalArgumentException iae) {
              ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
            }
          } else {
            try {
              commonFields.setCommonItems(startBeat, endBeat, instrument);
              this.unitToEdit.setPitch(pitch);
              this.unitToEdit.setOctave(octave);
              FileManager.UNSAVED_CHANGES_PROP.set(true);

              this.close();
            } catch (IllegalArgumentException iae) {
              ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
            }
          }
        }
      } catch (NullPointerException npe) {
        ErrorPopup.showError(this.getScene().getWindow(), "All items must be filled in.");
      }
    });

    Button listen = new Button("Listen");

    listen.setOnMousePressed(e -> {
      try {
        int startBeat = commonFields.getStartBeat();
        int endBeat = commonFields.getEndBeat();
        int instrument = commonFields.getItem().getIndex();
        Pitch pitch = pitchPicker.getValue();
        int octave = octavePicker.getValue();

        try {
          Note note = new Note(startBeat, endBeat, instrument, pitch, octave);

          Player.stopAllUnits();
          Player.playUnit(note);
        } catch (IllegalArgumentException iae) {
          if (endBeat < startBeat) {
            Player.stopAllUnits();

            int length = Math.abs(endBeat - startBeat);
            Player.playUnit(new Note(0, length, instrument, pitch, octave));
          }
        }
      } catch (NullPointerException npe) {
        ErrorPopup.showError(this.getScene().getWindow(), "All items must be filled in.");
      }
    });

    listen.setOnMouseReleased(e -> Player.stopAllUnits());

    VBox addNote = new VBox();
    addNote.setSpacing(10);
    addNote.getChildren().addAll(commonFields, pitchOctave, this.buttonBox(confirm, listen));

    return addNote;
  }

  /**
   * Creates the menu options to add a new drum beat.
   *
   * @return a box with the menu options to add a new drum beat
   */
  private VBox addPercussionPopup() {
    Button confirm;

    if (this.unitToEdit == null) {
      confirm = new Button("Add note");
    } else {
      confirm = new Button("Edit note");
    }

    CommonFields commonFields = new CommonFields(this.unitToEdit, this.layer, this.clickedBeat,
        true);
    commonFields.setPadding(new Insets(10, 0, 0, 0));

    confirm.setOnAction(e -> {
      try {
        int startBeat = commonFields.getStartBeat();
        int endBeat = commonFields.getEndBeat();
        int instrument = commonFields.getItem().getIndex();

        if (this.unitToEdit == null) {
          try {
            Drum drum = new Drum(startBeat, endBeat, instrument);
            this.layer.addUnit(drum);
            FileManager.UNSAVED_CHANGES_PROP.set(true);
            this.close();
          } catch (IllegalArgumentException iae) {
            ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
          }
        } else {
          if (this.unitToEdit.isDrum()) {
            try {
              commonFields.setCommonItems(startBeat, endBeat, instrument);
              FileManager.UNSAVED_CHANGES_PROP.set(true);

              this.close();
            } catch (IllegalArgumentException iae) {
              ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
            }
          } else {
            try {
              this.layer.removeUnit(this.unitToEdit);
              Drum drum = new Drum(startBeat, endBeat, instrument);
              this.layer.addUnit(drum);

              FileManager.UNSAVED_CHANGES_PROP.set(true);
              this.close();
            } catch (IllegalArgumentException iae) {
              ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
            }
          }
        }

      } catch (NullPointerException npe) {
        ErrorPopup.showError(this.getScene().getWindow(), "All items must be filled in.");
      }
    });

    Button listen = new Button("Listen");

    listen.setOnMousePressed(e -> {
      try {
        int startBeat = commonFields.getStartBeat();
        int endBeat = commonFields.getEndBeat();
        int instrument = commonFields.getItem().getIndex();

        try {
          Drum drum = new Drum(startBeat, endBeat, instrument);

          Player.stopAllUnits();
          Player.playUnit(drum);
        } catch (IllegalArgumentException iae) {
          if (endBeat < startBeat) {
            Player.stopAllUnits();
            Player.playUnit(new Drum(0, Math.abs(endBeat - startBeat), instrument));
          }
        }

      } catch (NullPointerException npe) {
        ErrorPopup.showError(this.getScene().getWindow(), "All items must be filled in.");
      }
    });

    listen.setOnMouseReleased(e -> Player.stopAllUnits());

    VBox addPercussion = new VBox();
    addPercussion.setSpacing(10);
    addPercussion.getChildren().addAll(commonFields, this.buttonBox(confirm, listen));

    return addPercussion;
  }

  /**
   * Creates a box to house the confirm and listen buttons. If the unit to edit is not {@code null},
   * a delete button is also added.
   *
   * @param confirm a button for confirming any changes
   * @param listen  a button for listening to the unit
   * @return a box to house the confirm and listen buttons (and a delete button if the unit to edit
   * is not {@code null})
   */
  private HBox buttonBox(Button confirm, Button listen) {
    HBox buttonBox = new HBox();
    buttonBox.getChildren().addAll(confirm, listen);
    buttonBox.setPadding(new Insets(20, 0, 0, 0));

    if (this.unitToEdit != null) {
      buttonBox.getChildren().add(this.deleteButton());
    }

    return buttonBox;
  }

  /**
   * Creates a button to be used to delete the unit. This should only be used when the unit to edit
   * is not {@code null}.
   *
   * @return a button to be used to delete the unit
   */
  private Button deleteButton() {
    Button deleteButton = new Button("Delete unit");
    deleteButton.setOnAction(e -> {
      this.layer.removeUnit(this.unitToEdit);

      this.refresh.run();
      FileManager.UNSAVED_CHANGES_PROP.set(true);
      Player.stopAllUnits();
      this.unpause.run();

      Stage stage = (Stage) this.getScene().getWindow();
      stage.close();
    });

    return deleteButton;
  }

  /**
   * Refreshes the song, stops any units that are playing, unpauses the song, and closes this
   * popup.
   */
  private void close() {
    this.refresh.run();
    Player.stopAllUnits();
    this.unpause.run();

    Stage stage = (Stage) this.getScene().getWindow();
    stage.close();
  }

  /**
   * A class to construct the menu items common to both notes and drum beats (instrument, start
   * beat, end beat).
   */
  private static final class CommonFields extends VBox {

    private final ComboBox<Sound> soundsBox;
    private final Spinner<Integer> start;
    private final Spinner<Integer> end;

    private final Unit unitToEdit;
    private final Layer layer;

    /**
     * Constructs an instance of the common fields to be used for either notes or drum beats. The
     * common fields are start beat, end beat, and instrument.
     *
     * @param unitToEdit  {@code null} if creating new unit, otherwise is the unit to be edited
     * @param layer       the layer the unit is in
     * @param clickedBeat the beat that was clicked to trigger this window opening
     * @param isDrumTab   whether or not this common fields is to be displayed in the drum tab
     */
    private CommonFields(Unit unitToEdit, Layer layer, int clickedBeat, boolean isDrumTab) {
      this.unitToEdit = unitToEdit;
      this.layer = layer;

      this.setSpacing(10);

      Sound[] soundList;
      Sound initSound;

      if (isDrumTab) {
        soundList = Percussion.values();
      } else {
        soundList = Instrument.values();
      }

      if (unitToEdit == null) {
        if (isDrumTab) {
          initSound = Percussion.AcousticBassDrum;
        } else {
          initSound = Instrument.AcousticGrandPiano;
        }
      } else {
        if (unitToEdit.isDrum()) {
          if (isDrumTab) {
            initSound = Percussion.getSound(unitToEdit.getInstrument());
          } else {
            initSound = Instrument.AcousticGrandPiano;
          }
        } else {
          if (isDrumTab) {
            initSound = Percussion.AcousticBassDrum;
          } else {
            initSound = Instrument.getSound(unitToEdit.getInstrument());
          }
        }
      }

      this.soundsBox = new ComboBox<>();
      this.soundsBox.getItems().setAll(soundList);

      this.soundsBox.setCellFactory(soundListView -> new ListCell<>() {
        @Override
        protected void updateItem(Sound sound, boolean b) {
          super.updateItem(sound, b);
          if (sound != null) {
            if (isDrumTab) {
              this.setText((sound.getIndex() - 33) + ". " + sound.getName());
            } else {
              this.setText((sound.getIndex() + 1) + ". " + sound.getName());
            }
          }
        }
      });

      this.soundsBox.setButtonCell(new ListCell<>() {
        @Override
        protected void updateItem(Sound sound, boolean b) {
          super.updateItem(sound, b);

          if (sound != null) {
            this.setText(sound.getName());
          }
        }
      });

      this.soundsBox.setOnShowing(e -> {
        ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) this.soundsBox.getSkin();
        ListView<?> list = (ListView<?>) skin.getPopupContent();

        int initIndex = this.soundsBox.getSelectionModel().getSelectedIndex();
        list.scrollTo(initIndex);
      });

      this.soundsBox.setValue(initSound);

      if (unitToEdit == null) {
        int leftFreeBeat = layer.leftFreeBeat(clickedBeat);
        int rightFreeBeat = layer.rightFreeBeat(clickedBeat);

        this.start = new Spinner<>(leftFreeBeat + 1, rightFreeBeat + 1, clickedBeat + 1);
        this.end = new Spinner<>(leftFreeBeat + 1, rightFreeBeat + 1, clickedBeat + 1);
      } else {
        int leftFreeBeat = layer.leftFreeBeat(unitToEdit);
        int rightFreeBeat = layer.rightFreeBeat(unitToEdit);
        int startBeat = unitToEdit.getStartBeat();
        int endBeat = unitToEdit.getEndBeat();

        this.start = new Spinner<>(leftFreeBeat + 1, rightFreeBeat + 1, startBeat + 1);
        this.end = new Spinner<>(leftFreeBeat + 1, rightFreeBeat + 1, endBeat + 1);
      }

      this.start.setEditable(true);
      this.end.setEditable(true);

      this.start.setPromptText("Start beat");
      this.end.setPromptText("End beat");

      this.start.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d+")) {
          this.start.getEditor().setText(oldVal);
        }
      });

      this.end.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d+")) {
          this.end.getEditor().setText(oldVal);
        }
      });

      Label startBeat = new Label("Start beat");
      Label endBeat = new Label("End beat");

      startBeat.setMaxWidth(60);
      endBeat.setMaxWidth(60);

      GridPane startEndBeat = new GridPane();

      startEndBeat.setVgap(4);
      startEndBeat.setHgap(4);

      startEndBeat.add(this.start, 0, 0);
      startEndBeat.add(this.end, 1, 0);
      startEndBeat.add(startBeat, 0, 1);
      startEndBeat.add(endBeat, 1, 1);

      this.getChildren().addAll(this.soundsBox, startEndBeat);
    }

    /**
     * Sets the start beat, end beat, and instrument of the unit to edit. This should only be called
     * if the unit to edit is not {@code null}.
     *
     * @param startBeat  the start beat
     * @param endBeat    the end beat
     * @param instrument the instrument
     * @throws IllegalArgumentException if the given start beat, end beat, or instrument are
     *                                  invalid
     */
    private void setCommonItems(int startBeat, int endBeat, int instrument)
        throws IllegalArgumentException {
      this.unitToEdit.setStartEnd(startBeat, endBeat, this.layer);
      this.unitToEdit.setInstrument(instrument);
    }

    /**
     * Gets the currently selected start beat for the unit.
     *
     * @return the currently selected start beat for the unit
     */
    private int getStartBeat() {
      return this.start.getValue() - 1;
    }

    /**
     * Gets the currently selected end beat for the unit.
     *
     * @return the currently selected end beat for the unit
     */
    private int getEndBeat() {
      return this.end.getValue() - 1;
    }

    /**
     * Gets the Sound currently selected in the sound box.
     *
     * @return the Sound currently selected in the sound box
     */
    private Sound getItem() {
      return this.soundsBox.getValue();
    }
  }
}
