package net.kyrptonaught.diggusmaximus;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class StartExcavatePacket {
    private static final Identifier START_EXCAVATE_PACKET = new Identifier(DiggusMaximusMod.MOD_ID, "start_excavate_packet");

    static void registerReceivePacket() {
        ServerSidePacketRegistry.INSTANCE.register(START_EXCAVATE_PACKET, (packetContext, packetByteBuf) -> {
            BlockPos blockPos = packetByteBuf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
                if (DiggusMaximusMod.getOptions().enabled) {
                    if (blockPos.isWithinDistance(packetContext.getPlayer().getPos(), 10)) {
                        Excavate excavate = new Excavate(blockPos, packetContext.getPlayer());
                        excavate.startExcavate();
                    }
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendExcavatePacket(BlockPos blockPos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockPos);
        MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(START_EXCAVATE_PACKET, new PacketByteBuf(buf)));

    }
}