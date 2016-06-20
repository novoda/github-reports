batch-aws
=========

_Components for the Amazon Web Services: lambdas, SQS and CloudWatch Events._

---------

`batch-aws` contains all the components that will run on Amazon AWS with regards to the interfaces in the `batch` module.

### Configuration

Any project referencing `batch-aws` must configure the following credentials.

#### Amazon

To be able to use the Amazon AWS, you must put a `amazon.credentials` file in the resources or classloader accessible folder, with the following
properties defined:

* `AWS_ACCESS_KEY_ID`, access key ID for your IAM user
* `AWS_SECRET_ACCESS_KEY`, secret access key for your IAM user
* `AWS_LAMBDA_NAME`, (only if your module needs to start the default lambda), name of the batch lambda

For an example, see the `reports-batch` [`amazon.credentials.sample` file](../reports-batch/src/main/resources/amazon.credentials.sample).

#### Email

In order to notify the completion and erroring of jobs, you must put a `email.credentials` file in the resources or classloader accessible folder,
with the following properties defined:

* `EMAIL_HOST`, the SMTP server host
* `EMAIL_PORT`, the SMTP server port
* `EMAIL_USE_SSL`, whether to use SSL or not (`true` if you want to use SSL, any different value otherwise)
* `EMAIL_FROM`, the email you want to send notifications from
* `EMAIL_USERNAME`, the username for the sender email
* `EMAIL_PASSWORD`, the password for the sender email

For an example, see the `reports-batch` [`email.credentials.sample` file](../reports-batch/src/main/resources/email.credentials.sample).
