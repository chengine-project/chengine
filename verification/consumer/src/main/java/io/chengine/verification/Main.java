package io.chengine.verification;

import io.chengine.Chengine;
import io.chengine.autoconfigure.ChengineTelegramAutoConfiguration;
import io.chengine.session.postgres.PostgresSessionStore;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Main {
    public static void main(String[] args) throws Exception {
        for (Class<?> type : new Class<?>[] {Chengine.class, ChengineTelegramAutoConfiguration.class,
                PostgresSessionStore.class, Update.class}) {
            Class.forName(type.getName());
        }
        System.out.println("Published Chengine modules loaded successfully");
    }
}
