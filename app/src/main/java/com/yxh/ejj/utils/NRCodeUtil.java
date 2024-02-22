package com.yxh.ejj.utils;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public
/**
 * @Description  {具体做什么}
 * @Author wushan
 * @Date 2021/12/9 22:14
 */

class NRCodeUtil {

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

//    /**
//     * 解析指令后的业务标识处理
//     * @param ywtypeStrTemp  16进制业务code
//     */
//    private static void dealBussCodeEnd( String ywtypeStrTemp,int index,String xml,IDecodeListener listener) {
//
//        switch (ywtypeStrTemp) {
//            case NetConstant.HX_WIFI_PWD_BUSINESS_CODE_2:
//               //南瑞发起的连接
//                listener.decodeSuccess(ywtypeStrTemp,index,xml);
//                break;
//            case NetConstant.HX_WIFI_PWD_BUSINESS_CODE_81:
//                //发送指令到仪器后，仪器返回的确认包指令，自增操作已经在start时执行过，此处不做处理
//                break;
//            case NetConstant.HX_WIFI_PWD_BUSINESS_CODE_3:
//                //仪器发送试验数据结果指令，收到后需要返回确认包指令 800000003 给仪器 序号原值返回
//                YcApp.getInstance().wifiRequestViewModel.sendMessage(
//                        encode(index,getErrorXml(200),NetConstant.HX_WIFI_PWD_BUSINESS_CODE_83));
//                break;
//            default:
//                break;
//        }
//
//    }

    public static Integer bytesReverse2Int(byte[] array) {
        String hexStr = bytes2HexStringReverse(array);
        String devStr = hexToDec(hexStr);
        return Integer.parseInt(devStr);
    }

