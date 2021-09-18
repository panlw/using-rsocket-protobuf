package xyz.neopan.hello.rsocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Hooks;
import xyz.neopan.hello.rsocket.protobuf.RSocketRpcConfig;

/**
 * @author panlw
 * @since 2021/9/13
 */
@Slf4j
@Import(RSocketRpcConfig.class)
@SpringBootApplication(exclude = RSocketMessagingAutoConfiguration.class)
public class RSocketRpcServer {

    public static void main(String[] args) {
        // https://github.com/rsocket/rsocket-java/issues/1018
        Hooks.onErrorDropped(e -> log.trace(
            "[APP] callback when rsocket was disposed, it is noisy :("));

        SpringApplication.run(RSocketRpcServer.class);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onReady() {
        return event -> log.info("[APP] I am ready ({})",
            event.getSpringApplication().getWebApplicationType());
    }

}
