package com.ted.enums;

/**
 * 状态
 *
 * @author Ted
 */
public enum StatusEnum {
    ACTIVE(1, "正常"),
    DELETED(0, "禁用");

    //成员变量
    private int id;
    private String name;

    //构造方法
    StatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id id
     * @return StatusEnum
     */
    public static StatusEnum getEnum(int id) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getId() == id) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * @param name name
     * @return StatusEnum
     */
    public static StatusEnum getEnumByName(String name) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getName().equals(name)) {
                return statusEnum;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
