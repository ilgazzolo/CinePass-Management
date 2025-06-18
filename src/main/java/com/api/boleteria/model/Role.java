package com.api.boleteria.model.Enums;

public enum Role {
    ADMIN,
    CLIENT;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
