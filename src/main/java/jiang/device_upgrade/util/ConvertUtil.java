package jiang.device_upgrade.util;

/**
 * @description: 进制转换工具类
 * @author: jiangbug@outlook.com
 * @create: 2019-03-27 22:41
 */
public class ConvertUtil {

    /**
     * 将 32位 int 转换为 2进制字符串
     * @param n
     */
    public static String int2BinStr(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 31; i >= 0; i--)
            s.append(n >>> i & 1);
        return s.toString();
    }


    /**
     * 将 32位 int 转换为 2进制字符串
     * 并将高16位 与低16位转换为 十六进制字符串
     * @param n
     */
    public static String[] int2HexHightLow(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 31; i >= 0; i--)
            s.append(n >>> i & 1);
        String bin = s.toString();
        String height = bin.substring(0, 16);
        String low = bin.substring(16);

        //System.out.println("H -> "+height + "\nL -> "+low);

        return new String[2];
    }


    /**
     * 二进制数组转16进制字符串
     * e.g :
     *      int i = 5; 二进制表示 0(8) 0(8) 00000000 00000101
     *
     *      切分为高16位 -> 0(8) 0(8)   低16位 -> 00000000 00000101
     *
     *      高16位 转为 16进制字符串
     *          new byte[]{0,0} -> 00
     *      低16位 转为 16进制字符串
     *          new byte[]{0,5} -> 05
     *      高低16位 一起转16进制
     *          new byte[]{0(00000000),0(00000000),0(00000000),5(00000101)} -> 00 00 00 05
     *
     *      int i = 131071;（65536 + 65535）二进制表示 0000 0000 0000 0001 1111 1111 1111 1111
     *      高低16位一起转16进制
     *          new byte[]{0(00000000),1(00000001),255(11111111),255(11111111)} -> 00 01 FF FF
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);

            //stringBuilder.append(i + ":");

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }

        return stringBuilder.toString().substring(0, stringBuilder.toString().length()-1);
    }



    public static void main(String[] args) {
//        System.out.println(int2HexHightLow(5));
//        int i = 0x05;
//        System.out.println(i);
//        System.out.println(bytesToHexString(new byte[]{0,0,0, 1, 15,15,15,15}));
        System.out.println(Integer.toHexString(Integer.parseInt("1111111111111111",2)));

    }



}
