package com.ultikits.inventoryapi;

import com.ultikits.enums.Colors;
import com.ultikits.main.UltiCore;
import com.ultikits.manager.ItemStackManager;
import com.ultikits.main.UltiCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The type Inventory manager.
 */
public class InventoryManager implements InventoryManagerAPI {

    private Inventory inventory;
    private final InventoryHolder owner;
    private final int slots;
    private int pageNumber = 0;
    private String title;
    private final String groupTitle;
    private boolean isPageButtonEnabled = false;
    private boolean isOkCancelEnabled = false;
    private boolean isLastLineDisabled = false;
    private boolean autoAddPage = true;
    private Colors backGroundColor;
    private Buttons middleButton = Buttons.QUIT;

    /**
     * Instantiates a new Inventory manager.
     *
     * @param owner the owner
     * @param slots the slots
     * @param title the title
     */
    public InventoryManager(InventoryHolder owner, int slots, String title) {
        this.owner = owner;
        this.slots = slots;
        this.groupTitle = title;
        create();
    }

    private InventoryManager(InventoryHolder owner, int slots, String title, int pageNumber) {
        this.owner = owner;
        this.slots = slots;
        this.pageNumber = pageNumber;
        this.title = title + String.format(" " + UltiCore.getWords("inventory_manager_title_page_number"), pageNumber);
        this.groupTitle = title;
        this.isPageButtonEnabled = true;
        this.isLastLineDisabled = true;
        create();
    }

    /**
     * Instantiates a new Inventory manager.
     *
     * @param owner the owner
     * @param slots the slots
     * @param title the title
     * @param block the block
     */
    public InventoryManager(InventoryHolder owner, int slots, String title, boolean block) {
        this.owner = owner;
        this.slots = slots;
        this.groupTitle = title;
        if (!block) {
            create();
        }
    }

    @Override
    public void create() {
        if (title == null) {
            title = groupTitle;
        }
        inventory = Bukkit.createInventory(owner, slots, title);
        if (isPageButtonEnabled) {
            setPageButtons();
        } else if (isOkCancelEnabled) {
            setOkCancelButtons();
        } else if (isLastLineDisabled) {
            fillLastLine();
            forceSetItem(inventory.getSize() - 5, middleButton.getItemStack());
        }
        if (this.backGroundColor != null) {
            setBackgroundColor(this.backGroundColor);
        }
    }

    @Override
    public void presetPage(ViewType type) {
        this.isLastLineDisabled = type.isLastLineEnabled();
        if (isLastLineDisabled) {
            this.isPageButtonEnabled = type.isPageButtonEnabled();
            this.isOkCancelEnabled = type.isOkCancelEnabled();
            if (isPageButtonEnabled) {
                this.autoAddPage = type.isAutoAddPage();
                this.pageNumber = type.getPageNumber();
                this.middleButton = type.getMiddleButton();
            }
        }
        this.backGroundColor = type.getBackGroundColor();
    }

    @Override
    public void setItem(int position, ItemStack item) {
        if (position >= this.getSize()) {
            return;
        }
        if (position < 0) {
            return;
        }
        if (inventory.getItem(position) == null) {
            inventory.setItem(position, item);
        } else if (isBackGround(inventory.getItem(position))) {
            forceSetItem(position, item);
        } else {
            setItem(position + 1, item);
        }
    }

    @Override
    public void forceSetItem(int position, ItemStack item) throws IndexOutOfBoundsException {
        inventory.setItem(position, item);
    }

    @Override
    public void addItem(ItemStack item) {
        clearBackGround();
        Map<Integer, ItemStack> items = inventory.addItem(item);
        if (items.values().size() == 0) {
            setBackgroundColor(this.backGroundColor);
            return;
        }
        if (!isPageButtonEnabled) {
            return;
        }
        InventoryManager nextPage = ViewManager.getViewByName(getGroupTitle() + String.format(" " + UltiCore.getWords("inventory_manager_title_page_number"), (getPageNumber() + 1)));
        if (nextPage == null && autoAddPage) {
            InventoryManager newPage = new InventoryManager(this.owner, this.slots, getGroupTitle(), getPageNumber() + 1);
            if (this.backGroundColor != null) {
                newPage.setBackgroundColor(this.backGroundColor);
            }
            ViewManager.registerView(newPage);
            newPage.addItem(item);
        } else if (nextPage != null) {
            if (!nextPage.getInventory().contains(item)) {
                nextPage.addItem(item);
            }
        }
        setBackgroundColor(this.backGroundColor);
    }

