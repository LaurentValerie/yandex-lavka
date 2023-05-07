package ru.yandex.yandexlavka.presentation;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BucketFactory {

    public Bucket createBucket(int rateLimit, int rateDurationInSeconds) {
        Bandwidth limit = Bandwidth.classic(rateLimit, Refill.intervally(rateLimit, Duration.ofSeconds(rateDurationInSeconds)));
        return Bucket.builder().addLimit(limit).build();
    }

}
