package ro.rootblind.hytale.plugdiscord;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.util.Config;

import jakarta.servlet.http.HttpServlet;
import net.nitrado.hytale.plugins.webserver.WebServerPlugin;

import ro.rootblind.hytale.plugdiscord.servlets.SendMessageServlet;
import ro.rootblind.hytale.plugdiscord.servlets.TestServlet;;

public class Plugdiscord extends JavaPlugin {
    private WebServerPlugin webServerPlugin;
    private final Config<PlugConfig> config = this.withConfig("PlugConfig", PlugConfig.CODEC);

    public Plugdiscord(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();
        // set the config
        config.save();

        // register commands

        // register events

        // register servlets for the webserver
        this.servletRegisterHandler();
    }

    private void servletRegisterHandler() {
        var plugin = PluginManager.get().getPlugin(new PluginIdentifier("Nitrado", "WebServer"));

        if(!(plugin instanceof WebServerPlugin webServer)) {
            return;
        }

        this.webServerPlugin = webServer;
        
        attachRoute("/test", new TestServlet(webServerPlugin, this)); // used to test the connection
        attachRoute("/send/message", new SendMessageServlet(this.webServerPlugin, this));
    }

    public Config<PlugConfig> getConfig() {
        return this.config; // use .get() to access the data
    }

    private void attachRoute(@Nonnull String route, @Nonnull HttpServlet servlet) {
        try {
            webServerPlugin.addServlet(this, route, servlet);
        } catch(Exception e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Failed to register route " + route);
        }
    }
}
