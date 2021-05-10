package com.ted.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Content实体类
 *
 * @author Ted
 */
@Data
public class Content implements Serializable {
    private static final long serialVersionUID = -8945335516175714008L;

    private long id;
    private long tableId;
    private String tableName;
    private String detail;
}
