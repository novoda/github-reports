aws
===

_Components for the Amazon Web Services: lambdas, SQS and CloudWatch Events._

---

`aws` contains all the components that will run on Amazon AWS. This module can be directly deployed to AWS.

### Configuration

Any project referencing `aws` must configure the Amazon AWS credentials as follows.

To be able to use the Amazon AWS, you must put a `amazon.credentials` file in the resources or classloader accessible folder, with the following
properties defined:

* `AWS_ACCESS_KEY_ID`, access key ID for your IAM user
* `AWS_SECRET_ACCESS_KEY`, secret access key for your IAM user

For an example, see the `reports-batch-aws` [`amazon.credentials.sample` file](../reports-batch-aws/src/main/resources/amazon.credentials.sample).
