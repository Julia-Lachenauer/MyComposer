package mycomposer.view;

import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mycomposer.FileManager;
import mycomposer.Player;

/**
 * Represents the controls used to play, pause, reset, increment forward, change the tempo, and
 * toggle autoscroll.
 */
public class PlaybackControls extends GridPane {

  private final Button pauseButton;
  private Slider tempo;

  /**
   * Creates a playback controls bar which allows the user to control the playback of the song.
   *
   * @param togglePlay       an operation to toggle playing and pausing the song
   * @param resetSong        an operation to reset the song
   * @param incrementBeat    an operation to increment the beat forward
   * @param toggleAutoScroll an operation to toggle autoscroll
   * @param setTempo         an operation to set the tempo to the given value
   * @param pauseButton      the button to use for pausing/playing the song
   */
  public PlaybackControls(Runnable togglePlay, Runnable resetSong, Runnable incrementBeat,
      Runnable toggleAutoScroll, Consumer<Integer> setTempo, Button pauseButton) {

    this.pauseButton = pauseButton;

    this.setStyle("-fx-background-color: -playback-bar-color;");

    for (int i = 0; i < 3; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / 3);
      this.getColumnConstraints().add(columnConstraints);
    }

    VBox reset = this.resetButton(resetSong);
    VBox pause = this.pauseButton(togglePlay);
    VBox forward = this.forwardButton(incrementBeat);

    HBox playControlsBox = new HBox();
    playControlsBox.setAlignment(Pos.BASELINE_CENTER);
    playControlsBox.setStyle("-fx-background-color: transparent;");
    HBox.setMargin(pause, new Insets(10, 0, 10, 0));
    playControlsBox.getChildren().addAll(reset, pause, forward);

    CheckBox autoScroll = new CheckBox("Autoscroll");
    autoScroll.setSelected(true);
    autoScroll.setOnAction(e -> toggleAutoScroll.run());

