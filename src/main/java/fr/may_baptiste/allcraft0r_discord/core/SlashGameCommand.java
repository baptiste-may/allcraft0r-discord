package fr.may_baptiste.allcraft0r_discord.core;

import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashGameCommand extends SlashCommand {

  public static final long MINIMAL_BET = 25;

  @Override
  public SlashCommandData getCommandData() {
    return super.getCommandData()
        .addOptions(
            new OptionData(OptionType.INTEGER, "mise", "La mise de départ", false)
                .setMinValue(MINIMAL_BET));
  }

  protected Optional<Long> canPlay(SlashCommandInteractionEvent event, MoneyService moneyService) {
    final long bet = event.getOption("mise", MINIMAL_BET, OptionMapping::getAsLong);
    final long userMoney = moneyService.getMoney(event.getUser().getId());

    if (bet > userMoney) {
      event.reply("❌ Tu n'as pas assez de redstone !").setEphemeral(true).queue();
      return Optional.empty();
    }

    moneyService.addMoney(event.getUser().getId(), -bet);
    return Optional.of(bet);
  }
}
