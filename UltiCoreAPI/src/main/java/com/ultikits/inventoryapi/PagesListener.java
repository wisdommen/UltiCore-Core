package com.ultikits.inventoryapi;

import com.ultikits.beans.CancelResult;
import com.ultikits.main.UltiCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class PagesListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory currentInventory = event.getClickedInventory();
        ItemStack clicked = event.getCurrentItem();
        InventoryManager inventoryManager = ViewManager.getViewByInventory(currentInventory);
        if (inventoryManager == null) {
            return;
        }
        if (clicked != null && clicked.getType() != Material.AIR) {
            if (event.getSlot() < inventoryManager.getSize() && !inventoryManager.isBackGround(clicked)) {
                CancelResult cancelled = onItemClick(event, player, inventoryManager, clicked);
                cancelEvent(event, cancelled);
            } else if (clicked.getItemMeta() != null) {
                if (!inventoryManager.isLastLineDisabled()) {
                    return;
                }
                if (inventoryManager.isBackGround(clicked)) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
                String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                if (itemName.equals(UltiCore.getWords("button_previous"))) {
                    event.setResult(Event.Result.DENY);
                    if (inventoryManager.getTitle().contains(String.format(" 第%d页", 1))) {
                        return;
                    }
                    InventoryManager previousInventory = ViewManager.getViewByName(inventoryManager.getGroupTitle() + " " + String.format("第%d页", (inventoryManager.getPageNumber() - 1)));
                    if (previousInventory != null) {
                        player.openInventory(previousInventory.getInventory());
                    }
                } else if (itemName.equals(UltiCore.getWords("button_next"))) {
                    event.setResult(Event.Result.DENY);
                    InventoryManager nextInventory = ViewManager.getViewByName(inventoryManager.getGroupTitle() + " " + String.format("第%d页", (inventoryManager.getPageNumber() + 1)));
                    if (nextInventory != null) {
                        player.openInventory(nextInventory.getInventory());
                    }
                } else if (itemName.equals(UltiCore.getWords("button_quit"))) {
                    event.setResult(Event.Result.DENY);
                    player.closeInventory();
                } else if (itemName.equals(UltiCore.getWords("button_back")) || itemName.equals(UltiCore.getWords("button_ok")) || itemName.equals(UltiCore.getWords("button_cancel"))) {
                    event.setResult(Event.Result.DENY);
                    InventoryManager lastInventory = ViewManager.getLastView(inventoryManager);
                    if (lastInventory == null) {
                        player.closeInventory();
                    } else {
                        player.openInventory(lastInventory.getInventory());
                    }
                } else {
                    CancelResult cancelled = onItemClick(event, player, inventoryManager, clicked);
                    cancelEvent(event, cancelled);
                }
            }
        }
    }

    private void cancelEvent(InventoryClickEvent event, CancelResult cancelled) {
        switch (cancelled) {
            case TRUE:
                event.setCancelled(true);
                break;
            case FALSE:
                event.setCancelled(false);
                break;
            case NONE:
                break;
        }
    }

    /**
     * You need to complete the event when item clicked.
     * You don't need to worry about the last line if you create the page with preset page.
     * 你需要实现当物品被点击后的方法。
     * 你不必处理最后一行的点击事件如果此界面是你使用预设界面创建的。
     *
     * @param event            InventoryClickEvent
     * @param player           Player who clicked the item
     * @param inventoryManager The inventoryManager that response to this inventory
     * @param clickedItem      the item that been clicked
     */
    public abstract CancelResult onItemClick(InventoryClickEvent event, Player player, InventoryManager inventoryManager, ItemStack clickedItem);

}
