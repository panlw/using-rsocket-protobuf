package xyz.neopan.hello.rsocket.protobuf.impl;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import xyz.neopan.hello.rsocket.protobuf.EchoRequest;
import xyz.neopan.hello.rsocket.protobuf.EchoResponse;
import xyz.neopan.hello.rsocket.protobuf.EchoService;

/**
 * @author panlw
 * @since 2021/9/13
 */
@Slf4j
@Component
class EchoServiceImpl implements EchoService {

    private EchoResponse handleMessage(EchoRequest message) {
        return EchoResponse.newBuilder()
                .setResponseMessage("Hi, " + message.getRequestMessage())
                .build();
    }

    @Override
    public Mono<EchoResponse> requestReply(EchoRequest message, ByteBuf metadata) {
        log.info("[ECHO] requestReply: {}", message.getRequestMessage());
        return Mono.just(handleMessage(message));
    }

}
