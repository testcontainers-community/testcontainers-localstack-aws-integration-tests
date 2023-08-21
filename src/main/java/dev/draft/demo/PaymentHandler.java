package dev.draft.demo;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentHandler.class);

    final List<Payment> payments = new ArrayList<>();

    DynamoDbClient dynamoDbClient;

    public PaymentHandler(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @SqsListener("payment-queue")
    public void handlePayment(Payment payment) {
        LOG.info("Payment details received: " + payment.toString());

        payments.add(payment);

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

        enhancedClient.table("Payments", TableSchema.fromBean(Payment.class)).putItem(payment);

        LOG.info("Payment details saved in table");
    }
}
