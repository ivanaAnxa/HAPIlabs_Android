package com.anxa.hapilabs.common.connection;


import java.util.ArrayList;


public class ConnectionManager {


    public static final int MAX_HTTP_CONNECTIONS = 5;

    private ArrayList<Runnable> active = new ArrayList<Runnable>();
    private ArrayList<Runnable> queue = new ArrayList<Runnable>();

    private static ConnectionManager instance;

    public static ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public void push(Runnable runnable) {
        queue.add(runnable);
        if (active.size() < MAX_HTTP_CONNECTIONS) {
            startNext();
        }
    }

    private void startNext() {
        if (!queue.isEmpty()) {
            Runnable next = queue.get(0);
            queue.remove(0);
            active.add(next);
            Thread thread = new Thread(next);
            thread.start();
        }
    }

    public void didComplete(Runnable runnable) {
        active.remove(runnable);
        startNext();
    }

    public void clearQueue(){
        queue.clear();
    }

}

	