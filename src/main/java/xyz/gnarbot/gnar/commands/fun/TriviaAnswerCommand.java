package xyz.gnarbot.gnar.commands.fun;

import com.google.inject.Inject;
import xyz.gnarbot.gnar.handlers.commands.Command;
import xyz.gnarbot.gnar.handlers.commands.CommandExecutor;
import xyz.gnarbot.gnar.handlers.servers.Host;
import xyz.gnarbot.gnar.utils.Note;
import xyz.gnarbot.gnar.utils.TriviaQuestions;

@Command(aliases = "wow")
public class TriviaAnswerCommand extends CommandExecutor {

    @Inject
    public Host host;

    @Override
    public void execute(Note msg, String label, String[] args) {
        if(!TriviaQuestions.isSetup()) {
            TriviaQuestions.init();
        }

        try {
            int key = Integer.valueOf(args[0]);
            msg.reply(TriviaQuestions.getAnswer(key));
        } catch (Exception e) {
            msg.reply("Please enter a number.");
        }
    }

}