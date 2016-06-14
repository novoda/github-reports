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

import java.time.Instant;

public class AmazonAlarmService implements AlarmService<AmazonAlarm, AmazonConfiguration> {

    private static final String MINUTE = "minute";
    private static final String MINUTES = "minutes";
    private static final String ALARM_NAME_SEPARATOR = "-";

    private final AmazonConfigurationConverter amazonConfigurationConverter;
    private final AmazonCloudWatchEventsClient amazonEventsClient;

    public static AmazonAlarmService newInstance(AmazonCredentialsService amazonCredentialsService) {
        AmazonConfigurationConverter amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();
        return new AmazonAlarmService(amazonConfigurationConverter, amazonCredentialsService);
    }

    private AmazonAlarmService(AmazonConfigurationConverter amazonConfigurationConverter, AmazonCredentialsService amazonCredentialsService) {
        this.amazonConfigurationConverter = amazonConfigurationConverter;
        AWSCredentials awsCredentials = amazonCredentialsService.getAWSCredentials();
        amazonEventsClient = new AmazonCloudWatchEventsClient(awsCredentials);
    }

    @Override
    public AmazonAlarm createAlarm(AmazonConfiguration configuration, long minutes, String workerName) {
        String alarmName = configuration.jobName() + ALARM_NAME_SEPARATOR + Instant.now().getEpochSecond();
        return AmazonAlarm.newInstance(configuration, minutes, alarmName, workerName);
    }

    @Override
    public AmazonAlarm postAlarm(AmazonAlarm alarm) throws AlarmOperationFailedException {
        try {
            saveRule(alarm);
            addTargetToRule(alarm);
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

    private void addTargetToRule(AmazonAlarm alarm) throws ConfigurationConverterException {
        PutTargetsRequest putTargetsRequest = buildPutTargetsRequest(alarm);
        amazonEventsClient.putTargets(putTargetsRequest);
    }

    private PutTargetsRequest buildPutTargetsRequest(AmazonAlarm alarm) throws ConfigurationConverterException {
        Target target = buildTarget(alarm);
        return new PutTargetsRequest()
                .withRule(getRuleName(alarm))
                .withTargets(target);
    }

    private Target buildTarget(AmazonAlarm alarm) throws ConfigurationConverterException {
        return new Target()
                .withId(getRuleName(alarm))
                .withArn(alarm.getWorkerName())
                .withInput(amazonConfigurationConverter.toJson(alarm.getConfiguration()));
    }

    private String getScheduleExpressionForMinutes(long minutes) {
        long normalizedMinutes = Math.max(minutes, 1);
        String rate = Long.toString(normalizedMinutes) + " " + (minutes > 1 ? MINUTES : MINUTE);
        return "rate(" + rate + ")";
    }

    @Override
    public AmazonAlarm removeAlarm(AmazonAlarm alarm) {
        removeTargetFromRule(alarm);
        removeRule(alarm);
        return alarm;
    }

    private void removeTargetFromRule(AmazonAlarm alarm) {
        RemoveTargetsRequest removeTargetsRequest = buildRemoveTargetsRequest(alarm);
        amazonEventsClient.removeTargets(removeTargetsRequest);
    }

    private RemoveTargetsRequest buildRemoveTargetsRequest(AmazonAlarm alarm) {
        String ruleName = getRuleName(alarm);
        return new RemoveTargetsRequest()
                .withIds(ruleName)
                .withRule(ruleName);
    }

    private void removeRule(AmazonAlarm alarm) {
        DeleteRuleRequest deleteRuleRequest = buildDeleteRuleRequest(alarm);
        amazonEventsClient.deleteRule(deleteRuleRequest);
    }

    private DeleteRuleRequest buildDeleteRuleRequest(AmazonAlarm alarm) {
        return new DeleteRuleRequest().withName(getRuleName(alarm));
    }

    private String getRuleName(AmazonAlarm alarm) {
        return alarm.getName();
    }

}
