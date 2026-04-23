package com.nageoffer.shortlink.admin.test;

public class ShortlinkAdminTest {

    public static final String sql = "CREATE TABLE t_user_%d (\n" +
            "    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "    username VARCHAR(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户名',\n" +
            "    password VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '密码',\n" +
            "    real_name VARCHAR(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '真实姓名',\n" +
            "    phone VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '手机号',\n" +
            "    mail VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '邮箱',\n" +
            "    deletion_time BIGINT(20) NULL COMMENT '注销时间戳',\n" +
            "    create_time DATETIME NULL COMMENT '创建时间',\n" +
            "    update_time DATETIME NULL COMMENT '修改时间',\n" +
            "    del_flag TINYINT(1) NULL DEFAULT 0 COMMENT '删除标识 0: 未删除 1: 已删除',\n" +
            "    PRIMARY KEY (id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf(sql+"%n", i);
        }
    }
}
