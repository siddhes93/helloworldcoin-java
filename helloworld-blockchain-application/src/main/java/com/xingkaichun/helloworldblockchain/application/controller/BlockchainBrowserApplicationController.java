package com.xingkaichun.helloworldblockchain.application.controller;

import com.xingkaichun.helloworldblockchain.application.service.BlockchainBrowserApplicationService;
import com.xingkaichun.helloworldblockchain.application.vo.BlockchainBrowserApplicationApi;
import com.xingkaichun.helloworldblockchain.application.vo.block.*;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.PageCondition;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.Response;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.ResponseMessage;
import com.xingkaichun.helloworldblockchain.application.vo.node.QueryBlockchainHeightRequest;
import com.xingkaichun.helloworldblockchain.application.vo.node.QueryBlockchainHeightResponse;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tool.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tool.SizeTool;
import com.xingkaichun.helloworldblockchain.core.tool.TransactionDtoTool;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.setting.GenesisBlockSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块链浏览器应用控制器：查询区块、交易、地址等功能。
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class BlockchainBrowserApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private BlockchainBrowserApplicationService blockchainBrowserApplicationService;



    /**
     * 根据交易哈希查询交易
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionByTransactionHashResponse> queryTransactionByTransactionHash(@RequestBody QueryTransactionByTransactionHashRequest request){
        try {
            TransactionVo transactionVo = blockchainBrowserApplicationService.queryTransactionByTransactionHash(request.getTransactionHash());
            if(transactionVo == null){
                return Response.fail(ResponseMessage.NOT_FOUND_TRANSACTION);
            }

            QueryTransactionByTransactionHashResponse response = new QueryTransactionByTransactionHashResponse();
            response.setTransaction(transactionVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "根据交易哈希查询交易失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据区块哈希与交易高度查询交易列表
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTIONS_BY_BLOCK_HASH_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionsByBlockHashTransactionHeightResponse> queryTransactionsByBlockHashTransactionHeight(@RequestBody QueryTransactionsByBlockHashTransactionHeightRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            if(StringUtil.isEmpty(request.getBlockHash())){
                return Response.requestParamIllegal();
            }
            List<TransactionVo> transactionVos = blockchainBrowserApplicationService.queryTransactionListByBlockHashTransactionHeight(request.getBlockHash(),pageCondition.getFrom(),pageCondition.getSize());
            QueryTransactionsByBlockHashTransactionHeightResponse response = new QueryTransactionsByBlockHashTransactionHeightResponse();
            response.setTransactions(transactionVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "根据交易高度查询交易失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据地址获取交易输出
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionOutputByAddressResponse> queryTransactionOutputByAddress(@RequestBody QueryTransactionOutputByAddressRequest request){
        try {
            TransactionOutputVo3 transactionOutputVo3 = blockchainBrowserApplicationService.queryTransactionOutputByAddress(request.getAddress());
            QueryTransactionOutputByAddressResponse response = new QueryTransactionOutputByAddressResponse();
            response.setTransactionOutput(transactionOutputVo3);
            return Response.success(response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据交易输出ID获取交易输出
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionOutputByTransactionOutputIdResponse> queryTransactionOutputByTransactionOutputId(@RequestBody QueryTransactionOutputByTransactionOutputIdRequest request){
        try {
            TransactionOutputVo3 transactionOutputVo3 = blockchainBrowserApplicationService.queryTransactionOutputByTransactionOutputId(request.getTransactionHash(),request.getTransactionOutputIndex());
            QueryTransactionOutputByTransactionOutputIdResponse response = new QueryTransactionOutputByTransactionOutputIdResponse();
            response.setTransactionOutput(transactionOutputVo3);
            return Response.success(response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 查询区块链高度
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCKCHAIN_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockchainHeightResponse> queryBlockchainHeight(@RequestBody QueryBlockchainHeightRequest request){
        try {
            long blockchainHeight = blockchainNetCore.getBlockchainCore().queryBlockchainHeight();
            QueryBlockchainHeightResponse response = new QueryBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询区块链高度失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据交易哈希查询未确认交易
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_UNCONFIRMED_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryUnconfirmedTransactionByTransactionHashResponse> queryUnconfirmedTransactionByTransactionHash(@RequestBody QueryUnconfirmedTransactionByTransactionHashRequest request){
        try {
            UnconfirmedTransactionVo unconfirmedTransactionVo = blockchainBrowserApplicationService.queryUnconfirmedTransactionByTransactionHash(request.getTransactionHash());
            if(unconfirmedTransactionVo == null){
                return Response.fail(ResponseMessage.NOT_FOUND_UNCONFIRMED_TRANSACTIONS);
            }
            QueryUnconfirmedTransactionByTransactionHashResponse response = new QueryUnconfirmedTransactionByTransactionHashResponse();
            response.setTransaction(unconfirmedTransactionVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "根据交易哈希查询未确认交易失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 查询未确认交易
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_UNCONFIRMED_TRANSACTIONS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryUnconfirmedTransactionsResponse> queryUnconfirmedTransactions(@RequestBody QueryUnconfirmedTransactionsRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionDto> transactionDtos = blockchainNetCore.getBlockchainCore().queryUnconfirmedTransactions(pageCondition.getFrom(),pageCondition.getSize());
            if(transactionDtos == null){
                return Response.fail(ResponseMessage.NOT_FOUND_UNCONFIRMED_TRANSACTIONS);
            }

            List<UnconfirmedTransactionVo> unconfirmedTransactionVos = new ArrayList<>();
            for(TransactionDto transactionDto : transactionDtos){
                UnconfirmedTransactionVo unconfirmedTransactionVo = blockchainBrowserApplicationService.queryUnconfirmedTransactionByTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
                if(unconfirmedTransactionVo != null){
                    unconfirmedTransactionVos.add(unconfirmedTransactionVo);
                }
            }
            QueryUnconfirmedTransactionsResponse response = new QueryUnconfirmedTransactionsResponse();
            response.setUnconfirmedTransactions(unconfirmedTransactionVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询未确认交易失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据区块高度查询区块
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockByBlockHeightResponse> queryBlockByBlockHeight(@RequestBody QueryBlockByBlockHeightRequest request){
        try {
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(request.getBlockHeight());
            if(blockVo == null){
                return Response.fail(ResponseMessage.NOT_FOUND_BLOCK);
            }
            QueryBlockByBlockHeightResponse response = new QueryBlockByBlockHeightResponse();
            response.setBlock(blockVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询获取失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 根据区块哈希查询区块
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockByBlockHashResponse> queryBlockByBlockHash(@RequestBody QueryBlockByBlockHashRequest request){
        try {
            Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHash(request.getBlockHash());
            if(block == null){
                return Response.fail(ResponseMessage.NOT_FOUND_BLOCK);
            }
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(block.getHeight());
            QueryBlockByBlockHashResponse response = new QueryBlockByBlockHashResponse();
            response.setBlock(blockVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "[根据区块哈希查询区块]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 查询最新的10个区块
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TOP10_BLOCKS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTop10BlocksResponse> queryTop10Blocks(@RequestBody QueryTop10BlocksRequest request){
        try {
            List<Block> blocks = new ArrayList<>();
            long blockHeight = blockchainNetCore.getBlockchainCore().queryBlockchainHeight();
            while (true){
                if(blockHeight <= GenesisBlockSetting.HEIGHT){
                    break;
                }
                Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHeight(blockHeight);
                blocks.add(block);
                if(blocks.size() >= 10){
                    break;
                }
                blockHeight--;
            }

            List<BlockVo2> blockVos = new ArrayList<>();
            for(Block block : blocks){
                BlockVo2 blockVo = new BlockVo2();
                blockVo.setHeight(block.getHeight());
                blockVo.setBlockSize(SizeTool.calculateBlockSize(block));
                blockVo.setTransactionCount(BlockTool.getTransactionCount(block));
                blockVo.setMinerIncentiveValue(BlockTool.getWritedIncentiveValue(block));
                blockVo.setTime(TimeUtil.formatMillisecondTimestamp(block.getTimestamp()));
                blockVo.setHash(block.getHash());
                blockVos.add(blockVo);
            }

            QueryTop10BlocksResponse response = new QueryTop10BlocksResponse();
            response.setBlocks(blockVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "[查询最新的10个区块]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
}