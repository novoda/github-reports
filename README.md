github-reports
==============

_Team statistics from GitHub into your shell._

--------------

**To know how to build/configure/use each module, please click on the links to access the documentation**.

### Base modules

Base modules allow for behaviour decomposition, each taking care of one specific functionality.

* [`core`](core/README.md) contains all the models and main interfaces shared across the project
* [`db-layer`](db-layer/README.md) implements the persistence layer on a MySQL database
* [`network`](network/README.md) cotains all the network (HTTP) components used to query API
* [`github`](github/README.md) contains the components to query the Github API and persist the retrieved elements on the
  database
* [`float`](float/README.md) contains all the components needed to query the Float API
* [`web-service`](web-service/README.md) contains the endpoint handlers for Amazon AWS Gateway written as Lambdas
* [`github-hooks-web-service`](github-hooks-web-service/README.md) contains the the components used to receive and parse Github webhook notifications.

### Batch modules

Batch modules allow the historical retrieval of data from Github and persistence on the database, using base modules.

* [`batch`](batch/README.md) defines a shared recursive batch mechanism that can be implemented with different cloud
  providers
* [`batch-aws`](batch-aws/README.md) contains a service client layer for the needed Amazon AWS services (CloudWatch
  Events, SQS, Lambda)
* [`batch-aws-lambda`](batch-aws-lambda/README.md) contains the Amazon AWS Lambda function that will recurse, using SQS,
  to fetch and store Github data

### User Interfaces

User interfaces allow to execute all the processes implemented in the project.

* [`reports-batch`](reports-batch/README.md) contains a CLI to query the Github APIs from your machine or from AWS, and
  store retrieved data in the remote/RDS database
* [`reports-stats`](reports-stats/README.md) contains a CLI to query for statistics on data available in the remote/RDS
  database
* [`reports-stats-google-sheets`](reports-stats-google-sheets/README.md) contains a Google Apps Script project to query
  for statistics on data available in the remote/RDS database and output them in a Google Spreadsheet

