batch
=====

_Batch recursive mechanism based on queues and alarms to retrieve and persist historical Github data._

-----

### Abstract algorithm

The `batch` module provides generic interfaces and a concrete `Worker` to retrieve and store historical Github data in a queue/alarm based recursive
algorithm. The components used in the process are:

* a queue made of messages (atomic operations)
* an alarm system
* a worker
* a notifier

The algorithm roughly works like this (see [`BasicWorker.java`](src/main/java/com/novoda/github/reports/batch/worker/BasicWorker.java) for the actual
implementation):

The worker grabs the first element in the queue and:
* **(base step)** If there is no element in the queue, it notifies the completion
* **(recursive step)** If there is an element, it executes the main function
  - If the function returns successfully
    * It removes the first element in the queue (the processed one)
    * It adds the next "things to do" messages in the queue
    * It calls the worker again
  - If the function fails
    * If the failure is due to Github API rate limit, it creates a new alarm that will wake the worker in X seconds, where X is the "new token
    available in" time
    * If the failure is external, it calls the worker again
* If the worker was started from an alarm, delete said alarm
* Returns

### Usage as a library

Since this library only contains interfaces abstract classes, you need to implement the following classes in a separate module:

* worker classes
  - `WorkerHandler`, accepts a message read from the queue and returns a list of "next messages" that will be added to the queue
  - `WorkerHandlerService`, returns the appropriate implementation of `WorkerHandler`
  - `WorkerService`, is able to launch a new instance of a worker
* alarm classes
  - `Alarm`, holds the number of minutes until the alarm is triggered, plus the worker name to trigger
  - `AlarmService`, API to create, retrieve and delete alarms
* configuration classes
  - `Configuration`, holds the configuration structure common to each atomic operation
  - `NotifierConfiguration`, holds the (optional) notification configuration
* notification classes
  - `Notifier`, notifies completion and error events
  - `NotifierService`, returns a new instance of the `Notifier` implementation
* queue classes
  - `Queue`, API to add, read, remove messages from a queue
  - `QueueService`, API to create, read and delete queues
  - `GetRepositoriesQueueMessage`, top-level queue message to read repositories of an organisation at a given page
  - `GetIssuesQueueMessage`, queue message to read all issues of a repository at a given page
  - `GetEventsQueueMessage`, queue message to read all events of an issue at a given page
  - `GetCommentsQueueMessage`, queue message to read all comments in an issue at a given page
  - `GetReviewCommentsQueueMessage`, queue message to read all review comments in a PR at a given page

Then, to start the batch process, create a `BasicWorker` passing all of the required objects you have implemented.

See, for example. [`batch-aws](../batch-aws/README.md) for an implementation that uses Amazon AWS.
