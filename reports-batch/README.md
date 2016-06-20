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

#### Amazon (AWS only)

To be able to use the Amazon AWS, you must put a `amazon.credentials` file in the resources or classloader accessible folder, with the following
properties defined:

* `AWS_ACCESS_KEY_ID`, access key ID for your IAM user
* `AWS_SECRET_ACCESS_KEY`, secret access key for your IAM user
For an example, see the [`amazon.credentials.sample` file](src/main/resources/amazon.credentials.sample).

#### Email (AWS only)

In order to notify the completion and erroring of jobs, you must put a `email.credentials` file in the resources or classloader accessible folder,
with the following properties defined:

* `EMAIL_HOST`, the SMTP server host
* `EMAIL_PORT`, the SMTP server port
* `EMAIL_USE_SSL`, whether to use SSL or not (`true` if you want to use SSL, any different value otherwise)
* `EMAIL_FROM`, the email you want to send notifications from
* `EMAIL_USERNAME`, the username for the sender email
* `EMAIL_PASSWORD`, the password for the sender email

### Distribute the application

To build a zip file of the application, run the `distZip` Gradle task: this will generate a zip file in the `build/distributions` directory, in order
to upload and re-use the CLI without having to rebuild it every time.

**Note**: distribution tasks strip out some files from the resources, so you have to re-configure your application by putting the proper
`.credentials` files in the `lib` directory (the one containing the JAR files) in the extracted zip structure.

### Debug from IntelliJ IDEA

To debug the application in IDEA, simply put the credential files in the `src/main/resources` directory and create a new Debug configuration:

1. Go to Run -> Edit Configurations...
2. Click on the + icon and select "Application" as a new configuration
3. Put the `com.novoda.github.reports.batch.Main` class as the "Main class"
4. Type **only the program arguments** in the "Program arguments" text field (e.g. `novoda --from 2016-01-01`)
5. Apply the changes
6. Run the created configuration

### Usage

The CLI has 2 commands available, `local` and `aws`, that execute the batch process locally or on Amazon AWS.

**Note**: the examples in the following sections assume that you're running the applications from the `bin` directory in the distribution folder,
where you have the `reports-batch` bash file available. If you are running from the IDE, remember that **`reports-batch` is not a program argument**.

#### Local execution

To use `reports-batch`, simply run:

```shell
$ reports-batch local your-organization-name --from 2016-01-01
```

The `from` parameter is optional and can be specified as an ISO-8601 date/time string.

The program may temporarily halt if the Github API calls reach a predefined rate limit, resuming on its own after said limit gets reset.

#### Execution on Amazon AWS

To use `reports-batch`, simply run:

```shell
$ reports-batch aws your-organization-name --from 2016-01-01 --email carl@novoda.com franceso@novoda.com
```

The `from` parameter is optional and can be specified as an ISO-8601 date/time string.

The program will delegate the job to AWS and report that it has correctly started, then it will exit. You will receive a completion notification on
the specified emails, if provided.
