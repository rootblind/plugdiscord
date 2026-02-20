package ro.rootblind.hytale.plugdiscord.dto;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DiscordMessagePost {
    public static final BuilderCodec<DiscordMessagePost> CODEC =
        BuilderCodec.builder(
            DiscordMessagePost.class, 
            new java.util.function.Supplier<DiscordMessagePost>() {
                @Override
                public DiscordMessagePost get() {
                    return new DiscordMessagePost();
                }        
            }
        )
        .append(new KeyedCodec<>("Message", DiscordMessageObject.CODEC),
            (o, v) -> o.message = v,
            o -> o.message).add()
        .build();
    public DiscordMessagePost() {
            this.message = new DiscordMessageObject();
    }

    private DiscordMessageObject message;
    
    public DiscordMessageObject getMessage() {
        return message;
    }

    public void setMessage(DiscordMessageObject message) {
        this.message = message;
    }

    
}
