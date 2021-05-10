package com.ted.enums;

/**
 * 性别
 *
 * @author Ted
 */
public enum GenderEnum {
    MALE(1, "男"),
    FEMALE(0, "女");

    //成员变量
    private int id;
    private String name;

    //构造方法
    GenderEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id id
     * @return GenderEnum
     */
    public static GenderEnum getEnum(int id) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getId() == id) {
                return gender;
            }
        }
        return null;
    }

    /**
     * @param name name
     * @return GenderEnum
     */
    public static GenderEnum getEnumByName(String name) {
        for (GenderEnum genderEnum : GenderEnum.values()) {
            if (genderEnum.getName().equals(name)) {
                return genderEnum;
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