    autoScroll.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE) {
        e.consume();
        this.pauseButton.fire();
      }
    });

    this.add(this.tempoBox(setTempo), 0, 0);
    this.add(playControlsBox, 1, 0);
    this.add(autoScroll, 2, 0);
  }

  /**
   * Sets the tempo slider to match the Player tempo.
   */
  public void setTempo() {
    this.tempo.setValue(Player.getTempo());
    FileManager.UNSAVED_CHANGES_PROP.set(false);
  }

  /**
   * Creates a button to pause/play the song.
   *
   * @param togglePlay an operation to toggle pausing and playing the song
   * @return a button to pause/play the song
   */
  private VBox pauseButton(Runnable togglePlay) {
    ImageView pauseImage = new ImageView(new Image("/resources/pause.png"));
    ImageView playImage = new ImageView(new Image("/resources/play.png"));

    this.pauseButton.getStyleClass().remove("button");
    this.pauseButton.getStyleClass().add("pause-button");
    this.pauseButton.setGraphic(playImage);
    this.pauseButton.setMouseTransparent(true);
    this.pauseButton.setOnAction(e -> togglePlay.run());
    this.pauseButton.setScaleX(0.9);
    this.pauseButton.setScaleY(0.9);

    VBox mouseAreaPause = new VBox();
    mouseAreaPause.setOnMouseClicked(e -> togglePlay.run());
    mouseAreaPause.setAlignment(Pos.CENTER);
    mouseAreaPause.setMinWidth(120);
    mouseAreaPause.setStyle("-fx-background-color: transparent;");
    mouseAreaPause.getChildren().add(this.pauseButton);

    ColorAdjust adjust = new ColorAdjust();
    this.pauseButton.setEffect(adjust);
    Effects.setBrightenOnHover(mouseAreaPause, adjust);

    this.pauseButton.graphicProperty().bind(Bindings.when(Player.PLAY_PROP).then(pauseImage)
        .otherwise(playImage));

    return mouseAreaPause;
  }

  /**
   * Creates a button to reset the song.
   *
   * @param resetSong an operation to reset the song
   * @return a button to reset the song
   */
  private VBox resetButton(Runnable resetSong) {
    ImageView resetImage = new ImageView(new Image("/resources/reset.png"));

    Button reset = new Button();
    reset.setGraphic(resetImage);

    VBox mouseAreaReset = new VBox();
    mouseAreaReset.setOnMouseClicked(e -> resetSong.run());

    this.setUpSideButton(reset, mouseAreaReset);

    return mouseAreaReset;
  }

  /**
   * Creates a button to increment the beat forward.
   *
   * @param incrementBeat an operation to increment the beat forward
   * @return a button to increment the beat forward
   */
  private VBox forwardButton(Runnable incrementBeat) {
    ImageView forwardImage = new ImageView(new Image("/resources/reset.png"));

    Button forward = new Button();
    forward.setGraphic(forwardImage);
    forward.setScaleX(-1);

    VBox mouseAreaForward = new VBox();
    mouseAreaForward.setOnMouseClicked(e -> incrementBeat.run());

    this.setUpSideButton(forward, mouseAreaForward);

    return mouseAreaForward;
  }

  /**
   * Sets up and styles the given side button (either forward or reset) and corresponding button
   * area.
   *
   * @param button     the side button to set up and style
   * @param buttonArea the button area surrounding the button to set up and style
   */
  private void setUpSideButton(Button button, VBox buttonArea) {
    button.setMouseTransparent(true);
    button.setStyle("-fx-background-color: transparent;");

    buttonArea.setStyle("-fx-background-color: transparent;");
    buttonArea.setAlignment(Pos.CENTER);
    buttonArea.setMinHeight(90);
    buttonArea.getChildren().add(button);

    Effects.setDarkenAndShrinkOnHover(buttonArea, button);

    button.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE) {
        e.consume();
        this.pauseButton.fire();
      }
    });
  }

  /**
   * Creates a box containing a slider for adjusting the tempo of the song.
   *
   * @param setTempo an operation to set the tempo to the given value
   * @return a box containing a slider for adjusting the tempo of the song
   */
  private VBox tempoBox(Consumer<Integer> setTempo) {
    Label tempoVal = new Label();
    tempoVal.setStyle("-fx-text-fill: -numbers-text-color;");
    tempoVal.setPadding(new Insets(0, 0, 0, 8));

    this.tempo = new Slider(10, 500, Player.getTempo()) {
      @Override
      protected void layoutChildren() {
        super.layoutChildren();

        Region thumb = (Region) this.lookup(".thumb");
        if (thumb != null && thumb.layoutXProperty() != null) {
          tempoVal.layoutXProperty().bindBidirectional(thumb.layoutXProperty());
        }
      }
    };

    this.tempo.setMinWidth(400);

    this.tempo.styleProperty().bind(Bindings.createStringBinding(() -> {
      int val = (int) ((this.tempo.getValue() - this.tempo.getMin()) /
          (this.tempo.getMax() - this.tempo.getMin()) * 100);
      return "-slider-track-color: linear-gradient(to right, -slider-fill-color " + val
          + "%, -slider-bg-color " + val + "%);";
    }, this.tempo.valueProperty()));

    this.tempo.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (!oldVal.equals(newVal)) {
        setTempo.accept(newVal.intValue());
      }
    });

    tempoVal.textProperty().bind(this.tempo.valueProperty().asString("%2.0f"));

    Label tempoLabel = new Label("Tempo (BPM)");
    tempoLabel.setStyle("-fx-text-fill: -numbers-text-color;");
    VBox.setMargin(tempoLabel, new Insets(10, 0, 0, 5));

    VBox tempoBox = new VBox();
    tempoBox.setPadding(new Insets(0, 10, 0, 10));
    tempoBox.setAlignment(Pos.CENTER_LEFT);
    tempoBox.getChildren().addAll(tempoVal, this.tempo, tempoLabel);

    return tempoBox;
  }
}
