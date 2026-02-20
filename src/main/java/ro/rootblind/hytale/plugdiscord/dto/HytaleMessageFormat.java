package ro.rootblind.hytale.plugdiscord.dto;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HytaleMessageFormat {
    public static final BuilderCodec<HytaleMessageFormat> CODEC = BuilderCodec.builder(
        HytaleMessageFormat.class, HytaleMessageFormat::new
    )
    .append(new KeyedCodec<Boolean>("Channel", Codec.BOOLEAN),
        (fmt, value) -> fmt.channel = value,
        fmt -> fmt.channel).add()
    .append(new KeyedCodec<Boolean>("Timestamp", Codec.BOOLEAN),
        (fmt, value) -> fmt.timestamp = value,
        fmt -> fmt.timestamp).add()
    .build();

    // this decides whether or not the discord message that ends up on the hytale server
    // uses these components in its final formatting
    // example message_date [message_time] | in: #channel_name from: @discord_username
    // or from: @discord_username if channel and timestamp are set to false 
    private boolean channel = true;
    private boolean timestamp = true;

    public HytaleMessageFormat() {};
    public HytaleMessageFormat(boolean useChannel, boolean useTimestamp) {
        this.channel = useChannel;
        this.timestamp = useTimestamp;
    }

    public boolean useChannel() {
        return channel;
    }

    public boolean useTimestamp() {
        return timestamp;
    }

    public void setUseChannel(boolean toggle) {
        this.channel = toggle;
    }

    public void setUseTimestamp(boolean toggle) {
        this.timestamp = toggle;
    }

}
