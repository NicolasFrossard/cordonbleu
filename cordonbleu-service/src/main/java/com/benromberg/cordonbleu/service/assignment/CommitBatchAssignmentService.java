package com.benromberg.cordonbleu.service.assignment;

import com.benromberg.cordonbleu.data.model.Commit;
import com.benromberg.cordonbleu.data.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommitBatchAssignmentService {

    public List<CommitBatchAssignment> generateCommitBatchAssignments(List<Commit> commits, List<User> users) {
        UsersRandomLoopIterator usersIterator = new UsersRandomLoopIterator(users);

        return commits.stream()
                .collect(Collectors.groupingBy(commit -> commit.getAuthor().getEmail()))
                .entrySet()
                .stream()
                .map(entry -> toCommitBatchAssignment(usersIterator.getNext(), entry))
                .collect(Collectors.toList());
    }

    private CommitBatchAssignment toCommitBatchAssignment(User user, Map.Entry<String, List<Commit>> entry) {
        return new CommitBatchAssignment(user, entry.getValue().iterator().next().getAuthor(), entry.getValue());
    }

    private class UsersRandomLoopIterator {
        List<User> shuffledUsers;
        Iterator<User> iterator;

        private UsersRandomLoopIterator(List<User> users) {
            shuffledUsers = new ArrayList<>(users);
            Collections.shuffle(shuffledUsers);
            iterator = shuffledUsers.iterator();
        }

        private User getNext() {
            if (!iterator.hasNext()) {
                iterator = shuffledUsers.iterator();
            }
            return iterator.next();
        }
    }
}
