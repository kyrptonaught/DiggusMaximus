package net.kyrptonaught.diggusmaximus;


import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StartExcavatePacket {
    private static final Identifier START_EXCAVATE_PACKET = new Identifier(DiggusMaximusMod.MOD_ID, "start_excavate_packet");

    static void registerReceivePacket() {
        ServerPlayNetworking.registerGlobalReceiver(START_EXCAVATE_PACKET, (server, player, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            BlockPos blockPos = packetByteBuf.readBlockPos();
            Identifier blockID = packetByteBuf.readIdentifier();
            int facingID = packetByteBuf.readInt();
            Direction facing = facingID == -1 ? null : Direction.byId(facingID);
            int shapeKey = packetByteBuf.readInt();
            server.execute(() -> {
                if (DiggusMaximusMod.getOptions().enabled) {
                    if (blockPos.isWithinDistance(player.getPos(), 10)) {
                        new Excavate(blockPos, blockID, player, facing).startExcavate(shapeKey);
                    }
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendExcavatePacket(BlockPos blockPos, Identifier blockID, Direction facing, int shapeSelection) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockPos);
        buf.writeIdentifier(blockID);
        buf.writeInt(facing == null ? -1 : facing.getId());
        buf.writeInt(shapeSelection);
        ClientPlayNetworking.send(START_EXCAVATE_PACKET, new PacketByteBuf(buf));
    }
}