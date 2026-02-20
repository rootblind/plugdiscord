// Interfaces and types to respect the database tables

import type { Snowflake } from "discord.js"

interface GuildTable {
    id?: number,
    guild: Snowflake
}

interface GuildChannelTable extends GuildTable {
    channel: Snowflake
}

interface GuildRolePair {
    guild: Snowflake,
    role: Snowflake
}

type GuildMessageTable =
    | (GuildChannelTable & { messageid: Snowflake })
    | (GuildChannelTable & { message: Snowflake })

type GuildChannelWithType =
    | (GuildChannelTable & { channeltype: string })
    | (GuildChannelTable & { type: string })
    | (GuildChannelTable & { eventtype: string })

interface ColumnValuePair {
    column: string,
    value: unknown
}

export type CommunicationType = 0 | 1;

/**
 *  @param communication_type 0 - OFF | 1 - Bidirectional
 *  @param use_server_name whether to specify the server name of origin in messages
 */
export interface GuildChatConfig extends GuildTable {
    communication_type: CommunicationType,
    use_server_name: boolean
}

export interface BotConfig {
    id?: number,
    backup_db_schedule: string | null
}

export interface ServerConnection extends GuildTable {
    channel: string,
    webhook: string,
    server_name: string,
    host: Buffer,
    port: number,
    endpoint: string,
    secret: Buffer
}

export interface HytaleHostPort {
    host: string,
    port: number
}

export type {
    GuildTable,
    GuildChannelTable,
    GuildMessageTable,
    GuildChannelWithType,
    GuildRolePair,
    ColumnValuePair
}