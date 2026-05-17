package com.leads.sandbox.test.project.register.command;

public class RegisterClient {
    private SaveClientDetails saveClientDetails;
    private SaveClientAddress saveClientAddress;
    private SaveClientAccountInfo saveClientAccountInfo;

    public SaveClientDetails getRegisterClientDetails() {
        return saveClientDetails;
    }

    public void setRegisterClientDetails(SaveClientDetails saveClientDetails) {
        this.saveClientDetails = saveClientDetails;
    }

    public SaveClientAddress getRegisterClientAddress() {
        return saveClientAddress;
    }

    public void setRegisterClientAddress(SaveClientAddress saveClientAddress) {
        this.saveClientAddress = saveClientAddress;
    }

    public SaveClientAccountInfo getRegisterClientAccountInfo() {
        return saveClientAccountInfo;
    }

    public void setRegisterClientAccountInfo(SaveClientAccountInfo saveClientAccountInfo) {
        this.saveClientAccountInfo = saveClientAccountInfo;
    }
}
