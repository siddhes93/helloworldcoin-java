package com.xingkaichun.helloworldblockchain.application.vo.framwork;

/**
 *
 * @author 邢开春 409060350@qq.com
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
    public static<T> Response requestParamIllegal(){
        return fail(ResponseMessage.REQUEST_PARAM_ILLEGAL);
    }
    public static<T> Response serviceUnavailable(){
        return fail(ResponseMessage.SERVICE_UNAVAILABLE);
    }
    public static<T> Response serviceUnauthorized(){
        return fail(ResponseMessage.SERVICE_UNAUTHORIZED);
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
