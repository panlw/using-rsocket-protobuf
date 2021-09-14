package xyz.neopan.hello.rsocket.protobuf.impl;

import com.google.protobuf.Empty;
import io.netty.buffer.Unpooled;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import xyz.neopan.hello.rsocket.client.RSocketClientRSocket;
import xyz.neopan.hello.rsocket.protobuf.*;
import xyz.neopan.test.BeanTest;

import java.time.Duration;

/**
 * Each request will re{@link RSocketConnector#connect(ClientTransport)}
 *
 * @author panlw
 * @since 2021/9/14
 */
@BeanTest(DemoServiceImplTest.Config.class)
class DemoServiceImplTest {

    @Slf4j
    static class Config {

        public static final RetryBackoffSpec RSOCKET_RETRY =
                Retry.fixedDelay(2, Duration.ofSeconds(2));

        @Bean
        RSocketClient rSocketClient() {
            log.info("[TEST] rSocketClient");
            final var transport =
                    TcpClientTransport.create("localhost", 7001);
            final var source = RSocketConnector.create()
                    .payloadDecoder(PayloadDecoder.ZERO_COPY)
                    .reconnect(RSOCKET_RETRY)
                    .connect(transport);
            return RSocketClient.from(source);
        }

        @Bean
        DemoService simpleServiceMono(RSocketClient rSocketClient) {
            log.info("[TEST] simpleServiceMono");
            return new DemoServiceClient(new RSocketClientRSocket(rSocketClient));
        }

        @Bean
        EchoService echoServiceMono(RSocketClient rSocketClient) {
            log.info("[TEST] echoServiceMono");
            return new EchoServiceClient(new RSocketClientRSocket(rSocketClient));
        }
    }

    @Autowired
    private DemoService demoService;

    private Mono<DemoResponse> requestReply() {
        final var req = DemoRequest.newBuilder()
                .setRequestMessage("Neo").build();
        return demoService.requestReply(req, Unpooled.EMPTY_BUFFER);
    }

    private Mono<Empty> fireAndForget() {
        final var req = DemoRequest.newBuilder()
                .setRequestMessage("Neo").build();
        return demoService.fireAndForget(req, Unpooled.EMPTY_BUFFER);
    }

    private Flux<DemoResponse> requestStream() {
        final var req = DemoRequest.newBuilder()
                .setRequestMessage("Neo").build();
        return demoService.requestStream(req, Unpooled.EMPTY_BUFFER);
    }

    private Mono<DemoResponse> streamingRequestSingleResponse() {
        final var req = Flux.just("Neo", "Foo")
                .map(x -> DemoRequest.newBuilder().setRequestMessage(x).build());
        return demoService.streamingRequestSingleResponse(req, Unpooled.EMPTY_BUFFER);
    }

    private Flux<DemoResponse> streamingRequestAndResponse() {
        final var req = Flux.just("Neo", "Foo")
                .map(x -> DemoRequest.newBuilder().setRequestMessage(x).build());
        return demoService.streamingRequestAndResponse(req, Unpooled.EMPTY_BUFFER);
    }

    @Test
    void test_requestReply() {
        StepVerifier.create(requestReply())
                .consumeNextWith(x -> System.out.println("res: " + x.getResponseMessage()))
                .verifyComplete();
    }

    @Test
    void test_fireAndForget() {
        StepVerifier.create(fireAndForget())
                .verifyComplete();
    }

    @Test
    void test_requestStream() {
        StepVerifier.create(requestStream())
                .consumeNextWith(x -> System.out.println("res: " + x.getResponseMessage()))
                .verifyComplete();
    }

    @Test
    void test_streamingRequestSingleResponse() {
        StepVerifier.create(streamingRequestSingleResponse())
                .consumeNextWith(x -> System.out.println("res: " + x.getResponseMessage()))
                .verifyComplete();
    }

    @Test
    void test_streamingRequestAndResponse() {
        StepVerifier.create(streamingRequestAndResponse())
                .consumeNextWith(x -> System.out.println("res1: " + x.getResponseMessage()))
                .consumeNextWith(x -> System.out.println("res2: " + x.getResponseMessage()))
                .verifyComplete();
    }

    @Autowired
    private EchoService echoService;

    private Mono<EchoResponse> echo() {
        final var req = EchoRequest.newBuilder()
                .setRequestMessage("Foo").build();
        return echoService.requestReply(req, Unpooled.EMPTY_BUFFER);
    }

    @Test
    void test_echo() {
        StepVerifier.create(echo())
                .consumeNextWith(x -> System.out.println("res: " + x.getResponseMessage()))
                .verifyComplete();
    }

}
