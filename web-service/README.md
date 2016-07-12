web-service
===========

_Web Service to query for Github Reports Stats._

-----------

This Web Service is a collection of Amazon AWS Lambdas exposed through AWS API Gateway.

### Configuration

As a first step, install the [AWS CLI](https://aws.amazon.com/cli/) and [configure it with your credentials and region]
(http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html#cli-quick-configuration).

#### Role creation

Create a new role for your lambda with the following command:

```shell
aws iam create-role \
  --role-name github-reports-ws-role \
  --assume-role-policy-document file://assets/github-reports-ws-role.json
```

#### Deploy Lambdas

To build and deploy all lambdas, run the `uploadActionLambdas` Gradle task.

#### Create REST API

You can create the REST API with the [AWS Web UI](https://console.aws.amazon.com/apigateway), by adding resources and
methods manually, or you can do it with the AWS CLI.

To create the REST API with the AWS CLI, launch the following:

```shell
aws apigateway create-rest-api --name "Github Reports API"
```

AWS CLI will return something like:

```json
{
    "name": "Github Reports API",
    "id": "API_ID",
    "createdDate": 1467815537
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
            "id": "API_ROOT_ID"
        }
    ]
}
```

Take note of the root path ID as well.

#### Create endpoint mappings

##### Repositories

Create `/repositories` endpoint with the following commands:

```shell
aws apigateway create-resource --rest-api-id API_ID --parent-id API_ROOT_ID --path-part repositories
```

The output will be something like:

```shell
{
    "path": "/repositories",
    "pathPart": "repositories",
    "id": "RESOURCE_ID",
    "parentId": "API_ROOT_ID"
}
```

Take note of the resource ID.

```shell
aws apigateway put-method --rest-api-id API_ID --resource-id RESOURCE_ID \
    --http-method GET --no-api-key-required --authorization-type NONE
```

We're now ready to bind the endpoint to the proper lambda:

```shell
aws apigateway put-integration --rest-api-id API_ID --resource-id RESOURCE_ID \
    --http-method GET --type AWS
    --uri arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:YOUR_ACCOUNT_ID:function:github-reports-repositories-get
```

**TODO**: add more.

#### Deploy

Use the [API Gateway Web console](https://console.aws.amazon.com/apigateway) to ship your Web Service to
staging/production and use the provided base URL to access the deployed endpoints.
