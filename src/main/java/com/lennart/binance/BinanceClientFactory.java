package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

/**
 * Created by LennartMac on 15/05/2021.
 */
public class BinanceClientFactory {

    public static BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "jeWCbhJx1Smghax75XupLI51BW1IRD4oJm7PeCBrQ7xz89hUQ4UjqVcq8EyOEBD6",
                "yYGe5sFsHqZDhhg79LW3OhjOOZrhxWELO4vMLMfbtNYNU6XcyijQaCARzIUROuK7");
        return factory.newRestClient();
    }
}
