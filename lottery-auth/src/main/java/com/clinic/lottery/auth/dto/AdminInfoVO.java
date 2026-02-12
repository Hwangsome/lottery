package com.clinic.lottery.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * 管理员信息 VO
 */
@Data
public class AdminInfoVO {

    private String id;
    private String username;
    private String name;
    private Integer role;
    private List<String> permissions;
}
