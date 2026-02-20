package ro.rootblind.hytale.plugdiscord;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import ro.rootblind.hytale.plugdiscord.dto.HytaleMessageFormat;
import ro.rootblind.hytale.plugdiscord.dto.PlugCommunicationType;

public class PlugConfig {
    public static final BuilderCodec<PlugConfig> CODEC = BuilderCodec.builder(
        PlugConfig.class, PlugConfig::new
    )
    .append(
        new KeyedCodec<String>("ServerName", Codec.STRING),
        (config, value) -> config.serverName = value,
        config -> config.serverName
    ).add()
    .append(
        new KeyedCodec<String>("GuildSnowflake", Codec.STRING),
        (config, value) -> config.guildSnowflake = value,
        config -> config.guildSnowflake).add()
    .append(new KeyedCodec<String>("DiscordSendMessageRoute", Codec.STRING), 
        (config, value) -> config.discordSendMessageRoute = value, 
        config -> config.discordSendMessageRoute).add()
    .append(new KeyedCodec<String>("CommunicationType", Codec.STRING),
        (config, value) -> config.communicationType = PlugCommunicationType.valueOf(value),
        config -> config.communicationType.name()).add()
    .append(
        new KeyedCodec<HytaleMessageFormat>("Format", HytaleMessageFormat.CODEC),
        (config, value) -> config.format = value,
        config -> config.format
    ).add().build();

    private String serverName = "Hytale";
    private String guildSnowflake;

    private String discordSendMessageRoute;
    private PlugCommunicationType communicationType = PlugCommunicationType.OFF;
    private HytaleMessageFormat format = new HytaleMessageFormat();

    public PlugConfig() {}

    public PlugConfig(String guildId, String discordSendMessageRoute, PlugCommunicationType type) {
        this.guildSnowflake = guildId;
        this.discordSendMessageRoute = discordSendMessageRoute;
        this.communicationType = type;
        this.format = new HytaleMessageFormat();
    }

    public PlugConfig(
        String guildId, 
        String discordSendMessageRoute, 
        HytaleMessageFormat format, 
        PlugCommunicationType type
    ) {
        this.guildSnowflake = guildId;
        this.discordSendMessageRoute = discordSendMessageRoute;
        this.format = format;
        this.communicationType = type;
    }

    // getters
    public String getGuildSnowflake() {
        return this.guildSnowflake;
    }

    public String getDiscordSendMessageRoute() {
        return this.discordSendMessageRoute;
    }

    public PlugCommunicationType getCommunicationType() {
        return this.communicationType;
    }

    public HytaleMessageFormat getFormat() {
        return this.format;
    }

    // setters
    public void setGuildSnowflake(String id) {
        this.guildSnowflake = id;
    }

    public void setDiscordSendMessageRoute(String route) {
        this.discordSendMessageRoute = route;
    }

    public void setCommunicationType(PlugCommunicationType type) {
        this.communicationType = type;
    }

    public void setFormat(HytaleMessageFormat format) {
        this.format = format;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
