package xyz.gnarbot.gnar.commands.handlers

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.servers.GuildData
import xyz.gnarbot.gnar.utils.Utils

class CommandHandler(private val guildData: GuildData, private val bot: Bot) {
    val disabled: MutableList<CommandRegistry.CommandEntry> = mutableListOf()

    val enabled get() = bot.commandRegistry.entries.apply { removeAll(disabled) }

    /**
     * @return the amount of successful requests on this command handler.
     */
    var requests = 0
        private set

    /**
     * Call the command based on the message content.
     *
     * @param message Message object.
     * @param content String content of the message.
     */
    fun callCommand(message: Message, content: String) {
        if (!content.startsWith(bot.prefix)) return

        // Tokenize the message.
        val tokens = Utils.fastSplit(content, ' ')

        val label = tokens[0].substring(bot.prefix.length).toLowerCase()

        val args = tokens.subList(1, tokens.size)
        
        val entry = bot.commandRegistry.getEntry(label) ?: return

        if (disabled.contains(entry)) {
            message.respond().error("This command is disabled by the server owner.").queue()
            return
        }

        val cls = entry.cls

        val meta = entry.meta

        val member = message.member

        if (meta.administrator) {
            if (!bot.admins.contains(member.id)) {
                message.respond().error("This command is for bot administrators only.").queue()
                return
            }
        }

        if (meta.channelPermissions.isNotEmpty()) {
            val channel = message.textChannel
            if (!member.hasPermission(message.textChannel, *meta.channelPermissions)) {
                val requirement = meta.channelPermissions.map(Permission::getName)
                message.respond().error("You lack the following permissions: `$requirement` in ${channel.asMention}.").queue()
                return
            }
        }
        if (meta.voicePermissions.isNotEmpty()) {
            val channel = member.voiceState.channel
            if (channel == null) {
                message.respond().error("This command requires you to be in a voice channel.").queue()
                return
            }
            if (!member.hasPermission(channel, *meta.voicePermissions)) {
                val requirement = meta.voicePermissions.map(Permission::getName)
                message.respond().error("You lack the following permissions: `$requirement` in ${channel.name}.").queue()
                return
            }
        }
        if (meta.guildPermissions.isNotEmpty()) {
            if (!member.hasPermission(*meta.guildPermissions)) {
                val requirement = meta.guildPermissions.map(Permission::getName)
                message.respond().error("You lack the following permissions: `$requirement`.").queue()
                return
            }
        }

//        if (meta.level.value > author.category.value) {
//            message.respond().error("Insufficient bot level.\n${meta.level.requirement}")
//            return
//        }

        try {
            requests++
            val cmd = cls.newInstance()

            cmd.jda = guildData.shard
            cmd.shard = guildData.shard
            cmd.guild = guildData.guild
            cmd.guildData = guildData
            cmd.commandHandler = this
            cmd.bot = bot
            cmd.commandMeta = meta

            cmd.execute(message, args)
        } catch (e: PermissionException) {
            message.respond().error("The bot lacks the permission `"
                    + e.permission.getName() + "` required to perform this command.").queue()
        } catch (e: RuntimeException) {
            message.respond().error("**Exception**: " + e.message).queue()
            e.printStackTrace()
        }
    }

    /**
     * Enable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun enableCommand(cmd: CommandRegistry.CommandEntry) {
        disabled -= cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun enableCommand(label: String) {
        enableCommand(bot.commandRegistry.getEntry(label))
    }

    /**
     * Disable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun disableCommand(cmd: CommandRegistry.CommandEntry) {
        disabled += cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun disableCommand(label: String) {
        disableCommand(bot.commandRegistry.getEntry(label))
    }
}