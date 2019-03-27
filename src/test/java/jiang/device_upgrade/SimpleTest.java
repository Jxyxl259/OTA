package jiang.device_upgrade;

import org.junit.Test;

/**
 * @description:
 * @author:
 * @create: 2019-03-27 22:39
 */
public class SimpleTest {

    @Test
    public void int2BinStr() {
        int i = -1;
        binaryToDecimal(i);

    }

    public void binaryToDecimal(int n) {
        for (int i = 31; i >= 0; i--)
            System.out.print(n >>> i & 1);
    }

}
