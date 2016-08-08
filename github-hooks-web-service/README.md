github-hooks-web-service
========================

_Web Service for the github webhooks to hit._

-----------

This Web Service is to contain an AWS lambda, to be exposed through AWS API Gateway. The aim is to configure the
[webhooks](https://developer.github.com/webhooks/) for your organisation so they hit this API with events (containing data). This data is to be
parsed and stored appropriately.

## Configuration

### Amazon AWS

If you haven't installed or configured the AWS CLI yet, please refer to [`web-service`](web-service/README.md) in order to do so. All the steps
are similar, but we'll detail them here anyway.

#### Role creation

Create a new role for your lambda with the following command:

```shell
aws iam create-role \
    --role-name github-reports-role \
    --assume-role-policy-document file://assets/github-reports-role.json
```

Then, to add the necessary policies to your role, run the following commands:

```shell
aws iam attach-role-policy --role-name github-reports-role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaRole
aws iam attach-role-policy --role-name github-reports-role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole
```

#### Lambda upload

To upload or update the Lambda on your Amazon AWS account, just run the Gradle task `uploadWebhookLambda` (or `uWL`).


#### Create API

Using the [AWS Web UI](https://console.aws.amazon.com/apigateway) might be an easier way to setup your API, but you can still do it manually
through the AWS CLI.

To create the API with the AWS CLI:

```shell
aws apigateway create-rest-api --name "Github Reports Webhooks API"
```

AWS CLI will return something like:

```json
{
    "name": "Github Reports Webhooks API",
    "id": "API ID",
    "createdDate": 1470319082
}
```

Take note of the ID, as we will use it in the following steps.

Now grab the root of your API with the command:

```shell
aws apigateway get-resources --rest-api-id API_ID
```

The output will be something like:

```json
{
    "items": [
        {
            "path": "/",
            "id": "ROOT ID"
        }
    ]
}
```

Take note of the root path ID as well.

##### Create endpoint mappings

Create `/webhook` endpoint with the following commands:

```shell
aws apigateway create-resource \
    --rest-api-id API_ID \
    --parent-id ROOT_ID \
    --path-part webhook
```

The output will be something like:

```json
{
    "path": "/webhook",
    "pathPart": "webhook",
    "id": "RESOURCE ID",
    "parentId": "kna1h2rtg4"
}
```

Take note of the resource ID. Now add a POST method to it:

```shell
aws apigateway put-method \
    --rest-api-id API_ID \
    --resource-id RESOURCE_ID \
    --http-method POST \
    --no-api-key-required \
    --authorization-type NONE
```

You should see something like:

```json
{
    "apiKeyRequired": false,
    "httpMethod": "POST",
    "authorizationType": "NONE"
}
```

We're now ready to bind the endpoint to the proper lambda:

```shell
aws apigateway put-integration \
    --rest-api-id API_ID \
    --resource-id RESOURCE_ID \
    --http-method POST \
    --type AWS \
    --integration-http-method POST \
    --uri arn:aws:apigateway:AWS_REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:aws-region:YOUR ACCOUNT ID:function:github-reports-webhook-post/invocations
```

A result as the following should come up:

```json
{
    "httpMethod": "POST",
    "passthroughBehavior": "WHEN_NO_MATCH",
    "cacheKeyParameters": [],
    "type": "AWS",
    "uri": "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:aws-region:91320918103:function:github-reports-webhook-post/invocations",
    "cacheNamespace": "qrkrjs"
}
```

For more information check the [AWS tutorial](http://docs.aws.amazon.com/lambda/latest/dg/with-on-demand-https-example-configure-event-source.html)
on setting up API gateway with lambda.

#### Deploy

Use the [API Gateway Web console](https://console.aws.amazon.com/apigateway) to ship your Web Service to
staging/production and take note of the provided base URL as you'll need it to setup your webhooks.

### Github

You can setup the webhooks on an organisation basis or individually, per repository. We'll be covering the organisation scenario here but setting
up webhooks on each repo is similar with the only difference being you'll need to do this for each repo, under the repo settings.

#### Organisation

Under your organisation's settings webhooks section - `https://github.com/organizations/ORGANISATION/settings/hooks` - press the "**Add webhook**"
button.

You should now add your AWS API gateway endpoint in the "**Payload URL**" text box. Pick `application/json` as the content type and select 
individual events in the *"Which events would you like to trigger this webhook?"* section.

Currently we support a limited set of all the events (and not all the actions for each event): 

- Commit comment
- Issue comment
- Issues
- Pull request
- Pull request review comment

The "Active" checkbox should be selected by default. Now press the "**Add webhook**" button to finish the process.

You might want to visit this section of your organisation's settings every now and then, as you can easily check the most recent webhook 
deliveries, with details such as the POST request body (the actual event) and the AWS Lambda's response.

