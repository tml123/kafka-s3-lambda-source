## What this project demonstrates

Demonstrates using a Lambda function to monitor an S3 bucket to publish to a Kafka topic.  Currently, the Confluent platform only provides an S3 sink connector.  The Classes provided here demonstrate a potential method of developing an S3 source with Lambda.

### Environment Variables

* `KAFKA_CLUSTER_URI` is the broadcasted IP of the Kafka cluster.
* `PRODUCER_TOPIC` is the topic to which the Lambda function publishes. 

### Monitoring an S3 Bucket

AWS provides a [tutorial](https://docs.aws.amazon.com/lambda/latest/dg/with-s3-example.html) for utilizing Lambda with S3.  Following that tutorial will show how to set up Lambda to monitor changes to S3.

You'll need to:

* Create an execution role.  I assign permissions to my Lambda function to utilize S3 services without really limiting what the Lambda function can and cannot do.  You obviously can get a bit more explicit. I just wanted to be able to demonstrate that it can work.
* Enable Events on your S3 bucket.  When creating an S3 bucket (or after), you'll want to go to `Advanced Settings` and enable the event.  You'll be able to select Lambda as the `notification destination`.

Once you do that, you should be in a good position to publish to a topic.  Then, you'll just want to set up a Consumer of whatever topic you've created.