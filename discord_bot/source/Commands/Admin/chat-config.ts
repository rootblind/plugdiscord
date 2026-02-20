import { PermissionFlagsBits, SlashCommandBuilder } from "discord.js";
import { ChatCommand } from "../../Interfaces/command.js";
import GuildChatConfigRepo from "../../Repositories/guildchatconfig.js";
import { embed_chatconfig, embed_error } from "../../utility_modules/embed_builders.js";
import { CommunicationType } from "../../Interfaces/database_types.js";

const chat_config: ChatCommand = {
    data: new SlashCommandBuilder()
        .setName("chat-config")
        .setDescription("Change the configuration of how the cross-platform chat works for bot's end.")
        .setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
        .addSubcommand(subcommand =>
            subcommand.setName("discord")
                .setDescription("Discord related configuration")
                .addStringOption(option =>
                    option.setName("communication-type")
                        .setDescription("0 - OFF | 1 - Bidirectional")
                        .addChoices(
                            {
                                name: "off",
                                value: "0"
                            },
                            {
                                name: "bidirectional",
                                value: "1"
                            }
                        )
                        .setRequired(true)
                )
                .addBooleanOption(option =>
                    option.setName("use-server-name")
                        .setDescription("Whether the messages should contain the server name of origin")
                )
        )
        .addSubcommand(subcommand =>
            subcommand.setName("info")
                .setDescription("Show the current chat configuration.")
        )
        .toJSON(),
    async execute(interaction) {
        await interaction.deferReply();

        const guild = interaction.guild;
        const options = interaction.options;
        const subcommand = options.getSubcommand();

        if (subcommand === "discord") { // discord-side configuration
            const communication_type: CommunicationType =
                Number(options.getString("communication-type", true)) as CommunicationType; // the choice guarantees
            const use_server_name = options.getBoolean("use-server-name", false) ?? true; // defaults to true / enable

            const config = await GuildChatConfigRepo.insert(guild.id, communication_type, use_server_name);

            await interaction.editReply({
                embeds: [
                    embed_chatconfig(config)
                ]
            });
        } else if (subcommand === "info") { // show the current configuration for both server and discord side
            const config = await GuildChatConfigRepo.getGuildConfig(guild.id);
            if (config) {
                return await interaction.editReply({
                    embeds: [
                        embed_chatconfig(config)
                    ]
                });
            } else {
                return await interaction.editReply({
                    embeds: [
                        embed_error(
                            "Run `/chat-config discord` before using this command",
                            "No configuration found for this guild"
                        )
                    ]
                });
            }
        }
    },

    cooldown: 5,
    userPermissions: [
        PermissionFlagsBits.Administrator
    ],
    botPermissions: [
        PermissionFlagsBits.SendMessages
    ]
}

export default chat_config;