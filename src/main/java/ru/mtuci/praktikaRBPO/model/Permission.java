package ru.mtuci.praktikaRBPO.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    READ("read"),
    MODIFICATION("modification");

    private final String permission;

    @Override
    public String toString() {
        return permission;
    }
}
