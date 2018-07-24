package crimestreamapp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class StreamFileProducer {

    private final Properties props;
    private final KafkaProducer producer;

    StreamFileProducer(final String URI, 
                        final String keySerializer, 
                        final String valueSerializer) {
        this.props = new Properties();
        this.props.put("bootstrap.servers", URI);
        this.props.put("acks", "all");
        this.props.put("retries", 0);
        this.props.put("batch.size", 16438);
        this.props.put("buffer.memory", 33554432);
        this.props.put("key.serializer", keySerializer);
        this.props.put("value.serializer", valueSerializer);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(this.props);
        this.producer = producer;
    }

    public void sendProducerRecord(final ProducerRecord producerRecord) {
        this.producer.send(producerRecord);
    }

    public void closeProducer() {
        this.producer.close();
    }


}