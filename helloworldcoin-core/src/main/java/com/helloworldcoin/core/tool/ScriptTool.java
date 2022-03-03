package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.script.InputScript;
import com.helloworldcoin.core.model.script.OperationCode;
import com.helloworldcoin.core.model.script.OutputScript;
import com.helloworldcoin.core.model.script.Script;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.netcore.dto.InputScriptDto;
import com.helloworldcoin.netcore.dto.OutputScriptDto;
import com.helloworldcoin.util.StringUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class ScriptTool {

    public static String inputScript2String(InputScript inputScript) {
        return script2String(inputScript);
    }
    public static String outputScript2String(OutputScript outputScript) {
        return script2String(outputScript);
    }
    public static String script2String(Script script) {
        String stringScript = "";
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = ByteUtil.hexStringToBytes(operationCode);
            if(ByteUtil.equals(OperationCode.OP_DUP.getCode(),bytesOperationCode)){
                stringScript = StringUtil.concatenate3(stringScript, OperationCode.OP_DUP.getName(),StringUtil.BLANKSPACE);
            }else if(ByteUtil.equals(OperationCode.OP_HASH160.getCode(),bytesOperationCode)){
                stringScript = StringUtil.concatenate3(stringScript, OperationCode.OP_HASH160.getName(),StringUtil.BLANKSPACE);
            }else if(ByteUtil.equals(OperationCode.OP_EQUALVERIFY.getCode(),bytesOperationCode)){
                stringScript = StringUtil.concatenate3(stringScript, OperationCode.OP_EQUALVERIFY.getName(),StringUtil.BLANKSPACE);
            }else if(ByteUtil.equals(OperationCode.OP_CHECKSIG.getCode(),bytesOperationCode)){
                stringScript = StringUtil.concatenate3(stringScript, OperationCode.OP_CHECKSIG.getName(),StringUtil.BLANKSPACE);
            }else if(ByteUtil.equals(OperationCode.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                stringScript = StringUtil.concatenate3(stringScript, OperationCode.OP_PUSHDATA.getName(),StringUtil.BLANKSPACE);
                stringScript = StringUtil.concatenate3(stringScript,operationData,StringUtil.BLANKSPACE);
            }else {
                throw new RuntimeException("Unrecognized OperationCode.");
            }
        }
        return stringScript;
    }




    public static Script createScript(InputScript inputScript, OutputScript outputScript) {
        Script script = new Script();
        script.addAll(inputScript);
        script.addAll(outputScript);
        return script;
    }
    public static InputScript createPayToPublicKeyHashInputScript(String sign, String publicKey) {
        InputScript script = new InputScript();
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(sign);
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(publicKey);
        return script;
    }
    public static OutputScript createPayToPublicKeyHashOutputScript(String address) {
        OutputScript script = new OutputScript();
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        String publicKeyHash = AccountUtil.publicKeyHashFromAddress(address);
        script.add(publicKeyHash);
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        return script;
    }




    public static boolean isPayToPublicKeyHashInputScript(InputScript inputScript) {
        InputScriptDto inputScriptDto = Model2DtoTool.inputScript2InputScriptDto(inputScript);
        return ScriptDtoTool.isPayToPublicKeyHashInputScript(inputScriptDto);
    }
    public static boolean isPayToPublicKeyHashOutputScript(OutputScript outputScript) {
        OutputScriptDto outputScriptDto = Model2DtoTool.outputScript2OutputScriptDto(outputScript);
        return ScriptDtoTool.isPayToPublicKeyHashOutputScript(outputScriptDto);
    }
}
