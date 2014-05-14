/*
 * This document is a part of the source code and related artifacts for CommonUtils, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/CommonUtils
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

package com.mstiles92.plugins.commonutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a method as the handler for a command or subcommand.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The name of the command. Subcommands are supported, and should be separated by periods. For example, a subcommand
     * "sub" of the main command "cmd" would be represented as "cmd.sub".
     *
     * @return the name of the command
     */
    public String name();

    /**
     * Get the permission that is required to use this command.
     *
     * @return the permission node required to use this command
     */
    public String permission() default "";

    /**
     * Get the message to be sent to the player when they do not have permission to execute this command.
     *
     * @return the message to send to players without permission to use this command
     */
    public String noPermission() default "You do not have permission to use that command.";

    /**
     * Get a list of aliases that can also be used to perform this command.
     *
     * @return an array of the valid aliases for this command
     */
    public String[] aliases() default {};

    /**
     * Get the description that will be shown when the player uses the help command.
     *
     * @return a string description of the command
     */
    public String description() default "";

    /**
     * Get the usage that will be shown when the player uses the help command.
     *
     * @return a string showing the usage of the command
     */
    public String usage() default "";
}
