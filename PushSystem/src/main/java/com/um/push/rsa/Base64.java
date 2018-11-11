package com.um.push.rsa;

public final class Base64
{
    /** 
     * Base64����?
     */
    private static final char[] BASE64CODE =
        {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/',};
    
    /** 
     * Base64����?
     */
    private static final byte[] BASE64DECODE =
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1,
            -1,
            -1, // ע������63��Ϊ����SMP��
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 63,
            -1,
            63, // ��/���͡�-���������?3��
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13,
            14, // ע������0��
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
            -1, // ��A���͡�=���������?��
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            -1, -1, -1, -1, -1,};
    
    private static final int HEX_255 = 0x0000ff;
    
    private static final int HEX_16515072 = 0xfc0000;
    
    private static final int HEX_258048 = 0x3f000;
    
    private static final int HEX_4032 = 0xfc0;
    
    private static final int HEX_63 = 0x3f;
    
    private static final int HEX_16711680 = 0xff0000;
    
    private static final int HEX_65280 = 0x00ff00;
    
    private static final int NUMBER_TWO = 2;
    
    private static final int NUMBER_THREE = 3;
    
    private static final int NUMBER_FOUR = 4;
    
    private static final int NUMBER_SIX = 6;
    
    private static final int NUMBER_EIGHT = 8;
    
    private static final int NUMBER_TWELVE = 12;
    
    private static final int NUMBER_SIXTEEN = 16;
    
    private static final int NUMBER_EIGHTEEN = 18;
    
    /**
     * ���췽��˽�л�����ֹʵ��   
     */
    private Base64()
    {
    }
    
    /** 
     * Base64���롣���ֽ��������ֽ�3��һ������4���ɼ��ַ� 
     * 
     * @param b 
     *            ��Ҫ��������ֽ���ݡ� 
     * @return ������Base64�ַ� 
     */
    public static String encode(byte[] b)
    {
        int code = 0;
        
        // ��ʵ�ʱ���󳤶ȿ����ڴ棬�ӿ��ٶ�?
        StringBuffer sb = new StringBuffer(((b.length - 1) / NUMBER_THREE) << NUMBER_TWO + NUMBER_FOUR);
        
        // ���б���
        for (int i = 0; i < b.length; i++)
        {
            code |=
                (b[i] << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT))
                    & (HEX_255 << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT));
            if (i % NUMBER_THREE == NUMBER_TWO || i == b.length - 1)
            {
                sb.append(BASE64CODE[(code & HEX_16515072) >>> NUMBER_EIGHTEEN]);
                sb.append(BASE64CODE[(code & HEX_258048) >>> NUMBER_TWELVE]);
                sb.append(BASE64CODE[(code & HEX_4032) >>> NUMBER_SIX]);
                sb.append(BASE64CODE[code & HEX_63]);
                code = 0;
            }
        }
        
        // ���ڳ��ȷ�3��������ֽ����飬����ǰ�Ȳ�?��������β��������=���棬
        // =�ĸ���Ͷ�ȱ�ĳ���һ�£��Դ�����ʶ�����ʵ�ʳ���
        if (b.length % NUMBER_THREE > 0)
        {
            sb.setCharAt(sb.length() - 1, '=');
        }
        if (b.length % NUMBER_THREE == 1)
        {
            sb.setCharAt(sb.length() - NUMBER_TWO, '=');
        }
        return sb.toString();
    }
    
    /** 
     * Base64���롣 
     * 
     * @param code 
     *            ��Base64�����ASCII�ַ� 
     * @return �������ֽ����?   
     */
    public static byte[] decode(String code)
    {
        // ������Ϸ���?
        if (code == null)
        {
            return null;
        }
        int len = code.length();
        if (len % NUMBER_FOUR != 0)
        {
            throw new IllegalArgumentException("Base64 string length must be 4*n");
        }
        if (code.length() == 0)
        {
            return new byte[0];
        }
        
        // ͳ�����ĵȺŸ���
        int pad = 0;
        if (code.charAt(len - 1) == '=')
        {
            pad++;
        }
        if (code.charAt(len - NUMBER_TWO) == '=')
        {
            pad++;
        }
        
        // ������Ⱥŵĸ���������ʵ����ݳ���
        int retLen = len / NUMBER_FOUR * NUMBER_THREE - pad;
        
        // �����ֽ�����ռ�?
        byte[] ret = new byte[retLen];
        
        // ������
        char ch1, ch2, ch3, ch4;
        int i;
        for (i = 0; i < len; i += NUMBER_FOUR)
        {
            int j = i / NUMBER_FOUR * NUMBER_THREE;
            ch1 = code.charAt(i);
            ch2 = code.charAt(i + 1);
            ch3 = code.charAt(i + NUMBER_TWO);
            ch4 = code.charAt(i + NUMBER_THREE);
            int tmp =
                (BASE64DECODE[ch1] << NUMBER_EIGHTEEN) | (BASE64DECODE[ch2] << NUMBER_TWELVE)
                    | (BASE64DECODE[ch3] << NUMBER_SIX) | (BASE64DECODE[ch4]);
            ret[j] = (byte)((tmp & HEX_16711680) >> NUMBER_SIXTEEN);
            if (i < len - NUMBER_FOUR)
            {
                ret[j + 1] = (byte)((tmp & HEX_65280) >> NUMBER_EIGHT);
                ret[j + NUMBER_TWO] = (byte)((tmp & HEX_255));
                
            }
            else
            {
                if (j + 1 < retLen)
                {
                    ret[j + 1] = (byte)((tmp & HEX_65280) >> NUMBER_EIGHT);
                }
                if (j + NUMBER_TWO < retLen)
                {
                    ret[j + NUMBER_TWO] = (byte)((tmp & HEX_255));
                }
            }
        }
        return ret;
    }
}
