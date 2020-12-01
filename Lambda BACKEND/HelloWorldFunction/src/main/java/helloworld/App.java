package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;


/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map<String, Object>, LexResponse> {


    AWSServices awsServices = new AWSServices();

    @Override
    public LexResponse handleRequest(Map<String, Object> input, Context context) {

        for (Map.Entry<String, Object> entry : input.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " Value: "+ entry.getValue());
        }


        HashMap map;
        String awsType = "";
        if (input.get("currentIntent") instanceof HashMap) {
            map = (HashMap)input.get("currentIntent");
            if (map.get("slots") instanceof HashMap){
                map = (HashMap)map.get("slots");
                if (map.get("type") instanceof String){
                    awsType = (String)map.get("type");

                }
            }

        }

        HashMap<String, Object> response = new HashMap<>();
        StringBuilder content = new StringBuilder("You asked for ");
        switch (awsType) {

            case "S3":
                response.put("S3", String.join(",",
                        awsServices.getS3Instances()));
                content.append("S3 instances");
                break;
            case "DYNAMODB":
                response.put("DYNAMODB", String.join(",",
                        awsServices.getDynamoDBInstances()));
                content.append("Dynamo instances");
                break;
            case "LAMBDA":
                response.put("LAMBDA", String.join(",",
                        awsServices.getLambdaInstances()));
                content.append("Lambda instances");
                break;
            case "EC2":
                response.put("EC2", String.join(",",
                        awsServices.getEC2Instances()));
                content.append("ec2 instances");
                break;
            case "ALL":
                response.put("S3", String.join(",",
                        awsServices.getS3Instances()));
                response.put("DYNAMODB", String.join(",",
                        awsServices.getDynamoDBInstances()));
                response.put("LAMBDA", String.join(",",
                        awsServices.getLambdaInstances()));
                response.put("EC2", String.join(",",
                        awsServices.getEC2Instances()));
                content.append("all instances");
                break;
            default:
                response.put("ERROR", "Service not available");

        }

        Message message = new Message("PlainText", content.toString());
        DialogAction da = new DialogAction("Close", "Fulfilled", message);


        return new LexResponse(da, response);
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
