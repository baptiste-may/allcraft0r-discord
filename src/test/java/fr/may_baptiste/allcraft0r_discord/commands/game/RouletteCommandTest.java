package fr.may_baptiste.allcraft0r_discord.commands.game;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.game.roulette.RouletteImageRenderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RouletteCommandTest {

  @Nested
  class PayoutCalculations {

    @Test
    @DisplayName("should payout 35x bet on green when zero hits")
    void shouldPayout35xOnGreenZero() {
      assertThat(RouletteCommand.calculateGain(50, "green", 0)).isEqualTo(50 * 35);
    }

    @Test
    @DisplayName("should payout 0 on non-green bets when zero hits")
    void shouldPayout0OnNonGreenZero() {
      assertThat(RouletteCommand.calculateGain(50, "red", 0)).isZero();
      assertThat(RouletteCommand.calculateGain(50, "black", 0)).isZero();
      assertThat(RouletteCommand.calculateGain(50, "even", 0)).isZero();
      assertThat(RouletteCommand.calculateGain(50, "odd", 0)).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3, 32, 19, 21, 25, 34})
    @DisplayName("should payout 2x bet on red numbers")
    void shouldPayout2xOnRedNumbers(int redNumber) {
      assertThat(RouletteImageRenderer.isRed(redNumber)).isTrue();
      assertThat(RouletteCommand.calculateGain(100, "red", redNumber)).isEqualTo(200);
      assertThat(RouletteCommand.calculateGain(100, "black", redNumber)).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 13, 11, 8, 10, 24, 33, 20, 31, 22, 29, 28, 35, 26, 15, 4, 2, 17})
    @DisplayName("should payout 2x bet on black numbers")
    void shouldPayout2xOnBlackNumbers(int blackNumber) {
      assertThat(RouletteImageRenderer.isBlack(blackNumber)).isTrue();
      assertThat(RouletteCommand.calculateGain(100, "black", blackNumber)).isEqualTo(200);
      assertThat(RouletteCommand.calculateGain(100, "red", blackNumber)).isZero();
    }

    @Test
    @DisplayName("should payout 2x bet on even numbers")
    void shouldPayout2xOnEvenNumbers() {
      assertThat(RouletteCommand.calculateGain(50, "even", 14)).isEqualTo(100);
      assertThat(RouletteCommand.calculateGain(50, "even", 15)).isZero();
    }

    @Test
    @DisplayName("should payout 2x bet on odd numbers")
    void shouldPayout2xOnOddNumbers() {
      assertThat(RouletteCommand.calculateGain(50, "odd", 15)).isEqualTo(100);
      assertThat(RouletteCommand.calculateGain(50, "odd", 14)).isZero();
    }
  }

  @Nested
  class ImageRendering {

    @Test
    @DisplayName("should render wheel PNG byte array successfully")
    void shouldRenderWheelImage() {
      byte[] imageBytes = RouletteImageRenderer.renderWheel(17);
      assertThat(imageBytes).isNotEmpty();
      assertThat(imageBytes.length).isGreaterThan(1000);
    }
  }
}
