package org.bitkernel.client;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class Emulator {
    private static final int USER_NUMBER = 30 * 10000;
    private static final int ACCESS_PER_USER = 3;
    private static final int TOTAL_ACCESS_NUM = USER_NUMBER * ACCESS_PER_USER;
    private static final long MIN_INTERVAL_IN_MINUTES = 1;
    private static final int EMULATE_TIME_IN_MINUTES = 5;

    private static final Random random = new Random();
    private static final String ip = "localhost";
    private static final int port = 8080;

    private int perAccessIntervalInMills;
    private final List<User> users = new ArrayList<>();
    private final LongAdder completedNum = new LongAdder();
    private final LongAdder accessNum = new LongAdder();

    public static void main(String[] args) {
        Emulator emulator = new Emulator();
        emulator.run();
    }

    public void run() {
        logger.info("Start client emulator");
        for (int i = 0; i < USER_NUMBER; i++) {
            User user = new User(i);
            users.add(user);
        }
        logger.info("Create {} users success", USER_NUMBER);
        logger.info("Access number per user: {}, total access number: {}",
                ACCESS_PER_USER, TOTAL_ACCESS_NUM);
        logger.info("Minimal interval: {} min, for each user to access server", MIN_INTERVAL_IN_MINUTES);

        int threadNum = Runtime.getRuntime().availableProcessors() + 1;
        int perThreadAccessNum = (int) (TOTAL_ACCESS_NUM * 1.0 / threadNum);
        logger.info("Thread number: {}, per thread access number: {}",
                threadNum, perThreadAccessNum);

        long emulateSeconds = TimeUnit.MINUTES.toSeconds(EMULATE_TIME_IN_MINUTES);
        perAccessIntervalInMills = (int) (1000 / (perThreadAccessNum * 1.0 / emulateSeconds));
        logger.info("Emulate time: {} minutes, average access time interval in thread: {} ms",
                EMULATE_TIME_IN_MINUTES, perAccessIntervalInMills);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new Executor());
        }
    }

    class Executor implements Runnable {
        @Override
        public void run() {
            while (completedNum.intValue() < USER_NUMBER) {
                int idx = random.nextInt(USER_NUMBER);
                User u = users.get(idx);
                synchronized (u) {
                    if (!canAccess(u)) {
                        continue;
                    }
                    sendRequest(u);
                    increase(u);
                }
                int sleepNum = random.nextInt(perAccessIntervalInMills * 2) + 1;
                try {
                    Thread.sleep(sleepNum);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void increase(@NotNull User u) {
        u.incNumVisits();
        accessNum.increment();
        if (u.getNumVisits() == ACCESS_PER_USER) {
            completedNum.increment();
            logger.debug("Id({}) access complete, total {} complete", u.getId(), completedNum.intValue());
        }
        if (accessNum.intValue() % 10000 == 0) {
            logger.info("access {}, complete {}", accessNum.intValue(), completedNum.intValue());
        }
    }

    private boolean canAccess(@NotNull User u) {
        return u.getNumVisits() < ACCESS_PER_USER &&
                System.currentTimeMillis() - u.getLastVisit() >= TimeUnit.MINUTES.toMillis(MIN_INTERVAL_IN_MINUTES);
    }

    private void sendRequest(@NotNull User user) {
        try {
            String urlFormat = "http://%s:%d/account/cache/id2/%d";
            String url = String.format(urlFormat, ip, port, user.getId());
            String response = HttpUtils.sendGetRequest(url);
            logger.debug("Response for " + url + ": " + response);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

class User {
    @Getter
    private final int id;
    @Getter
    private int numVisits;
    @Getter
    private long lastVisit;

    public User(int id) {
        this.id = id;
        numVisits = 0;
    }

    public void incNumVisits() {
        lastVisit = System.currentTimeMillis();
        numVisits++;
    }
}
