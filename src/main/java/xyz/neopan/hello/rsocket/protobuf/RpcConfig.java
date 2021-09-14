package xyz.neopan.hello.rsocket.protobuf;

import io.rsocket.SocketAcceptor;
import io.rsocket.ipc.RequestHandlingRSocket;
import io.rsocket.rpc.AbstractRSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author panlw
 * @since 2021/9/14
 */
@Slf4j
@ComponentScan
@Configuration
public class RpcConfig {

    /**
     * 参考 {@code RSocketServerAutoConfiguration.EmbeddedServerConfiguration#rSocketServerBootstrap}
     */
    @Bean
    RSocketServerBootstrap protobufRSocketServerBootstrap(
            RSocketServerFactory rSocketServerFactory,
            List<AbstractRSocketService> services
    ) {
        log.info("[APP] protobufRSocketServerBootstrap ({})", services.size());
        final var rSocket = new RequestHandlingRSocket();
        services.forEach(rSocket::withEndpoint);
        return new RSocketServerBootstrap(rSocketServerFactory, SocketAcceptor.with(rSocket));
    }

}
