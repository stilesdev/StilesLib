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

package com.mstiles92.plugins.stileslib.menu.events;

import com.google.common.base.Preconditions;
import com.mstiles92.plugins.stileslib.menu.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired off by the Menu when it recieves an InventoryClickEvent from the MenuListener. This event is
 * handled by the individual MenuItems.
 */
public class MenuClickEvent extends Event {
    private HandlerList handlerList = new HandlerList();
    private Player player;
    private Menu menu;
    private Result result;
    private Menu submenu;

    /**
     * Create a MenuClickEvent for the specified Player.
     *
     * @param player the Player who clicked an item in a Menu Inventory
     * @param menu the Menu which was clicked
     */
    public MenuClickEvent(Player player, Menu menu) {
        this.player = player;
        this.menu = menu;
        this.result = Result.REFRESH;
    }

    /**
     * Get the HandlerList for this event.
     *
     * @return the HandlerList for this event
     */
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * Get the Player involved in this event.
     *
     * @return the Player who clicked
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the Menu which created this event.
     *
     * @return the Menu which created this event
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Get the Result of this event.
     *
     * @return the Result of this event
     */
    public Result getResult() {
        return result;
    }

    /**
     * Set the Result of this event, which specifies what happens after this event is finished.
     *
     * @param result the Result of this event
     */
    public void setResult(Result result) {
        Preconditions.checkNotNull(result, "Result can not be null in MenuClickEvent!");

        this.result = result;
    }

    /**
     * Get the Menu that should be opened after the event is finished, if any.
     *
     * @return the Menu that should be opened after the event is finished, or null if another Menu should not be opened.
     */
    public Menu getSubmenu() {
        return submenu;
    }

    /**
     * Set the Menu that should be opened after the event is finished when the result is set to SUBMENU.
     *
     * @param submenu the Menu that should be opened after the event is finished
     */
    public void setSubmenu(Menu submenu) {
        this.submenu = submenu;
    }

    /**
     * Possible outcomes that could take place after a MenuClickEvent is finished.
     */
    public enum Result {
        /**
         * Refresh all item icons in the current Menu, leaving it open for further choices.
         */
        REFRESH,

        /**
         * Close the menu completely.
         */
        CLOSE,

        /**
         * Open a new Menu, which should be specified by setSubmenu().
         */
        SUBMENU,

        /**
         * Open the Menu that opened this Menu.
         */
        PREVIOUS
    }
}
