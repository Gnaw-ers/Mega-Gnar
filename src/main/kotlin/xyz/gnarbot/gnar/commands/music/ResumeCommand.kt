package xyz.gnarbot.gnar.commands.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["resume"],
        description = "Resume the music queue."
)
@BotInfo(
        id = 476,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        djLock = true
)
class ResumeCommand : MusicCommandExecutor(false, false, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val scheduler = manager.scheduler

        if (scheduler.queue.isEmpty()) {
            context.send().issue("The queue is empty.\n$PLAY_MESSAGE").queue()
            return
        }

        if (scheduler.lastTrack != null) {
            context.send().error("There's nothing to resume as the player has been active here!")
            return
        }

        //Poll next from queue and force that track to play.
        scheduler.nextTrack()

        context.send().info("Queue has been resumed.").queue()
    }
}