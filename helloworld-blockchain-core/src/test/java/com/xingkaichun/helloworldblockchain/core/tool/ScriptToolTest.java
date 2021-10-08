package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCode;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.InputScriptDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.OutputScriptDto;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;


public class ScriptToolTest {

    @Test
    public void bytesScriptTest()
    {
        InputScriptDto inputScriptDto = new InputScriptDto();
        inputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        inputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");

        InputScriptDto resumeInputScriptDto =  ScriptDtoTool.inputScriptDto(ScriptDtoTool.bytesInputScript(inputScriptDto));
        Assert.assertEquals(JsonUtil.toString(inputScriptDto),JsonUtil.toString(resumeInputScriptDto));



        OutputScriptDto outputScriptDto = new OutputScriptDto();
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));

        OutputScriptDto resumeOutputScriptDto =  ScriptDtoTool.outputScriptDto(ScriptDtoTool.bytesOutputScript(outputScriptDto));
        Assert.assertEquals(JsonUtil.toString(outputScriptDto),JsonUtil.toString(resumeOutputScriptDto));
    }
}
