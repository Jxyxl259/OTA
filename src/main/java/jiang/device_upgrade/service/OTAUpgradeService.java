package jiang.device_upgrade.service;

import jiang.device_upgrade.entity.OTAUpDateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author:
 * @create: 2019-03-22 18:17
 */
@Service("otaUpgradeService")
public class OTAUpgradeService {

    private static Logger log = LoggerFactory.getLogger(OTAUpgradeService.class);

    public static ConcurrentHashMap<String, HashMap<Integer, byte[]>> otaFileNamePackageMapping = new ConcurrentHashMap<>(2);

    @Resource(name = "otaManager")
    private OTAUpDateManager otaManager;

    /**
     * 将文件保存到服务器
     *
     * @param srcFile
     * @return
     */
    public boolean saveToServer(MultipartFile srcFile) {

        File repo = new File("/data/OTA");
        if(!repo.exists()){
            boolean mkdir = repo.mkdirs();
            if(mkdir){
                log.info("创建 OTA升级文件上传路径 -> \"/data/OTA\" 成功");
            }
        }

        File destfile = new File("/data/OTA" + File.separator + srcFile.getOriginalFilename());

        InputStream is = null;

        try {
            is = srcFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(is != null){
            File file = inputstreamToFile(is, destfile);
            return true;
        }else{
            return false;
        }

    }


    /**
     * 输入流转换为文件
     *
     * @param ins
     * @param file
     * @return
     */
    private File inputstreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 固件升级，
     * 逻辑：
     *      先将文件以流的形式读进内存中，
     *      按照文件大小划分成报文段（128字节一个报文）
     *      计算好总的报文数量，放到 ConcurrentHashMap中, 初始化容量为报文的总数量 -1 （报文下标从0开始）
     *          计算容量（报文数量 文件大小/128  不能整除 文件大小/128 +1）
     *          k -> 报文下标
     *          v -> 报文数据
     *
     *      组织发送报文
     *          e.g:
     *              发送第一个数据包:                80 00 00(高位) 00(低位) 80(数据长度) + 128字节的数据
     *              发送不能整除的最后一个数据包：     80 00 xx(高位) xx(低位) 05(数据长度) + 5字节的数据
     * @param ota_name
     */
    public void upgrade(String ota_name, String  destinationIpPort) {

        File f = new File("/data/OTA/" + ota_name);

        // 文件大小
        long dataLegth = f.length();

        // 报文长度是否是128字节的整数倍
        byte mod = Byte.valueOf("" + dataLegth%128);

        // 要发送的报文总数量
        Long packageNum = mod == 0 ? dataLegth/128 : (dataLegth/128) + 1;

        // OTA升级Map
        HashMap<Integer, byte[]> dataMap = new HashMap<>(packageNum.intValue());

        try {
            int i = 0;
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[128];
            int len = -1;
            while((len = fis.read(data, 0, 128)) != -1){
                //StringWriter sw = new StringWriter(128);
                // 使用base64 进行编码
                // sw.write(new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8));
                //sw.write(new String(data, StandardCharsets.UTF_8));
//                dataMap.put(i++, sw.toString());
                byte[] copy = Arrays.copyOf(data, 128);
                dataMap.put(i++, copy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        otaFileNamePackageMapping.put(ota_name, dataMap);
        otaManager.update(ota_name, destinationIpPort);

        log.info("启用线程池，发送升级程序数据包...");
    }
}
