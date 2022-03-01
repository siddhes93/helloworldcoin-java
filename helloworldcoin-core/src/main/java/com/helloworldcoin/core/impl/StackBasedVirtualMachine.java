package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.VirtualMachine;
import com.helloworldcoin.core.model.script.BooleanCode;
import com.helloworldcoin.core.model.script.OperationCode;
import com.helloworldcoin.core.model.script.Script;
import com.helloworldcoin.core.model.script.Result;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.tool.TransactionTool;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.StringUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class StackBasedVirtualMachine extends VirtualMachine {

    @Override
    public Result execute(Transaction transactionEnvironment, Script script) throws RuntimeException {
        Result stack = new Result();

        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = ByteUtil.hexStringToBytes(operationCode);
            if(ByteUtil.equals(OperationCode.OP_DUP.getCode(),bytesOperationCode)){
                if(stack.size()<1){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                stack.push(stack.peek());
            }else if(ByteUtil.equals(OperationCode.OP_HASH160.getCode(),bytesOperationCode)){
                if(stack.size()<1){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                String publicKey = stack.pop();
                String publicKeyHash = AccountUtil.publicKeyHashFromPublicKey(publicKey);
                stack.push(publicKeyHash);
            }else if(ByteUtil.equals(OperationCode.OP_EQUALVERIFY.getCode(),bytesOperationCode)){
                if(stack.size()<2){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                if(!StringUtil.equals(stack.pop(),stack.pop())){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
            }else if(ByteUtil.equals(OperationCode.OP_CHECKSIG.getCode(),bytesOperationCode)){
                if(stack.size()<2){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                String publicKey = stack.pop();
                String signature = stack.pop();
                byte[] bytesSignature = ByteUtil.hexStringToBytes(signature);
                boolean verifySignatureSuccess = TransactionTool.verifySignature(transactionEnvironment,publicKey,bytesSignature);
                if(!verifySignatureSuccess){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                stack.push(ByteUtil.bytesToHexString(BooleanCode.TRUE.getCode()));
            }else if(ByteUtil.equals(OperationCode.OP_PUSHDATA.getCode(),bytesOperationCode)){
                if(script.size()<i+2){
                    throw new RuntimeException("Virtual Machine Execute Error.");
                }
                i++;
                stack.push(script.get(i));
            }else {
                throw new RuntimeException("Virtual Machine Execute Error.");
            }
        }
        return stack;
    }
}
