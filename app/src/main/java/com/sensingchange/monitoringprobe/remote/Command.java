package com.sensingchange.monitoringprobe.remote;

public interface Command<T> {
    void execute(T data);
}