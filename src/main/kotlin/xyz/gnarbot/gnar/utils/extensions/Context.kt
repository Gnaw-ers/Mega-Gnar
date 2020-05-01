package xyz.gnarbot.gnar.utils.extensions

import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.sharding.ShardManager
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.db.Database
import xyz.gnarbot.gnar.db.guilds.GuildData

val Context.db: Database
    get() = Bot.getInstance().db()

val Context.shardManager: ShardManager
    get() = this.jda.shardManager!!

val Context.data: GuildData
    get() = Bot.getInstance().options.ofGuild(guild!!)