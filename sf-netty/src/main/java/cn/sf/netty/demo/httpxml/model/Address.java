package cn.sf.netty.demo.httpxml.model;

import lombok.Data;

@Data
public class Address {
    /**
     * First line of street information (required).
     */
    private String street1;
    /**
     * Second line of street information (optional).
     */
    private String street2;
    private String city;
    /**
     * State abbreviation (required for the U.S. and Canada, optional
     * otherwise).
     */
    private String state;
    /**
     * Postal code(required for the U.S.and Canada,optional otherwise).
     */
    private String postCode;
    /**
     * Country name (optional, U.S. assumed if not supplied).
     */
    private String country;
}