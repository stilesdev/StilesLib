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

package com.mstiles92.plugins.stileslib.menu.menus;

import com.google.common.base.Preconditions;
import com.mstiles92.plugins.stileslib.menu.MenuInventoryHolder;
import com.mstiles92.plugins.stileslib.menu.events.MenuClickEvent;
import com.mstiles92.plugins.stileslib.menu.items.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A menu with clickable icons to be displayed to a Player in an Inventory.
 */
public class Menu {
    private String title;
    private int numRows;
    private MenuItem[] contents;
    private Menu previousMenu;

    /**
     * Create a basic Menu with the specified title and size.
     *
     * @param title the title to be shown to the Player in the open Inventory
     * @param numRows the number of rows that should be present in the Inventory, must be between 1 and 6
     */
    public Menu(String title, int numRows) {
        Preconditions.checkNotNull(title, "Menu title must not be null!");
        Preconditions.checkArgument(numRows > 0 && numRows < 7, "Number of rows in menu must be between 1 and 6! Was: %s", numRows);

        this.title = title;
        this.numRows = numRows;
        contents = new MenuItem[numRows * 9];
    }

    /**
     * Get the title of the Menu which will be displayed in the open Inventory.
     *
     * @return the title of the Menu
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the number of rows in the Menu.
     *
     * @return the number of rows in the Menu
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Set a MenuItem to a specific position in the Menu. The position is zero-based, so the top-left slot is 0, the
     * top-right is 8, the far-left in the second row is 9, and so on.
     *
     * @param position the zero-based position in which to put the MenuItem
     * @param item the MenuItem to put in the Menu
     */
    public void setItem(int position, MenuItem item) {
        Preconditions.checkElementIndex(position, contents.length);

        contents[position] = item;
    }

    /**
     * Get the Menu which opened this Menu, if this Menu was opened by another.
     *
     * @return the Menu which opened this Menu if this Menu was opened by another, null if it was not
     */
    public Menu getPreviousMenu() {
        return previousMenu;
    }

    /**
     * Set the Menu that opened this Menu.
     *
     * @param previousMenu the Menu that opened this Menu.
     */
    public void setPreviousMenu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    /**
     * Open and display this Menu to a Player.
     *
     * @param player the Player to display the Menu to
     */
    public void open(Player player) {
        Preconditions.checkNotNull(player, "Player opening a menu inventory must not be null!");
        Preconditions.checkState(player.isOnline(), "Player opening a menu inventory must be online!");

        Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(this, Bukkit.createInventory(player, numRows * 9)), numRows * 9, title);

        applyMenuToInventory(inventory, player);

        player.openInventory(inventory);
    }

    /**
     * Handle the Player clicking with this Menu's Inventory open.
     *
     * @param event the InventoryClickEvent fired by the Player clicking a slot in this Menu's Inventory.
     */
    public void handleClick(InventoryClickEvent event) {
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }

        int clickedSlot = event.getRawSlot();

        if (clickedSlot >= 0 && clickedSlot < numRows * 9 && contents[clickedSlot] != null) {
            Player player = (Player) event.getWhoClicked();

            if (contents[clickedSlot].visibleTo(player)) {
                MenuClickEvent menuClickEvent = new MenuClickEvent(player);
                contents[clickedSlot].onClick(menuClickEvent);

                switch (menuClickEvent.getResult()) {
                    case REFRESH:
                        refreshMenu(player);
                        break;
                    case CLOSE:
                        player.closeInventory();
                        break;
                    case SUBMENU:
                        Menu submenu = menuClickEvent.getSubmenu();
                        Preconditions.checkNotNull(submenu, "Result was set to SUBMENU, but no submenu was specified by MenuClickEvent.setSubmenu(Menu)");
                        submenu.setPreviousMenu(this);
                        submenu.open(player);
                        break;
                    case PREVIOUS:
                        Preconditions.checkNotNull(previousMenu, "Result was set to PREVIOUS when there was no menu to go back to!");
                        previousMenu.open(player);
                        break;
                }
            }
        }
    }

    /**
     * Refresh all items in this Menu's Inventory, to reflect any changes that may have been made to the contents.
     *
     * @param player the Player who is currently viewing the Menu
     */
    private void refreshMenu(Player player) {
        if (player.getOpenInventory() != null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if (inventory.getHolder() instanceof MenuInventoryHolder && ((MenuInventoryHolder) inventory.getHolder()).getMenu().equals(this)) {
                applyMenuToInventory(inventory, player);
            }
        }
    }

    /**
     * Apply the current Menu's contents to the provided Inventory.
     *
     * @param inventory the Inventory to apply the current Menu's contents to
     * @param player the Player that the MenuItem will use to check for visibility and to get the display icon
     */
    private void applyMenuToInventory(Inventory inventory, Player player) {
        inventory.clear();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].visibleTo(player)) {
                inventory.setItem(i, contents[i].getDisplayIcon(player));
            }
        }
    }
}
