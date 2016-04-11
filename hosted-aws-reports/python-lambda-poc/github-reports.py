import sys
import logging
import rds_config
import pymysql
##rds settings
rds_host  = "ire-mysql-github-reports.cmekjjceogeb.eu-west-1.rds.amazonaws.com"
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name
port = 3306

logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
    logger.info("Connecting to database.")
    conn = pymysql.connect(host=rds_host, port=port,user=name, passwd=password, db=db_name, connect_timeout=15)
except Exception, e:
    logger.error("ERROR: Unexpected error: Could not connect to MySql instance." + str(e))
    sys.exit()

logger.info("SUCCESS: Connection to RDS mysql instance succeeded")
def handlerReports(event, context):
    logger.info("Starting handler.")
    """
    This function fetches content from mysql RDS instance
    """

    item_count = 0

    try:
        with conn.cursor() as cur:
            cur.execute("create table IF NOT EXISTS Employee3 ( EmpID  int NOT NULL, Name varchar(255) NOT NULL, PRIMARY KEY (EmpID))")  
            cur.execute('insert into Employee3 (EmpID, Name) values(1, "Joe")')
            cur.execute('insert into Employee3 (EmpID, Name) values(2, "Bob")')
            cur.execute('insert into Employee3 (EmpID, Name) values(3, "Mary")')
            cur.execute("select * from Employee3")
            for row in cur:
                item_count += 1
                logger.info(row)
                print(row)
    finally:
        conn.close()

    return "Added %d items from RDS MySQL table" %(item_count)

#handlerReports("", "")