reports-batch
=============

_CLI to mine data from your Github organization._

-------------

`reports-batch` is a CLI program to fetch and store relevant data about your Github organization.

### Configuration

#### Database

To configure the database instance you want to connect to, put a `database.credentials` file in the resources or classloader accessible folder, with
the following properties defined:

* `DB_USER`, username for the database instance
* `DB_PASSWORD`, password for the database instance
* `DB_CONNECTION_STRING`, JDBC connection string to the database instance

For an example, see the [`database.credentials.sample` file](src/main/resources/database.credentials.sample).

#### Github

To be able to use the Github API, you must put a `github.credentials` file in the resources or classloader accessible folder, with the
`GITHUB_OAUTH_TOKEN` property set to an API key that you can generate on your Github organization profile.

For an example, see the [`github.credentials.sample` file](src/main/resources/github.credentials.sample).

### Usage

**Note:** the CLI isn't parametrized yet, but this is how you will be using it.

To use `reports-batch` , simply run:

```shell
$ reports-batch your-organization-name --from 2016-01-01
```

The `from` parameter is optional and can be specified as an ISO-8601 date/time string.

The program may temporarily halt if the Github API calls reach a predefined rate limit, resuming on its own after said limit gets reset.
