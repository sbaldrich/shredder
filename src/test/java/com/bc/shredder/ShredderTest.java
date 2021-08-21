package com.bc.shredder;

import com.bc.shredder.store.DataManager;
import com.bc.shredder.store.ShredderConfig;
import com.bc.shredder.user.User;
import com.bc.shredder.user.UserStore;
import com.bc.shredder.user.UserStoreImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

class ShredderTest {
    private static final Logger log = LoggerFactory.getLogger(ShredderTest.class);

    @Test
    void shredderTest() throws IOException {
        ShredderConfig config = ShredderConfig.init("/datasource.yml");
        Assertions.assertNotNull(config);
        DataManager dataManager = DataManager.get(config);
        UserStore store = new UserStoreImpl(dataManager);
        Map<Long, String> savedUsers = new ConcurrentHashMap<>();

        int threads = 10;
        int usersPerThread = 100;
        Runnable runnable = () -> {
            for(int i = 0; i < usersPerThread; i++){
                User user = store.save(new User("fake-user_" + ThreadLocalRandom.current().nextInt(2000)));
                log.info("Saved user {}", user);
                savedUsers.put(user.getUserId(), user.getHandle());
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for(int i = 0; i < threads; i++){
            executor.submit(runnable);
        }

        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(2, TimeUnit.SECONDS);
            Assertions.assertTrue(finished);
        } catch (InterruptedException e) {
            log.warn("Users creation took way too long to finish", e);
        }

        Assertions.assertEquals(threads * usersPerThread, savedUsers.size());

        savedUsers.forEach((userid, username) -> {
            log.info("Looking for user [id: {}, handle: {}]", userid, username);
            User user = store.fetch(userid);
            Assertions.assertNotNull(user);
            Assertions.assertEquals(username, user.getHandle());
        });
    }
}