package ro.rootblind.hytale.plugdiscord.servlets;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nitrado.hytale.plugins.webserver.WebServerPlugin;
import net.nitrado.hytale.plugins.webserver.authorization.RequirePermissions;
import net.nitrado.hytale.plugins.webserver.servlets.TemplateServlet;
import ro.rootblind.hytale.plugdiscord.Permissions;
import ro.rootblind.hytale.plugdiscord.PlugConfig;
import ro.rootblind.hytale.plugdiscord.Plugdiscord;
import ro.rootblind.hytale.plugdiscord.dto.DiscordMessagePost;
import ro.rootblind.hytale.plugdiscord.dto.HytaleMessageFormat;
import ro.rootblind.hytale.plugdiscord.dto.PlugCommunicationType;

public class SendMessageServlet extends TemplateServlet {
    private PlugConfig config;
    public SendMessageServlet(WebServerPlugin parent, Plugdiscord plugin) {
        super(parent, plugin);
        this.config = plugin.getConfig().get();
    }

    @Override
    @RequirePermissions(mode = RequirePermissions.Mode.ANY, value = { Permissions.SEND_CHAT_MESSAGE })
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        BsonDocument bson;
        try {
            bson = BsonDocument.parse(new String(req.getInputStream().readAllBytes()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\": \"Invalid JSON\"}");
            return;
        }

        SendMessageRequestValidator validator = sendMessageRequestValidator(bson);
        // if the request is not valid, respond to it
        if (!validator.isValid()) {
            res.setStatus(validator.getStatus());
            res.getWriter().write(validator.getResponse());
            return;
        }

        // the request object is valid
        BsonValue parsedDocument = bson;
        DiscordMessagePost request = DiscordMessagePost.CODEC.decode(parsedDocument, new ExtraInfo());

        // validate the guild id
        if(!request.getMessage().getSnowflakes().getGuildId().equals(this.config.getGuildSnowflake())) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"The guild snowflake is different from the one in the config file.\"}");
            return;
        }

        if(this.config.getCommunicationType() != PlugCommunicationType.BIDIRECTIONAL) {
            // if the communication type is not set for sending discord-hytale messages
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(
                String
                    .format(
                        "{\"error\":\"Violation of communication type - set to %d\"}", 
                        this.config.getCommunicationType()
                    )
                );
            return;
        }

        Map<String, World> worlds = Universe.get().getWorlds();
        for (var world : worlds.entrySet()) {
            world.getValue().sendMessage(Message.raw(formatMessage(request)));
        }

        res.setStatus(validator.getStatus());
        res.getWriter().write(validator.getResponse());
    }

    private class SendMessageRequestValidator {
        private String response;
        private boolean valid;
        private int status;

        protected SendMessageRequestValidator(String response, boolean valid, int status) {
            this.response = response;
            this.valid = valid;
            this.status = status;
        }

        protected boolean isValid() {
            return this.valid;
        }

        protected String getResponse() {
            return this.response;
        }

        protected int getStatus() {
            return this.status;
        }
    }

    // validate whether the json parsed respects all the keys and fields
    protected SendMessageRequestValidator sendMessageRequestValidator(BsonDocument bson) {
        if (!bson.containsKey("Message")) {
            return new SendMessageRequestValidator(
                    "{\"error\":\"The message field is missing\"}",
                    false,
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        // message field is exists
        BsonDocument message = bson.getDocument("Message");

        if (!isStringKeyValid(message, "Content")
                || !isStringKeyValid(message, "Sender")
                || !isStringKeyValid(message, "Channel")
                || !isStringKeyValid(message, "Guild")) {
            return new SendMessageRequestValidator(
                    "{\"error\":\"The message field has missing keys or empty values\"}",
                    false,
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        if (!message.containsKey("Snowflakes")) {
            return new SendMessageRequestValidator(
                    "{\"error\":\"The message.snowflakes field is missing\"}",
                    false,
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        BsonDocument snowflakes = message.getDocument("Snowflakes");
        if (!isStringKeyValid(snowflakes, "Snowflake")
                || !isStringKeyValid(snowflakes, "SenderUserId")
                || !isStringKeyValid(snowflakes, "ChannelId")
                || !isStringKeyValid(snowflakes, "GuildId")
                || !isStringKeyValid(snowflakes, "Timestamp")) {
            return new SendMessageRequestValidator(
                    "{\"error\":\"The message.snowflakes has missing keys or empty values\"}",
                    false,
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        // passing all ifs means it's valid
        BsonDocument responseBson = new BsonDocument()
                .append("status", new BsonString("ok"))
                .append("received", new BsonString(bson.getDocument("Message").getString("Content").getValue()));

        return new SendMessageRequestValidator(responseBson.toJson(), true, HttpServletResponse.SC_OK);
    }

    protected boolean isStringKeyValid(BsonDocument doc, String key) {
        return doc.containsKey(key) && !doc.getString(key).getValue().isBlank();
    }

    public String formatMessage(DiscordMessagePost post) {
        String hytaleMessage = "";
        HytaleMessageFormat format = this.config.getFormat();
        if(format.useTimestamp()) {
            long epochSeconds = Long.parseLong(post.getMessage().getSnowflakes().getTimestamp());
            Instant instant = Instant.ofEpochSecond(epochSeconds);
            ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy [HH:mm]");
            hytaleMessage += dateTime.format(dateFormatter) + " | ";
        }
        if(format.useChannel()) {
            hytaleMessage += "IN: #" + post.getMessage().getChannel() + " | ";
        }

        hytaleMessage += "from: @" + post.getMessage().getSender() + ": " + post.getMessage().getContent();

        return hytaleMessage;
    }
}
