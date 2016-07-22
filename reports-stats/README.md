reports-stats
=============

_CLI to extract statistics from your Github data._

-------------

`reports-stats` is a CLI program to perform statistics on your Github data available in a database.

### Configuration

To configure the database instance you want to connect to, put a `database.credentials` file in the resources or 
classloader accessible folder, with the following properties defined:

* `DB_USER`, username for the database instance
* `DB_PASSWORD`, password for the database instance
* `DB_CONNECTION_STRING`, JDBC connection string to the database instance

For an example, see the [`database.credentials.sample` file](src/main/resources/database.credentials.sample).

### Distribute the application

To build a zip file of the application, run the `distZip` Gradle task: this will generate a zip file in the 
`build/distributions` directory, in order to upload and re-use the CLI without having to rebuild it every time.

**Note**: distribution tasks strip out some files from the resources, so you have to re-configure your application by 
putting the proper `.credentials` files in the `lib` directory (the one containing the JAR files) in the extracted zip 
structure.

### Debug from IntelliJ IDEA

To debug the application in IDEA, simply put the credential files in the `src/main/resources` directory and create a new
Debug configuration:

1. Go to Run -> Edit Configurations...
2. Click on the + icon and select "Application" as a new configuration
3. Put the `com.novoda.github.reports.stats.Main` class as the "Main class"
4. Type **only the program arguments** in the "Program arguments" text field (e.g.
   `user frapontillo --repo github-reports --project pt --from 2016-01-01 --to 2016-12-31`)
5. Apply the changes
6. Run the created configuration

### Usage

The CLI has 6 commands available, `user`, `repo`, `project`, `pr`, `overall` and `aggregate`, that return statistics 
about different objects.

**Note**: the examples in the following sections assume that you're running the applications from the `bin` directory in
the distribution folder, where you have the `reports-stats` bash file available. If you are running from the IDE, 
remember that **`reports-stats` is not a program argument**.

#### User statistics

The `user` command returns statistics about a specific user in your organization, and accepts the following parameters:

* the default argument is the username you want to retrieve statistics for
* `--repo` (optional), the name of the repository to retrieve data from
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
$ reports-stats user frapontillo --repo github-reports --from 2016-01-01 --to 2016-12-31

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

The `repo` command returns statistics about a specific repository in your organization, and accepts the following 
parameters:

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

The `project` command returns statistics about a specific project in your organization, and accepts the following 
parameters:

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

Name: pt
Number of opened issues: 239
Number of opened PRs: 526
Number of commented issues: 2337
Number of merged PRs: 136
Number of other events: 3435
Number of participating users: 25
```

#### Pull Request statistics

The `pr` command returns statistics (optionally grouped by month or week) about different measures of contributions.
You can configure the search with:

* the default argument is the space separated list of users to retrieve statistics for (defaults to all users in the 
organisation if left empty)
* `--from` (optional), the start date of the range to retrieve statistics from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve statistics from (ISO-8601 compliant)
* `--repositories`, (optional defaults to all) list of repositories to include in the statistics
* `--groupBy`, (optional) can be `MONTH` or `WEEK`
* `--average`, (optional, defaults to not set) can be set or not

The returned statistics are groups (minimum one for no grouping) with user statistics about:

* user ID
* username
* number of merged PRS
* number of opened PRs
* number of comments by other people on user's PRs
* number of comments by user on other people's PRs
* number of comments on all PRs
* number of comments on own PRs
* average of comments by other people on user's PRs
* average of comments by user on other merged PRs
* type of user (always ORGANISATION for now)

If the `average` option is set to `true`, average measures for the organisation user will be printed as well.

```shell
$ reports-stats pr --groupBy MONTH --average

