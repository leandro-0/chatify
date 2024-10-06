package com.pucmm.assignment.chatify.core.utils;

public enum UserStatus {
    ONLINE {
        @Override
        public String toString() {
            return "online";
        }
    },
    OFFLINE {
        @Override
        public String toString() {
            return "offline";
        }
    }
}
