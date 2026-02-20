package ro.rootblind.hytale.plugdiscord.servlets;

import java.io.IOException;

import org.bson.BsonDocument;
import org.bson.BsonString;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nitrado.hytale.plugins.webserver.WebServerPlugin;
import net.nitrado.hytale.plugins.webserver.authorization.RequirePermissions;
import net.nitrado.hytale.plugins.webserver.servlets.TemplateServlet;
import ro.rootblind.hytale.plugdiscord.Permissions;
import ro.rootblind.hytale.plugdiscord.PlugConfig;
import ro.rootblind.hytale.plugdiscord.Plugdiscord;
import ro.rootblind.hytale.plugdiscord.dto.PlugCommunicationType;

public class TestServlet extends TemplateServlet {
    private PlugConfig config;
    public TestServlet(WebServerPlugin parent, Plugdiscord plugin) {
        super(parent, plugin);
        this.config = plugin.getConfig().get();
    }

    @Override
    @RequirePermissions(
        mode = RequirePermissions.Mode.ANY,
        value = { Permissions.READ_BASIC }
    )
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        var guildId = req.getParameter("GuildId");
        
        if(guildId == null || guildId.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(
                new BsonDocument()
                    .append("error", new BsonString("GuildId parameter is missing or is blank"))
                    .toJson()
            );

            return;
        }

        if(config.getCommunicationType().equals(PlugCommunicationType.OFF)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write(
                new BsonDocument()
                    .append("error", new BsonString("The communication type is set to OFF"))
                    .toJson()
            );
            return;
        }

        if(!config.getGuildSnowflake().equals(guildId)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write(
                new BsonDocument()
                    .append("error", new BsonString("The GuildId received is not the one in the config file"))
                    .toJson()
            );
            return;
        }

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(
            new BsonDocument()
                .append("ServerName", new BsonString(config.getServerName()))
                .toJson()
        );
    }
}
