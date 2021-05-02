package com.lennart;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 29/08/2017.
 */
public class TweetMachine {

    private Twitter twittertje;

    public TweetMachine() {
        setTwittertje();
    }

    public String getMostRecentElonTweet() throws Exception {
        String consumerKey = "XB2wdvrDkQeyidS5KhNYFp5oP";
        String consumerSecret = "hR7Biwi6B4qNVwtjQlms7BlPu6RvborgYPnh5MPMRs0ApSEFKE";
        String accessToken = "1295662434229919744-9faVomqpSw6qI0bPocBGJvCMxOpGHZ";
        String accessSecret = "oDuujFjYP8vWIVH89AjZ9BFoeYlzw252VQjj1Rd4UmxPg";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();

        Paging paging = new Paging(1, 1);

        List<String> elonTweets = twitter.getUserTimeline("elonmusk", paging).stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());

        String lastElonTweet;

        if(elonTweets.isEmpty()) {
            lastElonTweet = "NA";
        } else {
            lastElonTweet = elonTweets.get(0);
        }

        return lastElonTweet;
    }

    public String getMostRecentDikkeNiffoTweet() throws Exception {
        String consumerKey = "XB2wdvrDkQeyidS5KhNYFp5oP";
        String consumerSecret = "hR7Biwi6B4qNVwtjQlms7BlPu6RvborgYPnh5MPMRs0ApSEFKE";
        String accessToken = "1295662434229919744-9faVomqpSw6qI0bPocBGJvCMxOpGHZ";
        String accessSecret = "oDuujFjYP8vWIVH89AjZ9BFoeYlzw252VQjj1Rd4UmxPg";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();
        Paging paging = new Paging(1, 1);

        List<String> elonTweets = twitter.getUserTimeline("dikkeniffo", paging).stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());

        String lastElonTweet;

        if(elonTweets.isEmpty()) {
            lastElonTweet = "NA";
        } else {
            lastElonTweet = elonTweets.get(0);
        }

        return lastElonTweet;
    }

    private void setTwittertje() {
        String consumerKey = "XB2wdvrDkQeyidS5KhNYFp5oP";
        String consumerSecret = "hR7Biwi6B4qNVwtjQlms7BlPu6RvborgYPnh5MPMRs0ApSEFKE";
        String accessToken = "1295662434229919744-9faVomqpSw6qI0bPocBGJvCMxOpGHZ";
        String accessSecret = "oDuujFjYP8vWIVH89AjZ9BFoeYlzw252VQjj1Rd4UmxPg";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        twittertje = factory.getInstance();
    }
}
