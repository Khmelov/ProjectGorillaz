package com.javarush.lesson08;

import com.javarush.lesson07.Cnn;
import lombok.SneakyThrows;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CnnPool {

    public static final int SIZE = 10;

    private static final List<Connection> realConnection = new ArrayList<>();
    private static final BlockingQueue<Connection> proxies = new LinkedBlockingQueue<>(SIZE);

    @SneakyThrows
    private static void init() {
        for (int i = 0; i < SIZE; i++) {
            Connection connection = Cnn.get();
            realConnection.add(connection);
            proxies.add((Connection) getProxyInstance(connection));
        }
    }

    private static Object getProxyInstance(Connection connection) {
        return Proxy.newProxyInstance(
                CnnPool.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        return proxies.add((Connection) proxy);
                    } else {
                        return method.invoke(connection, args);
                    }
                }
        );
    }

    @SneakyThrows
    public static Connection get() {
        if (realConnection.isEmpty()) {
            init();
        }
        return proxies.take();
    }

    public static void close() throws SQLException {
        for (Connection con : realConnection) {
            con.close();
        }
    }


}
