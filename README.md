github-reports
==============

_Team statistics from GitHub into your shell._

--------------

**To know how to build/configure/use each module, please click on the links to access the documentation**.

### Base modules

Base modules allow for behaviour decomposition, each taking care of one specific functionality.

* [`core`](core/README.md) contains all the models and main interfaces shared across the project
* [`db-layer`](db-layer/README.md) implements the persistence layer on a MySQL database
* [`github`](gitub/README.md) contains the components to query the Github API and persist the retrieved elements on the database

### Batch modules

Batch modules allow the historical retrieval of data from Github and persistence on the database, using base modules.

* [`batch`](batch/README.md) defines a shared recursive batch mechanism that can be implemented with different cloud providers
* [`batch-aws`](batch-aws/README.md) contains a service client layer for the needed Amazon AWS services (CloudWatch Events, SQS, Lambda)
* [`batch-aws-lambda`](batch-aws-lambda/README.md) contains the Amazon AWS Lambda function that will recurse, using SQS, to fetch and store Github data

### Command Line Interfaces

CLIs are the user interfaces and allow to execute all the processes implemented in the project.

* [`reports-batch`](reports-batch/README.md) contains a CLI to query the Github APIs from your machine or from AWS, and store retrieved data in the
remote/RDS database
* [`reports-stats`](reports-stats/README.md) contains a CLI to query for statistics on data available in the remote/RDS database
