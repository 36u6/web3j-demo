package com.hubwiz.demo;

import io.reactivex.disposables.Disposable;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.utils.Convert;
import org.web3j.utils.Async;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.BigInteger;


import com.hubwiz.demo.contracts.Voting;

class FilterHelper {
    private Web3j web3j;
    private List<String> accounts;

    public FilterHelper() throws Exception {
        web3j = Web3j.build(new HttpService("http://localhost:8545"),1000,Async.defaultExecutorService());
        accounts = web3j.ethAccounts().send().getAccounts();
    }

    public void demoTx() throws Exception {
        ClientTransactionManager ctm = new ClientTransactionManager(web3j,accounts.get(0));
        Transfer transfer = new Transfer(web3j,ctm);
        String to = accounts.get(1);
        BigDecimal value = BigDecimal.valueOf(1);
        TransactionReceipt receipt = transfer.sendFunds(to,value,Convert.Unit.WEI).send();
        System.out.println("receipt => " + receipt);
    }

    public void blockFilter() throws Exception {
        Disposable sub = web3j.blockFlowable(true)
                .subscribe(ethBlock -> {
                    System.out.println("new block =>");
                    EthBlock.Block block = ethBlock.getBlock();
                    System.out.println("hash: " + block.getHash());
                    System.out.println("number: " + block.getNumber());
                    System.out.println("miner: " + block.getMiner());
                    System.out.println("author: " + block.getAuthor());
                    System.out.println("timestamp: " + block.getTimestamp());
                    System.out.println("stateRoot: " + block.getStateRoot());
                });
    }

    public void txFilter() throws Exception {
        Disposable sub = web3j.transactionFlowable().subscribe(tx -> {
            System.out.println("new transaction =>");
            System.out.println("hash: " + tx.getHash());
            System.out.println("from: " + tx.getFrom());
            System.out.println("to: " + tx.getTo());
            System.out.println("value: " + tx.getValue());
            System.out.println("input: " + tx.getInput());
        });
    }

    public void pendingTxFilter() throws Exception {
        Disposable sub = web3j.pendingTransactionFlowable().subscribe(tx -> {
            System.out.println("new pending transaction =>");
            System.out.println("hash: " + tx.getHash());
            System.out.println("from: " + tx.getFrom());
            System.out.println("to: " + tx.getTo());
            System.out.println("value: " + tx.getValue());
            System.out.println("input: " + tx.getInput());
        });
    }

    public void replayFilter() throws Exception{
        Disposable sub = web3j.replayPastBlocksFlowable(DefaultBlockParameterName.EARLIEST,DefaultBlockParameterName.LATEST,false)
                .subscribe(ethBlock -> {
                    EthBlock.Block block = ethBlock.getBlock();
                    System.out.println("hash: " + block.getHash());
                    System.out.println("number: " + block.getNumber());
                });
    }

    private byte[] s2b32(String str){
        byte[] a = new byte[32];
        System.arraycopy(str.getBytes(),0,a,32-str.length(),str.length());
        return a;
    }

    public Voting deployContract(int accountIndex) throws Exception {
        String account = accounts.get(accountIndex);
        ClientTransactionManager ctm = new ClientTransactionManager(web3j,account);
        BigInteger gasPrice = Contract.GAS_PRICE;
        BigInteger gasLimit = Contract.GAS_LIMIT;
        List<byte[]> candidates = new ArrayList<byte[]>();
        candidates.add(s2b32("Tommy"));
        candidates.add(s2b32("Jerry"));
        candidates.add(s2b32("Micky"));
        Voting voting = Voting.deploy(web3j,ctm,gasPrice,gasLimit,candidates).send();
        System.out.println("address: " + voting.getContractAddress());
        System.out.println("receipt: " + voting.getTransactionReceipt());
        return voting;
    }

    public void eventFilter(Voting voting) throws Exception {
        EthFilter filter = new EthFilter();

//        voting.voteEventObservable(filter).subscribe(event -> {
//            System.out.println(event.voter + " : " + new String(event.candidate));
//        });
    }

    public static void main(String[] args) throws Exception {
        FilterHelper fh = new FilterHelper();
        //监听新块事件
        fh.blockFilter();
        //监听新合约事件
        fh.txFilter();
        //监听待定合约事件
        fh.pendingTxFilter();
        //用一个交易触发监听
        fh.demoTx();
        //监听合约事件
        Voting voting = fh.deployContract(0);
        fh.eventFilter(voting);
    }
}