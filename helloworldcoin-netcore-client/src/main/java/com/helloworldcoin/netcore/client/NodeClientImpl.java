package com.helloworldcoin.netcore.client;

import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.setting.NetworkSetting;
import com.helloworldcoin.util.JsonUtil;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.NetUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeClientImpl implements NodeClient {

    private String ip;

    public NodeClientImpl(String ip) {
        this.ip = ip;
    }

    @Override
    public PostTransactionResponse postTransaction(PostTransactionRequest request) {
        try {
            String requestUrl = getUrl(API.POST_TRANSACTION);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,PostTransactionResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public PingResponse pingNode(PingRequest request) {
        try {
            String requestUrl = getUrl(API.PING);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,PingResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public GetBlockResponse getBlock(GetBlockRequest request) {
        try {
            String requestUrl = getUrl(API.GET_BLOCK);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,GetBlockResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            String requestUrl = getUrl(API.GET_NODES);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetNodesResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public PostBlockResponse postBlock(PostBlockRequest request) {
        try {
            String requestUrl = getUrl(API.POST_BLOCK);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,PostBlockResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public PostBlockchainHeightResponse postBlockchainHeight(PostBlockchainHeightRequest request) {
        try {
            String requestUrl = getUrl(API.POST_BLOCKCHAIN_HEIGHT);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, PostBlockchainHeightResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            String requestUrl = getUrl(API.GET_BLOCKCHAIN_HEIGHT);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetBlockchainHeightResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest request) {
        try {
            String requestUrl = getUrl(API.GET_UNCONFIRMED_TRANSACTIONS);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetUnconfirmedTransactionsResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    private String getUrl(String api) {
        return "http://" + ip + ":" + NetworkSetting.PORT + api;
    }
}