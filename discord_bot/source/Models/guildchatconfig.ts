import type { Result } from "pg";

import database from "../Config/database.js";
import type { GuildChatConfig } from "../Interfaces/database_types.js";
import { errorLogHandle } from "../utility_modules/error_logger.js";

export default async function GuildChatConfig(): Promise<Result<GuildChatConfig>> {
    try {
        const result: Result<GuildChatConfig> = await database.query(
            `CREATE TABLE IF NOT EXISTS guildchatconfig(
                id SERIAL PRIMARY KEY,
                guild BIGINT NOT NULL,
                communication_type INT DEFAULT 0,
                use_server_name BOOLEAN DEFAULT TRUE,
                CONSTRAINT unique_guild_config UNIQUE (guild)
            )`
        );

        return result;
    } catch (error) {
        errorLogHandle(error);
        throw error;
    }
}