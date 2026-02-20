import type { Snowflake } from "discord.js";
import type { ServerConnection } from "../Interfaces/database_types.js";
import database from "../Config/database.js";
import { SelfCache } from "../Config/SelfCache.js";
import { encryptor } from "../utility_modules/utility_methods.js";

const serverConnectionCache = new SelfCache<string, ServerConnection | null>(24 * 60 * 60_000);

class ServerConnectionRepository {
    key(guildId: string, serverName: string): string {
        return `${guildId}:${serverName}`;
    }
    /**
     * The raw database
     * @param guildId Guild Snowflake
     * @returns ServerConnection object
     */
    async getServerConnection(guildId: Snowflake, serverName: string): Promise<ServerConnection | null> {
        const cache = serverConnectionCache.get(this.key(guildId, serverName));
        if (cache !== undefined) return cache;

        const { rows: data } = await database.query<ServerConnection>(
            `SELECT * FROM serverconnection WHERE guild=$1 AND server_name=$2`,
            [guildId, serverName]
        );

        if (data.length && data[0]) {
            serverConnectionCache.set(this.key(guildId, serverName), data[0]);
            return data[0];
        } else {
            serverConnectionCache.set(this.key(guildId, serverName), null);
            return null;
        }
    }

    /**
     * Insert or update guild's connection to hytale server
     * @param guildId Guild Snowflake
     * @param host Host as a string
     * @param port Host port
     */
    async insert(
        guildId: Snowflake,
        channelId: Snowflake,
        webhookId: Snowflake,
        server_name: string,
        host: string,
        port: number,
        endpoint: string,
        secret: Buffer
    ) {
        const encrypted_host = encryptor(host);
        const { rows: newRow } = await database.query<ServerConnection>(
            `INSERT INTO serverconnection (guild, channel, webhook, server_name, host, port, endpoint, secret)
                VALUES($1, $2, $3, $4, $5, $6, $7, $8)
                    ON CONFLICT (guild, host, port, endpoint)
                    DO UPDATE SET 
                        channel = EXCLUDED.channel, 
                        webhook = EXCLUDED.webhook, 
                        server_name = EXCLUDED.server_name,
                        secret = EXCLUDED.secret
            RETURNING *;`,
            [guildId, channelId, webhookId, server_name, encrypted_host, port, endpoint, secret]
        );

        serverConnectionCache.set(this.key(guildId, newRow[0]!.server_name), newRow[0]!);
        return newRow[0]!;
    }

    /**
     * 
     * @param guildId Guild Snowflake
     * @returns The hex string of guild's secret
     */
    async getSecretHash(guildId: Snowflake, serverName: string): Promise<string | null> {
        const cache = serverConnectionCache.get(this.key(guildId, serverName));
        if (cache !== undefined) {
            if (cache === null) return null;

            return cache.secret.toString("hex");
        }

        const { rows: data } = await database.query<ServerConnection>(
            `SELECT secret FROM serverconnection WHERE guild=$1 AND server_name=$2`,
            [guildId, serverName]
        );

        if (data.length && data[0]) {
            return data[0].secret.toString("hex");
        } else {
            serverConnectionCache.set(guildId, null);
            return null;
        }
    }

    async deleteGuild(guildId: Snowflake) {
        await database.query(`DELETE FROM serverconnection WHERE guild=$1`, [guildId]);
    }
}

const ServerConnectionRepo = new ServerConnectionRepository();
export default ServerConnectionRepo;