package mycomposer.view;

import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mycomposer.Player;
import mycomposer.controller.Features;
import mycomposer.model.Layer;

/**
 * Represents an interactive view for the composer allowing the user to create and edit songs.
 */
public class ComposerViewBase extends BorderPane implements ComposerView {

  private Features features;
  private List<Layer> layers;

  private boolean autoScroll;
  private boolean pausedFromPopup;

  private BeatOverlay overlay;
  private Timeline timeline;
  private PlaybackControls controls;
  private FileMenu fileMenu;
  private final VBox layerGrid;
  private final Button pause;

  private final ScrollPane gridScrollPane;
  private final ScrollPane numberPane;
  private final ScrollPane listScrollPane;

  private final BooleanProperty typingProp;

  /**
   * Creates an instance of the composer view.
   */
  public ComposerViewBase() {
    super();

    this.autoScroll = true;
    this.pausedFromPopup = false;

    this.pause = new Button();
    this.layerGrid = new VBox();

    this.typingProp = new SimpleBooleanProperty();

    this.numberPane = new ScrollPane();
    this.numberPane.addEventFilter(ScrollEvent.SCROLL, e -> {
      if (e.getDeltaY() != 0) {
        e.consume();
      }
    });
    this.numberPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    this.numberPane.setVbarPolicy(ScrollBarPolicy.NEVER);
    this.numberPane.setMaxHeight(15);
    this.numberPane.setMinHeight(15);

    this.listScrollPane = new ScrollPane();
    this.listScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
      if (e.getDeltaX() != 0) {
        e.consume();
      }
    });
    this.listScrollPane.setFitToHeight(true);
    this.listScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    this.listScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);

    this.gridScrollPane = new ScrollPane();
    this.gridScrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    this.gridScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    this.gridScrollPane.hvalueProperty().bindBidirectional(this.numberPane.hvalueProperty());
    this.gridScrollPane.vvalueProperty().bindBidirectional(this.listScrollPane.vvalueProperty());

    this.gridScrollPane.prefHeightProperty().bind(this.heightProperty());
    this.listScrollPane.prefHeightProperty().bind(this.heightProperty());

    this.numberPane.setStyle("-fx-background: -numbers-background-color;");
    this.listScrollPane.setStyle("-fx-background-color: -layer-list-background-color;");
    this.gridScrollPane.setStyle("-fx-background: -window-background-color;-fx-border-width: 0;");
    this.setStyle("-fx-background-color: -window-background-color;-fx-border-width: 0;");

    this.gridScrollPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE && !this.typingProp.get()) {
        e.consume();
        this.pause.fire();
      }
    });

    this.listScrollPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE && !this.typingProp.get()) {
        e.consume();
        this.pause.fire();
      }
    });

    this.numberPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE && !this.typingProp.get()) {
        e.consume();
        this.pause.fire();
      }
    });

    this.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.SPACE && !this.typingProp.get()) {
        this.pause.fire();
      }
    });

    this.gridScrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
      double w = newVal.getWidth();
      this.numberPane.setMaxWidth(w);
    });
  }

  @Override
  public void setFeatures(Features features) {
    this.features = features;
  }

  @Override
  public void setPlaybackControls() {
    this.controls = new PlaybackControls(this::togglePlay, this::reset, this::incrementBeat,
        () -> this.autoScroll = !this.autoScroll, this.features::setTempo, this.pause);
    this.controls.prefWidthProperty().bind(this.widthProperty());

    this.setBottom(this.controls);
  }

  @Override
  public void setFileMenu() {
    MenuBar menuBar = new MenuBar();
    menuBar.setStyle("-fx-background-color: -playback-bar-color;");

    this.fileMenu = new FileMenu((Stage) this.getScene().getWindow(), this.features::setSong,
        this.timeline);
    menuBar.getMenus().add(this.fileMenu);

    menuBar.setPadding(new Insets(0, 0, 5, 0));

    this.setTop(menuBar);
  }

  @Override
  public void pause() {
    Player.pause();
    this.timeline.stop();
    this.overlay.clear();
  }

  @Override
  public void refresh(List<Layer> layers, boolean resetScroll) {
    if (resetScroll) {
      this.gridScrollPane.setHvalue(0);
      this.listScrollPane.setVvalue(0);

      this.controls.setTempo();
    }

    this.layers = layers;

    this.overlay = new BeatOverlay();
    this.overlay.highlightCell(Player.BEAT);

    this.timeline = this.updateEachBeat(e -> {
      if (Player.incrementBeat()) {
        this.overlay.highlightCell(Player.BEAT);

        if (this.autoScroll) {
          this.gridScrollPane.setHvalue((double) Player.BEAT / (Player.FINAL_BEAT + 1));
        }
      } else {
        Player.pause();
        this.timeline.stop();
        Player.reset();
      }
    });

    if (this.fileMenu != null) {
      this.fileMenu.setTimeline(this.timeline);
    }

    this.layerGrid.getChildren().clear();

    this.setCenter(this.layerGridWithNumbers());
    this.setLeft(this.layerList());
  }

  /**
   * Updates the layer grid color for the given layer at the given layer index.
   *
   * @param layer      the layer to update
   * @param layerIndex the index of the given layer
   */
  private void updateLayerColor(Layer layer, int layerIndex) {
    LayerGrid lg = new LayerGrid(layer, this.features::refresh, this::pauseFromPopup,
        this::unpauseFromPopup);

    this.layerGrid.getChildren().set(layerIndex, lg);
  }

  /**
   * Generates a layer grid with beat numbers on top.
   *
   * @return a layer grid with beat numbers on top
   */
  private VBox layerGridWithNumbers() {
    for (Layer layer : this.layers) {
      LayerGrid lg = new LayerGrid(layer, this.features::refresh, this::pauseFromPopup,
          this::unpauseFromPopup);

      this.layerGrid.getChildren().add(lg);
    }

    StackPane pane = new StackPane();
    pane.getChildren().add(this.layerGrid);
    pane.getChildren().add(this.overlay);
    pane.setStyle("-fx-background-color: -window-background-color;-fx-border-width: 0;");

    this.gridScrollPane.setContent(pane);
    this.numberPane.setContent(new BeatNumbers(this.overlay, this::togglePlay));

    VBox box = new VBox();
    box.getChildren().addAll(this.numberPane, this.gridScrollPane);
    box.setStyle("-fx-background-color: -window-background-color;");

    return box;
  }

  /**
   * Generates a visual list of all the layers.
   *
   * @return a visual list of all the layers
   */
  private VBox layerList() {
    this.listScrollPane.setContent(
        new LayerList(this.layers, this.features::addLayer, this.features::removeLayer,
            this::pauseFromPopup, this::unpauseFromPopup, this.features::refresh,
            this::updateLayerColor, this.typingProp));

    HBox topPadding = new HBox();
    topPadding.getStyleClass().add("layer-list-padding");
    HBox.setHgrow(topPadding, Priority.ALWAYS);

    HBox bottomPadding = new HBox();
    bottomPadding.getStyleClass().add("layer-list-padding");
    HBox.setHgrow(bottomPadding, Priority.ALWAYS);

    Button addLayerButton = new Button("+");
    addLayerButton.setAlignment(Pos.TOP_CENTER);
    addLayerButton.getStyleClass().remove("button");
    addLayerButton.getStyleClass().add("plus-button");
    addLayerButton.setMaxSize(15, 15);
    addLayerButton.setMinSize(15, 15);

    Tooltip tooltip = new Tooltip("Add Layer");
    tooltip.setShowDelay(new Duration(100));
    Tooltip.install(addLayerButton, new Tooltip("Add Layer"));

    addLayerButton.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.SPACE && !this.typingProp.get()) {
        e.consume();
        this.pause.fire();
      }
    });

    addLayerButton.setOnAction(e -> {
      this.pauseFromPopup();
      Stage addLayerStage = new Stage();
      LayerPopup layerPopup = new LayerPopup(this.features::addLayer, this.features::removeLayer,
          this.features::refresh);

      Scene addLayerScene = new Scene(layerPopup, 350, 350);
      addLayerScene.getStylesheets().add("mycomposer/view/Popup.css");
      addLayerStage.setScene(addLayerScene);

      addLayerStage.initOwner(this.getScene().getWindow());
      addLayerStage.initModality(Modality.WINDOW_MODAL);
      addLayerStage.setResizable(false);
      addLayerStage.setTitle("New Layer");

      addLayerStage.show();
      layerPopup.requestFocus();
      layerPopup.focusOnTextField();

      addLayerStage.setOnHidden(event -> this.unpauseFromPopup());
    });

    topPadding.getChildren().add(addLayerButton);

    VBox layerListWithTop = new VBox();
    layerListWithTop.getChildren().addAll(topPadding, this.listScrollPane, bottomPadding);
    layerListWithTop.setStyle("-fx-background-color: -layer-list-background-color;");
    layerListWithTop.setMinWidth(120);

    return layerListWithTop;
  }

  /**
   * Resets the song back to the beginning.
   */
  private void reset() {
    if (!Player.isPlaying() || Player.songComplete()) {
      this.timeline.stop();
      Player.pause();
    }

    Player.reset();
    this.overlay.clear();
    this.overlay.highlightCell(0);
    this.gridScrollPane.setHvalue(0);
  }

  /**
   * Increments the beat of the song as long as the song is not at the final beat.
   */
  private void incrementBeat() {
    if (Player.isPlaying()) {
      this.pause();
    }

    if (Player.incrementBeat()) {
      Player.setBeat(Player.BEAT);
      this.overlay.highlightCell(Player.BEAT);
    }
  }

  /**
   * Toggles between playing and pausing the song.
   */
  private void togglePlay() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
    } else {
      if (Player.FINAL_BEAT > 0) {
        this.overlay.clear();
        Player.play();
        this.timeline.play();
      }
    }
  }

  /**
   * If the song is playing, pauses the song and indicates that a popup has caused the song to
   * pause.
   */
  private void pauseFromPopup() {
    if (Player.isPlaying()) {
      Player.pause();
      this.timeline.stop();
      this.pausedFromPopup = true;
    }
  }

  /**
   * If a popup had caused the song to pause, plays the song.
   */
  private void unpauseFromPopup() {
    if (this.pausedFromPopup) {
      this.pausedFromPopup = false;
      if (Player.FINAL_BEAT > 0) {
        this.overlay.clear();
        Player.play();
        this.timeline.play();
      }
    }
  }

  /**
   * Creates a timeline which updates at every beat of the song based on the tempo of the song.
   *
   * @param updateEvent the event to call every beat
   * @return a timeline which updates each beat repeatedly
   */
  private Timeline updateEachBeat(EventHandler<ActionEvent> updateEvent) {
    Timeline timeline = new Timeline();
    timeline.setCycleCount(Animation.INDEFINITE);

    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0), updateEvent));
    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0)));

    timeline.setRate(Player.getTempo() / 7.5);
    timeline.rateProperty().bind(Player.TEMPO_PROP.divide(7.5));

    return timeline;
  }
}
