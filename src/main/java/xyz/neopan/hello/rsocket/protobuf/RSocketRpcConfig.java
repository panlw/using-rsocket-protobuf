package xyz.neopan.hello.rsocket.protobuf;

import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.ipc.RequestHandlingRSocket;
import io.rsocket.rpc.AbstractRSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参考 {@code RSocketServerAutoConfiguration.EmbeddedServerConfiguration}
 *
 * @author panlw
 * @since 2021/9/14
 */
@Slf4j
@ComponentScan
@Configuration
@EnableConfigurationProperties(RSocketProperties.class)
public class RSocketRpcConfig {

    @Bean
    RSocketServerFactory rSocketServerFactory(
        RSocketProperties properties, ReactorResourceFactory resourceFactory,
        ObjectProvider<RSocketServerCustomizer> customizers
    ) {
        final NettyRSocketServerFactory factory = new NettyRSocketServerFactory();
        factory.setResourceFactory(resourceFactory);
        factory.setTransport(properties.getServer().getTransport());

        final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getServer().getAddress()).to(factory::setAddress);
        map.from(properties.getServer().getPort()).to(factory::setPort);
        map.from(properties.getServer().getFragmentSize()).to(factory::setFragmentSize);
        map.from(properties.getServer().getSsl()).to(factory::setSsl);

        factory.setRSocketServerCustomizers(
            customizers.orderedStream().collect(Collectors.toList())
        );

        return factory;
    }

    @Bean
    RSocketServerBootstrap rSocketServerBootstrap(
        RSocketServerFactory rSocketServerFactory,
        List<AbstractRSocketService> services
    ) {
        log.info("[APP] protobufRSocketServerBootstrap ({})", services.size());
        final var rSocket = new RequestHandlingRSocket();
        services.forEach(rSocket::withEndpoint);
        return new RSocketServerBootstrap(rSocketServerFactory, SocketAcceptor.with(rSocket));
    }

    @Bean
    RSocketServerCustomizer frameDecoderRSocketServerCustomizer() {
        return server -> server.payloadDecoder(PayloadDecoder.ZERO_COPY);
    }

}
