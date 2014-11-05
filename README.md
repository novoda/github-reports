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
