package jiang.device_upgrade.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @description: UDP数据报发送工具类
 * @author: jiangbug@outlook.com
 * @create: 2019-03-26 21:59
 */


public class UDPPackageSend {

    /**
     *
     * @param datagramSocket 创建datagramSocket 并指定发送端口 8111, 全部发送完成之后，并且不再收到客户端发来的丢失重传请求报文之后再关闭连接
     * @param dataPackage 要发送的数据 dataPackage
     * @param destinationIPPort 目的 ip:port
     */
    public static void send(DatagramSocket datagramSocket, String dataPackage, String destinationIPPort){
        //String data="hello UDP";
        String[] split = destinationIPPort.split(":");
        try {
            //发送数据
            datagramSocket.send(new DatagramPacket(
                    dataPackage.getBytes(),
                    dataPackage.getBytes().length,
                    InetAddress.getByName(split[0]),
                    Integer.parseInt(split[1])));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
