reports-stats
=============

_CLI to extract statistics from your Github data._

-------------

`reports-stats` is a CLI program to perform statistics on your Github data available in a database.

### Configuration

To configure the program, you need to put a `database.credentials` (see [`db-layer` docs](../db-layer/README.md)) file in the root project directory.

### Usage

The CLI has 3 commands available, `user`, `repo` and `project`, that return statistics about different objects.

#### User statistics

The `user` command returns statistics about a specific user in your organization, and accepts the following parameters:

* the default argument is the username you want to retrieve statistics for
* `--repo`, the name of the repository to retrieve data from
* `--project`, the project name to retrieve data from (a project may contain 0 or more repositories)
* `--from`, the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to`, the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information:

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of other people's comments on this user's issues and PRs
* number of repositories worked on (it is always 1 if the repository is declared in the input arguments)

#### Repository statistics

The `repo` command returns statistics about a specific repository in your organization, and accepts the following parameters:

* the default argument is the username you want to retrieve statistics for
* `--from`, the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to`, the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information:

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of participating users

#### Project statistics

The `project` command returns statistics about a specific project in your organization, and accepts the following parameters:

* the default argument is the project you want to retrieve statistics for
* `--from`, the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to`, the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information (same as for the repository):

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of participating users