    /** 用于打印输出 测试用
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] array) {
        StringBuilder builder = new StringBuilder();

        for (byte b : array) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }

        String result = builder.toString().toUpperCase();
        return result;
    }

    public static String bytes2HexStringReverse(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i = array.length-1 ; i>=0; --i) {
            String hex = Integer.toHexString(array[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }
        return builder.toString().toUpperCase();
    }


    /**
     * Get XML String of utf-8
     *
     * @return XML-Formed string
     */
    public static String getUTF8XMLString(String xml) {
        // A StringBuffer Object
        StringBuffer sb = new StringBuffer();
        sb.append(xml);
        String xmString = "";
        String xmlUTF8="";
        try {
            xmString = new String(sb.toString().getBytes("UTF-8"));
            xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
            System.out.print("utf-8 编码：" + xmlUTF8) ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // return to String Formed
        return xmlUTF8;
    }

    /**
     * int -> 16str
     * @param value
     * @return
     */
    public static String int2Hex(int value) {
        return int2Hex(value, 2);
    }

    /**
     * int ->16str
     * @param value
     * @param length
     * @return
     */
    public static String int2Hex(int value, int length) {

        String st = String.format("%" + length + "s", Integer.toHexString(value).toUpperCase());
        st = st.replaceAll(" ", "0");
        return st;
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */
    public static String strTo16(String s) {
//        String str = "";
//        for (int i = 0; i < s.length(); i++) {
//            int ch = (int) s.charAt(i);
//            String s4 = Integer.toHexString(ch);
//            str = str + s4;
//        }
//        return str.toUpperCase();

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = s.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制转换成为string类型字符串
     * @param hexStr
     * @return
     */
    public static String hexStringToString(String hexStr) {
//        if (s == null || s.equals("")) {
//            return null;
//        }
//        s = s.replace(" ", "");
//        byte[] baKeyword = new byte[s.length() / 2];
//        for (int i = 0; i < baKeyword.length; i++) {
//            try {
//                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            s = new String(baKeyword, "UTF-8");
//            new String();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return s;

        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static String hexToDec(String hexStr) {
        /*String str;
        if (hexStr.equals("00000000")) {
            str = hexStr;
        } else {
            str = hexStr.replaceFirst("^0*", "");
        }*/
        int data = Integer.parseInt(hexStr, 16);
        return Integer.toString(data, 10);
    }


    /**
     * 16进制字符串转byte数组
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }



    /**
     * 将int转为高字节在前，低字节在后的byte数组（大端）
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] intToByteBig(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组（小端）
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] intToByteLittle(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /** float转byte(小端序) */
    public static byte[] float2ByteLittle(float big) {
        int data = Float.floatToIntBits(big);
        return intToByteLittle(data);
    }

    /**
     * byte数组到int的转换(小端)
     *
     * @param bytes
     * @return
     */
    public static int bytes2IntLittle(byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;

        return int1 | int2 | int3 | int4;
    }

    /**
     * byte数组到int的转换(大端)
     *
     * @param bytes
     * @return
     */
    public static int bytes2IntBig(byte[] bytes) {
        int int1 = bytes[3] & 0xff;
        int int2 = (bytes[2] & 0xff) << 8;
        int int3 = (bytes[1] & 0xff) << 16;
        int int4 = (bytes[0] & 0xff) << 24;

        return int1 | int2 | int3 | int4;
    }

    /**
     * 将short转为高字节在前，低字节在后的byte数组（大端）
     *
     * @param n short
     * @return byte[]
     */
    public static byte[] shortToByteBig(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 将short转为低字节在前，高字节在后的byte数组(小端)
     *
     * @param n short
     * @return byte[]
     */
    public static byte[] shortToByteLittle(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 读取小端byte数组为short
     *
     * @param b
     * @return
     */
    public static short byteToShortLittle(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    /**
     * 读取大端byte数组为short
     *
     * @param b
     * @return
     */
    public static short byteToShortBig(byte[] b) {
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }

    /**
     * long类型转byte[] (大端)
     *
     * @param n
     * @return
     */
    public static byte[] longToBytesBig(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * long类型转byte[] (小端)
     *
     * @param n
     * @return
     */
    public static byte[] longToBytesLittle(long n) {
        byte[] b = new byte[8];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        b[4] = (byte) (n >> 32 & 0xff);
        b[5] = (byte) (n >> 40 & 0xff);
        b[6] = (byte) (n >> 48 & 0xff);
        b[7] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * byte[]转long类型(小端)
     *
     * @param array
     * @return
     */
    public static long bytesToLongLittle(byte[] array) {
        return ((((long) array[0] & 0xff) << 0)
                | (((long) array[1] & 0xff) << 8)
                | (((long) array[2] & 0xff) << 16)
                | (((long) array[3] & 0xff) << 24)
                | (((long) array[4] & 0xff) << 32)
                | (((long) array[5] & 0xff) << 40)
                | (((long) array[6] & 0xff) << 48)
                | (((long) array[7] & 0xff) << 56));
    }

    /**
     * byte[]转long类型(大端)
     *
     * @param array
     * @return
     */
    public static long bytesToLongBig(byte[] array) {
        return ((((long) array[0] & 0xff) << 56)
                | (((long) array[1] & 0xff) << 48)
                | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32)
                | (((long) array[4] & 0xff) << 24)
                | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8)
                | (((long) array[7] & 0xff) << 0));
    }


    /**
     * 生成 crc32 校验码. *
     *
     * @paramdata 待校验数据
     * @paramsize 数据长度
     * @returncrc32 校验码
     */
    public static int getCrc32(byte[] data, int size) {
        int[] table = {0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419,
                0x706af48f, 0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4,
                0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07,
                0x90bf1d91, 0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de,
                0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7, 0x136c9856,
                0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
                0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4,
                0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
                0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3,
                0x45df5c75, 0xdcd60dcf, 0xabd13d59, 0x26d930ac, 0x51de003a,
                0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599,
                0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
                0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190,
                0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f,
                0x9fbfe4a5, 0xe8b8d433, 0x7807c9a2, 0x0f00f934, 0x9609a88e,
                0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
                0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
                0x6c0695ed, 0x1b01a57b, 0x8208f4c1,
                0xf50fc457, 0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c,
                0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65, 0x4db26158,
                0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
                0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846,
                0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
                0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525,
                0x206f85b3, 0xb966d409,
                0xce61e49f, 0x5edef90e, 0x29d9c998,
                0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b,
                0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
                0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12,
                0x94643b84, 0x0d6d6a3e,
                0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d,
                0x0a00ae27, 0x7d079eb1, 0xf00f9344, 0x8708a3d2, 0x1e01f268,
                0x6906c2fe, 0xf762575d,
                0x806567cb, 0x196c3671, 0x6e6b06e7,
                0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f,
                0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
                0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd,
                0x48b2364b, 0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60,
                0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79, 0xcb61b38c,
                0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703,
                0x220216b9, 0x5505262f,
                0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92,
                0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
                0x9b64c2b0, 0xec63f226,
                0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785,
                0x05005713, 0x95bf4a82, 0xe2b87a14,
                0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7,
                0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e,
                0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777, 0x88085ae6,
                0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
                0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354,
                0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
                0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53,
                0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9, 0xbdbdf21c, 0xcabac28a,
                0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729,
                0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
                0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d,};
        int crc = 0xffffffff;
        for (int i = 0; i < size; i++) {
            crc = (crc >>> 8 ^ table[(crc ^ data[i]) & 0xff]);
        }
        return crc;
    }

    /**
     * 计算CRC16校验码
     *
     * @param data
     * @return
     */
    public static int getCRC16(byte[] data) {
        int CRC = calcCrc16(data, 0, data.length, 0xffff);
        return CRC;
    }

    /**
     * 计算CRC16校验
     *
     * @param data
     *            需要计算的数组
     * @param offset
     *            起始位置
     * @param len
     *            长度
     * @param preval
     *            之前的校验值
     * @return CRC16校验值
     */
    private static int calcCrc16(byte[] data, int offset, int len, int preval) {
        byte[] crc16_tab_h = {
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1,
                (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1,
                (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,

                (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1,
                (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
                (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01,(byte) 0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,(byte) 0x80, (byte)0x41,

                (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81,(byte) 0x40, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,(byte) 0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
                (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1,
                (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
                (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
                (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40
        } ;
        byte[] crc16_tab_l = {
                (byte)0x00, (byte)0xC0, (byte)0xC1, (byte)0x01, (byte)0xC3, (byte)0x03, (byte)0x02, (byte)0xC2, (byte)0xC6, (byte)0x06,
                (byte)0x07, (byte)0xC7, (byte)0x05, (byte)0xC5, (byte)0xC4, (byte)0x04, (byte)0xCC, (byte)0x0C, (byte)0x0D, (byte)0xCD,
                (byte)0x0F, (byte)0xCF, (byte)0xCE, (byte)0x0E, (byte)0x0A, (byte)0xCA, (byte)0xCB, (byte)0x0B, (byte)0xC9, (byte)0x09,
                (byte)0x08, (byte)0xC8, (byte)0xD8, (byte)0x18, (byte)0x19, (byte)0xD9, (byte)0x1B, (byte)0xDB, (byte)0xDA, (byte)0x1A,
                (byte)0x1E, (byte)0xDE, (byte)0xDF, (byte)0x1F, (byte)0xDD, (byte)0x1D, (byte)0x1C, (byte)0xDC, (byte)0x14, (byte)0xD4,
                (byte)0xD5, (byte)0x15, (byte)0xD7, (byte)0x17, (byte)0x16, (byte)0xD6, (byte)0xD2, (byte)0x12, (byte)0x13, (byte)0xD3,
                (byte)0x11, (byte)0xD1, (byte)0xD0, (byte)0x10, (byte)0xF0, (byte)0x30, (byte)0x31, (byte)0xF1, (byte)0x33, (byte)0xF3,
                (byte)0xF2, (byte)0x32, (byte)0x36, (byte)0xF6, (byte)0xF7, (byte)0x37, (byte)0xF5, (byte)0x35, (byte)0x34, (byte)0xF4,
                (byte)0x3C, (byte)0xFC, (byte)0xFD, (byte)0x3D, (byte)0xFF, (byte)0x3F, (byte)0x3E, (byte)0xFE, (byte)0xFA, (byte)0x3A,
                (byte)0x3B, (byte)0xFB, (byte)0x39, (byte)0xF9, (byte)0xF8, (byte)0x38, (byte)0x28, (byte)0xE8, (byte)0xE9, (byte)0x29,

                (byte)0xEB, (byte)0x2B, (byte)0x2A, (byte)0xEA, (byte)0xEE, (byte)0x2E, (byte)0x2F, (byte)0xEF, (byte)0x2D, (byte)0xED,
                (byte)0xEC, (byte)0x2C, (byte)0xE4, (byte)0x24, (byte)0x25, (byte)0xE5, (byte)0x27, (byte)0xE7, (byte)0xE6, (byte)0x26,
                (byte)0x22, (byte)0xE2, (byte)0xE3, (byte)0x23, (byte)0xE1, (byte)0x21, (byte)0x20, (byte)0xE0, (byte)0xA0, (byte)0x60,
                (byte)0x61, (byte)0xA1, (byte)0x63, (byte)0xA3, (byte)0xA2, (byte)0x62, (byte)0x66, (byte)0xA6, (byte)0xA7, (byte)0x67,
                (byte)0xA5, (byte)0x65, (byte)0x64, (byte)0xA4, (byte)0x6C, (byte)0xAC, (byte)0xAD, (byte)0x6D, (byte)0xAF, (byte)0x6F,
                (byte)0x6E, (byte)0xAE, (byte)0xAA, (byte)0x6A, (byte)0x6B, (byte)0xAB, (byte)0x69, (byte)0xA9, (byte)0xA8, (byte)0x68,
                (byte)0x78, (byte)0xB8, (byte)0xB9, (byte)0x79, (byte)0xBB, (byte)0x7B, (byte)0x7A, (byte)0xBA, (byte)0xBE, (byte)0x7E,
                (byte)0x7F, (byte)0xBF, (byte)0x7D, (byte)0xBD, (byte)0xBC, (byte)0x7C, (byte)0xB4, (byte)0x74, (byte)0x75, (byte)0xB5,
                (byte)0x77, (byte)0xB7, (byte)0xB6, (byte)0x76, (byte)0x72, (byte)0xB2, (byte)0xB3, (byte)0x73, (byte)0xB1, (byte)0x71,
                (byte)0x70, (byte)0xB0, (byte)0x50, (byte)0x90, (byte)0x91, (byte)0x51, (byte)0x93, (byte)0x53, (byte)0x52, (byte)0x92,

                (byte)0x96, (byte)0x56, (byte)0x57, (byte)0x97, (byte)0x55, (byte)0x95, (byte)0x94, (byte)0x54, (byte)0x9C, (byte)0x5C,
                (byte)0x5D, (byte)0x9D, (byte)0x5F, (byte)0x9F, (byte)0x9E, (byte)0x5E, (byte)0x5A, (byte)0x9A, (byte)0x9B, (byte)0x5B,
                (byte)0x99, (byte)0x59, (byte)0x58, (byte)0x98, (byte)0x88, (byte)0x48, (byte)0x49, (byte)0x89, (byte)0x4B, (byte)0x8B,
                (byte)0x8A, (byte)0x4A, (byte)0x4E, (byte)0x8E, (byte)0x8F, (byte)0x4F, (byte)0x8D, (byte)0x4D, (byte)0x4C, (byte)0x8C,
                (byte)0x44, (byte)0x84, (byte)0x85, (byte)0x45, (byte)0x87, (byte)0x47, (byte)0x46, (byte)0x86, (byte)0x82, (byte)0x42,

                (byte)0x43, (byte)0x83, (byte)0x41, (byte)0x81, (byte)0x80, (byte)0x40
        } ;
        int ucCRCHi = (preval & 0xff00) >> 8;
        int ucCRCLo = preval & 0x00ff;
        int iIndex;
        for (int i = 0; i < len; ++i) {
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];
            ucCRCHi = crc16_tab_l[iIndex];
        }
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
    }

    /**
     * 这个是把文件变成二进制流
     *
     * @return
     * @throws Exception
     */
    public static byte[] readStream() throws Exception {

//        String fileName = FILE_MODEL_DATA_DIR + "baowen.log";
        String fileName =  Environment.getExternalStorageDirectory() +
                File.separator +"datasend.log";
        FileInputStream fs = new FileInputStream(fileName);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();

    }

    public static void saveAsFileWriter(String content,String path) {
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(path, true);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