    @Override
    public void addItem(ItemStackManager itemStackManager) {
        addItem(itemStackManager.getItem());
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getGroupTitle() {
        if (groupTitle == null) {
            return title;
        }
        return groupTitle;
    }

    @Override
    public int getSize() {
        if (isLastLineDisabled) {
            return inventory.getSize() - 9;
        }
        return inventory.getSize();
    }

    private void setPageButtons() {
        this.title = groupTitle + " " + String.format(UltiCore.getWords("inventory_manager_title_page_number"), pageNumber);
        inventory = Bukkit.createInventory(owner, slots, title);
        fillLastLine();
        try {
            ItemStackManager back = new ItemStackManager(Buttons.PREVIOUS.getItemStack(), Buttons.PREVIOUS.getName());
            ItemStackManager quit = new ItemStackManager(middleButton.getItemStack(), middleButton.getName());
            ItemStackManager next = new ItemStackManager(Buttons.NEXT.getItemStack(), Buttons.NEXT.getName());
            this.forceSetItem(getSize() + 3, back.getItem());
            this.forceSetItem(getSize() + 4, quit.getItem());
            this.forceSetItem(getSize() + 5, next.getItem());
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void setOkCancelButtons() {
        fillLastLine();
        try {
            ItemStackManager ok = new ItemStackManager(Buttons.OK.getItemStack(), Buttons.OK.getName());
            ItemStackManager cancel = new ItemStackManager(Buttons.CANCEL.getItemStack(), Buttons.CANCEL.getName());
            this.forceSetItem(getSize() + 3, ok.getItem());
            this.forceSetItem(getSize() + 5, cancel.getItem());
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void fillLastLine() {
        ItemStack blackGlass = UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.BLACK);
        ItemStackManager blank = new ItemStackManager(blackGlass, "");
        for (int i = getSize(); i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null){
                continue;
            }
            inventory.setItem(i, blank.getItem());
        }
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public boolean isPageButtonEnabled() {
        return isPageButtonEnabled;
    }

    @Override
    public boolean isLastLineDisabled() {
        return isLastLineDisabled;
    }

    @Override
    public void clearView() {
        for (int i = 0; i < getSize(); i++) {
            forceSetItem(i, null);
        }
    }

    @Override
    public void setBackgroundColor(Colors backgroundColor) {
        if (backgroundColor == null) {
            return;
        }
        ItemStackManager itemStackManager = new ItemStackManager(UltiCore.versionAdaptor.getColoredPlaneGlass(backgroundColor), "");
        while (inventory.firstEmpty() > -1) {
            forceSetItem(inventory.firstEmpty(), itemStackManager.getItem());
        }
        this.backGroundColor = backgroundColor;
    }

    @Override
    public void clearBackGround() {
        for (int i = 0; i < getSize(); i++) {
            if (isBackGround(inventory.getItem(i))) {
                forceSetItem(i, null);
            }
        }
    }

    @Override
    public boolean isBackGround(@Nullable ItemStack item) {
        if (item == null) return false;
        if (this.backGroundColor == null) return false;
        if (item.getType() == UltiCore.versionAdaptor.getColoredPlaneGlass(Colors.BLACK).getType()) return true;
        return item.getType().equals(UltiCore.versionAdaptor.getColoredPlaneGlass(this.backGroundColor).getType()) && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("");
    }

    @Override
    public void setMiddleButton(Buttons middleButton) {
        this.middleButton = middleButton;
    }

    @Override
    public void setPageButtonEnabled(boolean isPageButtonEnabled) {
        this.isPageButtonEnabled = isPageButtonEnabled;
    }

    @Override
    public void setLastLineDisabled(boolean disabled) {
        this.isLastLineDisabled = disabled;
    }
}
