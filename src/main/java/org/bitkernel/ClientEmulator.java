package org.bitkernel;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ClientEmulator {
    private static final int USER_NUMBER = 30 * 10000;
    private static final int ACCESS_PER_USER = 3;
    private static final long MIN_INTERVAL_IN_MINUTES = 5;
    private static final Random random = new Random();
    private static final String ip = "localhost";
    private static final int port = 8080;
    private final List<User> users = new ArrayList<>();
    private final AtomicInteger completedNum = new AtomicInteger();

    public static void main(String[] args) {
        ClientEmulator emulator = new ClientEmulator();
        emulator.run();
    }

    public void run() {
        for (int i = 0; i < USER_NUMBER; i++) {
            User user = new User(i);
            users.add(user);
        }

        int threadNum = Runtime.getRuntime().availableProcessors() + 1;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new Executor());
        }
    }

    class Executor implements Runnable {
        @Override
        public void run() {
            while (completedNum.get() < USER_NUMBER) {
                int idx = random.nextInt(USER_NUMBER);
                User u = users.get(idx);
                if (!canAccess(u)) {
                    continue;
                }
                sendRequest(u);
                u.incNumVisits();
                if (u.getNumVisits() == ACCESS_PER_USER) {
                    int completed = completedNum.getAndIncrement() + 1;
                    logger.info("Id({}) access complete, total {} complete", u.getId(), completed);
                }
            }
        }

        private boolean canAccess(@NotNull User u) {
            return u.getNumVisits() < ACCESS_PER_USER &&
                    System.currentTimeMillis() - u.getLastVisit() >= TimeUnit.MINUTES.toMillis(MIN_INTERVAL_IN_MINUTES);
        }
    }

    private void sendRequest(@NotNull User user) {
        String urlFormat = "http://%s:%d/account/cache/id/%d";
        String url = String.format(urlFormat, ip, port, user.getId());
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                logger.debug("Response for " + url + ": " + response);
            } else {
                logger.error("Error while accessing " + url + ", response code: " + responseCode);
            }
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
