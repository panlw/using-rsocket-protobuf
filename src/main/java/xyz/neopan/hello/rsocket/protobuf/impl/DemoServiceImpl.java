package xyz.neopan.hello.rsocket.protobuf.impl;

import com.google.protobuf.Empty;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.neopan.hello.rsocket.protobuf.DemoRequest;
import xyz.neopan.hello.rsocket.protobuf.DemoResponse;
import xyz.neopan.hello.rsocket.protobuf.DemoService;

/**
 * @author panlw
 * @since 2021/9/13
 */
@Slf4j
@Component
class DemoServiceImpl implements DemoService {

    private DemoResponse handleMessage(DemoRequest message) {
        return DemoResponse.newBuilder()
                .setResponseMessage("Hi, " + message.getRequestMessage())
                .build();
    }

    @Override
    public Mono<DemoResponse> requestReply(DemoRequest message, ByteBuf metadata) {
        log.info("[DEMO] requestReply: {}", message.getRequestMessage());
        return Mono.just(handleMessage(message));
    }

    @Override
    public Mono<Empty> fireAndForget(DemoRequest message, ByteBuf metadata) {
        log.info("[DEMO] fireAndForget: {}", message.getRequestMessage());
        return Mono.empty();
    }

    @Override
    public Flux<DemoResponse> requestStream(DemoRequest message, ByteBuf metadata) {
        log.info("[DEMO] requestStream: {}", message.getRequestMessage());
        return Flux.just(handleMessage(message));
    }

    @Override
    public Mono<DemoResponse> streamingRequestSingleResponse(
            Publisher<DemoRequest> messages, ByteBuf metadata
    ) {
        log.info("[DEMO] streamingRequestSingleResponse");
        return Flux.from(messages).count().map(n ->
                DemoResponse.newBuilder()
                        .setResponseMessage("[DEMO] " + n + " messages arrived.")
                        .build()
        );
    }

    @Override
    public Flux<DemoResponse> streamingRequestAndResponse(
            Publisher<DemoRequest> messages, ByteBuf metadata
    ) {
        log.info("[DEMO] streamingRequestAndResponse");
        return Flux.from(messages).map(this::handleMessage);
    }

}
