package xyz.gnarbot.gnar.commands.handlers;

import xyz.gnarbot.gnar.members.Clearance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stores the initial data of a command class upon instantiation.
 *
 * @see CommandExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command
{
    /**
     * @return The aliases of this command.
     */
    String[] aliases();
    
    /**
     * @return The description of this command.
     */
    String description() default "No description provided.";
    
    /**
     * @return The usage of this command.
     */
    String usage() default "";
    
    /**
     * @return Flag to show this command in help.
     */
    boolean showInHelp() default true;
    
    /**
     * @return If the command require a separate instance.
     */
    boolean separate() default false;
    
    /**
     * @return if the command have fields that needs
     * to be injected.
     */
    boolean inject() default false;
    
    /**
     * @return The permission required for this command.
     */
    Clearance clearance() default Clearance.USER;
}