package com.helloworldcoin.application.vo.framwork;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class Response<T> {

    //only two status : success or fail
    private String status;
    private String message;
    private T data;

    private Response(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static<T> Response<T> success(T data){
        return new Response("success",null,data);
    }
    public static<T> Response fail(String message){
        return new Response("fail",message,null);
    }
    public static<T> Response serviceUnavailable(){
        return fail("service_unavailable");
    }
    public static<T> Response serviceUnauthorized(){
        return fail("service_unauthorized");
    }




    //region get set
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    //endregion
}
