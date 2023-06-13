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
    private static final long MIN_INTERVAL_IN_MINUTES = 1;
    private static final Random random = new Random();
    private static final String ip = "localhost";
    private static final int port = 8080;
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

        int totalAccessNum = USER_NUMBER * ACCESS_PER_USER;
        int threadNum = Runtime.getRuntime().availableProcessors() + 1;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new Executor());
        }
        logger.info("Create {} threads, need to access {} times in total", threadNum, totalAccessNum);
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
                    try {
                        sendRequest(u);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
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
            }
        }

        private boolean canAccess(@NotNull User u) {
            return u.getNumVisits() < ACCESS_PER_USER &&
                    System.currentTimeMillis() - u.getLastVisit() >= TimeUnit.MINUTES.toMillis(MIN_INTERVAL_IN_MINUTES);
        }
    }

    private void sendRequest(@NotNull User user) throws IOException {
        String urlFormat = "http://%s:%d/account/cache/id/%d";
        String url = String.format(urlFormat, ip, port, user.getId());
        String response = HttpUtils.sendGetRequest(url);
        logger.info("Response for " + url + ": " + response);
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
