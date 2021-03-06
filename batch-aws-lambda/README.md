batch-aws-lambda
================

_Amazon AWS recursive Lambda to fetch Github events._

----------------

`batch-aws-lambda` is an Amazon AWS Lambda handler implementation for the `batch` module, that fetches and stores historical data about your Github
organisation from Amazon AWS using lambdas, SQS and events.

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

### Allow Scheduled Events

After uploading the Lambda for the first time, you need to enable it to receive Scheduled Events (alarms):

```shell
aws lambda add-permission \
    --statement-id 'Allow-scheduled-events' \
    --action 'lambda:InvokeFunction'\
    --principal 'events.amazonaws.com'\
    --function-name function:github-reports-lambda
```
