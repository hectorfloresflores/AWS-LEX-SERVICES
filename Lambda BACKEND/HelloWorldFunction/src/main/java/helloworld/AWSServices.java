package helloworld;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AWSServices {

    final AmazonS3 s3;
    final AmazonDynamoDB dynamoDB;
    final AmazonEC2 ec2;
    final AWSLambda lamda;


    public AWSServices() {

        s3 = AmazonS3ClientBuilder.defaultClient();
        dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        ec2 = AmazonEC2ClientBuilder.defaultClient();
        lamda = AWSLambdaClientBuilder.defaultClient();

    }

    List<String> getS3Instances() {
        return s3.listBuckets().stream().map(Bucket::getName)
                .collect(Collectors.toList());
    }

    List<String> getDynamoDBInstances() {
        return dynamoDB.listTables().getTableNames();
    }

    List<String> getEC2Instances() {
        boolean done = false;
        List<String> result = new LinkedList<>();
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    result.add(instance.getInstanceId());
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
        return result;
    }

    List<String> getLambdaInstances() {

        List<String> result = new LinkedList<>();
        List<FunctionConfiguration> list = lamda.listFunctions().getFunctions();

        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            FunctionConfiguration config = (FunctionConfiguration)iter.next();
            result.add(config.getFunctionName());

        }
        return result;
    }

}
