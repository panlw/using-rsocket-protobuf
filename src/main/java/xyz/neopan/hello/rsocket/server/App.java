package xyz.neopan.hello.rsocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Hooks;
import xyz.neopan.hello.rsocket.protobuf.RpcConfig;

/**
 * @author panlw
 * @since 2021/9/13
 */
@Slf4j
@Import(RpcConfig.class)
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        // https://github.com/rsocket/rsocket-java/issues/1018
        Hooks.onErrorDropped(e -> log.trace(
                "[APP] callback when rsocket was disposed, it is noisy :("));

        SpringApplication.run(App.class);
    }

    @Bean
    ApplicationRunner initializer() {
        return args -> {
            log.info("[APP] initializing...");
            log.info("[APP] initialized.");
        };
    }

}
