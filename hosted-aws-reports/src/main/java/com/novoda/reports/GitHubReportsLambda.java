package com.novoda.reports;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.*;

public class GitHubReportsLambda implements RequestHandler<GitHubReportsLambda.LambdaRequest, GitHubReportsLambda.LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(LambdaRequest input, Context context) {
        NewLineLogger logger = new NewLineLogger(context.getLogger());
        logger.log("start");

        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            String protocol = "jdbc:mysql://";
            String endpoint = "ire-mysql-github-reports.cmekjjceogeb.eu-west-1.rds.amazonaws.com";
            String databaseName = "githubreportsdb";
            int port = 3306;
            String url = protocol + endpoint + ":" + port + "/" + databaseName;
            String user = "halreports";
            String password = "novoda951";
            connection = DriverManager.getConnection(url, user, password);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // Reflection
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            // DB
            e.printStackTrace();
            throw new IllegalStateException(e);
        }

        int itemCount = 0;
        try {
            Statement statement = connection.createStatement();

            statement.execute("create table IF NOT EXISTS Employee3 ( EmpID  int NOT NULL, Name varchar(255) NOT NULL, PRIMARY KEY (EmpID))");
            statement.execute("insert into Employee3 (EmpID, Name) values(1, \"Joe\")");
            statement.execute("insert into Employee3 (EmpID, Name) values(2, \"Bob\")");
            statement.execute("insert into Employee3 (EmpID, Name) values(3, \"Mary\")");

            ResultSet resultSet = statement.executeQuery("select * from Employee3");
            while (resultSet.next()) {
                itemCount++;
                String id = resultSet.getString("EmpID");
                String name = resultSet.getString("Name");
                logger.log(id + " " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(e.toString());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new LambdaResponse("Added " + itemCount + " items from RDS MySQL table");
    }

    public static class LambdaRequest {

        public LambdaRequest() {

        }
    }

    public static class LambdaResponse {
        private String result;

        public LambdaResponse(String result) {
            this.result = result;
        }

        public LambdaResponse() {

        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }
}
