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

##### Google Apps Script

For the first time, you need to create a Google Apps Script project:

1. Go to [https://script.google.com](https://script.google.com) and create a new project
2. Save the new project by giving it a name
3. The project will be saved in your Google Drive, with a URL like `https://script.google.com/a/macros/novoda.com/d/THIS_IS_THE_PROJECT_ID/edit`
4. Follow the instructions in the [`gapps`] (https://github.com/danthareja/node-google-apps-script#11-default-apps-script-developer-console-project)
   to download the credentials JSON
5. Authenticate `gapps` following the [provided instructions](https://github.com/danthareja/node-google-apps-script#2-authenticate-gapps)
   using the credentials JSON you just downloaded
6. Initialize the project using the ID of the Google Apps Script project we found at step 3 and the `-s` parameter set
   to the `build` directory
   ([detailed instructions here](https://github.com/danthareja/node-google-apps-script#31-an-existing-apps-script-project))

Now you're ready to develop on this project!

##### Local configuration

To configure the plugin, you need to create a `src/config.json` (see [`src/config.sample.json`]
(src/config.sample.json)) containing an `api` attribute that points to the Web Service.

#### Available tasks

The available Gulp tasks are:
* `lint`, runs ESLint on the codebase, highlighting any syntax errors/warnings
* `karma`, runs all the tests (specs) in the codebase using PhantomJS (you can re-configure it for any other browser)
  and generates code coverage as an HTML website and as a Cobertura XML file
* `test`, runs `lint` and `karma` in parallel
* `build`, executes the tests and builds the uploadable project
* `upload`, builds the project and then uploads it to Google Drive
* `serve`, builds a development version of the project and starts a live reload server to test UI

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

The projects `src` folder is divided into the following directories:

* `shared`, contains all the `js` files that will be shared across `web` and `plugin`
* `plugin` contains all the `js` files that will be uploaded to Google Apps Script (**important**: do not use NodeJS
modules here, since they won't be available on Apps Script)
* `web`, contains pages (templates and partials) that will be compiled and sent to Google Apps Script

#### Dependency Injection

Every file usually represents a class, whose constructor accepts all of its dependencies, so we can easily mock them in
tests. Please always adhere to this pattern in order to avoid deploying and manually tests basic behaviours on Apps
Script.

### How to use

TODO once flow and sheets are finalized.
