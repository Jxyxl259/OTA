package jiang.device_upgrade.entity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jiang.device_upgrade.service.OTAUpgradeService;
import jiang.device_upgrade.util.UDPPackageSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @description: Ota程序升级管理器
 * @author: jiangbug@outlook.com
 * @create: 2019-03-26 09:16
 */
@Component("otaManager")
public class OTAUpDateManager {

    private static Logger log = LoggerFactory.getLogger(OTAUpDateManager.class);

    private static final String HEADER = "80 00 ";
    private static final String HEX_128 = "80 ";
    private static final String NO_HIGH = "00 ";

    //public static final int H1 = 0x80;// 128
    //public static final int H2 = 0x00;


//    public static void main(String[] args) {
//        System.out.println(H1);
//    }

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("send-ota-package-pool-%d").build();

    ExecutorService executorService = new ThreadPoolExecutor(
            10,
            20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(512),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );


    /**
     * 使用udp协议发送升级数据报
     *      创建发送端Socket对象——DatagramSocket
     *      创建数据并将数据打包到DatagramPacket对象
     *      通过Socket发送
     *      释放相关资源
     * @param updateFileName
     */
    public void update(String updateFileName, String destinationIPPort) {

        HashMap<Integer, byte[]> dataMap = OTAUpgradeService.otaFileNamePackageMapping.get(updateFileName);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(8111);

            for(Map.Entry<Integer, byte[]> entry : dataMap.entrySet()){
                UDPPackageSend.send(socket, makeDataPackage(entry.getKey(),entry.getValue()), destinationIPPort);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(socket != null)
                socket.close();
        }

    }

    /**
     * 组织数据报格式
     * 数据报格式：按照128字节分包数据，比如数据总长度为LENZ 则数据包个数为 packageNum = LENZ/128 ,如果不能有余数则 packageNum = (LENZ/128)+1
     * 数据分包已经在service层分好，这里的 data就是 每个包的实际数据部分
     * 这里仅组织数据报
     * 数据报格式
     *      80 00 H L(数据报下标 0 ~ packageNum-1) DL(后面真实数据长度) 数据
     *      其中 H L分别为数据下标的高位和低位
     * @return
     */
    public static String makeDataPackage(Integer idx, byte[] data){
        StringBuilder s = new StringBuilder(HEADER);
        if(idx > 0xFFFF){
            String bin = Integer.toBinaryString(idx);
            String h = bin.substring(0, bin.length() - 16);
            String l = bin.substring(bin.length() - 16);
            h = Integer.toHexString(Integer.parseInt(h, 2));
            l = Integer.toHexString(Integer.parseInt(l, 2));
            s.append(h).append(" ").append(l).append(" ");
        }else {
            s.append(NO_HIGH).append(Integer.toHexString(idx)).append(" ");

        }

        // 数据长度表示 DL
        if(data.length == 128){
            s.append(HEX_128);
        }else{
            s.append(Integer.toHexString(data.length));
        }
        String body = new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
        s.append(body);
        return s.toString();
    }





    public static void main(String[] args) {
//        int i = 2999 / 3000;
//        System.out.println(i);
        System.out.println(0x0005);
        System.out.println(Integer.toBinaryString(65535));
        System.out.println(Integer.toBinaryString(131071));
        System.out.println(Integer.toBinaryString(127));
        //System.out.println(Integer.highestOneBit(65536));
        //System.out.println(Integer.lowestOneBit(65536));

        Integer i = new Integer(65535);
        System.out.println(i.toString(i, 2));
        System.out.println(0X0f);
    }
}



















