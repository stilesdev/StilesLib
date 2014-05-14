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

package com.mstiles92.plugins.commonutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCompleter implements TabCompleter {
    private Map<String, Map.Entry<Method, Object>> completers = new HashMap<>();

    public void addCompleter(String label, Method m, Object o) {
        completers.put(label, new AbstractMap.SimpleEntry<>(m, o));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuilder sb = new StringBuilder();
            sb.append(label.toLowerCase());
            for (int j = 0; j < i; j++) {
                if (!args[j].equals("") && !args[j].equals(" ")) {
                    sb.append(".").append(args[j].toLowerCase());
                }
            }
            String commandLabel = sb.toString();

            if (completers.containsKey(commandLabel)) {
                Map.Entry<Method, Object> entry = completers.get(commandLabel);
                try {
                    List<String> labelParts = (List<String>) entry.getKey().invoke(entry.getValue(), new Arguments(sender, command, commandLabel, args, commandLabel.split("\\.").length - 1));

                    if (labelParts.size() == 0) {
                        return null;
                    } else {
                        return labelParts;
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
