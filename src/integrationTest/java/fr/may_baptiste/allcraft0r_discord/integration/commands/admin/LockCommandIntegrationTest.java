package fr.may_baptiste.allcraft0r_discord.integration.commands.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.commands.admin.LockCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

class LockCommandIntegrationTest extends AbstractIntegration {

  @Autowired LockCommand lockCommand;

  @Nested
  class CommandMetaData {

    @Test
    @DisplayName("should require MESSAGE_MANAGE default permission")
    void shouldRequireMessageManageDefaultPermission() {
      final var data = lockCommand.getCommandData();
      assertThat(data.getDefaultPermissions().getPermissionsRaw())
          .isEqualTo(
              net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.enabledFor(
                      Permission.MESSAGE_MANAGE)
                  .getPermissionsRaw());
    }
  }

  @Nested
  class LockingChannel {

    @Test
    @DisplayName("should deny sending messages and announce lock")
    void shouldDenySendingMessagesAndAnnounceLock() {
      final var fixture = mockLockFixture(true, "Maintenance");

      lockCommand.onCommandExecution(fixture.event());

      verify(fixture.permissionOverrideAction()).deny(Permission.MESSAGE_SEND);
      assertThat(captureReplyEmbed(fixture.event()).getTitle())
          .isEqualTo("Le channel a été lock !");

      ArgumentCaptor<MessageEmbed> captor = ArgumentCaptor.captor();
      verify(fixture.textChannel()).sendMessageEmbeds(captor.capture(), any(MessageEmbed[].class));
      assertThat(captor.getValue().getTitle()).isEqualTo("Ce channel a été lock ⛔️");
      assertThat(captor.getValue().getDescription()).isEqualTo("Maintenance");
    }
  }

  @Nested
  class AlreadyLocked {

    @Test
    @DisplayName("should reply that channel is already locked")
    void shouldReplyThatChannelIsAlreadyLocked() {
      final var fixture = mockLockFixture(false, null);

      lockCommand.onCommandExecution(fixture.event());

      assertThat(captureReplyEmbed(fixture.event()).getTitle())
          .isEqualTo("Le channel est déjà lock !");
      verify(fixture.textChannel(), never()).upsertPermissionOverride(any(Role.class));
      verify(fixture.textChannel(), never())
          .sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class));
    }
  }

  private AdminChannelFixture mockLockFixture(boolean canEveryoneSendMessages, String reason) {
    return mockAdminChannelFixture(
        "lock",
        canEveryoneSendMessages,
        reason,
        (action, perm) -> when(action.deny(perm)).thenReturn(action));
  }
}
