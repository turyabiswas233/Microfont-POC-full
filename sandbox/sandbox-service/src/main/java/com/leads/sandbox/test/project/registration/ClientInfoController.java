package com.leads.sandbox.test.project.registration;

import com.leads.sandbox.test.project.register.command.RegisterClient;
import com.leads.sandbox.test.project.register.command.RegisterClientId;
import com.leads.sandbox.test.project.register.command.SaveClientAccountInfo;
import com.leads.sandbox.test.project.register.command.SaveClientAddress;
import com.leads.sandbox.test.project.register.query.RetrieveClient;
import com.leads.sandbox.test.project.register.query.RetrieveClientAccountInfoList;
import com.leads.sandbox.test.project.register.query.RetrieveClientAddressList;
import com.leads.sandbox.test.project.register.query.RetrieveClientInfo;
import com.leads.sandbox.test.project.register.service.ClientService;
import com.leads.sandbox.test.project.registration.implService.ClientServiceImpl;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(
        origins = "*",
        allowCredentials = "false",
        allowedHeaders = "*",
        methods = {
                RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT,
        }
)
public class ClientInfoController {
    private final ClientService clientService;

    public ClientInfoController(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/genid")
    public ResponseEntity<RetrieveClientInfo> generateClientId(@RequestBody @Valid RegisterClientId registerClientId) {
        return ResponseEntity.ok(clientService.registerClientId(registerClientId));
    }

    @PostMapping("/{clientId}/create")
    public ResponseEntity<Boolean> createClientDetails(@PathVariable Long clientId, @RequestBody @Valid RegisterClient request) {
        boolean isClientCreated = clientService.registerFullClient(clientId, request.getRegisterClientDetails(), request.getRegisterClientAddress(), request.getRegisterClientAccountInfo());
        return ResponseEntity.ok(isClientCreated);
    }

    @PostMapping("/{clientId}/address")
    public ResponseEntity<Boolean> saveClientAddress(@PathVariable Long clientId, @RequestBody @Valid SaveClientAddress request) {
        boolean isSavedClientAddress = clientService.saveClientAddress(clientId, request);
        return ResponseEntity.ok(isSavedClientAddress);
    }
    @PostMapping("/{clientId}/account")
    public ResponseEntity<Boolean> saveClientAccountInfo(@PathVariable Long clientId, @RequestBody @Valid SaveClientAccountInfo request) {
        boolean isSavedClientAddress = clientService.saveClientAccountInfo(clientId, request);
        return ResponseEntity.ok(isSavedClientAddress);
    }

    @GetMapping
    public ResponseEntity<List<RetrieveClient>> retrieveAllClientsList() {
        return ResponseEntity.ok(clientService.retrieveAllClients());
    }


    @PutMapping("/{clientId}")
    public ResponseEntity<RetrieveClient> updateClient(@PathVariable Long clientId, @RequestBody @Valid RegisterClient request) {
        RetrieveClient rc = clientService.updateFullClient(clientId, request.getRegisterClientDetails(), request.getRegisterClientAddress(), request.getRegisterClientAccountInfo());
        return ResponseEntity.ok(rc);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<RetrieveClient> getClient(@PathVariable Long clientId) {
        RetrieveClient client = clientService.retrieveClient(clientId);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/address")
    public ResponseEntity<List<RetrieveClientAddressList>> getClientAddressList() {
        List<RetrieveClientAddressList> retrieveClientAddressLists = clientService.retrieveClientsAddresses();
        return ResponseEntity.ok(retrieveClientAddressLists);
    }

    @GetMapping("/account")
    public ResponseEntity<List<RetrieveClientAccountInfoList>> getClientAddress() {
        List<RetrieveClientAccountInfoList> retrieveClientAccountInfoList = clientService.retrieveClientsAccountInfoList();
        return ResponseEntity.ok(retrieveClientAccountInfoList);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Boolean> deleteClient(@PathVariable Long clientId) {
        boolean isClientDeleted = clientService.deleteFullClient(clientId);
        return ResponseEntity.ok(isClientDeleted);
    }

    @DeleteMapping("/{clientId}/address")
    public ResponseEntity<Boolean> deleteClientAddress(@PathVariable Long clientId) {
        boolean isClientAddressDeleted = clientService.deleteClientAddress(clientId);
        return ResponseEntity.ok(isClientAddressDeleted);
    }

    @DeleteMapping("/{clientId}/account")
    public ResponseEntity<Boolean> deleteClientAccountInfo(@PathVariable Long clientId) {
        boolean isClientAccountDeleted = clientService.deleteClientAccountInfo(clientId);
        return ResponseEntity.ok(isClientAccountDeleted);
    }

}



















