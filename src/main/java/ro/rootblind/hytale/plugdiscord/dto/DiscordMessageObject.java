package ro.rootblind.hytale.plugdiscord.dto;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DiscordMessageObject {

    public static final BuilderCodec<DiscordMessageObject> CODEC =
        BuilderCodec.builder(DiscordMessageObject.class, DiscordMessageObject::new)
            .append(new KeyedCodec<>("Content", Codec.STRING),
                (o, v) -> o.content = v,
                o -> o.content).add()
            .append(new KeyedCodec<>("Sender", Codec.STRING),
                (o, v) -> o.sender = v,
                o -> o.sender).add()
            .append(new KeyedCodec<>("Channel", Codec.STRING),
                (o, v) -> o.channel = v,
                o -> o.channel).add()
            .append(new KeyedCodec<>("Guild", Codec.STRING),
                (o, v) -> o.guild = v,
                o -> o.guild).add()
            .append(new KeyedCodec<>("Snowflakes", DiscordMessageSnowflakes.CODEC),
                (o, v) -> o.snowflakes = v,
                o -> o.snowflakes).add()
            .build();

    public DiscordMessageObject() {
        this.snowflakes = new DiscordMessageSnowflakes();
    }
    
    private String content;
    private String sender;
    private String channel;
    private String guild;
    private DiscordMessageSnowflakes snowflakes;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public DiscordMessageSnowflakes getSnowflakes() {
        return snowflakes;
    }

    public void setSnowflakes(DiscordMessageSnowflakes snowflakes) {
        this.snowflakes = snowflakes;
    }

    
}
