/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package crimestreamapp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.net.URLDecoder;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code split each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class ProcessS3File implements RequestHandler<S3Event, String> {

    public String handleRequest(S3Event s3event, Context context) {
        System.out.println(s3event.toJson());
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();
            String srcKey = record.getS3().getObject().getKey().replace('+', ' ');
            srcKey = URLDecoder.decode(srcKey, "UTF-8");

            AmazonS3 s3Client = new AmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));

            InputStream objectData = s3Object.getObjectContent();

            System.out.println(objectData);
            Scanner sc = new Scanner(objectData);

            StreamFileProducer streamFileProducer = new StreamFileProducer(
                System.getenv("KAFKA_CLUSTER_URI"), 
                "org.apache.kafka.common.serialization.StringSerializer", 
                "org.apache.kafka.common.serialization.StringSerializer"
            );

            while (sc.hasNextLine()) {
                streamFileProducer.sendProducerRecord(new ProducerRecord<String, String>(System.getenv("PRODUCER_TOPIC"), "key", sc.nextLine()));
            }

            streamFileProducer.closeProducer();
            return "Okay";
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
