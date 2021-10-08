package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCode;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactionOmitTrueTest()
    {
        TransactionDto transactionDto = new TransactionDto();

        TransactionDto transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,true),true);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        List<TransactionInputDto> transactionInputDtos = new ArrayList<>();
        TransactionInputDto transactionInputDto = new TransactionInputDto();
        transactionInputDto.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto.setTransactionOutputIndex(0);
        transactionInputDtos.add(transactionInputDto);
        transactionDto.setInputs(transactionInputDtos);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,true),true);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        List<TransactionOutputDto> transactionOutputDtos = new ArrayList<>();
        TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
        OutputScriptDto outputScriptDto = new OutputScriptDto();
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        transactionOutputDto.setOutputScript(outputScriptDto);
        transactionOutputDto.setValue(10);
        transactionOutputDtos.add(transactionOutputDto);
        transactionDto.setOutputs(transactionOutputDtos);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,true),true);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        TransactionInputDto transactionInputDto2 = new TransactionInputDto();
        transactionInputDto2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto2.setTransactionOutputIndex(0);
        transactionInputDtos.add(transactionInputDto2);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,true),true);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        TransactionOutputDto transactionOutputDto2 = new TransactionOutputDto();
        OutputScriptDto outputScriptDto2 = new OutputScriptDto();
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        transactionOutputDto2.setOutputScript(outputScriptDto2);
        transactionOutputDto2.setValue(20);
        transactionOutputDtos.add(transactionOutputDto2);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,true),true);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));
    }

    @Test
    public void bytesTransactionOmitFalseTest()
    {
        TransactionDto transactionDto = new TransactionDto();

        TransactionDto transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,false),false);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        List<TransactionInputDto> transactionInputDtos = new ArrayList<>();
        TransactionInputDto transactionInputDto = new TransactionInputDto();
        transactionInputDto.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto.setTransactionOutputIndex(0);
        InputScriptDto inputScriptDto = new InputScriptDto();
        inputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        inputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDto.setInputScript(inputScriptDto);
        transactionInputDtos.add(transactionInputDto);
        transactionDto.setInputs(transactionInputDtos);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,false),false);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        List<TransactionOutputDto> transactionOutputDtos = new ArrayList<>();
        TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
        OutputScriptDto outputScriptDto = new OutputScriptDto();
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        transactionOutputDto.setOutputScript(outputScriptDto);
        transactionOutputDto.setValue(10);
        transactionOutputDtos.add(transactionOutputDto);
        transactionDto.setOutputs(transactionOutputDtos);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,false),false);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        TransactionInputDto transactionInputDto2 = new TransactionInputDto();
        transactionInputDto2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto2.setTransactionOutputIndex(0);
        InputScriptDto inputScriptDto2 = new InputScriptDto();
        inputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        inputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDto2.setInputScript(inputScriptDto2);
        transactionInputDtos.add(transactionInputDto2);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,false),false);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));

        TransactionOutputDto transactionOutputDto2 = new TransactionOutputDto();
        OutputScriptDto outputScriptDto2 = new OutputScriptDto();
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto2.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        transactionOutputDto2.setOutputScript(outputScriptDto2);
        transactionOutputDto2.setValue(20);
        transactionOutputDtos.add(transactionOutputDto2);

        transactionDto2 = TransactionDtoTool.transactionDto(TransactionDtoTool.bytesTransaction(transactionDto,false),false);
        Assert.assertEquals(JsonUtil.toString(transactionDto),JsonUtil.toString(transactionDto2));
    }
}
