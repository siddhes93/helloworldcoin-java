package com.helloworldcoin.netcore.dto;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeDto {

    private String ip;

    public NodeDto() {
    }
    public NodeDto(String ip) {
        this.ip = ip;
    }





    //region get set
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    //endregion
}
