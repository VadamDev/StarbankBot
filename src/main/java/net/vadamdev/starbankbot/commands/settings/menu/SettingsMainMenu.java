package net.vadamdev.starbankbot.commands.settings.menu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.jdautils.smart.SmartInteractionsManager;
import net.vadamdev.jdautils.smart.entities.SmartButton;
import net.vadamdev.jdautils.smart.messages.MessageContent;
import net.vadamdev.jdautils.smart.messages.SmartMessage;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.transaction.DistributionMode;
import net.vadamdev.starbankbot.utils.StarbankEmbed;
import net.vadamdev.starbankbot.utils.Utils;

import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 11/01/2024
 */
public class SettingsMainMenu extends AbstractSettingsMenu {
    public static final SmartMessage SETTINGS_MAIN_MENU = SmartMessage.fromProvider(new SettingsMainMenu());

    static final SmartMessage SETTINGS_LANGUAGE_MENU = SmartMessage.fromProvider(new SettingsLanguageMenu());
    static final SmartMessage SETTINGS_TRANSACTION_MENU = SmartMessage.fromProvider(new SettingsTransactionMenu());
    static final SmartMessage SETTINGS_OVERRIDE_MENU = SmartMessage.fromProvider(new SettingsAllowOverrideMenu());

    @Override
    public void init(Guild guild, MessageContent contents) {
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(guild);
        final Lang lang = config.getLang();
        final DistributionMode distributionMode = config.getDistributionMode();

        /*
           Embed
         */

        contents.setEmbed(new StarbankEmbed()
                .setTitle("Star Bank - " + lang.localize("settings.name"))
                .setDescription(
                        lang.localize("settings.mainMenu.description", str -> str
                                .replace("%lang_name%", lang.getDisplayName())
                                .replace("%lang_flag%", lang.getFlag().getName())
                                .replace("%distribution_mode%", distributionMode.getDisplayName())
                                .replace("%percentage_line%", distributionMode.equals(DistributionMode.PERCENTAGE) ? lang.localize("settings.mainMenu.percentageLine", str1 -> str1.replace("%percentage%", String.valueOf(config.TRANSACTION_PERCENTAGE))) : "")
                                .replace("%allow_override%", Utils.displayBoolean(config.TRANSACTION_ALLOW_OVERRIDE)))
                )
                .setColor(StarbankEmbed.CONFIG_COLOR)
                .build());

        /*
           Components
         */

        contents.addComponents(
                SmartButton.of(Button.primary("StarBank-Settings-Language", lang.getFlag()), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SETTINGS_LANGUAGE_MENU.open(event.getMessage());
                }),

                SmartButton.of(Button.primary("StarBank-Settings-Transaction", Emoji.fromUnicode("\uD83D\uDCB5")), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SETTINGS_TRANSACTION_MENU.open(event.getMessage());
                }),

                SmartButton.of(Button.danger("StarBank-Settings-Close", lang.localize("settings.close")), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();

                    final Message message = event.getMessage();

                    SmartInteractionsManager.unregisterSmartMessage(message.getGuild().getId(), message.getId());
                    message.editMessageComponents(message.getComponents().stream().map(LayoutComponent::asDisabled).collect(Collectors.toList())).queue();
                })
        );
    }
}
