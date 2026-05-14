package com.leads.sandbox.test.project.register.command;

public class RegisterClient {
    private RegisterClientDetails registerClientDetails;
    private RegisterClientAddress registerClientAddress;
    private RegisterClientAccountInfo registerClientAccountInfo;

    public RegisterClientDetails getRegisterClientDetails() {
        return registerClientDetails;
    }

    public void setRegisterClientDetails(RegisterClientDetails registerClientDetails) {
        this.registerClientDetails = registerClientDetails;
    }

    public RegisterClientAddress getRegisterClientAddress() {
        return registerClientAddress;
    }

    public void setRegisterClientAddress(RegisterClientAddress registerClientAddress) {
        this.registerClientAddress = registerClientAddress;
    }

    public RegisterClientAccountInfo getRegisterClientAccountInfo() {
        return registerClientAccountInfo;
    }

    public void setRegisterClientAccountInfo(RegisterClientAccountInfo registerClientAccountInfo) {
        this.registerClientAccountInfo = registerClientAccountInfo;
    }
}
