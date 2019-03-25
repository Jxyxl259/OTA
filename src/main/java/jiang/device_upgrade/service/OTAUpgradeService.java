package jiang.device_upgrade.service;

import jiang.device_upgrade.controller.OTAUpgradeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @description:
 * @author:
 * @create: 2019-03-22 18:17
 */
@Service("otaUpgradeService")
public class OTAUpgradeService {

    private static Logger log = LoggerFactory.getLogger(OTAUpgradeService.class);

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
}
