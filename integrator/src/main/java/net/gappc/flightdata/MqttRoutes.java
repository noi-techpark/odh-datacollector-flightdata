package net.gappc.flightdata;

import org.apache.camel.builder.RouteBuilder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class MqttRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DynamoDbClient client = DynamoDbClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();
        bindToRegistry("client", client);
        from("paho-mqtt5:#")
                .process(new ItemProcessor())
//                .tracing()
                .log(">>> ${body}")
//                .to("aws2-ddb://mqtt-test?region=AWS_REGION&accessKey=AWS_ACCESS_KEY_ID&secretKey=AWS_SECRET_ACCESS_KEY")
                .to("aws2-ddb://mqtt-test?amazonDDBClient=#client");
//                .to("stream:out");
    }
}
