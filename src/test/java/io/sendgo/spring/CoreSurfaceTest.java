package io.sendgo.spring;

import io.sendgo.SendgoClient;
import io.sendgo.SendgoConfig;
import io.sendgo.model.ShortUrlRequest;

/** sendgo-java 1.1.0 의 shortUrl() 이 스타터에서 보이는지 컴파일로 확인. */
class CoreSurfaceTest {
    void shortUrlIsReachable() {
        SendgoClient sendgo = new SendgoClient(
                SendgoConfig.builder().accessKey("a").secretKey("b").build());

        sendgo.shortUrl().create(ShortUrlRequest.builder()
                .targetUrl("https://example.com")
                .build());
    }
}
