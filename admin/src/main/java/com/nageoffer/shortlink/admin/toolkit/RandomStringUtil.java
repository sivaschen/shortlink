package com.nageoffer.shortlink.admin.toolkit;

import java.security.SecureRandom;

/**
 * 随机字符串生成工具类
 */
public final class RandomStringUtil {

    // 包含数字、大写字母和小写字母的字符池
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 使用 SecureRandom 保证密码学安全，比普通的 Random 产生的随机数更难被预测
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 私有化构造方法，防止工具类被实例化
     */
    private RandomStringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 生成固定长度为 6 的随机字符串 (包含大小写字母和数字)
     *
     * @return 6位随机字符串
     */
    public static String generate6CharString() {
        return generateRandomString(6);
    }

    /**
     * 通用方法：生成指定长度的随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        // 使用 StringBuilder 拼接字符串，提高性能
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 从字符池中随机挑取一个字符的索引
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            // 将挑取的字符追加到结果中
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }


}
