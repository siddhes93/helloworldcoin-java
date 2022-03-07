package com.helloworldcoin.core.model.script;


/**
 * OperationCode enumeration
 * Please describe exactly how each opcode is processed.
 *
 * @author x.king xdotking@gmail.com
 */
public enum OperationCode {
    /**
     * Suppose the current stack element is (A B C D E) (stack top <--- stack bottom)
     * If there are less than 0 elements in the stack, an exception is thrown.
     * Put the next data F in the script on the stack.
     * After the operation, the stack element is (F A B C D E) (stack top <--- stack bottom)
     * The full name of the opcode is OPERATION_CODE_PUSH_DATA
     */
    OP_PUSHDATA(new byte[]{(byte)0x00}, "OP_PUSHDATA"),
    /**
     * Suppose the current stack element is (A B C D E) (stack top <--- stack bottom)
     * If there are less than 1 elements in the stack, an exception is thrown.
     * Copy (the top element A of the stack), (the copy result element is A2)
     * Put A2 on the stack.
     * After the operation, the stack element is (A2 A B C D E) (stack top <--- stack bottom)
     * The full name of the opcode is OPERATION_CODE_DUPLICATE
     */
    OP_DUP(new byte[]{(byte)0x01}, "OP_DUP"),
    /**
     * Suppose the current stack element is (A B C D E) (stack top <--- stack bottom)
     * If there are less than 1 elements in the stack, an exception is thrown.
     * remove the top element A from the stack
     * Use element A as the public key to find the address A2 (do HASH256 hash for A first, and then do HASH160 hash operation, get A2)
     * Put A2 on the stack.
     * After the operation, the stack element is (A2 B C D E) (stack top <--- stack bottom)
     * The full name of the opcode is OPERATION_CODE_HASH160
     */
    OP_HASH160(new byte[]{(byte)0x02}, "OP_HASH160"),
    /**
     * Suppose the current stack element is (A B C D E) (stack top <--- stack bottom)
     * If there are less than 2 elements in the stack, an exception is thrown.
     * delete the top element A 、B from the stack
     * Compare whether elements A and B are equal, throw an exception if they are not equal.
     * After the operation, the stack element is (C D E) (stack top <--- stack bottom)
     * The full name of the opcode is OPERATION_CODE_EQUAL_VERIFY
     */
    OP_EQUALVERIFY(new byte[]{(byte)0x03}, "OP_EQUALVERIFY"),
    /**
     * Suppose the current stack element is (A B C D E) (stack top <--- stack bottom)
     * If there are less than 2 elements in the stack, an exception is thrown.
     * delete the top element A 、B from the stack
     * Element A is the public key, and element B is the transaction signature. The public key
     * is used to verify whether the transaction signature is correct. If the verification fails,
     * throw an exception; if the verification succeeds, then put true on the stack.
     * After the operation, the stack element is (true C D E) (stack top <--- stack bottom)
     * The full name of the opcode is OPERATION_CODE_CHECK_SIGNATURE
     */
    OP_CHECKSIG(new byte[]{(byte)0x04},"OP_CHECKSIG");





    OperationCode(byte[] code, String name) {
        this.code = code;
        this.name = name;
    }

    private byte[] code;
    private String name;

    public byte[] getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
