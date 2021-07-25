package com.xingkaichun.helloworldblockchain.application.vo.framwork;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class Response<T> {

    private String status;
    private String message;
    private T data;

    public Response(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public static<T> Response<T> createSuccessResponse(String message){
        return createSuccessResponse(message,null);
    }
    public static<T> Response<T> createSuccessResponse(String message, T data){
        return new Response("success",message,data);
    }
    public static<T> Response createFailResponse(String message){
        return new Response("failed",message,null);
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
