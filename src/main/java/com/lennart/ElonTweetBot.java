package com.lennart;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;

import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 01/05/2021.
 */
public class ElonTweetBot {

    private TweetMachine tweetMachine;
    private BinanceMachine binanceMachine;
    String latestDikkeNiffoTweetWithKeyword;

    public ElonTweetBot(TweetMachine tweetMachine, BinanceMachine binanceMachine) {
        this.tweetMachine = tweetMachine;
        this.binanceMachine = binanceMachine;
    }

    public static void main(String[] args) throws Exception {
        ElonTweetBot elonTweetBot = new ElonTweetBot(new TweetMachine(), new BinanceMachine());

        while(true) {
            elonTweetBot.continuousTestDikkeNiffoLogic();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private void continuousTestDikkeNiffoLogic() throws Exception {
        String mostRecentDikkeNiffoTweet = tweetMachine.getMostRecentDikkeNiffoTweet();

        if(mostRecentDikkeNiffoTweet.contains("eije") && !mostRecentDikkeNiffoTweet.equals(latestDikkeNiffoTweetWithKeyword)) {
            latestDikkeNiffoTweetWithKeyword = mostRecentDikkeNiffoTweet;
            System.out.println("ELONDOGE -> most recent tweet contains 'eije', placing Binance order");
            binanceMachine.placeDogeLimitBuyOrder();
        }
    }

    private void continuousElonLogic() throws Exception {
        String mostRecentElonTweet = tweetMachine.getMostRecentElonTweet();

        if(elonTweetContainsKeyword(mostRecentElonTweet)) {
            NewOrderResponse marketOrderResponse = binanceMachine.placeDogeMarketBuyOrder();

            while(!marketOrderResponse.getStatus().equals(OrderStatus.FILLED)) {
                System.out.println("Waiting for fill of market order");
            }

            double priceOfMarketOrder = Double.valueOf(marketOrderResponse.getPrice());
            double priceToSell = priceOfMarketOrder * 1.1;

            binanceMachine.placeDogeLimitSellOrder(String.valueOf(priceToSell));
        }
    }

    private boolean elonTweetContainsKeyword(String latestElonTweet) {
        String elonTweetToLowerCase = latestElonTweet.toLowerCase();

        if(elonTweetToLowerCase.contains("major")) {
            return true;
        }

        return false;
    }


}
