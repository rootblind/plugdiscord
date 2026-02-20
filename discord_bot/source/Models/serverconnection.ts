import type { Result } from "pg";

import database from "../Config/database.js";
import type { ServerConnection } from "../Interfaces/database_types.js";
import { errorLogHandle } from "../utility_modules/error_logger.js";

export default async function ServerConnection(): Promise<Result<ServerConnection>> {
    try {
        const result: Result<ServerConnection> = await database.query(
            `CREATE TABLE IF NOT EXISTS serverconnection(
                id SERIAL PRIMARY KEY,
                guild BIGINT NOT NULL,
                channel BIGINT NOT NULL,
                webhook BIGINT NOT NULL,
                host BYTEA NOT NULL,
                port INT NOT NULL,
                endpoint TEXT NOT NULL,
                secret BYTEA NOT NULL,
                server_name TEXT NOT NULL,

                CONSTRAINT unique_connection UNIQUE (guild, host, port, endpoint)
            )`
        );

        return result;
    } catch (error) {
        errorLogHandle(error);
        throw error;
    }
}