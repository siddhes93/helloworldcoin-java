package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.script.OperationCode;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.netcore.dto.InputScriptDto;
import com.helloworldcoin.netcore.dto.OutputScriptDto;
import com.helloworldcoin.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;


public class ScriptToolTest {

    @Test
    public void bytesScriptTest()
    {
        InputScriptDto inputScriptDto = new InputScriptDto();
        inputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        inputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");

        InputScriptDto resumeInputScriptDto =  ScriptDtoTool.bytes2InputScript(ScriptDtoTool.inputScript2Bytes(inputScriptDto));
        Assert.assertEquals(JsonUtil.toString(inputScriptDto),JsonUtil.toString(resumeInputScriptDto));



        OutputScriptDto outputScriptDto = new OutputScriptDto();
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        outputScriptDto.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        outputScriptDto.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));

        OutputScriptDto resumeOutputScriptDto =  ScriptDtoTool.bytes2OutputScript(ScriptDtoTool.outputScript2Bytes(outputScriptDto));
        Assert.assertEquals(JsonUtil.toString(outputScriptDto),JsonUtil.toString(resumeOutputScriptDto));
    }
}
