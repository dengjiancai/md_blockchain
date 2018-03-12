package com.mindata.blockchain.socket.client;

import com.mindata.blockchain.socket.base.AbstractAioHandler;
import com.mindata.blockchain.socket.base.AbstractBlockHandler;
import com.mindata.blockchain.socket.body.HeartBeatBody;
import com.mindata.blockchain.socket.common.Const;
import com.mindata.blockchain.socket.handler.GenerateBlockResponseHandler;
import com.mindata.blockchain.socket.handler.LastBlockInfoResponseHandler;
import com.mindata.blockchain.socket.handler.TotalBlockInfoResponseHandler;
import com.mindata.blockchain.socket.packet.BlockPacket;
import com.mindata.blockchain.socket.packet.PacketType;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.utils.json.Json;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuweifeng wrote on 2018/3/12.
 */
public class BlockClientAioHandler extends AbstractAioHandler implements ClientAioHandler {
    private static Map<Byte, AbstractBlockHandler<?>> handlerMap = new HashMap<>();

    static {
        handlerMap.put(PacketType.GENERATE_BLOCK_REPONSE, new GenerateBlockResponseHandler());
        handlerMap.put(PacketType.TOTAL_BLOCK_INFO_REPONSE, new TotalBlockInfoResponseHandler());
        handlerMap.put(PacketType.LAST_BLOCK_INFO_REPONSE, new LastBlockInfoResponseHandler());
    }

    @Override
    public BlockPacket heartbeatPacket() {
        BlockPacket blockPacket = new BlockPacket();
        blockPacket.setType(PacketType.HEART_BEAT);
        try {
            blockPacket.setBody(Json.toJson(new HeartBeatBody("I am heartBeat")).getBytes(Const.CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return blockPacket;
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        BlockPacket blockPacket = (BlockPacket) packet;
        Byte type = blockPacket.getType();
        AbstractBlockHandler<?> blockHandler = handlerMap.get(type);
        blockHandler.handler(blockPacket, channelContext);
    }
}