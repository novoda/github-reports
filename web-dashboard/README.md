web-dashboard
=============

_Angular 2 Web application that shows Github cross-project contributors and slackers._

---------------------------

**Note**: this project was generated with [angular-cli](https://github.com/angular/angular-cli) version 
1.0.0-beta.11-webpack.2.
To get more help on the `angular-cli` use `ng --help` or go check out the 
[Angular-CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

### How to develop

#### Pre-requisites                      

This project was generated with [angular-cli](https://github.com/angular/angular-cli) version 1.0.0-beta.11-webpack.2.

Simply install the CLI globally with:

```shell
$ npm install -g angular-cli@webpack
```

To get more help on the `angular-cli` use `ng --help` or go check out the 
[Angular-CLI README](https://github.com/angular/angular-cli/blob/master/README.md).


#### Setup

To install application and development dependencies, simply run `npm install`.

#### Available tasks

The available `ng` tasks are:

* `ng serve` to start a dev server. Just navigate to `http://localhost:4200/`: the app will automatically reload if you
  change any of the source files.
* `ng generate component component-name` to generate a new component. You can also use 
  `ng generate directive/pipe/service/class`.
* `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for
  a production build.
* `ng test` to execute the unit tests, code coverage and JUnit test reports via [Karma](https://karma-runner.github.io).
  Tests are executed as soon as any code changes (if you want to turn off auto watch, add `--watch false`).
* `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/). Before running the tests 
  make sure you are serving the app via `ng serve`.
* `ng github-pages:deploy` to deploy to Github Pages.

The Gradle `test` task simply delegates to `ng test --watch false` to provide compatibility with the root project.

#### Conventions

The project follows code conventions defined by the TypeScript and Angular community. Code styling is defined in
`tslint.json` and is validated automatically by the `ng` CLI.
