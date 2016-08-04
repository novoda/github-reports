github-hooks-web-service
========================

_Web Service for the github webhooks to hit._

-----------

This Web Service is to contain an AWS lambda, to be exposed through AWS API Gateway. The aim is to configure the
[webhooks](https://developer.github.com/webhooks/) for your organisation so they hit this API with events (containing data). This data is to be
parsed and stored appropriately.

### Configuration

#### Amazon AWS

As a first step, install the [AWS CLI](https://aws.amazon.com/cli/) and [configure it with your credentials and region]
(http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html#cli-quick-configuration).

You need to add a `lambda.properties` file in the classloader accessible folder, referencing the ARN of the AWS Lambda that will handle the
actual processing in the `AWS_LAMBDA_ARN` property (see [`lambda.properties.sample`](src/main/resources/lambda.properties.sample)).

### Role creation

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

### Lambda upload

To upload or update the Lambda on your Amazon AWS account, just run the Gradle task `uploadLambda`.


#### Create REST API

**TODO**

#### Create endpoint mappings

**TODO**

#### Deploy API

**TODO**
