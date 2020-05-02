package xyz.gnarbot.gnar.entities

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import xyz.gnarbot.gnar.Launcher
import xyz.gnarbot.gnar.entities.sharding.BucketedController
import xyz.gnarbot.gnar.utils.IntentHelper
import java.util.*

class ExtendedShardManager(private val shardManager: ShardManager): ShardManager by shardManager {
    fun openPrivateChannel(userId: Long): RestAction<PrivateChannel> {
        return shards.first { it != null }.openPrivateChannelById(userId)
    }

    companion object {
        fun create(token: String, apply: DefaultShardManagerBuilder.() -> Unit = {}): ExtendedShardManager {
            return DefaultShardManagerBuilder.create(token, IntentHelper.enabledIntents)
                .apply {
                    // General
                    setActivityProvider { Activity.playing(Launcher.configuration.game.format(it)) }

                    // Gateway
                    setSessionController(BucketedController(Launcher.configuration.bucketFactor, 215616923168276480L))
                    setShards(Launcher.credentials.shardStart, Launcher.credentials.shardEnd - 1)
                    setShardsTotal(Launcher.credentials.totalShards)
                    setMaxReconnectDelay(32)

                    // Audio
                    setAudioSendFactory(NativeAudioSendFactory(1000))

                    // Performance
                    setBulkDeleteSplittingEnabled(false)
                    disableCache(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS))
                }
                .apply(apply)
                .build()
                .let(::ExtendedShardManager)
        }
    }
}