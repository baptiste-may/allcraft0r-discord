package fr.may_baptiste.allcraft0r_discord.integration.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.exception.AdminChannelIdInvalidException;
import fr.may_baptiste.allcraft0r_discord.system.exception.GuildIdInvalidException;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DiscordConfigIntegrationTest extends AbstractIntegration {

  @Autowired DiscordConfig discordConfig;
  @Autowired List<SlashCommand> commands;

  @Nested
  class RedstoneFormatting {

    @Test
    @DisplayName("Should format number with redstone emoji")
    void shouldFormatRedstoneNumber() {
      assertThat(discordConfig.formatRedstoneNumber(100)).contains("100").contains("<:redstone:");
    }

    @Test
    @DisplayName("Should return formatted redstone emoji string")
    void shouldReturnFormattedRedstoneEmoji() {
      assertThat(discordConfig.getRedstoneEmoji()).startsWith("<:redstone:").endsWith(">");
    }
  }

  @Nested
  class JdaBeanInitialization {

    private JDABuilder createMockBuilder(JDA mockJda) {
      JDABuilder builderMock = mock(JDABuilder.class);
      when(builderMock.enableIntents(any(GatewayIntent.class), any(GatewayIntent[].class)))
          .thenReturn(builderMock);
      when(builderMock.addEventListeners(any(Object[].class))).thenReturn(builderMock);
      when(builderMock.build()).thenReturn(mockJda);
      return builderMock;
    }

    @Test
    @DisplayName("Should throw GuildIdInvalidException when guild is not found")
    void shouldThrowGuildIdInvalidExceptionWhenGuildNotFound() throws Exception {
      DiscordConfig testConfig = new DiscordConfig("dummy-token", 123L, 456L, "redstone-emoji");

      JDA mockJda = mock(JDA.class);
      SelfUser mockSelfUser = mock(SelfUser.class);
      when(mockJda.awaitStatus(JDA.Status.CONNECTED)).thenReturn(mockJda);
      when(mockJda.getSelfUser()).thenReturn(mockSelfUser);
      when(mockSelfUser.getName()).thenReturn("Bot");
      when(mockSelfUser.getId()).thenReturn("123");
      when(mockJda.getGuildById(123L)).thenReturn(null);

      try (var mockedJdaBuilder = mockStatic(JDABuilder.class)) {
        JDABuilder builderMock = createMockBuilder(mockJda);
        mockedJdaBuilder.when(() -> JDABuilder.createDefault(anyString())).thenReturn(builderMock);

        assertThatThrownBy(() -> testConfig.jda(commands))
            .isInstanceOf(GuildIdInvalidException.class);
      }
    }

    @Test
    @DisplayName("Should throw AdminChannelIdInvalidException when admin channel is not found")
    void shouldThrowAdminChannelIdInvalidExceptionWhenAdminChannelNotFound() throws Exception {
      DiscordConfig testConfig = new DiscordConfig("dummy-token", 123L, 456L, "redstone-emoji");

      JDA mockJda = mock(JDA.class);
      SelfUser mockSelfUser = mock(SelfUser.class);
      Guild mockGuild = mock(Guild.class);

      when(mockJda.awaitStatus(JDA.Status.CONNECTED)).thenReturn(mockJda);
      when(mockJda.getSelfUser()).thenReturn(mockSelfUser);
      when(mockSelfUser.getName()).thenReturn("Bot");
      when(mockSelfUser.getId()).thenReturn("123");
      when(mockJda.getGuildById(123L)).thenReturn(mockGuild);
      when(mockGuild.getChannelById(TextChannel.class, 456L)).thenReturn(null);

      try (var mockedJdaBuilder = mockStatic(JDABuilder.class)) {
        JDABuilder builderMock = createMockBuilder(mockJda);
        mockedJdaBuilder.when(() -> JDABuilder.createDefault(anyString())).thenReturn(builderMock);

        assertThatThrownBy(() -> testConfig.jda(commands))
            .isInstanceOf(AdminChannelIdInvalidException.class);
      }
    }

    @Test
    @DisplayName("Should initialize JDA, fetch guild and channel, and update commands successfully")
    void shouldInitializeJdaSuccessfully() throws Exception {
      DiscordConfig testConfig = new DiscordConfig("dummy-token", 123L, 456L, "redstone-emoji");

      JDA mockJda = mock(JDA.class);
      SelfUser mockSelfUser = mock(SelfUser.class);
      Guild mockGuild = mock(Guild.class);
      TextChannel mockChannel = mock(TextChannel.class);
      CommandListUpdateAction mockUpdateAction = mock(CommandListUpdateAction.class);

      when(mockJda.awaitStatus(JDA.Status.CONNECTED)).thenReturn(mockJda);
      when(mockJda.getSelfUser()).thenReturn(mockSelfUser);
      when(mockSelfUser.getName()).thenReturn("Bot");
      when(mockSelfUser.getId()).thenReturn("123");
      when(mockJda.getGuildById(123L)).thenReturn(mockGuild);
      when(mockGuild.getName()).thenReturn("Test Guild");
      when(mockGuild.getId()).thenReturn("456");
      when(mockGuild.getChannelById(TextChannel.class, 456L)).thenReturn(mockChannel);
      when(mockChannel.getName()).thenReturn("admin-channel");
      when(mockChannel.getId()).thenReturn("789");
      when(mockGuild.updateCommands()).thenReturn(mockUpdateAction);
      when(mockUpdateAction.addCommands(anyList())).thenReturn(mockUpdateAction);
      doNothing().when(mockUpdateAction).queue(any(), any());

      try (var mockedJdaBuilder = mockStatic(JDABuilder.class)) {
        JDABuilder builderMock = createMockBuilder(mockJda);
        mockedJdaBuilder.when(() -> JDABuilder.createDefault(anyString())).thenReturn(builderMock);

        JDA result = testConfig.jda(commands);

        assertThat(result).isEqualTo(mockJda);
        assertThat(testConfig.getGuild()).isEqualTo(mockGuild);
        assertThat(testConfig.getAdminChannel()).isEqualTo(mockChannel);
        verify(mockUpdateAction).queue(any(), any());
      }
    }
  }
}
