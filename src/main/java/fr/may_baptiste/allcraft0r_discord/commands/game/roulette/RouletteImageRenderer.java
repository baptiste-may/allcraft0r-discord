package fr.may_baptiste.allcraft0r_discord.commands.game.roulette;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

public final class RouletteImageRenderer {

  public static final List<Integer> NUMBERS_WITH_ZERO =
      List.of(
          6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12,
          35, 3, 26, 0, 32, 15, 19, 4, 21, 2, 25, 17, 34);

  public static final List<Integer> NUMBERS_WITHOUT_ZERO =
      NUMBERS_WITH_ZERO.stream().filter(n -> n != 0).toList();

  private static final int IMAGE_SIZE = 500;
  private static final double CENTER = IMAGE_SIZE / 2.0;
  private static final double PADDING = 40.0;

  private RouletteImageRenderer() {}

  public static byte[] renderWheel(int resultNb) {
    final var image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
    final var g = image.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Background Felt
    g.setColor(new Color(0, 68, 0));
    g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

    // Outer Gold Rim
    g.setColor(new Color(212, 175, 55));
    g.fill(
        new Ellipse2D.Double(
            PADDING - 20, PADDING - 20, (CENTER - PADDING + 20) * 2, (CENTER - PADDING + 20) * 2));

    final double rotation = ThreadLocalRandom.current().nextDouble() * 2.0 * Math.PI;
    final double anglePerSection = (2.0 * Math.PI) / NUMBERS_WITH_ZERO.size();

    boolean isDark = true;
    final double wheelRadius = CENTER - PADDING;

    for (int i = 0; i < NUMBERS_WITH_ZERO.size(); i++) {
      final int nb = NUMBERS_WITH_ZERO.get(i);
      final Color color;
      if (nb == 0) {
        color = new Color(0, 170, 0);
      } else if (isDark) {
        color = new Color(15, 23, 42);
      } else {
        color = new Color(220, 38, 38);
      }

      final double startAngle = i * anglePerSection + rotation;
      final double endAngle = (i + 1) * anglePerSection + rotation;
      final double middleAngle = startAngle + anglePerSection / 2.0;

      // Draw Wheel Slice
      final var slice = new Path2D.Double();
      slice.moveTo(CENTER, CENTER);
      final int steps = 15;
      for (int step = 0; step <= steps; step++) {
        final double a = startAngle + (endAngle - startAngle) * (step / (double) steps);
        slice.lineTo(CENTER + Math.cos(a) * wheelRadius, CENTER + Math.sin(a) * wheelRadius);
      }
      slice.closePath();

      g.setColor(color);
      g.fill(slice);

      g.setColor(Color.WHITE);
      g.setStroke(new BasicStroke(2.5f));
      g.draw(slice);

      // Draw Number Text
      final var oldTx = g.getTransform();
      g.translate(CENTER, CENTER);
      g.rotate(middleAngle);
      g.translate(IMAGE_SIZE / 2.3 - PADDING, 0);
      g.rotate(Math.PI / 2.0);

      g.setColor(Color.WHITE);
      g.setFont(new Font("SansSerif", Font.BOLD, 15));
      final var fm = g.getFontMetrics();
      final var nbStr = String.valueOf(nb);
      g.drawString(nbStr, -fm.stringWidth(nbStr) / 2, fm.getAscent() / 2 - 2);

      g.setTransform(oldTx);

      // Draw Silver Ball on winning number
      if (nb == resultNb) {
        final double distance = IMAGE_SIZE / 3.0 - PADDING;
        final double ballX = CENTER + Math.cos(middleAngle) * distance;
        final double ballY = CENTER + Math.sin(middleAngle) * distance;

        final var ballCenter = new Point2D.Double(ballX - 2, ballY - 2);
        final float[] dist = {0.0f, 0.3f, 0.7f, 1.0f};
        final Color[] colors = {
          Color.WHITE, new Color(240, 240, 240), new Color(208, 208, 208), new Color(160, 160, 160)
        };
        final var ballGradient = new RadialGradientPaint(ballCenter, 8.0f, dist, colors);

        g.setPaint(ballGradient);
        g.fill(new Ellipse2D.Double(ballX - 7, ballY - 7, 14, 14));
      }

      if (nb != 0) {
        isDark = !isDark;
      }
    }

    // Inner Metallic Ring 1
    final double innerRadius1 = IMAGE_SIZE / 2.65 - PADDING;
    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(3f));
    g.draw(
        new Ellipse2D.Double(
            CENTER - innerRadius1, CENTER - innerRadius1, innerRadius1 * 2, innerRadius1 * 2));

    // Center Brass Hub
    final double hubRadius = IMAGE_SIZE / 3.5 - PADDING;
    g.setColor(new Color(184, 148, 31));
    g.fill(
        new Ellipse2D.Double(CENTER - hubRadius, CENTER - hubRadius, hubRadius * 2, hubRadius * 2));

    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(3f));
    g.draw(
        new Ellipse2D.Double(CENTER - hubRadius, CENTER - hubRadius, hubRadius * 2, hubRadius * 2));

    g.dispose();

    try (final var baos = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", baos);
      return baos.toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render roulette wheel image", e);
    }
  }

  public static boolean isRed(int number) {
    if (number == 0) {
      return false;
    }
    final int index = NUMBERS_WITHOUT_ZERO.indexOf(number);
    return index != -1 && index % 2 == 1;
  }

  public static boolean isBlack(int number) {
    if (number == 0) {
      return false;
    }
    final int index = NUMBERS_WITHOUT_ZERO.indexOf(number);
    return index != -1 && index % 2 == 0;
  }
}
