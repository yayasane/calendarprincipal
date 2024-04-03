package sn.veli.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Day getDaySample1() {
        return new Day().id(1L).date("date1").dayOfWeek("dayOfWeek1");
    }

    public static Day getDaySample2() {
        return new Day().id(2L).date("date2").dayOfWeek("dayOfWeek2");
    }

    public static Day getDayRandomSampleGenerator() {
        return new Day().id(longCount.incrementAndGet()).date(UUID.randomUUID().toString()).dayOfWeek(UUID.randomUUID().toString());
    }
}
