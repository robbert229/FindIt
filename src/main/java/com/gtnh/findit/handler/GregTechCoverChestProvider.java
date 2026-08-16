package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.forge.IItemHandler;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.service.itemfinder.FindItemRequest;

import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.common.covers.Cover;
import gregtech.common.covers.CoverChest;

public class GregTechCoverChestProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        if (!(tileEntity instanceof ICoverable coverable)) {
            return null;
        }

        return request -> {
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                if (!coverable.hasCoverAtSide(side)) {
                    continue;
                }

                if (coverChestMatches(player, request, coverable.getCoverAtSide(side))) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        return null;
    }

    private static boolean coverChestMatches(EntityPlayer player, FindItemRequest request, Cover cover) {
        if (!(cover instanceof CoverChest coverChest)) {
            return false;
        }

        IItemHandler items = coverChest.getItems();
        for (int slot = 0; slot < items.getSlots(); slot++) {
            ItemStack slotItem = items.getStackInSlot(slot);
            if (slotItem != null && request.isStackSatisfies(player, slotItem)) {
                return true;
            }
        }

        return false;
    }
}
