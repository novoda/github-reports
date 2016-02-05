package com.novoda.reports;

class Report {
    private final String user;
    private final long mergedPrs;
    private final long createdPrs;
    private final long otherPeopleComments;
    private final long usersComments;

    public Report(String user, long mergedPrs, long createdPrs, long otherPeopleComments, long usersComments) {
        this.user = user;
        this.mergedPrs = mergedPrs;
        this.createdPrs = createdPrs;
        this.otherPeopleComments = otherPeopleComments;
        this.usersComments = usersComments;
    }

    @Override
    public String toString() {
        return "User " + user + " merged " + mergedPrs + " PRs.\n" +
                "User " + user + " created " + createdPrs + " PRs.\n" +
                "People wrote " + otherPeopleComments + " comments in " + user + "'s PRs.\n" +
                "User " + user + " wrote " + usersComments + " comments in other peoples PRs.\n";
    }

    public static class Builder {
        private final String user;
        private long mergedPrs;
        private long createdPrs;
        private long otherPeopleComments;
        private long usersComments;

        public Builder(String user) {
            this.user = user;
        }

        public Builder withMergedPullRequests(long count) {
            this.mergedPrs = count;
            return this;
        }

        public Builder withCreatedPullRequests(long count) {
            this.createdPrs = count;
            return this;
        }

        public Builder withOtherPeopleCommentsCount(long count) {
            otherPeopleComments = count;
            return this;
        }

        public Builder withUsersCommentCount(long count) {
            usersComments = count;
            return this;
        }

        public Report build() {
            return new Report(user, mergedPrs, createdPrs, otherPeopleComments, usersComments);
        }
    }
}
