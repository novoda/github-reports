github-reports
==============

_Team statistics from GitHub into your shell._

--------------

The project is made of the following modules (you can click on each project to access the specific documentation):

* [`core`](core/README.md) contains all the models and main interfaces shared across the project
* [`db-layer`](db-layer/README.md) implements the persistence layer on a MySQL database
* [`service`](service/README.md) contains the components to query the Github API and persist the retrieved elements on the database
* [`reports-batch-local`](reports-batch-local/README.md) contains a CLI to query the Github APIs from your machine and store retrieved data in the
remote/rds database
* [`aws`](aws/README.md) contains the components to be deployed on Amazon AWS
* [`reports-batch-aws`](reports-batch-aws/README.md) contains a CLI to query the Github APIs from Amazon AWS and store retrieved data on the database
* [`lambda`](lambda/README.md) contains the Amazon AWS Lambda function that will recurse over itself, using SQS, to fetch and store Github data
* [`reports-stats`](reports-stats/README.md) contains a CLI to query for statistics on data available in the remote/rds database

