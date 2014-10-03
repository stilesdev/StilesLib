/*
 * This document is a part of the source code and related artifacts for StilesLib, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/StilesLib
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the license.
 */

package com.mstiles92.plugins.stileslib.commands;

import com.mstiles92.plugins.stileslib.commands.annotations.Command;
import com.mstiles92.plugins.stileslib.commands.annotations.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/**
 * The main class of this command framework, used for registering and handling all commands.
 */
public class CommandRegistry {
    private Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<>();
    private CommandMap map;
    private Plugin plugin;
    private Logger logger;
    private String noPermissionDefault = null;
    private String playerOnlyDefault = null;

    /**
     * The main constructor of the command registry.
     *
     * @param plugin the plugin that the commands should be registered under
     */
    public CommandRegistry(Plugin plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();

        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();

            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set a message to be sent to the user when they do not have permission for a command, overriding the value set up
     * in the Command annotation. Set the message to null to go back to using the values in the annotations.
     *
     * @param message the message to be used for players with no permission
     */
    public void setDefaultNoPermissionMessage(String message) {
        noPermissionDefault = message;
    }

    /**
     * Set a message to be sent to a non-player command sender when the command is marked as player-only, overriding the
     * value set up in the Command annotation. Set the message to null to go back to using the values in the annotations.
     *
     * @param message the message to be used for non-player command senders attempting to use a player-only command
     */
    public void setDefaultPlayerOnlyMessage(String message) {
        playerOnlyDefault = message;
    }

    /**
     * Get a set of all of labels for all of the commands that are registered.
     *
     * @return a Set of the labels of all registered commands
     */
    public Set<String> getCommandLabels() {
        return commandMap.keySet();
    }

    /**
     * Method that should be called to handle any command registered with this framework.
     * This should be called in the plugin's main class, from the overridden onCommand method, forwarding all arguments
     * to the parameters in this method.
     */
    public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuilder sb = new StringBuilder();
            sb.append(label.toLowerCase());
            for (int j = 0; j < i; j++) {
                sb.append(".").append(args[j].toLowerCase());
            }
            String commandLabel = sb.toString();

            if (commandMap.containsKey(commandLabel)) {
                Map.Entry<Method, Object> entry = commandMap.get(commandLabel);
                Command command = entry.getKey().getAnnotation(Command.class);
                if (!sender.hasPermission(command.permission())) {
                    sender.sendMessage((sender instanceof Player ? ChatColor.RED : "") + (noPermissionDefault == null ? command.noPermission() : noPermissionDefault));
                    return true;
                }

                if (command.playerOnly() && !(sender instanceof Player)) {
                    sender.sendMessage((playerOnlyDefault == null) ? command.playerOnlyMessage() : playerOnlyDefault);
                    return true;
                }

                try {
                    entry.getKey().invoke(entry.getValue(), new Arguments(sender, cmd, label, args, commandLabel.split("\\.").length - 1));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return true;
            }
        }

        return true;
    }

    /**
     * Registers all of the commands and tab completers annotated in the provided implementation of CommandHandler.
     *
     * @param handler the CommandHandler implementation to register
     */
    public void registerCommands(CommandHandler handler) {
        for (Method m : handler.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                Command command = m.getAnnotation(Command.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != Arguments.class) {
                    logger.warning("Unable to register command " + m.getName() + ". Unexpected method arguments!");
                    continue;
                }

                registerCommand(command, command.name(), m, handler);
                for (String alias : command.aliases()) {
                    registerCommand(command, alias, m, handler);
                }
            } else if (m.getAnnotation(TabCompleter.class) != null) {
                TabCompleter completer = m.getAnnotation(TabCompleter.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes().length == 0 || m.getParameterTypes()[0] != Arguments.class) {
                    logger.warning("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                    continue;
                }

                if (m.getReturnType() != List.class) {
                    logger.warning("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    continue;
                }

                registerTabCompleter(completer.name(), m, handler);
                for (String alias : completer.aliases()) {
                    registerTabCompleter(alias, m, handler);
                }
            }
        }
    }

    /**
     * Registers help contents for all of the registered commands.
     */
    public void registerHelp() {
        Set<HelpTopic> help = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

        for (String s : commandMap.keySet()) {
            if (!s.contains(".")) {
                org.bukkit.command.Command cmd = map.getCommand(s);
                HelpTopic topic = new GenericCommandHelpTopic(cmd);
                help.add(topic);
            }
        }

        IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help, "Below is a list of all " + plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

    /**
     * Register an individual method as a handler of a command.
     *
     * @param command the Command annotation for the method
     * @param label the label of the command
     * @param m the method to be used to handle the command
     * @param handler the class that the method came from
     */
    private void registerCommand(Command command, String label, Method m, CommandHandler handler) {
        Map.Entry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, handler);
        commandMap.put(label.toLowerCase(), entry);
        String commandLabel = label.split("\\.")[0].toLowerCase();

        if (map.getCommand(commandLabel) == null) {
            org.bukkit.command.Command cmd = new CustomCommand(commandLabel, plugin);
            map.register(plugin.getName(), cmd);
        }

        if (!command.description().equals("") && commandLabel.equalsIgnoreCase(label)) {
            map.getCommand(commandLabel).setDescription(command.description());
        }

        if (!command.usage().equals("") && commandLabel.equalsIgnoreCase(label)) {
            map.getCommand(commandLabel).setUsage(command.usage());
        }
    }

    /**
     * Register an individual method as a tab completer for a command.
     *
     * @param label the label of the command
     * @param m the method to be used to handle the tab completion for the command
     * @param handler the class that the method came from
     */
    private void registerTabCompleter(String label, Method m, CommandHandler handler) {
        String commandLabel = label.split("\\.")[0].toLowerCase();

        if (map.getCommand(commandLabel) == null) {
            org.bukkit.command.Command cmd = new CustomCommand(commandLabel, plugin);
            map.register(plugin.getName(), cmd);
        }

        if (map.getCommand(commandLabel) instanceof CustomCommand) {
            CustomCommand command = (CustomCommand) map.getCommand(commandLabel);
            if (command.completer == null) {
                command.completer = new CustomCompleter();
            }
            command.completer.addCompleter(label, m, handler);
        } else if (map.getCommand(commandLabel) instanceof PluginCommand) {
            try {
                Object command = map.getCommand(commandLabel);
                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command) == null) {
                    CustomCompleter completer = new CustomCompleter();
                    completer.addCompleter(label, m, handler);
                    field.set(command, completer);
                } else if (field.get(command) instanceof CustomCompleter) {
                    CustomCompleter completer = (CustomCompleter) field.get(command);
                    completer.addCompleter(label, m, handler);
                } else {
                    logger.warning("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
