package com.benromberg.cordonbleu.data.dao;

import java.util.List;
import java.util.Optional;

import com.benromberg.cordonbleu.data.model.CodeRepositoryMetadata;
import com.benromberg.cordonbleu.data.model.CommitAuthor;
import com.benromberg.cordonbleu.data.model.Team;
import com.benromberg.cordonbleu.data.model.User;

public class CommitFilter {
    private final Team team;
    private final List<CodeRepositoryMetadata> repositories;
    private final List<CommitAuthor> authors;
    private final List<User> users;
    private final boolean approved;
    private final Optional<String> lastCommitHash;
    private final Optional<String> fetchedAfterCommitHash;
    private final int limit;
    private final Optional<User> assignedTo;

    public CommitFilter(Team team, List<CodeRepositoryMetadata> repositories, List<CommitAuthor> authors, List<User> users,
            boolean approved, Optional<String> lastCommitHash, Optional<String> fetchedAfterCommitHash, int limit,
            Optional<User> assignedTo) {
        this.team = team;
        this.repositories = repositories;
        this.authors = authors;
        this.users = users;
        this.approved = approved;
        this.lastCommitHash = lastCommitHash;
        this.fetchedAfterCommitHash = fetchedAfterCommitHash;
        this.limit = limit;
        this.assignedTo = assignedTo;
    }

    public Team getTeam() {
        return team;
    }

    public List<CodeRepositoryMetadata> getRepositories() {
        return repositories;
    }

    public List<CommitAuthor> getAuthors() {
        return authors;
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean isApproved() {
        return approved;
    }

    public Optional<String> getLastCommitHash() {
        return lastCommitHash;
    }

    public Optional<String> getFetchAfterCommitHash() {
        return fetchedAfterCommitHash;
    }

    public int getLimit() {
        return limit;
    }

    public Optional<User> getAssignedTo() {
        return assignedTo;
    }
}