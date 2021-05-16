package com.binance.api.client.domain;

public interface Retriable<T> {

    long getTimestamp();

    T timestamp(long newTimestamp);

}