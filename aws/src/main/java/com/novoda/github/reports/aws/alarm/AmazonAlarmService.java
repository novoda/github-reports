package com.novoda.github.reports.aws.alarm;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClient;
import com.amazonaws.services.cloudwatchevents.model.DeleteRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.RemoveTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.amazonaws.services.cloudwatchevents.model.Target;
import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.AmazonConfigurationConverter;
import com.novoda.github.reports.aws.configuration.ConfigurationConverterException;
import com.novoda.github.reports.aws.credentials.AmazonCredentialsService;
import com.novoda.github.reports.util.SystemClock;

public class AmazonAlarmService implements AlarmService<AmazonAlarm, AmazonConfiguration> {

    private static final String MINUTE = "minute";
    private static final String MINUTES = "minutes";
    private static final String ALARM_NAME_SEPARATOR = "-";

    private final AmazonConfigurationConverter amazonConfigurationConverter;
    private final AmazonCloudWatchEventsClient amazonEventsClient;
    private final SystemClock systemClock;

    public static AmazonAlarmService newInstance(AmazonCredentialsService amazonCredentialsService) {
        AWSCredentials awsCredentials = amazonCredentialsService.getAWSCredentials();
        AmazonConfigurationConverter amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();
        AmazonCloudWatchEventsClient amazonEventsClient = new AmazonCloudWatchEventsClient(awsCredentials);
        SystemClock systemClock = SystemClock.newInstance();
        return new AmazonAlarmService(amazonConfigurationConverter, amazonEventsClient, systemClock);
    }

    private AmazonAlarmService(AmazonConfigurationConverter amazonConfigurationConverter,
                               AmazonCloudWatchEventsClient amazonEventsClient,
                               SystemClock systemClock) {
        this.amazonConfigurationConverter = amazonConfigurationConverter;
        this.amazonEventsClient = amazonEventsClient;
        this.systemClock = systemClock;
    }

    @Override
    public AmazonAlarm createNewAlarm(long minutes, String jobName, String workerName) {
        String alarmName = createNewAlarmName(jobName);
        return AmazonAlarm.newInstance(minutes, alarmName, workerName);
    }

    private String createNewAlarmName(String jobName) {
        return jobName + ALARM_NAME_SEPARATOR + systemClock.currentTimeSeconds();
    }

    @Override
    public AmazonAlarm postAlarm(AmazonAlarm alarm, AmazonConfiguration configuration) throws AlarmOperationFailedException {
        try {
            saveRule(alarm);
            addTargetToRule(alarm, configuration);
        } catch (Exception e) {
            throw new AlarmOperationFailedException("postAlarm", e);
        }
        return alarm;
    }

    private void saveRule(AmazonAlarm alarm) {
        PutRuleRequest putRuleRequest = buildPutRuleRequest(alarm);
        amazonEventsClient.putRule(putRuleRequest);
    }

    private PutRuleRequest buildPutRuleRequest(AmazonAlarm alarm) {
        return new PutRuleRequest()
                .withName(getRuleName(alarm))
                .withScheduleExpression(getScheduleExpressionForMinutes(alarm.getMinutes()))
                .withState(RuleState.ENABLED);
    }

    private void addTargetToRule(AmazonAlarm alarm, AmazonConfiguration configuration) throws ConfigurationConverterException {
        PutTargetsRequest putTargetsRequest = buildPutTargetsRequest(alarm, configuration);
        amazonEventsClient.putTargets(putTargetsRequest);
    }

    private PutTargetsRequest buildPutTargetsRequest(AmazonAlarm alarm, AmazonConfiguration configuration) throws ConfigurationConverterException {
        Target target = buildTarget(alarm, configuration);
        return new PutTargetsRequest()
                .withRule(getRuleName(alarm))
                .withTargets(target);
    }

    private Target buildTarget(AmazonAlarm alarm, AmazonConfiguration configuration) throws ConfigurationConverterException {
        return new Target()
                .withId(getRuleName(alarm))
                .withArn(alarm.getWorkerName())
                .withInput(amazonConfigurationConverter.toJson(configuration));
    }

    private String getScheduleExpressionForMinutes(long minutes) {
        long normalizedMinutes = Math.max(minutes, 1);
        String rate = Long.toString(normalizedMinutes) + " " + (minutes > 1 ? MINUTES : MINUTE);
        return "rate(" + rate + ")";
    }

    @Override
    public AmazonAlarm removeAlarm(AmazonAlarm alarm) {
        String alarmName = alarm.getName();
        removeAlarm(alarmName);
        return alarm;
    }

    @Override
    public String removeAlarm(String alarmName) {
        removeTargetFromRule(alarmName);
        removeRule(alarmName);
        return alarmName;
    }

    private void removeTargetFromRule(String alarmName) {
        RemoveTargetsRequest removeTargetsRequest = buildRemoveTargetsRequest(alarmName);
        amazonEventsClient.removeTargets(removeTargetsRequest);
    }

    private RemoveTargetsRequest buildRemoveTargetsRequest(String alarmName) {
        return new RemoveTargetsRequest()
                .withIds(alarmName)
                .withRule(alarmName);
    }

    private void removeRule(String alarmName) {
        DeleteRuleRequest deleteRuleRequest = buildDeleteRuleRequest(alarmName);
        amazonEventsClient.deleteRule(deleteRuleRequest);
    }

    private DeleteRuleRequest buildDeleteRuleRequest(String alarmName) {
        return new DeleteRuleRequest().withName(alarmName);
    }

    private String getRuleName(AmazonAlarm alarm) {
        return alarm.getName();
    }

}
