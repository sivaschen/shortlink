package com.nageoffer.shortlink.admin.test;

public class ShortlinkAdminTest {

    public static final String sql = "CREATE TABLE `t_link_goto_%d` (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "  `gid` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,\n" +
            "  `full_short_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf(sql+"%n", i);
        }
    }
}
