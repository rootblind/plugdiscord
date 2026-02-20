import type { Snowflake } from "discord.js";
import database from "../Config/database.js";
import { SelfCache } from "../Config/SelfCache.js";
import { GuildChatConfig } from "../Interfaces/database_types.js";

const guildChatConfigCache = new SelfCache<string, GuildChatConfig | null>(24 * 60 * 60_000);

class GuildChatConfigRepository {
    /**
     * 
     * @param guildId Guild Snowflake
     * @returns The guild chat config object or null if there is no configuration for the given guild
     */
    async getGuildConfig(guildId: Snowflake): Promise<GuildChatConfig | null> {
        const cache = guildChatConfigCache.get(`${guildId}`);

        if (cache !== undefined) return cache;

        const { rows: data } = await database.query<GuildChatConfig>(`SELECT * FROM guildchatconfig WHERE guild=$1`,
            [guildId]
        );

        if (data.length && data[0]) {
            const config: GuildChatConfig = {
                guild: data[0].guild,
                communication_type: data[0].communication_type,
                use_server_name: data[0].use_server_name
            }

            guildChatConfigCache.set(`${guildId}`, config);
            return config;
        } else {
            guildChatConfigCache.set(`${guildId}`, null);
            return null;
        }
    }

    /**
     * Initiate guild row
     * @param guildId Guild Snowflake
     */
    async insertGuildDefault(guildId: Snowflake): Promise<GuildChatConfig> {
        const config: GuildChatConfig = {
            guild: guildId,
            communication_type: 0,
            use_server_name: true
        }

        guildChatConfigCache.set(`${guildId}`, config);

        await database.query<GuildChatConfig>(
            `INSERT INTO guildchatconfig(guild, communication_type, use_server_name) 
                VALUES($1, $2, $3)
                ON CONFLICT (guild)
                DO UPDATE SET
                    communication_type = EXCLUDED.communication_type,
                    use_server_name = EXCLUDED.use_server_name
                RETURNING *;`,
            [guildId, config.communication_type, config.use_server_name]
        );

        return config;
    }

    /**
     * Insert custom configuration
     * @param guildId Guild Snowflake
     * @param communication_type 0 - OFF | 1 - Bidirectional
     * @param use_server_name whether to add the server name to messages or not
     */
    async insert(
        guildId: Snowflake,
        communication_type: 0 | 1 = 0,
        use_server_name: boolean = true
    ): Promise<GuildChatConfig> {

        const { rows: data } = await database.query<GuildChatConfig>(
            `INSERT INTO guildchatconfig(guild, communication_type, use_server_name)
                VALUES($1, $2, $3)
                    ON CONFLICT (guild)
                    DO UPDATE SET
                        communication_type = EXCLUDED.communication_type,
                        use_server_name = EXCLUDED.use_server_name
            RETURNING *;`,
            [guildId, communication_type, communication_type, use_server_name]
        );

        guildChatConfigCache.set(guildId, data[0]!);
        return data[0]!;
    }
}

const GuildChatConfigRepo = new GuildChatConfigRepository();
export default GuildChatConfigRepo;