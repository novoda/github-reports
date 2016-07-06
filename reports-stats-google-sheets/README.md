reports-stats-google-sheets
===========================

_Google Sheets add-on to execute Pull Request statistics in spreadsheets._

---------------------------

`reports-stats-google-sheets` is a Google Spreadsheet add-on that enables you to request parametrised Pull Request
statistics from a Google Sheet.

### How to develop

#### Pre-requisites

This module is written in JavaScript using [Gulp](http://gulpjs.com/) as a task runner,
[Karma](https://karma-runner.github.io/1.0/index.html) as a test runner and
[NodeJS](https://nodejs.org/en/) (v4.x LTS or later) as the execution environment.

Once you have installed NodeJS, navigate to the `reports-stats-google-sheets` directory and install all of the Node
dependencies:

```shell
npm install gulp-cli -g
npm install
```

To deploy the scripts on Google Drive, we use
[`node-google-apps-script`](https://github.com/danthareja/node-google-apps-script) that you must install with:

```shell
npm install -g node-google-apps-script
```

#### Configuration

For the first time, you need to create a Google Apps Script project:

1. Go to [https://script.google.com](https://script.google.com) and create a new project
2. Save the new project by giving it a name
3. The project will be saved in your Google Drive, with a URL like `https://script.google.com/a/macros/novoda.com/d/THIS_IS_THE_PROJECT_ID/edit`
4. Follow the instructions in the [`gapps`] (https://github.com/danthareja/node-google-apps-script#11-default-apps-script-developer-console-project)
   to download the credentials JSON
5. Authenticate `gapps` following the [provided instructions](https://github.com/danthareja/node-google-apps-script#2-authenticate-gapps)
   using the credentials JSON you just downloaded
6. Initialize the project using the ID of the Google Apps Script project we found at step 3
   ([detailed instructions here](https://github.com/danthareja/node-google-apps-script#31-an-existing-apps-script-project))

Now you're ready to develop on this project!

#### Available tasks

The available Gulp tasks are:
* `lint`, runs ESLint on the codebase, highlighting any syntax errors/warnings
* `karma`, runs all the tests (specs) in the codebase using PhantomJS (you can re-configure it for any other browser)
  and generates code coverage as an HTML website and as a Cobertura XML file
* `test`, runs `lint` and `karma` in parallel
* `upload`, executes the tests and then uploads the new project files to Google Drive

For example, to run the tests, execute the following in your command line:

```shell
$ gulp test
[16:55:31] Using gulpfile ~/Workspace/github-reports/reports-stats-google-sheets/gulpfile.js
[16:55:31] Starting 'lint'...
[16:55:31] Starting 'karma'...
05 07 2016 16:55:31.256:WARN [karma]: Port 9876 in use
05 07 2016 16:55:31.258:INFO [karma]: Karma v0.13.22 server started at http://localhost:9877/
05 07 2016 16:55:31.261:INFO [launcher]: Starting browser PhantomJS
[16:55:31] Finished 'lint' after 868 ms
05 07 2016 16:55:32.072:INFO [PhantomJS 2.1.1 (Mac OS X 0.0.0)]: Connected on socket /#OsvRgwC42JixSzO1AAAA with id 64783668
PhantomJS 2.1.1 (Mac OS X 0.0.0): Executed 19 of 19 SUCCESS (0.024 secs / 0.018 secs)
[16:55:32] Finished 'karma' after 1.08 s
[16:55:32] Starting 'test'...
[16:55:32] Finished 'test' after 50 μs
```

**Note**: to provide consistency with the Gradle build pipeline, `test` and `build` Gradle tasks have been added to this
project as well, but they both simply delegate work to `gulp test`. This allows for automatic integration in the CI.

#### Project structure

The projects is divided into the following directories:

* `src`, contains all the `js` files that will be uploaded to Google Apps Script (**important**: do not use NodeJS
modules here, since they won't be available on Apps Script)
* `test`, contains mock files and specs (tests) for the relevant modules that we want to test and mock

#### Dependency Injection

Every file usually represents a class, whose constructor accepts all of its dependencies, so we can easily mock them in
tests. Please always adhere to this pattern in order to avoid deploying and manually tests basic behaviours on Apps
Script.

### How to use

TODO once flow and sheets are finalized.
