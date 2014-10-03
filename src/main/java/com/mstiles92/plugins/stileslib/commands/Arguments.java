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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class used to hold information about command arguments as performed by users.
 */
public class Arguments {
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;

    /**
     * Used to construct a new instance of this class.
     *
     * @param sender the sender of the command
     * @param command the command performed by the sender
     * @param label the label used to perform the command
     * @param args the arguments used with the command
     * @param subCommand the index of the argument list where the subcommand argument list should start
     */
    protected Arguments(CommandSender sender, Command command, String label, String[] args, int subCommand) {
        String[] subArgs = new String[args.length - subCommand];
        System.arraycopy(args, subCommand, subArgs, 0, args.length - subCommand);

        StringBuffer buffer = new StringBuffer();
        buffer.append(label);
        for (int i = 0; i < subCommand; i++) {
            buffer.append(".").append(args[i]);
        }

        this.sender = sender;
        this.command = command;
        this.label = buffer.toString();
        this.args = subArgs;
    }

    /**
     * Get the sender of the command.
     *
     * @return the sender of the command
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Get the command performed by the sender.
     *
     * @return the command performed
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Get the label used to perform the command.
     *
     * @return the label used to perform the command
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the array of arguments used with the command.
     *
     * @return an array of the arguments used with the command
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Check whether the sender is a player.
     *
     * @return true if the sender is a player, false otherwise
     */
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    /**
     * Get the player who performed the command, if the sender was a player.
     *
     * @return the player object who performed the command, or null if the sender was not a player
     */
    public Player getPlayer() {
        return isPlayer() ? (Player) sender : null;
    }
}
