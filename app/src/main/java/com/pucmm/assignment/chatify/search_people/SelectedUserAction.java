package com.pucmm.assignment.chatify.search_people;

@FunctionalInterface
public interface SelectedUserAction {
    void mark(boolean checked, String email);
}
