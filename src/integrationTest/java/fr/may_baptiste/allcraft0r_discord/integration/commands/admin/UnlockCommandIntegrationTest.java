package fr.may_baptiste.allcraft0r_discord.integration.commands.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.commands.admin.UnlockCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
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

  private AdminChannelFixture mockUnlockFixture(boolean canEveryoneSendMessages, String reason) {
    return mockAdminChannelFixture(
        "unlock",
        canEveryoneSendMessages,
        reason,
        (action, perm) -> when(action.grant(perm)).thenReturn(action));
  }
}
