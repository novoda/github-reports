service
=======

_Service library to retrieve Github organisation data and store them on a database._

--------

This module provides the components to query the Github API and store the found data on a MySQL instance.

### Configuration

Any project referencing `service` must configure the database and Github credentials as follows.

#### Database

To configure the database instance you want to connect to, put a `database.credentials` file in the resources or classloader accessible folder of
your project, with the following properties defined:

* `DB_USER`, username for the database instance
* `DB_PASSWORD`, password for the database instance
* `DB_CONNECTION_STRING`, JDBC connection string to the database instance

For an example, see the `reports-batch-local` [`database.credentials.sample` file](../reports-batch-local/src/main/resources/database.credentials.sample).

#### Github

To be able to use the Github API, you must put a `github.credentials` file in the resources or classloader accessible folder, with the
`GITHUB_OAUTH_TOKEN` property set to an API key that you can generate on your Github organization profile.

For an example, see the `reports-batch-local` [`github.credentials.sample` file](../reports-batch-local/src/main/resources/github.credentials.sample).

### Usage

The main components of this module are:

* `GithubRepositoryService` that retrieves issues, comments and events for a Github repo
* `GithubIssueService` that retrieves repositories for an organisation
* `GithubPullRequestService` that retrieves diff comments for pull requests
* all the transformers in the `persistence` package that provide a way to pipe the persistence mechanism in a RxJava pipeline

### Build

The project uses jOOQ as a dynamic query building library. If you decide to swap out MySQL in favour of another DBMS, simply change the jOOQ
configuration in [`build.gradle`](build.gradle).

The SQL generation file is stored in [`sql/generate.sql`](sql/generate.sql).
Successful migration files should be put in the same folder for consistency.

When changing the database schema, you need to re-generate the Java models: to do so, simply run the Gradle task `generateReportsJooqSchemaSource`.