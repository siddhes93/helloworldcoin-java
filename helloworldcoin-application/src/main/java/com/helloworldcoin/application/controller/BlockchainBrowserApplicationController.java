package com.helloworldcoin.application.controller;

import com.helloworldcoin.application.service.BlockchainBrowserApplicationService;
import com.helloworldcoin.application.vo.BlockchainBrowserApplicationApi;
import com.helloworldcoin.application.vo.block.*;
import com.helloworldcoin.application.vo.framwork.PageCondition;
import com.helloworldcoin.application.vo.framwork.Response;
import com.helloworldcoin.application.vo.node.QueryBlockchainHeightRequest;
import com.helloworldcoin.application.vo.node.QueryBlockchainHeightResponse;
import com.helloworldcoin.application.vo.transaction.*;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.core.tool.SizeTool;
import com.helloworldcoin.core.tool.TransactionDtoTool;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.setting.GenesisBlockSetting;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Blockchain Browser Application Controller : query block、query transaction、query address and so on.
 *
 * @author x.king xdotking@gmail.com
 */
@RestController
public class BlockchainBrowserApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private BlockchainBrowserApplicationService blockchainBrowserApplicationService;



    /**
     * query transaction by transaction hash
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionByTransactionHashResponse> queryTransactionByTransactionHash(@RequestBody QueryTransactionByTransactionHashRequest request){
        try {
            TransactionVo transactionVo = blockchainBrowserApplicationService.queryTransactionByTransactionHash(request.getTransactionHash());
            QueryTransactionByTransactionHashResponse response = new QueryTransactionByTransactionHashResponse();
            response.setTransaction(transactionVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query transaction by transaction hash' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query transactions by (block hash and transaction height)
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTIONS_BY_BLOCK_HASH_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionsByBlockHashTransactionHeightResponse> queryTransactionsByBlockHashTransactionHeight(@RequestBody QueryTransactionsByBlockHashTransactionHeightRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionVo> transactionVos = blockchainBrowserApplicationService.queryTransactionListByBlockHashTransactionHeight(request.getBlockHash(),pageCondition.getFrom(),pageCondition.getSize());
            QueryTransactionsByBlockHashTransactionHeightResponse response = new QueryTransactionsByBlockHashTransactionHeightResponse();
            response.setTransactions(transactionVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query transactions by (block hash and transaction height)' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query transaction output by address
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionOutputByAddressResponse> queryTransactionOutputByAddress(@RequestBody QueryTransactionOutputByAddressRequest request){
        try {
            TransactionOutputVo3 transactionOutputVo3 = blockchainBrowserApplicationService.queryTransactionOutputByAddress(request.getAddress());
            QueryTransactionOutputByAddressResponse response = new QueryTransactionOutputByAddressResponse();
            response.setTransactionOutput(transactionOutputVo3);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query transaction output by address' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query transaction output by transaction output id
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryTransactionOutputByTransactionOutputIdResponse> queryTransactionOutputByTransactionOutputId(@RequestBody QueryTransactionOutputByTransactionOutputIdRequest request){
        try {
            TransactionOutputVo3 transactionOutputVo3 = blockchainBrowserApplicationService.queryTransactionOutputByTransactionOutputId(request.getTransactionHash(),request.getTransactionOutputIndex());
            QueryTransactionOutputByTransactionOutputIdResponse response = new QueryTransactionOutputByTransactionOutputIdResponse();
            response.setTransactionOutput(transactionOutputVo3);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query transaction output by transaction output id' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query blockchain height
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCKCHAIN_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockchainHeightResponse> queryBlockchainHeight(@RequestBody QueryBlockchainHeightRequest request){
        try {
            long blockchainHeight = blockchainNetCore.getBlockchainCore().queryBlockchainHeight();
            QueryBlockchainHeightResponse response = new QueryBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query blockchain height' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query unconfirmed transaction by transaction hash
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_UNCONFIRMED_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryUnconfirmedTransactionByTransactionHashResponse> queryUnconfirmedTransactionByTransactionHash(@RequestBody QueryUnconfirmedTransactionByTransactionHashRequest request){
        try {
            UnconfirmedTransactionVo unconfirmedTransactionVo = blockchainBrowserApplicationService.queryUnconfirmedTransactionByTransactionHash(request.getTransactionHash());
            QueryUnconfirmedTransactionByTransactionHashResponse response = new QueryUnconfirmedTransactionByTransactionHashResponse();
            response.setTransaction(unconfirmedTransactionVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query unconfirmed transaction by transaction hash' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query unconfirmed transactions
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_UNCONFIRMED_TRANSACTIONS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryUnconfirmedTransactionsResponse> queryUnconfirmedTransactions(@RequestBody QueryUnconfirmedTransactionsRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionDto> transactionDtos = blockchainNetCore.getBlockchainCore().queryUnconfirmedTransactions(pageCondition.getFrom(),pageCondition.getSize());
            if(transactionDtos == null){
                QueryUnconfirmedTransactionsResponse response = new QueryUnconfirmedTransactionsResponse();
                return Response.success(response);
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
            String message = "'query unconfirmed transactions' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query block by block height
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockByBlockHeightResponse> queryBlockByBlockHeight(@RequestBody QueryBlockByBlockHeightRequest request){
        try {
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(request.getBlockHeight());
            QueryBlockByBlockHeightResponse response = new QueryBlockByBlockHeightResponse();
            response.setBlock(blockVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query block by block height' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query block by block hash
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryBlockByBlockHashResponse> queryBlockByBlockHash(@RequestBody QueryBlockByBlockHashRequest request){
        try {
            Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHash(request.getBlockHash());
            if(block == null){
                QueryBlockByBlockHashResponse response = new QueryBlockByBlockHashResponse();
                return Response.success(response);
            }
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(block.getHeight());
            QueryBlockByBlockHashResponse response = new QueryBlockByBlockHashResponse();
            response.setBlock(blockVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query block by block hash' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query latest 10 blocks
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_LATEST_10_BLOCKS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryLatest10BlocksResponse> queryLatest10Blocks(@RequestBody QueryLatest10BlocksRequest request){
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

            QueryLatest10BlocksResponse response = new QueryLatest10BlocksResponse();
            response.setBlocks(blockVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query latest 10 blocks' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
}