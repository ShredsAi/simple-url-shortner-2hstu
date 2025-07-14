package ai.shreds.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureRabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${url.events.created.exchange:url.events.created}")
    private String createdExchange;

    @Value("${url.events.updated.exchange:url.events.updated}")
    private String updatedExchange;

    @Value("${url.events.deleted.exchange:url.events.deleted}")
    private String deletedExchange;

    @Value("${url.events.bulk.exchange:url.events.bulk}")
    private String bulkExchange;

    @Value("${url.events.validation.exchange:url.events.validation}")
    private String validationExchange;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(rabbitHost, rabbitPort);
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public DirectExchange urlCreatedExchange() {
        return new DirectExchange(createdExchange);
    }

    @Bean
    public DirectExchange urlUpdatedExchange() {
        return new DirectExchange(updatedExchange);
    }

    @Bean
    public DirectExchange urlDeletedExchange() {
        return new DirectExchange(deletedExchange);
    }

    @Bean
    public DirectExchange bulkOperationExchange() {
        return new DirectExchange(bulkExchange);
    }

    @Bean
    public DirectExchange validationRequestExchange() {
        return new DirectExchange(validationExchange);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("url.events.dlq", true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange urlCreatedExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(urlCreatedExchange).with("dlq");
    }
}