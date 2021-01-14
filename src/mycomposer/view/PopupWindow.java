package mycomposer.view;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Represents a popup window to be used for adding or editing notes or layers.
 */
public class PopupWindow extends Stage {

  /**
   * Constructs a popup window displaying the given content.
   *
   * @param owner        the stage which owns this popup
   * @param popupContent the content to display in this popup window
   * @param windowTitle  the title of this popup window
   */
  public PopupWindow(Stage owner, Region popupContent, String windowTitle) {
    Scene popupScene = new Scene(popupContent, 350, 350);
    popupScene.getStylesheets().add("mycomposer/view/Popup.css");

    this.setScene(popupScene);
    this.getIcons().add(new Image("/resources/icon.png"));
    this.initOwner(owner);
    this.initModality(Modality.WINDOW_MODAL);
    this.setResizable(false);
    this.setTitle(windowTitle);
  }
}
