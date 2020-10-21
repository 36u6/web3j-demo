package com.hubwiz.demo;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

/**
 * @author hofer.bhf
 * created on 2020/10/20 2:18 下午
 */
public class Hello {
    public static void main(String[] args) {
        new Hello().run();
    }

    public void run() {
        try {
            Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
            Request<?, Web3ClientVersion> request = web3j.web3ClientVersion();
            Web3ClientVersion web3ClientVersion = request.send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("clientVersion=" + clientVersion);
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
