package com.hubwiz.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

/**
 * @author hofer.bhf
 * created on 2020/10/20 2:40 下午
 */
public class AccountHelper {
    private Logger log = LoggerFactory.getLogger("AccountHelper");
    private String keystoreDir = "./keystore";

    private void logAccount(String[] tuple) {
        System.out.println("Private Key: " + tuple[0]);
        System.out.println("Public Key: " + tuple[1]);
        System.out.println("address: " + tuple[2]);
    }

    private String[] getAccountTuple(ECKeyPair keyPair) {
        return new String[]{
                keyPair.getPrivateKey().toString(16),
                keyPair.getPublicKey().toString(16),
                Keys.getAddress(keyPair)
        };
    }

    public String[] newAccount() throws Exception {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    public String[] importPrivateKey(String privateKey) throws Exception {
        BigInteger key = new BigInteger(privateKey, 16);
        ECKeyPair keyPair = ECKeyPair.create(key);
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    public String newWalletFile(String password) throws Exception {
        File dest = new File(keystoreDir);
        String walletFileName = WalletUtils.generateNewWalletFile(password, dest, true);
        System.out.println("Wallet file: " + walletFileName);
        return walletFileName;
    }

    public String[] loadWalletFile(String password, String walletFileName) throws Exception {
        String src = keystoreDir + "/" + walletFileName;
        Credentials credentials = WalletUtils.loadCredentials(password, src);
        ECKeyPair keyPair = credentials.getEcKeyPair();
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    private void logBip39Wallet(Bip39Wallet wallet) {
        System.out.println("Bip39 wallet file: " + wallet.getFilename());
        System.out.println("Bip39 wallet mnemonic: " + wallet.getMnemonic());
    }

    public String[] newBip39Wallet(String password) throws Exception {
        File dest = new File(keystoreDir);
        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(password, dest);
        logBip39Wallet(wallet);
        return new String[]{wallet.getFilename(), wallet.getMnemonic()};
    }

    private void logAccounts(List<String> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("account " + i + ": " + accounts.get(i));
        }
    }

    public String[] getNodeAccounts() throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        logAccounts(accounts);
        return accounts.toArray(new String[accounts.size()]);
    }


    public static void main(String[] args) throws Exception {
        AccountHelper AH = new AccountHelper();
        //创建密钥对
        String[] tuple = AH.newAccount();
        //导入私钥
        String[] tuple2 = AH.importPrivateKey(tuple[0]);
        //创建钱包文件
        String walletFileName = AH.newWalletFile("123");
        //载入钱包文件，创建账户凭证
        AH.loadWalletFile("123", walletFileName);
        //查看节点账户
        AH.getNodeAccounts();
    }
}
