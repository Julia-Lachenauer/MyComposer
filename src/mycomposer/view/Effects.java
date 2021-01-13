package mycomposer.view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

/**
 * Provides static methods to allow for effects to be added to nodes.
 */
public class Effects {

  private static final double darkenLevel = -0.5;
  private static final double lightenLevel = 0.3;
  private static final double speed = 0.2;
  private static final double shrink = 0.8;

  /**
   * Darkens the given ColorAdjust when the given node is hovered over and returns the ColorAdjust
   * to normal when the mouse leaves the node. The given ColorAdjust should already be assigned to a
   * node before calling this method.
   *
   * @param hoverNode the node to hover over to trigger the ColorAdjust to darken
   * @param adjust    the ColorAdjust to darken
   */
  public static void setDarkenOnHover(Node hoverNode, ColorAdjust adjust) {
    hoverNode.setOnMouseEntered(e -> fadeChange(adjust, darkenLevel).play());
    hoverNode.setOnMouseExited(e -> fadeReturn(adjust, darkenLevel).play());
  }

  /**
   * Brightens the given ColorAdjust when the given node is hovered over and returns the ColorAdjust
   * to normal when the mouse leaves the node. The given ColorAdjust should already be assigned to a
   * node before calling this method.
   *
   * @param hoverNode the node to hover over to trigger the ColorAdjust to brighten
   * @param adjust    the ColorAdjust to brighten
   */
  public static void setBrightenOnHover(Node hoverNode, ColorAdjust adjust) {
    hoverNode.setOnMouseEntered(e -> fadeChange(adjust, lightenLevel).play());
    hoverNode.setOnMouseExited(e -> fadeReturn(adjust, lightenLevel).play());
  }

  /**
   * Sets the given node to darken and shrink when the given hover node is hovered over. When the
   * mouse leaves the hover node, the node returns to normal. Note that this method adds a
   * ColorAdjust to the node receiving the effect.
   *
   * @param hoverNode the node to hover over to trigger the effect
   * @param node      the node which will receive the effect
   */
  public static void setDarkenAndShrinkOnHover(Node hoverNode, Node node) {
    ColorAdjust adjust = new ColorAdjust();
    node.setEffect(adjust);

    double xScale = node.getScaleX();
    double yScale = node.getScaleY();

    KeyValue initDark = new KeyValue(adjust.brightnessProperty(), 0, Interpolator.LINEAR);
    KeyValue initX = new KeyValue(node.scaleXProperty(), xScale, Interpolator.LINEAR);
    KeyValue initY = new KeyValue(node.scaleYProperty(), yScale, Interpolator.LINEAR);

    KeyValue dark = new KeyValue(adjust.brightnessProperty(), darkenLevel, Interpolator.LINEAR);
    KeyValue smallX = new KeyValue(node.scaleXProperty(), xScale * shrink, Interpolator.LINEAR);
    KeyValue smallY = new KeyValue(node.scaleYProperty(), yScale * shrink, Interpolator.LINEAR);

    KeyFrame enterStart = new KeyFrame(Duration.seconds(0), initDark, initX, initY);
    KeyFrame enterEnd = new KeyFrame(Duration.seconds(speed), dark, smallX, smallY);

    KeyFrame exitStart = new KeyFrame(Duration.seconds(0), dark, smallX, smallY);
    KeyFrame exitEnd = new KeyFrame(Duration.seconds(speed), initDark, initX, initY);

    Timeline enter = new Timeline(enterStart, enterEnd);
    Timeline exit = new Timeline(exitStart, exitEnd);

    enter.setCycleCount(1);
    exit.setCycleCount(1);

    hoverNode.setOnMouseEntered(e -> enter.play());
    hoverNode.setOnMouseExited(e -> exit.play());
  }

  /**
   * Creates a timeline which changes the brightness of the given ColorAdjust to the given change
   * value.
   *
   * @param adjust the ColorAdjust to animate
   * @param change the new brightness (negative for darker, positive for lighter)
   * @return a timeline which changes the brightness of the given ColorAdjust to the given change
   * value
   */
  private static Timeline fadeChange(ColorAdjust adjust, double change) {
    KeyValue init = new KeyValue(adjust.brightnessProperty(), 0);
    KeyValue darken = new KeyValue(adjust.brightnessProperty(), change);

    KeyFrame start = new KeyFrame(Duration.seconds(0), init);
    KeyFrame end = new KeyFrame(Duration.seconds(speed), darken);

    Timeline fadeIn = new Timeline(start, end);
    fadeIn.setCycleCount(1);

    return fadeIn;
  }

  /**
   * Creates a timeline which changes the brightness of the given ColorAdjust from the given change
   * value to normal (meaning brightness is 0).
   *
   * @param adjust the ColorAdjust to animate
   * @param change the original brightness (negative for darker, positive for lighter)
   * @return a timeline which changes the brightness of the given ColorAdjust from the given change
   * value to normal (meaning brightness is 0)
   */
  private static Timeline fadeReturn(ColorAdjust adjust, double change) {
    KeyValue init = new KeyValue(adjust.brightnessProperty(), change, Interpolator.LINEAR);
    KeyValue lighten = new KeyValue(adjust.brightnessProperty(), 0, Interpolator.LINEAR);

    KeyFrame start = new KeyFrame(Duration.seconds(0), init);
    KeyFrame end = new KeyFrame(Duration.seconds(speed), lighten);

    Timeline fadeOut = new Timeline(start, end);
    fadeOut.setCycleCount(1);

    return fadeOut;
  }
}
