package fr.may_baptiste.allcraft0r_discord.commands.game.blackjack;

import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Card;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Hand;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Suit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class BlackjackTableRenderer {

  private static final int MIN_WIDTH = 680;
  private static final int HEIGHT = 500;
  private static final int CARD_WIDTH = 92;
  private static final int CARD_HEIGHT = 135;
  private static final int CARD_GAP = 18;
  private static final int PADDING_X = 60;

  private static final Color CARD_BG = new Color(255, 255, 255);
  private static final Color CARD_BORDER = new Color(15, 23, 42);
  private static final Color RED_SUIT = new Color(220, 38, 38);
  private static final Color BLACK_SUIT = new Color(15, 23, 42);
  private static final Color SHADOW_COLOR = new Color(0, 0, 0, 160);
  private static final Color BRIGHT_GOLD = new Color(253, 224, 71);

  private BlackjackTableRenderer() {}

  public static byte[] renderTable(
      Hand playersCards,
      Hand masterCards,
      int playerWeight,
      int masterWeight,
      String authorName,
      long bet,
      Long profit,
      boolean gameEnded) {

    final int maxCards = Math.max(playersCards.size(), masterCards.size());
    final int handContentWidth = maxCards * CARD_WIDTH + Math.max(0, maxCards - 1) * CARD_GAP;
    final int requiredWidth = PADDING_X * 2 + handContentWidth;
    final int width = Math.max(MIN_WIDTH, requiredWidth);

    final var image = new BufferedImage(width, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    final var g = image.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // 1. Felt Table Background
    final var bgGradient =
        new GradientPaint(0, 0, new Color(22, 101, 52), 0, HEIGHT, new Color(20, 83, 45));
    g.setPaint(bgGradient);
    g.fillRect(0, 0, width, HEIGHT);

    // Felt border lines
    g.setColor(new Color(34, 197, 94, 120));
    g.setStroke(new BasicStroke(4f));
    g.drawRoundRect(20, 20, width - 40, HEIGHT - 40, 30, 30);

    g.setColor(new Color(234, 179, 8, 200));
    g.setStroke(new BasicStroke(2.5f));
    g.drawRoundRect(26, 26, width - 52, HEIGHT - 52, 24, 24);

    // 2. Dealer Section (Top)
    drawHandSection(g, "CROUPIER", masterCards, masterWeight, width, 95, 115);

    // 3. Player Section (Bottom)
    drawHandSection(g, authorName.toUpperCase(), playersCards, playerWeight, width, 290, 310);

    // 4. Result Overlay Banner at top center (if ended)
    if (gameEnded && profit != null) {
      drawResultBanner(g, width, playerWeight, masterWeight, profit, bet);
    }

    g.dispose();

    try (final var baos = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", baos);
      return baos.toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render blackjack table image", e);
    }
  }

  private static void drawHandSection(
      Graphics2D g, String label, Hand hand, int weight, int canvasWidth, int headerY, int cardsY) {
    // Header (Centered Horizontally on Canvas)
    g.setFont(new Font("SansSerif", Font.BOLD, 22));
    final String text = "%s  (Score: %d)".formatted(label, weight);
    final var fm = g.getFontMetrics();
    final int headerX = (canvasWidth - fm.stringWidth(text)) / 2;

    g.setColor(SHADOW_COLOR);
    g.drawString(text, headerX + 2, headerY + 2);

    g.setColor(BRIGHT_GOLD);
    g.drawString(text, headerX, headerY);

    // Cards (Centered Horizontally on Canvas)
    final int handWidth = hand.size() * CARD_WIDTH + Math.max(0, hand.size() - 1) * CARD_GAP;
    final int startX = (canvasWidth - handWidth) / 2;

    for (int i = 0; i < hand.size(); i++) {
      final var card = hand.getCards().get(i);
      final int x = startX + i * (CARD_WIDTH + CARD_GAP);
      drawSingleCard(g, card, x, cardsY, CARD_WIDTH, CARD_HEIGHT);
    }
  }

  private static void drawSingleCard(Graphics2D g, Card card, int x, int y, int width, int height) {

    // Card Drop Shadow
    g.setColor(new Color(0, 0, 0, 60));
    g.fillRoundRect(x + 4, y + 4, width, height, 14, 14);

    // Card Base
    g.setColor(CARD_BG);
    g.fillRoundRect(x, y, width, height, 14, 14);

    g.setColor(CARD_BORDER);
    g.setStroke(new BasicStroke(2.5f));
    g.drawRoundRect(x, y, width, height, 14, 14);

    final var isRed = card.suit() == Suit.HEART || card.suit() == Suit.DIAMOND;
    final var suitColor = isRed ? RED_SUIT : BLACK_SUIT;

    g.setColor(suitColor);

    // Rank (Top Left)
    g.setFont(new Font("SansSerif", Font.BOLD, 22));
    g.drawString(card.rank().getSymbol(), x + 9, y + 26);

    // Suit Symbol (Center)
    g.setFont(new Font("SansSerif", Font.BOLD, 46));
    final var suitSymbol = card.suit().getSymbol();
    final var metrics = g.getFontMetrics();
    final int symbolX = x + (width - metrics.stringWidth(suitSymbol)) / 2;
    final int symbolY = y + (height + metrics.getAscent()) / 2 - 10;
    g.drawString(suitSymbol, symbolX, symbolY);

    // Rank (Bottom Right)
    g.setFont(new Font("SansSerif", Font.BOLD, 20));
    final var rankStr = card.rank().getSymbol();
    final var rankMetrics = g.getFontMetrics();
    g.drawString(rankStr, x + width - rankMetrics.stringWidth(rankStr) - 9, y + height - 12);
  }

  private static void drawResultBanner(
      Graphics2D g, int canvasWidth, int playerWeight, int masterWeight, long profit, long bet) {
    String text;
    Color bannerBg;

    if (playerWeight > 21) {
      text = "PERDU (Bust)";
      bannerBg = new Color(220, 38, 38, 240);
    } else if (profit > bet * 2) {
      text = "BLACKJACK ! (+%d)".formatted(profit);
      bannerBg = new Color(234, 179, 8, 245);
    } else if (profit > 0 && profit > bet) {
      text = "GAGNÉ ! (+%d)".formatted(profit);
      bannerBg = new Color(22, 163, 74, 240);
    } else if (profit == bet) {
      text = "ÉGALITÉ (Push)";
      bannerBg = new Color(37, 99, 235, 240);
    } else {
      text = "PERDU !";
      bannerBg = new Color(220, 38, 38, 240);
    }

    final int bannerWidth = Math.min(460, canvasWidth - 80);
    final int bannerHeight = 55;
    final int x = (canvasWidth - bannerWidth) / 2;
    final int y = 22;

    // Drop Shadow behind banner
    g.setColor(new Color(0, 0, 0, 100));
    g.fillRoundRect(x + 5, y + 5, bannerWidth, bannerHeight, 22, 22);

    g.setColor(bannerBg);
    g.fillRoundRect(x, y, bannerWidth, bannerHeight, 22, 22);

    g.setColor(new Color(255, 255, 255));
    g.setStroke(new BasicStroke(3f));
    g.drawRoundRect(x, y, bannerWidth, bannerHeight, 22, 22);

    g.setFont(new Font("SansSerif", Font.BOLD, 26));
    final var fm = g.getFontMetrics();
    final int textX = x + (bannerWidth - fm.stringWidth(text)) / 2;
    final int textY = y + (bannerHeight + fm.getAscent()) / 2 - 4;

    // Text Shadow
    g.setColor(new Color(0, 0, 0, 180));
    g.drawString(text, textX + 2, textY + 2);

    g.setColor(Color.WHITE);
    g.drawString(text, textX, textY);
  }
}
