package com.gtnh.findit.service.blockfinder;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.api.covers.CoverRegistry;
import io.netty.buffer.ByteBuf;

public class FindBlockRequest implements IMessage {

    private Block blockToFind;
    private int metaToFind;
    private ItemStack coverToFind;

    public FindBlockRequest(Block block, int meta) {
        this.blockToFind = block;
        this.metaToFind = meta;
    }

    public FindBlockRequest(ItemStack cover) {
        this.coverToFind = cover;
    }

    public FindBlockRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            coverToFind = ProtoUtils.readItemStack(buf);
        } else {
            blockToFind = Block.getBlockById(buf.readShort());
            metaToFind = buf.readShort();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(coverToFind != null);
        if (coverToFind != null) {
            ProtoUtils.writeItemStack(buf, coverToFind);
        } else {
            buf.writeShort(Block.getIdFromBlock(blockToFind));
            buf.writeShort(metaToFind);
        }
    }

    public Block getBlockToFind() {
        return blockToFind;
    }

    public int getMetaToFind() {
        return metaToFind;
    }

    public ItemStack getCoverToFind() {
        return coverToFind;
    }

    public static class Handler implements IMessageHandler<FindBlockRequest, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(FindBlockRequest message, MessageContext ctx) {
            if ((message.coverToFind != null && CoverRegistry.isCover(message.coverToFind))
                    || (message.blockToFind != null && message.blockToFind != Blocks.air)) {
                FindIt.getBlockFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
