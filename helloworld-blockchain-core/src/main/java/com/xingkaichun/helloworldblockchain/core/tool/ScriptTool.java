package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCode;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.InputScriptDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.OutputScriptDto;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

/**
 * 脚本工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ScriptTool {

    //region 可视、可阅读的脚本，区块链浏览器使用
    public static String stringInputScript(InputScript inputScript) {
        return stringScript(inputScript);
    }
    public static String stringOutputScript(OutputScript outputScript) {
        return stringScript(outputScript);
    }
    public static String stringScript(Script script) {
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
                throw new RuntimeException("不能识别的指令");
            }
        }
        return stringScript;
    }
    //endregion

    /**
     * 构建完整脚本
     */
    public static Script createScript(InputScript inputScript, OutputScript outputScript) {
        Script script = new Script();
        script.addAll(inputScript);
        script.addAll(outputScript);
        return script;
    }

    /**
     * 创建P2PKH输入脚本
     */
    public static InputScript createPayToPublicKeyHashInputScript(String sign, String publicKey) {
        InputScript script = new InputScript();
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(sign);
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(publicKey);
        return script;
    }

    /**
     * 创建P2PKH输出脚本
     */
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

    /**
     * 是否是P2PKH输入脚本
     */
    public static boolean isPayToPublicKeyHashInputScript(InputScript inputScript) {
        InputScriptDto inputScriptDto = Model2DtoTool.inputScript2InputScriptDto(inputScript);
        return ScriptDtoTool.isPayToPublicKeyHashInputScript(inputScriptDto);
    }

    /**
     * 是否是P2PKH输出脚本
     */
    public static boolean isPayToPublicKeyHashOutputScript(OutputScript outputScript) {
        OutputScriptDto outputScriptDto = Model2DtoTool.outputScript2OutputScriptDto(outputScript);
        return ScriptDtoTool.isPayToPublicKeyHashOutputScript(outputScriptDto);
    }
}
