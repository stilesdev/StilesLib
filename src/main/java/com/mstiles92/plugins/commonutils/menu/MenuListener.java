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

package com.mstiles92.plugins.commonutils.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class MenuListener implements Listener {
    private static MenuListener instance = new MenuListener();
    private Plugin plugin;

    private MenuListener() {
    }

    public static MenuListener getInstance() {
        return instance;
    }

    public void register(Plugin plugin) {
        if (!isRegistered(plugin)) {
            this.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public boolean isRegistered(Plugin plugin) {
        if (this.plugin != null && this.plugin.equals(plugin)) {
            for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(plugin)) {
                if (registeredListener.getListener().equals(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void closeAllMenus() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                Inventory inventory = player.getOpenInventory().getTopInventory();

                if (inventory.getHolder() instanceof MenuInventoryHolder) {
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (this.plugin.equals(event.getPlugin())) {
            closeAllMenus();
            plugin = null;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {
            event.setCancelled(true);
            ((MenuInventoryHolder) event.getInventory().getHolder()).getMenu().handleClick(event);
        }
    }
}
