package xyz.neopan.test;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.*;

/**
 * @author neopan
 * @since 2020/8/4
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@ActiveProfiles("test")
@SpringJUnitConfig(
    initializers = ConfigDataApplicationContextInitializer.class
)
public @interface BeanTest {

    /**
     * Alias for {@link SpringJUnitConfig#classes}.
     */
    @AliasFor(annotation = SpringJUnitConfig.class, attribute = "classes")
    Class<?>[] value() default {};

}
