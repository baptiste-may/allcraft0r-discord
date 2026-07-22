package fr.may_baptiste.allcraft0r_discord.integration.commands.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.commands.admin.UnlockCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import java.util.function.Consumer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

class UnlockCommandIntegrationTest extends AbstractIntegration {

  @Autowired UnlockCommand unlockCommand;

  @Nested
  class CommandMetaData {

    @Test
    @DisplayName("should require MESSAGE_MANAGE default permission")
    void shouldRequireMessageManageDefaultPermission() {
      final var data = unlockCommand.getCommandData();
      assertThat(data.getDefaultPermissions().getPermissionsRaw())
          .isEqualTo(
              net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.enabledFor(
                      Permission.MESSAGE_MANAGE)
                  .getPermissionsRaw());
    }
  }

  @Nested
  class UnlockingChannel {

    @Test
    @DisplayName("should allow sending messages and announce unlock")
    void shouldAllowSendingMessagesAndAnnounceUnlock() {
      final var fixture = mockUnlockFixture(false, "Incident résolu");

      unlockCommand.onCommandExecution(fixture.event());

      verify(fixture.permissionOverrideAction()).grant(Permission.MESSAGE_SEND);
      assertThat(captureReplyEmbed(fixture.event()).getTitle())
          .isEqualTo("Le channel a été unlock !");

      final var captor = ArgumentCaptor.forClass(MessageEmbed.class);
      verify(fixture.textChannel()).sendMessageEmbeds(captor.capture(), any(MessageEmbed[].class));
      assertThat(captor.getValue().getTitle()).isEqualTo("Ce channel a été unlock ✅");
      assertThat(captor.getValue().getDescription()).isEqualTo("Incident résolu");
    }
  }

  @Nested
  class AlreadyUnlocked {

    @Test
    @DisplayName("should reply that channel is already unlocked")
    void shouldReplyThatChannelIsAlreadyUnlocked() {
      final var fixture = mockUnlockFixture(true, null);

      unlockCommand.onCommandExecution(fixture.event());

      assertThat(captureReplyEmbed(fixture.event()).getTitle())
          .isEqualTo("Le channel est déjà unlock !");
      verify(fixture.textChannel(), never()).upsertPermissionOverride(any(Role.class));
      verify(fixture.textChannel(), never())
          .sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class));
    }
  }

  private UnlockFixture mockUnlockFixture(boolean canEveryoneSendMessages, String reason) {
    final var event = buildEvent("unlock", "user-1");
    final var option = mock(OptionMapping.class);
    final var channelUnion = mock(GuildChannelUnion.class);
    final var guild = mock(Guild.class);
    final var everyoneRole = mock(Role.class);
    final var textChannel = mock(TextChannel.class);
    final var permissionOverrideAction = mock(PermissionOverrideAction.class);
    final var sendAction = mock(MessageCreateAction.class);

    when(option.getAsChannel()).thenReturn(channelUnion);
    when(channelUnion.asTextChannel()).thenReturn(textChannel);
    when(event.getOption("channel")).thenReturn(option);
    when(event.getOption(eq("channel"), any())).thenReturn(channelUnion);
    when(event.getOption(eq("raison"), isNull(), any())).thenReturn(reason);
    when(textChannel.getGuild()).thenReturn(guild);
    when(guild.getPublicRole()).thenReturn(everyoneRole);
    when(everyoneRole.hasPermission(textChannel, Permission.MESSAGE_SEND))
        .thenReturn(canEveryoneSendMessages);
    when(textChannel.upsertPermissionOverride(everyoneRole)).thenReturn(permissionOverrideAction);
    when(permissionOverrideAction.grant(Permission.MESSAGE_SEND))
        .thenReturn(permissionOverrideAction);
    doAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              final var success = (Consumer<PermissionOverride>) invocation.getArgument(0);
              success.accept(mock(PermissionOverride.class));
              return null;
            })
        .when(permissionOverrideAction)
        .queue(any());
    when(textChannel.sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class)))
        .thenReturn(sendAction);
    doNothing().when(sendAction).queue();

    return new UnlockFixture(event, textChannel, permissionOverrideAction);
  }

  private record UnlockFixture(
      SlashCommandInteractionEvent event,
      TextChannel textChannel,
      PermissionOverrideAction permissionOverrideAction) {}
}
