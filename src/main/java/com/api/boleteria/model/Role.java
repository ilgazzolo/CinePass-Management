package com.api.boleteria.model;

public enum Role {
    ADMIN,
    CLIENT;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
