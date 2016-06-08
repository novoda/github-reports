github-reports
==============

_Team statistics from GitHub into your shell._

--------------

The project is made of the following modules (you can click on each project to access the specific documentation):

* [`core`](core/README.md) contains all the models and main interfaces shared across the project
* [`db-layer`](db-layer/README.md) implements the persistence layer on a MySQL database
* [`service`](service/README.md) contains the components to query the Github API and persist the retrieved elements on the database
* [`reports-batch-local`](reports-batch-local/README.md) contains a CLI to query the Github APIs from your machine and store retrieved data on the
database
* [`reports-stats`](reports-stats/README.md) contains a CLI to query for statistics on data available on the database

