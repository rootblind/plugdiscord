package ro.rootblind.hytale.plugdiscord.dto;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DiscordMessageSnowflakes {

    public static final BuilderCodec<DiscordMessageSnowflakes> CODEC =
        BuilderCodec.builder(DiscordMessageSnowflakes.class, DiscordMessageSnowflakes::new)
            .append(new KeyedCodec<String>("Snowflake", Codec.STRING),
                (o, v) -> o.snowflake = v,
                o -> o.snowflake).add()
            .append(new KeyedCodec<>("SenderUserId", Codec.STRING),
                (o, v) -> o.senderUserId = v,
                o -> o.senderUserId).add()
            .append(new KeyedCodec<>("ChannelId", Codec.STRING),
                (o, v) -> o.channelId = v,
                o -> o.channelId).add()
            .append(new KeyedCodec<>("GuildId", Codec.STRING),
                (o, v) -> o.guildId = v,
                o -> o.guildId).add()
            .append(new KeyedCodec<>("Timestamp", Codec.STRING),
                (o, v) -> o.timestamp = v,
                o -> o.timestamp).add()
            .build();
            
    public DiscordMessageSnowflakes() {}

    public String snowflake;
    public String senderUserId;
    public String channelId;
    public String guildId;
    public String timestamp;

    public String getSnowflake() {
        return snowflake;
    }
    public void setSnowflake(String snowflake) {
        this.snowflake = snowflake;
    }
    public String getSenderUserId() {
        return senderUserId;
    }
    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }
    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public String getGuildId() {
        return guildId;
    }
    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    
}
