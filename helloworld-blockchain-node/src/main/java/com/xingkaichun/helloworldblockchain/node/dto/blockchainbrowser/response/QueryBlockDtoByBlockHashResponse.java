package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import lombok.Data;

@Data
public class QueryBlockDtoByBlockHashResponse {

    private Block block ;
}
