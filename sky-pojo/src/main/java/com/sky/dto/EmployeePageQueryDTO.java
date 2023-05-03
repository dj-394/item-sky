package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

    public int getPage(){
        return page == 0 ? 1 : page;
    }

    public int getPageSize() {
        return pageSize == 0 ? 10 : pageSize;
    }
}



