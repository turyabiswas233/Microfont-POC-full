package com.leads.sandbox.test.project.register.command;

public class RegisterClient {
    private UpdateClientDetails updateClientDetails;
    private UpdateClientAddress updateClientAddress;
    private UpdateClientAccountInfo updateClientAccountInfo;

    public UpdateClientDetails getRegisterClientDetails() {
        return updateClientDetails;
    }

    public void setRegisterClientDetails(UpdateClientDetails updateClientDetails) {
        this.updateClientDetails = updateClientDetails;
    }

    public UpdateClientAddress getRegisterClientAddress() {
        return updateClientAddress;
    }

    public void setRegisterClientAddress(UpdateClientAddress updateClientAddress) {
        this.updateClientAddress = updateClientAddress;
    }

    public UpdateClientAccountInfo getRegisterClientAccountInfo() {
        return updateClientAccountInfo;
    }

    public void setRegisterClientAccountInfo(UpdateClientAccountInfo updateClientAccountInfo) {
        this.updateClientAccountInfo = updateClientAccountInfo;
    }
}
