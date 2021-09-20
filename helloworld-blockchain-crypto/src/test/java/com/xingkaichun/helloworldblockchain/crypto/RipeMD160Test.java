package com.xingkaichun.helloworldblockchain.crypto;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertArrayEquals;


public class RipeMD160Test {

    private static final String[] messages = {
            "",
            "a",
            "abc",
            "message digest",
            "abcdefghijklmnopqrstuvwxyz",
            "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890"
    };

    private static final String[] digests = {
            "9c1185a5c5e9fc54612808977ee8f548b2258d31",
            "0bdc9d2d256b3ee9daae347be6f4dc835a467ffe",
            "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc",
            "5d0689ef49d2fae572b881b123a85ffa21595f36",
            "f71c27109c692c1b56bbdceb5b9d2865b3708dbc",
            "12a053384a9c0c88e405a06c27dcf49ada62eb2b",
            "b0e20b6e3116640286ed3a87a5713079b21f5189",
            "9b752e45573d4b39f4dbd3323cab82bf63326bfb"
    };

    @Test
    public void digestTest()
    {
        for(int i=0;i<messages.length;i++){
            byte[] messageDigest = Ripemd160Util.digest(toByteArray(messages[i]));
            assertArrayEquals(Hex.decode(digests[i]), messageDigest);
        }
    }
    @Test
    public void md5Test() throws NoSuchAlgorithmException {
        System.out.println(ByteUtil.bytesToHexString(Sha256Util.digest(new byte[]{0x00})));
        System.out.println(ByteUtil.bytesToHexString(Sha256Util.digest(new byte[]{0x01})));
        System.out.println(ByteUtil.bytesToHexString(Sha256Util.digest(new byte[]{0x02})));
        System.out.println(ByteUtil.bytesToHexString(Ripemd160Util.digest(new byte[]{0x00})));
        System.out.println(ByteUtil.bytesToHexString(Ripemd160Util.digest(new byte[]{0x01})));
        System.out.println(ByteUtil.bytesToHexString(Ripemd160Util.digest(new byte[]{0x02})));
        System.out.println(Base58Util.encode(ByteUtil.hexStringToBytes("18EFBA81F02B8BFF148118BB58F38820642CCCC159E32254AEC4606CE6C71CC4FC124A1C7B61122BABE0576669F515A77568EED494F1E60B65DF3284269A153C36EC8D5911D77998FA530C689531")));
        Account account = AccountUtil.accountFromPrivateKey("D026AD829A2EA66E7BE1FC004E0EB703543CFCD31E8047BEC7DA68E326194AAD");
        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getPublicKeyHash());
        System.out.println(account.getAddress());


        System.out.println(AccountUtil.signature("B4D475E477B08F2BE5C524264CB9EE21830177788DF011C0F8C79512AE49B540",new byte[]{0x00}));

    }


    private byte[] toByteArray(String input)
    {
        byte[] bytes = new byte[input.length()];
        for (int i = 0; i != bytes.length; i++)
        {
            bytes[i] = (byte)input.charAt(i);
        }
        return bytes;
    }
}
