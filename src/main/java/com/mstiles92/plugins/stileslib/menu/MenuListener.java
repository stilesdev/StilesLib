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

package com.mstiles92.plugins.stileslib.menu;

import com.google.common.base.Preconditions;
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

/**
 * The Listener that handles InventoryClickEvents for the menu system, passing on the events to the corresponding Menu.
 * Implemented as a Singleton, as only one instance of this listener ever needs to be registered.
 */
public class MenuListener implements Listener {
    private static MenuListener instance = new MenuListener();
    private Plugin plugin;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private MenuListener() {
    }

    /**
     * Get the static instance of this class.
     *
     * @return the instance of this class
     */
    public static MenuListener getInstance() {
        return instance;
    }

    /**
     * Register this Listener with the specified Plugin to catch events for the menu system.
     *
     * @param plugin the Plugin for which this Listener should be registered
     */
    public void register(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin must not be null when registering MenuListener!");

        if (!isRegistered(plugin)) {
            this.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    /**
     * Check if this Listener is registered with the specified Plugin.
     *
     * @param plugin the Plugin to be checked
     * @return true if the Listener is registered with the Plugin, false if it is not
     */
    public boolean isRegistered(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin must not be null when checking if MenuListener is registered!");

        if (this.plugin.equals(plugin)) {
            for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(plugin)) {
                if (registeredListener.getListener().equals(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Close every open Inventory that is a representation of a Menu.
     */
    public static void closeAllMenus() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                Inventory inventory = player.getOpenInventory().getTopInventory();

                if (inventory != null && inventory.getHolder() instanceof MenuInventoryHolder) {
                    player.closeInventory();
                }
            }
        }
    }

    /**
     * Handle the PluginDisableEvent, closing all Menus opened by that Plugin.
     *
     * @param event the PluginDisableEvent that was fired as a result of a Plugin becoming disabled.
     */
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (this.plugin.equals(event.getPlugin())) {
            closeAllMenus();
            plugin = null;
        }
    }

    /**
     * Handle the InventoryClickEvent, passing on the event to any applicable Menu.
     *
     * @param event the InventoryClickEvent that was fired as a result of a Player clicking on an Inventory.
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {
            event.setCancelled(true);
            ((MenuInventoryHolder) event.getInventory().getHolder()).getMenu().handleClick(event);
        }
    }
}
