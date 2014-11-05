# github-reports

Gets info from Github!

## Requirements

 * Ruby. Should work with versions 1.9.2, 1.9.3, 2.0.0, 2.1.0 - But has only been tested with 2.0.0
 * Bundle. Install via `gem install bundler`. Tested with 1.7.4
 * MongoDB. Install from the website http://www.mongodb.org/downloads or via brew `brew install mongodb`. Tested with 2.6.5
 * Set up mongodb: Make an empty folder in `/data/db` and change the owner to be not root. This will be different depending on the OS you're running on.
 * Optional: Atom editor with the Script package to run the ruby scripts from inside Atom.

## Usage

 * Clone the project and install all the necessary stuff mentioned in the `Requirements` section.
 * Install all the project dependencies using `bundle install` from the project root.
 * Run mongodb in a shell `mongod`
 * Run the `reports.rb` script.

  - Via command line `ruby reports.rb`.
  - You can use Atom's Script package via the app menu: Packages > Script > Run Script. Make sure the language is set as `Ruby` and not `Ruby on Rails`.

 * Profit!

## Customisation

At the moment because the API might change command line arguments aren't supported. In order to get different reports, just modify the `reports.rb` script to get the data you're interested in.

The API returns collections of maps with all the fields that the Github API supports.

## Under the hood

This application uses Github's octokit.rb library to interact with the Github API and the persistence layer uses mongoDB to store and retrieve data.

Here's how the data flow works:

 * Whenever you ask for info about repos, we fetch all the basic data from the organisation you ask for. This includes a list of all the repos in the organisation and all the Pull Requests for all the repositories. This gets persisted the first time so that the next time you don't need to download this data again.

 * Then on each query for repositories we load the full list of repositories into memory and do the filtering against that list.

 * The same way on each query for Pull Requests for a repository, the full list of Pulls is loaded and filtered in memory. This is a bit expensive because there might be lots of Pulls in all the repos of an organisation, but the API doesn't allow filtering via query parameters so for now this does the trick.

 * Review Comments work differently, they're loaded on-demand. This means they won't be downloaded from the API unless you ask for them. Because the API doesn't have proper filtering, we can't load only what we need so when you ask for comments, we have to download and store locally all the comments in a Pull Request and same as we do with repos and Pulls, we filter them in memory. This means this are heavy requests, especially if you're asking for comments in a big list of Pulls. As mentioned before, the list of comments for a Pull Request gets stored locally, so if they are already cached, they load from the database instead of the Internet.

 * Some API calls are paginated, but luckily enough octokit.rb can hide that by setting the `auto_paginate` property to true, so we take advantage of that.

---

Here are the APIs we use (so you can find the fields each object has if you need a specific field):

 * Repos: https://developer.github.com/v3/repos/#list-organization-repositories
 * Pull Requests: https://developer.github.com/v3/pulls/#list-pull-requests
 * Review Comments: https://developer.github.com/v3/pulls/comments/#list-comments-on-a-pull-request


## Limitations

 * There's a limit of 5000 requests per hour and it's pretty easy to hit that limit, specially when querying for comments in a large number of PRs. More info here: https://developer.github.com/changes/2012-10-14-rate-limit-changes/

 * The data isn't cleared from the database so it must be flushed manually. This means if today there's a new repo but you're using data from the database from yesterday, you won't see the repository in the reports until you clear the database.
