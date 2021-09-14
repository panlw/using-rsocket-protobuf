package xyz.neopan.hello.rsocket.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 通过{@link RSocketClient}，多个RSocket消费方可以公用一个RSocket连接。
 *
 * @author panlw
 * @since 2021/9/14
 */
@RequiredArgsConstructor
public class RSocketClientRSocket implements RSocket {

    private final RSocketClient client;

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return client.fireAndForget(Mono.just(payload));
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return client.requestResponse(Mono.just(payload));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return client.requestStream(Mono.just(payload));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return client.requestChannel(payloads);
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return client.metadataPush(Mono.just(payload));
    }

    @Override
    public boolean isDisposed() {
        return client.isDisposed();
    }

}
