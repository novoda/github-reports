reports-stats
=============

_CLI to extract statistics from your Github data._

-------------

`reports-stats` is a CLI program to perform statistics on your Github data available in a database.

### Configuration

To configure the database instance you want to connect to, put a `database.credentials` file in the resources or classloader accessible folder, with
the following properties defined:

* `DB_USER`, username for the database instance
* `DB_PASSWORD`, password for the database instance
* `DB_CONNECTION_STRING`, JDBC connection string to the database instance

For an example, see the [`database.credentials.sample` file](src/main/resources/database.credentials.sample).

### Distribute the application

To build a zip file of the application, run the `distZip` Gradle task: this will generate a zip file in the `build/distributions` directory, in order
to upload and re-use the CLI without having to rebuild it every time.

**Note**: distribution tasks strip out some files from the resources, so you have to re-configure your application by putting the proper
`.credentials` files in the `lib` directory (the one containing the JAR files) in the extracted zip structure.

### Debug from IntelliJ IDEA

To debug the application in IDEA, simply put the credential files in the `src/main/resources` directory and create a new Debug configuration:

1. Go to Run -> Edit Configurations...
2. Click on the + icon and select "Application" as a new configuration
3. Put the `com.novoda.github.reports.stats.Main` class as the "Main class"
4. Type **only the program arguments** in the "Program arguments" text field (e.g. `user frapontillo --repo github-reports --project pt
   --from 2016-01-01 --to 2016-12-31`)
5. Apply the changes
6. Run the created configuration

### Usage

The CLI has 3 commands available, `user`, `repo` and `project`, that return statistics about different objects.

**Note**: the examples in the following sections assume that you're running the applications from the `bin` directory in the distribution folder,
where you have the `reports-stats` bash file available. If you are running from the IDE, remember that **`reports-stats` is not a program argument**.

#### User statistics

The `user` command returns statistics about a specific user in your organization, and accepts the following parameters:

* the default argument is the username you want to retrieve statistics for
* `--repo` (optional), the name of the repository to retrieve data from
* `--project` (optional), the project name to retrieve data from (a project may contain 0 or more repositories)
* `--from` (optional), the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information:

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of other people's comments on this user's issues and PRs
* number of repositories worked on (it is always 1 if the repository is declared in the input arguments)

For example:

```shell
$ reports-stats user frapontillo --repo github-reports --project pt --from 2016-01-01 --to 2016-12-31

Username: frapontillo
Number of opened issues: 0
Number of opened PRs: 20
Number of commented issues: 42
Number of merged PRs: 14
Number of other events: 29
Number of other people's comments: 26
Number of repositories worked on: 1
```

#### Repository statistics

The `repo` command returns statistics about a specific repository in your organization, and accepts the following parameters:

* the default argument is the username you want to retrieve statistics for
* `--from` (optional), the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information:

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of participating users

For example:

```shell
$ reports-stats repo github-reports

Name: github-reports
Number of opened issues: 9
Number of opened PRs: 56
Number of commented issues: 137
Number of merged PRs: 47
Number of other events: 114
Number of participating users: 13
```

#### Project statistics

The `project` command returns statistics about a specific project in your organization, and accepts the following parameters:

* the default argument is the project you want to retrieve statistics for
* `--from` (optional), the start date of the range to retrieve data from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve data from (ISO-8601 compliant)

The data returned consists of the following information (same as for the repository):

* number of opened issues
* number of opened PRs
* number of commented issues
* number of merged PRs
* number of other events
* number of participating users


For example:

```shell
$ reports-stats project pt

Name: github-reports
Number of opened issues: 239
Number of opened PRs: 526
Number of commented issues: 2337
Number of merged PRs: 136
Number of other events: 3435
Number of participating users: 25
```
