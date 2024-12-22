package com.ouc.tcp.test;
import java.nio.ByteBuffer;//2.1
import java.util.zip.CRC32;

import com.ouc.tcp.message.MSG_STREAM;
import com.ouc.tcp.message.TCP_HEADER;
import com.ouc.tcp.message.TCP_PACKET;

public class CheckSum {
    public static short computeChkSum(TCP_PACKET tcpPack) {
        // 获取TCP首部的相关字段
        int seq = tcpPack.getTcpH().getTh_seq();
        int ack = tcpPack.getTcpH().getTh_ack();
        short sum = tcpPack.getTcpH().getTh_sum();
        int[] tcpData = tcpPack.getTcpS().getData();

        // 先预估用于存放首部相关字段和TCP数据组合的字节数组大小（考虑int占4字节等情况）
        byte[] dataToChecksum = new byte[12 + tcpData.length * 4];
        // 处理序列号（4字节）
        ByteBuffer sequenceNumberBuffer = ByteBuffer.allocate(4).putInt(seq);
        System.arraycopy(sequenceNumberBuffer.array(), 0, dataToChecksum, 0, 4);
        // 处理确认号（4字节）
        ByteBuffer acknowledgementNumberBuffer = ByteBuffer.allocate(4).putInt(ack);
        System.arraycopy(acknowledgementNumberBuffer.array(), 0, dataToChecksum, 4, 4);
        // 处理校验和字段（先填0，2字节）
        dataToChecksum[8] = 0;
        dataToChecksum[9] = 0;

        // 处理TCP数据部分，将int[]中的每个int转换为4字节表示并放入字节数组
        int offset = 10;
        for (int num : tcpData) {
            ByteBuffer intBuffer = ByteBuffer.allocate(4).putInt(num);
            System.arraycopy(intBuffer.array(), 0, dataToChecksum, offset, 4);
            offset += 4;
        }

        int checksum = 0;
        // 以每2个Byte为一个数进行两两相加
        for (int i = 0; i < dataToChecksum.length - 1; i += 2) {
            checksum += ((dataToChecksum[i] << 8) & 0xFFFF) + (dataToChecksum[i + 1] & 0xFF);
            if (checksum > 0xFFFF) {
                checksum = (checksum >> 16) + (checksum & 0xFFFF);
            }
        }
        // 如果数据长度是奇数，处理最后一个字节
        if (dataToChecksum.length % 2 == 1) {
            checksum += ((dataToChecksum[dataToChecksum.length - 1] << 8) & 0xFFFF);
            if (checksum > 0xFFFF) {
                checksum = (checksum >> 16) + (checksum & 0xFFFF);
            }
        }
        // 取反得到校验和
        return (short) (~checksum & 0xFFFF);
    }
}

/*public class CheckSum {

    //计算TCP报文段校验和：只需校验TCP首部中的seq、ack和sum，以及TCP数据字段
    public static short computeChkSum(TCP_PACKET tcpPack) {
        int checkSum = 0;



        return (short) checkSum;
    }

}*/