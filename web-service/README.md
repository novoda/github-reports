web-service
===========

_Web Service to query for Github Reports Stats._

-----------

This Web Service is a collection of Amazon AWS Lambdas exposed through AWS API Gateway.

### Configuration

#### Local properties

Add the following files to the resources, before uploading the lambdas to Amazon AWS:

* `database.credentials` (see [sample](src/main/resources/database.credentials.sample)) with the 
  [correct DB credentials](../db-layer/README.md#configuration)
* `float.credentials` (see [sample](src/main/resources/float.credentials.sample))  with the 
  [correct Float credentials](../float/README.md#configuration)
* `projects.json` (see [sample](src/main/resources/projects.json.sample)) with the project-to-repositories mapping
* `users.json` (see [sample](src/main/resources/users.json.sample)) with the Float-to-Github username mapping

#### Amazon AWS

As a first step, install the [AWS CLI](https://aws.amazon.com/cli/) and [configure it with your credentials and region]
(http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html#cli-quick-configuration).

##### Role creation

Create a new role for your lambda with the following command:

```shell
aws iam create-role \
  --role-name github-reports-ws-role \
  --assume-role-policy-document file://assets/github-reports-ws-role.json
```

##### Deploy Lambdas

To build and deploy all lambdas, run the `uploadActionLambdas` Gradle task.

##### Create REST API

You can create the REST API with the [AWS Web UI](https://console.aws.amazon.com/apigateway), by adding resources and
methods manually.

**Important**: please note that all resources need ane `OPTIONS` method to allow CORS requests.

Please do the following for all the resources you create:

1. Select Actions -> Enable CORS.
2. Make sure that `Access-Control-Allow-Origin` is set to `'*'`.
3. Click on the "Enable CORS and replace existing CORS headers" button, then confirm.

###### /users/org

Create a `/users/org` resource and add a `GET` method to it.

In the "Integration Request" section map the endpoint to the `github-reports-users-organisation-get` lambda function.

###### /repositories

Create a `/repositories` resource and add a `GET` method to it.

In the "Integration Request" section map the endpoint to the `github-reports-repositories-get` lambda function.

###### /stats

Create a `/stats` resource.

###### /stats/pr

Create a `/pr` sub-resource and add a `GET` method to it.

In the "Method Request" add the following URL Query String Parameters:

* `from`
* `to`
* `repos`
* `groupBy`
* `withAverage`

In the "Integration Request" section map the endpoint to the `github-reports-stats-pr-get` lambda function.

In the "Integration Request" section select "When there are no templates defined (recommended)" as "Request body 
passthrough" and add `application/json` as Content-Type  with the following mapping template:

```velocity
#set($params = $input.params().querystring)
## ------------------------------------ ##
#if($params.from != "")
#set($from = """${params.from}""")
#else
#set($from = "null")
#end
## ------------------------------------ ##
#if($params.to != "")
#set($to = """${params.to}""")
#else
#set($to = "null")
#end
## ------------------------------------ ##
#if($params.repos != "")
#set($repos = $params.repos)
#else
#set($repos = "[]")
#end
## ------------------------------------ ##
#if($params.groupBy != "")
#set($groupBy = """$params.groupBy""")
#else
#set($groupBy = "null")
#end
## ------------------------------------ ##
#if($params.withAverage != "")
#set($withAverage = $params.withAverage)
#else
#set($withAverage = "false")
#end
## ------------------------------------ ##
{
    "from": $from,
    "to": $to,
    "repos": $repos,
    "groupBy": $groupBy,
    "withAverage": $withAverage
}
```               

###### /stats/aggregated

Create a `/aggregated` sub-resource and add a `GET` method to it.

In the "Method Request" add the following URL Query String Parameters:

* `from`
* `to`
* `users`

In the "Integration Request" section map the endpoint to the `github-reports-stats-aggregated-get` lambda function.

In the "Integration Request" section select "When there are no templates defined (recommended)" as "Request body 
passthrough" and add `application/json` as Content-Type  with the following mapping template:

```velocity
#set($params = $input.params().querystring)
## ------------------------------------ ##
#if($params.from != "")
#set($from = """${params.from}""")
#else
#set($from = "null")
#end
## ------------------------------------ ##
#if($params.to != "")
#set($to = """${params.to}""")
#else
#set($to = "null")
#end
## ------------------------------------ ##
#if($params.users != "")
#set($users = $params.users)
#else
#set($users = "[]")
#end

## ------------------------------------ ##
{
    "from": $from,
    "to": $to,
    "users": $users
}
```

#### Deploy

Use the [API Gateway Web console](https://console.aws.amazon.com/apigateway) to ship your Web Service to
staging/production and use the provided base URL to access the deployed endpoints.
