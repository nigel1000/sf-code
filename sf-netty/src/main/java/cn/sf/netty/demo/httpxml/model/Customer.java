package cn.sf.netty.demo.httpxml.model;

import lombok.Data;

import java.util.List;

@Data
public class Customer {
    private long customerNumber;
    /**
     * Personal name.
     */
    private String firstName;
    /**
     * Family name.
     */
    private String lastName;
    /**
     * Middle name(s), if any.
     */
    private List middleNames;
}