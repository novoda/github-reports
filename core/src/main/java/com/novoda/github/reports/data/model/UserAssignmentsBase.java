package com.novoda.github.reports.data.model;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

interface UserAssignmentsBase {

    @Nullable Date assignmentStart();

    @Nullable Date assignmentEnd();

    String assignedProject();

    List<String> assignedRepositories();

}
