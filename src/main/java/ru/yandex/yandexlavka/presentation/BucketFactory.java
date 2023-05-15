package ru.yandex.yandexlavka.presentation;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**

 The {@code BucketFactory} class is responsible for creating a token bucket for rate limiting.

 This class uses the Token Bucket algorithm to limit the rate of some process.
 */
@Component
public class BucketFactory {
    /**

     Creates a new token bucket with the given rate limit and duration.
     @param rateLimit the maximum number of tokens that can be consumed in one interval
     @param rateDurationInSeconds the duration of an interval in seconds
     @return a new {@code Bucket} object with the specified parameters
     */
    public Bucket createBucket(int rateLimit, int rateDurationInSeconds) {
        Bandwidth limit = Bandwidth.classic(rateLimit, Refill.intervally(rateLimit, Duration.ofSeconds(rateDurationInSeconds)));
        return Bucket.builder().addLimit(limit).build();
    }

}
