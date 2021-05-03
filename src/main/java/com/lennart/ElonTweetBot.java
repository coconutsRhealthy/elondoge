package com.lennart;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;
import twitter4j.Status;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 01/05/2021.
 */
public class ElonTweetBot {

    private TweetMachine tweetMachine;
    private BinanceMachine binanceMachine;
    private String latestElonTweetWithKeyword;
    private boolean aTradeHasBeenDone = false;

    public ElonTweetBot(TweetMachine tweetMachine, BinanceMachine binanceMachine) {
        this.tweetMachine = tweetMachine;
        this.binanceMachine = binanceMachine;
    }

    public static void main(String[] args) throws Exception {
        ElonTweetBot elonTweetBot = new ElonTweetBot(new TweetMachine(), new BinanceMachine());
        int counter = 0;

        while(true) {
            elonTweetBot.continuousElonLogic();
            TimeUnit.SECONDS.sleep(1);

            counter++;
            if(counter == 100) {
                System.out.println("ELONDOGE -> counter is 100");
                counter = 0;
            }
        }
    }

    private void continuousElonLogic() throws Exception {
        Status mostRecentElonTweetStatus = tweetMachine.getMostRecentElonTweetStatus();

        if(mostRecentElonTweetStatus != null) {
            String mostRecentElonTweet = mostRecentElonTweetStatus.getText();

            if(mostRecentElonTweet != null) {
                if(elonTweetContainsKeyword(mostRecentElonTweet) && !mostRecentElonTweet.equals(latestElonTweetWithKeyword)) {
                    if(!aTradeHasBeenDone || thereIsSufficientTimeBetweenLastKeywordTweetAndCurrentOne(mostRecentElonTweetStatus)) {
                        System.out.println("ELONDOGE -> new tweet with keyword identified. Tweet: " + mostRecentElonTweet);
                        System.out.println("ELONDOGE -> placing market buy order");

                        latestElonTweetWithKeyword = mostRecentElonTweet;
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
                        aTradeHasBeenDone = true;
                    }
                }
            } else {
                System.out.println("ELONDOGE -> mostRecentElonTweetText is null!");
            }
        } else {
            System.out.println("ELONDOGE -> mostRecentElonTweetStatus is null!");
        }
    }

    private boolean elonTweetContainsKeyword(String latestElonTweet) {
        String elonTweetToLowerCase = latestElonTweet.toLowerCase();

        if(elonTweetToLowerCase.contains("doge")) {
            return true;
        }

        return false;
    }

    private boolean thereIsSufficientTimeBetweenLastKeywordTweetAndCurrentOne(Status status) {
        Date currentDate = new Date();
        Date createdAtDate = status.getCreatedAt();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        String currDateString = fmt.format(currentDate);
        String tweetDateString = fmt.format(createdAtDate);

        return !currDateString.equals(tweetDateString);
    }

    private String getPriceToSell(double priceOfMarketOrder) {
        double priceToSell = priceOfMarketOrder * 1.1;
        DecimalFormat formatter = new DecimalFormat("0.00000000");
        String priceToSellAsString = formatter.format(priceToSell);
        return priceToSellAsString;
    }
}