GROUP 2011-09
PullRequestStatsUser{id=1665273, username=alexstyl, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=666285, username=amlcurran, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=2678555, username=ataulm, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=246473, username=biafra23, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=655860, username=blundell, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=13833681, username=caroinberlin, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=29667, username=charroch, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=3942812, username=danybony, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=16539287, username=denis982, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1219911, username=devisnik, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=6638408, username=DigitalPencils, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=3073183, username=dominicfreeston, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=610924, username=Dorvaryn, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=2426348, username=eduardb, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=3766687, username=eduardourso, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=913571, username=Electryc, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=479277, username=florianmski, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1263058, username=fourlastor, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=16188104, username=FranAvilaNovoda, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1140238, username=frapontillo, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=797790, username=gbasile, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=7124036, username=hhaouat, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=2613297, username=Hutch4, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=6136159, username=jackSzm, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=10560214, username=joetimmins, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1104656, username=JozefCeluch, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=7768517, username=juankysoriano, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=8350018, username=leoniebrewin, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=354372, username=lgvalle, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=7858538, username=LPZilva, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=3380092, username=Mecharyry, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1069201, username=mr-archano, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1848238, username=ouchadam, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=837104, username=PaNaVTEC, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=6328929, username=qqipp, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=153802, username=rock3r, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1236394, username=ryanbateman, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=45467, username=stefanhoth, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=212528, username=takecare, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=763339, username=tasomaniac, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1046688, username=tobiasheine, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1015142, username=wltrup, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=1626673, username=xrigau, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
PullRequestStatsUser{id=6195662, username=yvettecook, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
AVERAGE EXTERNAL: 
null
AVERAGE ORGANISATION: 
PullRequestStatsUser{id=-2, username=ORGANISATION, mergedPrs=0.0000, openedPrs=0.0000, otherPeopleCommentsOnUserPrs=0.0000, userCommentsOnOtherPeoplePrs=0.0000, commentsOnAllPrs=0.0000, commentsOnOwnPrs=0.0000, averageOtherPeopleCommentsOnUserPrs=0E-8, averageUserCommentsOnMergedPrs=0E-8, type=ORGANISATION}
AVERAGE ASSIGNED: 
null

...
```

#### Overall statistics

The `overall` command returns, for each user, the list of all the assignments and the contributions divided by 
repository contributed to. You can use the following parameters:

* the default argument is the space separated list of users to retrieve statistics for (defaults to all users in the 
organisation if left empty)
* `--from` (optional), the start date of the range to retrieve assignments from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve assignments from (ISO-8601 compliant)

The data returned consists of a tree of users, projects and repositories, where repositories have the leaves:

* number of comments
* number of opened PRs
* number of merged PRs
* number of closed PRs
* number of opened issues
* number of closed issues

For example:

```shell
$ reports-stats overall frapontillo --from 2016-01-01

frapontillo was assigned to:
- "Induction" from 2016-04-18 00:00:00.0 to 2016-04-29 00:00:00.0 (repos: ), and worked on
  * "all-4"
    + 8 comments
    + 0 opened PRs
    + 0 merged PRs
    + 0 closed PRs
    + 0 opened issues
    + 0 closed issues
  * "oddschecker-android"
    + 1 comments
    + 0 opened PRs
    + 0 merged PRs
    + 0 closed PRs
    + 0 opened issues
    + 0 closed issues

...
```

#### Aggregated statistics

The `aggregate` command returns, for each user, the list of assigned projects, with the number of contributions, and the
list of repositories worked on without being assigned to them, with the number of contributions as well.
The parameters available for the command are:

* the default argument is the space separated list of users to retrieve statistics for (defaults to all users in the 
organisation if left empty)
* `--from` (optional), the start date of the range to retrieve statistics from (ISO-8601 compliant)
* `--to` (optional), the end date of the range to retrieve statistics from (ISO-8601 compliant)

The data returned is of a list of users, each one with an `assigned` and `external` contribution counts, divided by 
project and repository.
                                      
For example:

```shell
$ reports-stats aggregate frapontillo --from 2016-01-01

- frapontillo
  * assigned projects
    + R & D: Scheduled 445
    TOTAL: 445
  * external repositories
    + all-4 14
    + sqlite-provider 9
    + github-reports 111
    + merlin 7
    + aosp.changelog.to 1
    + spikes 63
    + snowy-village-wallpaper 2
    + sqlite-analyzer 29
    + kazak-android 1
    + all-4-android-tv-feature 11
    + novoda.github.io 2
    + oddschecker-android 1
    + base 26
    TOTAL: 277
```
