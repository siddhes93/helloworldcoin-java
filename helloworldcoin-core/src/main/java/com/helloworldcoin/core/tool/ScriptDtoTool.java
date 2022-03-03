package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.script.OperationCode;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.netcore.dto.InputScriptDto;
import com.helloworldcoin.netcore.dto.OutputScriptDto;
import com.helloworldcoin.netcore.dto.ScriptDto;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class ScriptDtoTool {

    //region Serialization and Deserialization
    public static byte[] inputScript2Bytes(InputScriptDto inputScript) {
        return script2Bytes(inputScript);
    }
    public static byte[] outputScript2Bytes(OutputScriptDto outputScript) {
        return script2Bytes(outputScript);
    }
    public static byte[] script2Bytes(ScriptDto script) {
        byte[] bytesScript = new byte[0];
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = ByteUtil.hexStringToBytes(operationCode);
            if(ByteUtil.equals(OperationCode.OP_DUP.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_HASH160.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_CHECKSIG.getCode(),bytesOperationCode)){
                bytesScript = ByteUtil.concatenate(bytesScript, ByteUtil.concatenateLength(bytesOperationCode));
            }else if(ByteUtil.equals(OperationCode.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                byte[] bytesOperationData = ByteUtil.hexStringToBytes(operationData);
                bytesScript = ByteUtil.concatenate3(bytesScript, ByteUtil.concatenateLength(bytesOperationCode), ByteUtil.concatenateLength(bytesOperationData));
            }else {
                throw new RuntimeException("Unrecognized OperationCode.");
            }
        }
        return bytesScript;
    }
    public static InputScriptDto bytes2InputScript(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        InputScriptDto inputScriptDto = new InputScriptDto();
        List<String> script = bytes2Script(bytesScript);
        inputScriptDto.addAll(script);
        return inputScriptDto;
    }
    public static OutputScriptDto bytes2OutputScript(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        OutputScriptDto outputScriptDto = new OutputScriptDto();
        List<String> script = bytes2Script(bytesScript);
        outputScriptDto.addAll(script);
        return outputScriptDto;
    }
    private static List<String> bytes2Script(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        int start = 0;
        List<String> script = new ArrayList<>();
        while (start<bytesScript.length){
            long bytesOperationCodeLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesScript,start,start + ByteUtil.BYTE8_BYTE_COUNT));
            start += ByteUtil.BYTE8_BYTE_COUNT;
            byte[] bytesOperationCode = ByteUtil.copy(bytesScript,start, start+(int) bytesOperationCodeLength);
            start += bytesOperationCodeLength;
            if(ByteUtil.equals(OperationCode.OP_DUP.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_HASH160.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    ByteUtil.equals(OperationCode.OP_CHECKSIG.getCode(),bytesOperationCode)){
                String stringOperationCode = ByteUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);
            }else if(ByteUtil.equals(OperationCode.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String stringOperationCode = ByteUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);

                long bytesOperationDataLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesScript,start,start + ByteUtil.BYTE8_BYTE_COUNT));
                start += ByteUtil.BYTE8_BYTE_COUNT;
                byte[] bytesOperationData = ByteUtil.copy(bytesScript,start, start+(int) bytesOperationDataLength);
                start += bytesOperationDataLength;
                String stringOperationData = ByteUtil.bytesToHexString(bytesOperationData);
                script.add(stringOperationData);
            }else {
                throw new RuntimeException("Unrecognized OperationCode.");
            }
        }
        return script;
    }
    //endregion

    public static InputScriptDto createPayToPublicKeyHashInputScript(String sign, String publicKey) {
        InputScriptDto script = new InputScriptDto();
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(sign);
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        script.add(publicKey);
        return script;
    }

    public static OutputScriptDto createPayToPublicKeyHashOutputScript(String address) {
        OutputScriptDto script = new OutputScriptDto();
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()));
        String publicKeyHash = AccountUtil.publicKeyHashFromAddress(address);
        script.add(publicKeyHash);
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()));
        script.add(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()));
        return script;
    }

    public static boolean isPayToPublicKeyHashInputScript(InputScriptDto inputScriptDto) {
        return  (inputScriptDto.size() == 4) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()),inputScriptDto.get(0))) &&
                (136 <= inputScriptDto.get(1).length() && 144 >= inputScriptDto.get(1).length()) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()),inputScriptDto.get(2))) &&
                (66 == inputScriptDto.get(3).length());
    }

    public static boolean isPayToPublicKeyHashOutputScript(OutputScriptDto outputScriptDto) {
        return  (outputScriptDto.size() == 6) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_DUP.getCode()),outputScriptDto.get(0))) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_HASH160.getCode()),outputScriptDto.get(1))) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_PUSHDATA.getCode()),outputScriptDto.get(2))) &&
                (40 == outputScriptDto.get(3).length()) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_EQUALVERIFY.getCode()),outputScriptDto.get(4))) &&
                (StringUtil.equals(ByteUtil.bytesToHexString(OperationCode.OP_CHECKSIG.getCode()),outputScriptDto.get(5)));
    }

    public static String getPublicKeyHashFromPayToPublicKeyHashOutputScript(OutputScriptDto outputScript) {
        return outputScript.get(3);
    }
}
