package ellpeck.actuallyadditions.inventory;

import ellpeck.actuallyadditions.inventory.slot.SlotOutput;
import ellpeck.actuallyadditions.items.InitItems;
import ellpeck.actuallyadditions.items.ItemCoffee;
import ellpeck.actuallyadditions.items.metalists.TheMiscItems;
import ellpeck.actuallyadditions.tile.TileEntityBase;
import ellpeck.actuallyadditions.tile.TileEntityCoffeeMachine;
import invtweaks.api.container.InventoryContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@InventoryContainer
public class ContainerCoffeeMachine extends Container{

    private TileEntityCoffeeMachine machine;

    public ContainerCoffeeMachine(InventoryPlayer inventory, TileEntityBase tile){
        this.machine = (TileEntityCoffeeMachine)tile;

        this.addSlotToContainer(new Slot(machine, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS, 37, 6));
        this.addSlotToContainer(new Slot(machine, TileEntityCoffeeMachine.SLOT_INPUT, 80, 42));
        this.addSlotToContainer(new SlotOutput(machine, TileEntityCoffeeMachine.SLOT_OUTPUT, 80, 73));

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 2; j++){
                this.addSlotToContainer(new Slot(machine, j+i*2+3, 125+j*18, 6+i*18));
            }
        }

        this.addSlotToContainer(new Slot(machine, TileEntityCoffeeMachine.SLOT_WATER_INPUT, 26, 73));
        this.addSlotToContainer(new SlotOutput(machine, TileEntityCoffeeMachine.SLOT_WATER_OUTPUT, 45, 73));

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 97 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 155));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.machine.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        final int inventoryStart = 13;
        final int inventoryEnd = inventoryStart+26;
        final int hotbarStart = inventoryEnd+1;
        final int hotbarEnd = hotbarStart+8;

        Slot theSlot = (Slot)this.inventorySlots.get(slot);
        if(theSlot.getHasStack()){
            ItemStack currentStack = theSlot.getStack();
            ItemStack newStack = currentStack.copy();

            if(currentStack.getItem() != null){
                if(slot <= hotbarEnd && slot >= inventoryStart){
                    if(currentStack.getItem() == InitItems.itemCoffeeBean){
                        this.mergeItemStack(newStack, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS+1, false);
                    }
                    if(currentStack.getItem() == InitItems.itemMisc && currentStack.getItemDamage() == TheMiscItems.CUP.ordinal()){
                        this.mergeItemStack(newStack, TileEntityCoffeeMachine.SLOT_INPUT, TileEntityCoffeeMachine.SLOT_INPUT+1, false);
                    }
                    if(ItemCoffee.getIngredientFromStack(newStack) != null){
                        this.mergeItemStack(newStack, 3, 10, false);
                    }
                    if(FluidContainerRegistry.containsFluid(newStack, new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME))){
                        this.mergeItemStack(newStack, TileEntityCoffeeMachine.SLOT_WATER_INPUT, TileEntityCoffeeMachine.SLOT_WATER_INPUT+1, false);
                    }
                }

                if(slot <= hotbarEnd && slot >= hotbarStart){
                    this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false);
                }

                else if(slot <= inventoryEnd && slot >= inventoryStart){
                    this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false);
                }

                else if(slot < inventoryStart){
                    this.mergeItemStack(newStack, inventoryStart, hotbarEnd+1, false);
                }

                if(newStack.stackSize == 0) theSlot.putStack(null);
                else theSlot.onSlotChanged();
                if(newStack.stackSize == currentStack.stackSize) return null;
                theSlot.onPickupFromSlot(player, newStack);

                return currentStack;
            }
        }
        return null;
    }
}