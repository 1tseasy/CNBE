package com.ouc.tcp.test;

import java.util.zip.CRC32;

import com.ouc.tcp.message.TCP_HEADER;
import com.ouc.tcp.message.TCP_PACKET;

public class CheckSum {
    public static short computeChkSum(TCP_PACKET tcpPack) {
        byte[] tcpHeader = tcpPack.getTcpH().toString().getBytes();
        byte[] tcpData = tcpPack.getTcpS().toString().getBytes();

        // 连接TCP头部和数据部分
        byte[] dataToChecksum = new byte[tcpHeader.length + tcpData.length];
        System.arraycopy(tcpHeader, 0, dataToChecksum, 0, tcpHeader.length);
        System.arraycopy(tcpData, 0, dataToChecksum, tcpHeader.length, tcpData.length);
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