package com.lennart;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 01/05/2021.
 */
public class ElonTweetBot {

    private TweetMachine tweetMachine;
    private BinanceMachine binanceMachine;
    private String latestElonTweetWithKeyword;
    private long latestTimeOfElonKeywordTweet = 0;

    public ElonTweetBot(TweetMachine tweetMachine, BinanceMachine binanceMachine) {
        this.tweetMachine = tweetMachine;
        this.binanceMachine = binanceMachine;
    }

    public static void main(String[] args) throws Exception {
        ElonTweetBot elonTweetBot = new ElonTweetBot(new TweetMachine(), new BinanceMachine());

        while(true) {
            elonTweetBot.continuousElonLogic();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private void continuousElonLogic() throws Exception {
        String mostRecentElonTweet = tweetMachine.getMostRecentElonTweet();

        if(elonTweetContainsKeyword(mostRecentElonTweet) && !mostRecentElonTweet.equals(latestElonTweetWithKeyword)) {
            if(thereIsSufficientTimeBetweenLastKeywordTweetAndCurrentOne()) {
                System.out.println("ELONDOGE -> new tweet with keyword identified. Tweet: " + mostRecentElonTweet);
                System.out.println("ELONDOGE -> placing market buy order");

                latestElonTweetWithKeyword = mostRecentElonTweet;
                latestTimeOfElonKeywordTweet = new Date().getTime();
                NewOrderResponse marketOrderResponse = binanceMachine.placeDogeMarketBuyOrder();

                while(!marketOrderResponse.getStatus().equals(OrderStatus.FILLED)) {
                    System.out.println("ELONDOGE -> waiting for fill of market order");
                }

                double priceOfMarketOrder = binanceMachine.getPriceOfLastTrade();
                System.out.println("ELONDOGE -> price of market buy order: " + priceOfMarketOrder);
                String priceToSell = getPriceToSell(priceOfMarketOrder);
                System.out.println("ELONDOGE -> price for limit sell order: " + priceToSell);
                System.out.println("ELONDOGE -> placing limit sell order");
                binanceMachine.placeDogeLimitSellOrder(String.valueOf(priceToSell));
            }
        }
    }

    private boolean elonTweetContainsKeyword(String latestElonTweet) {
        String elonTweetToLowerCase = latestElonTweet.toLowerCase();

        if(elonTweetToLowerCase.contains("eije")) {
            return true;
        }

        return false;
    }

    private boolean thereIsSufficientTimeBetweenLastKeywordTweetAndCurrentOne() {
        //todo: check server time
        //todo: nu gaat ie nog order versturen als tweede doge tweet was 5 min na eerste, en dan zijn er 2 uur verstreken
        //todo: je moet kijken naar created at property van het tweet item
        long currentTime = new Date().getTime();

        if(currentTime - latestTimeOfElonKeywordTweet > 7_200_000) {
            return true;
        }

        return false;
    }

    private String getPriceToSell(double priceOfMarketOrder) {
        double priceToSell = priceOfMarketOrder * 1.1;
        DecimalFormat formatter = new DecimalFormat("0.00000000");
        String priceToSellAsString = formatter.format(priceToSell);
        return priceToSellAsString;
    }
}
