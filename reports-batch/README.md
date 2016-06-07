reports-batch
=============

_CLI to mine data from your Github organization._

-------------

`reports-batch` is a CLI program to fetch and store relevant data about your Github organization.

### Configuration

To configure the program, you need to put both a `database.credentials` (see [`db-layer` docs](../db-layer/README.md)) and a `github.credentials` file
in the root project directory.

The `github.credentials` file must contain the `GITHUB_OAUTH_TOKEN` property, which you can generate on your Github organization profile.
For an example, see the [`github.credentials.sample` file](../github.credentials.sample).

### Usage

**Note:** the CLI isn't parametrized yet, but this is how you will be using it.

To use `reports-batch` , simply run:

```shell
$ reports-batch your-organization-name --since 2016-01-01
```

The `since` parameter is optional and can be specified as an ISO-8601 date/time string.

The program may temporarily halt if the Github API calls reach a predefined rate limit, resuming on its own after said limit gets reset.
