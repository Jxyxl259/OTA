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

//    private static final String HEADER = "80 00";
    private static final String SUP_1 = "0";
    private static final String SUP_2 = "00";
    private static final String SUP_3 = "000";
    private static final String SUP_4 = "0000";

    private static final String HEX_128 = "80";
    private static final String NO_HIGH = "0000";

    private static final String DONE = "0000 0000 0 ";

    // EOF 本次数据包传输结束标识
    private static final byte[] END_SIGN = new byte[]{69,79,70};

    // 单条udp数据包数据结尾标识
    private static final String TAIL = "$";


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
                UDPPackageSend.send(socket, makeDataPackage(dataMap.size(), entry.getKey(), entry.getValue(), false), destinationIPPort);
                // TODO 线程短暂休眠，另起线程监听发送的数据包的游标，页面展示升级包发送进度
            }
            Thread.currentThread().sleep(100);
            // 通知数据包都已发送完
            UDPPackageSend.send(socket, makeDataPackage(dataMap.size(), null,  null, true), destinationIPPort);

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
     *      xx xx(总的数据报个数，分高低位) H L(数据报下标 0 ~ packageNum-1) DL(后面真实数据长度) 数据
     *      其中 H L分别为数据下标的高位和低位
     * @return
     */
    public static String makeDataPackage(Integer totalPackageNum, Integer idx, byte[] data, boolean finished){

        if(!finished) {
            StringBuilder s = new StringBuilder(Integer.toHexString(totalPackageNum)).append(" ");
            if (idx > 0xFFFF) {
                String bin = Integer.toBinaryString(idx);
                String h = bin.substring(0, bin.length() - 16);
                String l = bin.substring(bin.length() - 16);
                h = Integer.toHexString(Integer.parseInt(h, 2));
                l = Integer.toHexString(Integer.parseInt(l, 2));
                // 对低位补0
                int length = 4 - l.length();
                StringBuilder fixZero = new StringBuilder();
                switch (length) {
                    case 1:
                        fixZero.append(SUP_1).append(l);
                        break;
                    case 2:
                        fixZero.append(SUP_2).append(l);
                        break;
                    case 3:
                        fixZero.append(SUP_3).append(l);
                        break;
                    case 4:
                        fixZero.append(SUP_4).append(l);
                        break;
                }
                s.append(h).append(" ").append(fixZero.toString()).append(" ");
            } else {
                s.append(NO_HIGH).append(" ").append(Integer.toHexString(idx)).append(" ");

            }

            // 数据长度表示 DL
            if (data.length == 128) {
                s.append(HEX_128).append(" ");
            } else {
                s.append(Integer.toHexString(data.length)).append(" ");
            }

            byte[] encode = Base64.getEncoder().encode(data);
            String body = new String(encode, StandardCharsets.UTF_8);
            s.append(body).append(" ").append(TAIL);
            log.info("send package ->{}", s.toString());
            return s.toString();
        }else{
            String s = new StringBuilder(Integer.toHexString(totalPackageNum)).append(" ").append(DONE).append(new String(END_SIGN, StandardCharsets.UTF_8)).append(" ").append(TAIL).toString();
            log.info("send package ->{}", s);
            return s;
        }

    }





    public static void main(String[] args) {
//        int i = 2999 / 3000;
//        System.out.println(i);
        System.out.println(Integer.toHexString(1535));

        System.out.println(0x0005);
        System.out.println(Integer.toBinaryString(65535));
        System.out.println(Integer.toBinaryString(131071));
        System.out.println(Integer.toBinaryString(127));
        //System.out.println(Integer.highestOneBit(65536));
        //System.out.println(Integer.lowestOneBit(65536));

        Integer i = new Integer(65535);
        System.out.println(i.toString(i, 2));
        System.out.println(0Xffff);
        System.out.println(Integer.toHexString(Integer.MAX_VALUE));
        System.out.println(Integer.MAX_VALUE);
        System.out.println(makeDataPackage(65552, 65552, new byte[]{12,21}, false));
        System.out.println(Integer.parseInt("010000",16));

        System.out.println((int)'E');
        System.out.println((int)'O');
        System.out.println((int)'F');
    }
}






















