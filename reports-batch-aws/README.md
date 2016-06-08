reports-batch-aws
=================

_CLI to mine data from your Github organization on Amazon AWS._

-------------

`reports-batch-aws` is a CLI program to fetch and store relevant data about your Github organization from Amazon AWS using lambdas, SQS and events.

### Configuration

**TODO**

### Usage

To use `reports-batch-aws` , simply run:

```shell
$ reports-batch-aws your-organization-name --from 2016-01-01 --email carl@novoda.com franceso@novoda.com
```

The `from` parameter is optional and can be specified as an ISO-8601 date/time string.

The program will delegate the job to AWS and report that it has correctly started, then it will exit. You will receive a completion notification on
the specified emails, if provided.
