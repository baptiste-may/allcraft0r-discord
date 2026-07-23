package fr.may_baptiste.allcraft0r_discord.integration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.system.repository.UserRepository;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Base class for slash-command integration tests.
 *
 * <p>Boots a real Spring context backed by an H2 in-memory database (configured in {@code
 * src/test/resources/application.properties}) and replaces the JDA infrastructure with Mockito
 * mocks so no real Discord connection is attempted.
 *
 * <h2>What subclasses get for free</h2>
 *
 * <ul>
 *   <li>{@link #jda} — the mocked {@link JDA} bean; stub it for commands that inspect connection
 *       state (e.g., {@code PingCommand} calls {@code jda.getGatewayPing()})
 *   <li>{@link #userRepository} — the real repository, useful for seeding or asserting DB state
 *   <li>{@link #discordConfig} — the real config bean; use {@link #mockAdminChannel()} to inject a
 *       channel mock for commands that write to it (e.g., {@code SendCommand})
 *   <li>{@link #buildEvent(String, String)} — a fully pre-wired event mock (both {@code
 *       replyEmbeds} overloads, {@code reply(String)}, and the JDA reference are all stubbed)
 *   <li>{@link #stubStringOption} / {@link #stubLongOption} — stub slash command options
 *   <li>{@link #captureReplyEmbed} / {@link #captureReplyEmbedSingle} / {@link #captureReplyText} —
 *       retrieve what the command actually replied
 *   <li>An automatic {@link #cleanDatabase()} {@code @BeforeEach} that wipes all users
 * </ul>
 */
@SpringBootTest
public abstract class AbstractIntegration {

  // ── Discord infrastructure ───────────────────────────────────────────────────

  /**
   * Mocked JDA bean — prevents any real Discord connection on context startup.
   *
   * <p>Subclasses can stub additional behaviour: e.g. {@code
   * when(jda.getGatewayPing()).thenReturn(42L)}.
   */
  @MockitoBean protected JDA jda;

  /** Real {@link DiscordConfig} bean, with {@code guild} and {@code adminChannel} left null. */
  @Autowired protected DiscordConfig discordConfig;

  // ── Persistence ──────────────────────────────────────────────────────────────

  /** Real repository backed by H2 in-memory — use it to seed or assert database state. */
  @Autowired protected UserRepository userRepository;

  // ── Lifecycle ────────────────────────────────────────────────────────────────

  protected ReplyCallbackAction lastReplyAction;

  @BeforeEach
  void cleanDatabase() {
    userRepository.deleteAll();
  }

  @org.junit.jupiter.api.AfterEach
  void resetAdminChannel() {
    ReflectionTestUtils.setField(discordConfig, "adminChannel", null);
  }

  // ── Event builder ────────────────────────────────────────────────────────────

  /**
   * Builds a fully pre-wired {@link SlashCommandInteractionEvent} mock.
   *
   * <p>The following are stubbed out of the box:
   *
   * <ul>
   *   <li>{@code event.replyEmbeds(Collection)} — used by most commands ({@code List.of(embed)})
   *   <li>{@code event.replyEmbeds(MessageEmbed, MessageEmbed...)} — used by {@code MoneyCommand}
   *   <li>{@code event.reply(String)} — used by {@code EyesCommand}, {@code TankCommand}, etc.
   *   <li>{@code event.getJDA()} — returns the shared {@link #jda} mock
   * </ul>
   *
   * @param commandName value returned by {@link SlashCommandInteractionEvent#getName()}
   * @param userId Discord user ID returned by {@link User#getId()}
   * @return a ready-to-use event mock
   */
  protected SlashCommandInteractionEvent buildEvent(String commandName, String userId) {
    User discordUser = mock(User.class);
    when(discordUser.getId()).thenReturn(userId);
    when(discordUser.getName()).thenReturn("testUser");

    ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
    this.lastReplyAction = replyAction;
    when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);
    when(replyAction.addContent(any())).thenReturn(replyAction);
    when(replyAction.addComponents(anyCollection())).thenReturn(replyAction);
    when(replyAction.setComponents(anyCollection())).thenReturn(replyAction);
    when(replyAction.setFiles(any(net.dv8tion.jda.api.utils.FileUpload[].class)))
        .thenReturn(replyAction);
    when(replyAction.setFiles(anyCollection())).thenReturn(replyAction);

    net.dv8tion.jda.api.interactions.InteractionHook hook =
        mock(net.dv8tion.jda.api.interactions.InteractionHook.class);
    net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction retrieveAction =
        mock(net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction.class);
    net.dv8tion.jda.api.entities.Message msg = mock(net.dv8tion.jda.api.entities.Message.class);
    when(msg.getIdLong()).thenReturn(100L);
    when(hook.retrieveOriginal()).thenReturn(retrieveAction);
    doAnswer(
            inv -> {
              @SuppressWarnings("unchecked")
              java.util.function.Consumer<net.dv8tion.jda.api.entities.Message> consumer =
                  inv.getArgument(0);
              if (consumer != null) consumer.accept(msg);
              return null;
            })
        .when(retrieveAction)
        .queue(any());

    doAnswer(
            inv -> {
              @SuppressWarnings("unchecked")
              java.util.function.Consumer<net.dv8tion.jda.api.interactions.InteractionHook>
                  consumer = inv.getArgument(0);
              if (consumer != null) consumer.accept(hook);
              return null;
            })
        .when(replyAction)
        .queue(any());
    doNothing().when(replyAction).queue();

    final var event = mock(SlashCommandInteractionEvent.class);
    when(event.getName()).thenReturn(commandName);
    when(event.getUser()).thenReturn(discordUser);
    when(event.getJDA()).thenReturn(jda);
    when(event.replyEmbeds(any())).thenReturn(replyAction);
    when(event.replyEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class)))
        .thenReturn(replyAction);
    when(event.reply(anyString())).thenReturn(replyAction);

    return event;
  }

  // ── Option stubs ─────────────────────────────────────────────────────────────

  /**
   * Stubs a STRING slash command option on the given event mock.
   *
   * @param event the event mock to configure
   * @param name the option name
   * @param value the string value to return
   */
  protected void stubStringOption(SlashCommandInteractionEvent event, String name, String value) {
    OptionMapping option = mock(OptionMapping.class);
    when(option.getAsString()).thenReturn(value);
    when(event.getOption(name)).thenReturn(option);
    when(event.getOption(eq(name), any())).thenReturn(value);
    when(event.getOption(eq(name), any(String.class), any())).thenReturn(value);
  }

  /**
   * Stubs a LONG (or INTEGER) slash command option on the given event mock.
   *
   * @param event the event mock to configure
   * @param name the option name
   * @param value the long value to return
   */
  protected void stubLongOption(SlashCommandInteractionEvent event, String name, long value) {
    OptionMapping option = mock(OptionMapping.class);
    when(option.getAsLong()).thenReturn(value);
    when(option.getAsInt()).thenReturn((int) value);
    when(event.getOption(name)).thenReturn(option);
    when(event.getOption(eq(name), any())).thenReturn(value);
    when(event.getOption(eq(name), anyLong(), any())).thenReturn(value);
  }

  // ── Discord infrastructure helpers ───────────────────────────────────────────

  /**
   * Creates a {@link TextChannel} mock and injects it as {@code DiscordConfig.adminChannel}.
   *
   * <p>Required for commands that write to the admin channel (e.g. {@code SendCommand}). The mock
   * captures calls to {@code sendMessageEmbeds} so you can verify them with {@code verify(channel)}
   * in the test.
   *
   * @return the injected {@link TextChannel} mock for verification
   */
  protected TextChannel mockAdminChannel() {
    TextChannel channel = mock(TextChannel.class);
    MessageCreateAction sendAction = mock(MessageCreateAction.class);
    when(channel.sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class)))
        .thenReturn(sendAction);
    doNothing().when(sendAction).queue(any());
    ReflectionTestUtils.setField(discordConfig, "adminChannel", channel);
    return channel;
  }

  // ── Capture helpers ──────────────────────────────────────────────────────────

  /**
   * Captures the first {@link MessageEmbed} sent via {@code event.replyEmbeds(Collection)}.
   *
   * <p>Use this for commands that reply with {@code List.of(embed)}: {@code DailyCommand}, {@code
   * DashboardCommand}, {@code EightBallCommand}, {@code HelpCommand}, {@code PingCommand}.
   *
   * @param event the event mock that was passed to the command
   * @return the first embed of the reply
   */
  protected MessageEmbed captureReplyEmbed(SlashCommandInteractionEvent event) {
    ArgumentCaptor<List<MessageEmbed>> captor = ArgumentCaptor.captor();
    verify(event).replyEmbeds(captor.capture());
    return captor.getValue().getFirst();
  }

  /**
   * Captures the first {@link MessageEmbed} sent via the single-embed varargs overload {@code
   * event.replyEmbeds(MessageEmbed, MessageEmbed...)}.
   *
   * <p>Use this for {@code MoneyCommand} and {@code SpinCommand}, which pass a single embed
   * directly without wrapping it in {@code List.of(...)}.
   *
   * @param event the event mock that was passed to the command
   * @return the embed that was passed to {@code replyEmbeds}
   */
  protected MessageEmbed captureReplyEmbedSingle(SlashCommandInteractionEvent event) {
    ArgumentCaptor<MessageEmbed> captor = ArgumentCaptor.captor();
    verify(event).replyEmbeds(captor.capture(), any(MessageEmbed[].class));
    return captor.getValue();
  }

  /**
   * Captures the text content sent via {@code event.reply(String)}.
   *
   * <p>Use this for commands that send a plain text reply: {@code EyesCommand}, {@code
   * TankCommand}, {@code LinksCommand}, {@code SpinCommand} (insufficient funds).
   *
   * @param event the event mock that was passed to the command
   * @return the text that was passed to {@code reply}
   */
  protected String captureReplyText(SlashCommandInteractionEvent event) {
    ArgumentCaptor<String> captor = ArgumentCaptor.captor();
    verify(event).reply(captor.capture());
    return captor.getValue();
  }

  // ── Admin channel fixture helper ──────────────────────────────────────────────

  protected record AdminChannelFixture(
      SlashCommandInteractionEvent event,
      TextChannel textChannel,
      PermissionOverrideAction permissionOverrideAction) {}

  protected AdminChannelFixture mockAdminChannelFixture(
      String commandName,
      boolean canEveryoneSendMessages,
      String reason,
      java.util.function.BiConsumer<PermissionOverrideAction, net.dv8tion.jda.api.Permission>
          permSetup) {
    final var event = buildEvent(commandName, "user-1");
    final var option = mock(OptionMapping.class);
    final var channelUnion =
        mock(net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion.class);
    final var guild = mock(net.dv8tion.jda.api.entities.Guild.class);
    final var everyoneRole = mock(net.dv8tion.jda.api.entities.Role.class);
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
    when(everyoneRole.hasPermission(textChannel, net.dv8tion.jda.api.Permission.MESSAGE_SEND))
        .thenReturn(canEveryoneSendMessages);
    when(textChannel.upsertPermissionOverride(everyoneRole)).thenReturn(permissionOverrideAction);
    if (permSetup != null) {
      permSetup.accept(permissionOverrideAction, net.dv8tion.jda.api.Permission.MESSAGE_SEND);
    }
    doAnswer(
            invocation -> {
              @SuppressWarnings("unchecked")
              final var success =
                  (java.util.function.Consumer<net.dv8tion.jda.api.entities.PermissionOverride>)
                      invocation.getArgument(0);
              success.accept(mock(net.dv8tion.jda.api.entities.PermissionOverride.class));
              return null;
            })
        .when(permissionOverrideAction)
        .queue(any());
    when(textChannel.sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class)))
        .thenReturn(sendAction);
    doNothing().when(sendAction).queue();

    return new AdminChannelFixture(event, textChannel, permissionOverrideAction);
  }
}
