package com.hubwiz.demo;

import com.hubwiz.demo.contracts.Voting;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hofer.bhf
 * created on 2020/10/21 2:40 下午
 */
public class ContractHelper {

    private Web3j web3j;
    private List<String> accounts;

    public ContractHelper() throws Exception {
        web3j = Web3j.build(new HttpService("http://localhost:8545"));
        accounts = web3j.ethAccounts().send().getAccounts();
    }

    private byte[] s2b32(String str) {
        byte[] a = new byte[32];
        System.arraycopy(str.getBytes(), 0, a, 32 - str.length(), str.length());
        return a;
    }

    public Voting deployContract(int accountIndex) throws Exception {
        String account = accounts.get(accountIndex);
        ClientTransactionManager ctm = new ClientTransactionManager(web3j, account);
        BigInteger gasPrice = Contract.GAS_PRICE;
        BigInteger gasLimit = Contract.GAS_LIMIT;
        List<byte[]> candidates = new ArrayList<byte[]>();
        candidates.add(s2b32("Tommy"));
        candidates.add(s2b32("Jerry"));
        candidates.add(s2b32("Micky"));
        Voting voting = Voting.deploy(web3j, ctm, gasPrice, gasLimit, candidates).send();
        System.out.println("address: " + voting.getContractAddress());
        System.out.println("receipt: " + voting.getTransactionReceipt());
        return voting;
    }

    public Voting loadContract(String address, int accountIndex) throws Exception {
        String account = accounts.get(accountIndex);
        ClientTransactionManager ctm = new ClientTransactionManager(web3j, account);
        BigInteger gasPrice = Contract.GAS_PRICE;
        BigInteger gasLimit = Contract.GAS_LIMIT;
        Voting voting = Voting.load(address, web3j, ctm, gasPrice, gasLimit);
        System.out.println("contract loaded");
        return voting;
    }

    public void callContractMethod(Voting voting, String candidate) throws Exception {
        TransactionReceipt receipt = voting.voteFor(s2b32(candidate)).send();
        System.out.println("receipt => " + receipt);
        BigInteger votes = voting.getVotesFor(s2b32(candidate)).send();
        System.out.println("votes for " + candidate + ": " + votes);
    }

    public static void main(String[] args) throws Exception {
        ContractHelper ch = new ContractHelper();
        //部署合约
        Voting c1 = ch.deployContract(1);
        //载入合约
        Voting c2 = ch.loadContract(c1.getContractAddress(), 3);
        //调用合约方法
        ch.callContractMethod(c1, "Tommy");
        ch.callContractMethod(c1, "Jerry");
        ch.callContractMethod(c2, "Jerry");
        ch.callContractMethod(c1, "Micky");
    }
}
