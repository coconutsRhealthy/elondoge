package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

/**
 * Created by LennartMac on 15/05/2021.
 */
public class BinanceClientFactory {

    public static BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "zzz",
                "zzz");
        return factory.newRestClient();
    }
}
