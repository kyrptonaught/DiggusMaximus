package net.kyrptonaught.diggusmaximus;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class StartExcavatePacket {
    private static final Identifier START_EXCAVATE_PACKET = new Identifier(DiggusMaximusMod.MOD_ID, "start_excavate_packet");

    static void registerReceivePacket() {
        ServerSidePacketRegistry.INSTANCE.register(START_EXCAVATE_PACKET, (packetContext, packetByteBuf) -> {
            BlockPos blockPos = packetByteBuf.readBlockPos();
            Block block = Registry.BLOCK.get(new Identifier(packetByteBuf.readString()));
            packetContext.getTaskQueue().execute(() -> {
                if (blockPos.isWithinDistance(packetContext.getPlayer().getPos(), 6)) {
                    Excavate excavate = new Excavate(blockPos, block, packetContext.getPlayer());
                    excavate.startExcavate();
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendExcavatePacket(BlockPos blockPos, String blockID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockPos);
        buf.writeString(blockID);
        MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(START_EXCAVATE_PACKET, new PacketByteBuf(buf)));

    }
}