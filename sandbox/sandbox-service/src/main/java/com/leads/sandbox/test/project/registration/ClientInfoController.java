package com.leads.sandbox.test.project.registration;

import com.leads.sandbox.test.project.register.command.RegisterClient;
import com.leads.sandbox.test.project.register.command.RegisterClientId;
import com.leads.sandbox.test.project.register.query.RetrieveClient;
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

    @GetMapping
    public ResponseEntity<List<RetrieveClient>> retrieveAllClientsList() {
        return ResponseEntity.ok(clientService.retrieveAllClients());
    }

    @PostMapping("/{clientId}/create")
    public ResponseEntity<Boolean> createClientDetails(@PathVariable Long clientId, @RequestBody @Valid RegisterClient request) {
        boolean isClientCreated = clientService.registerFullClient(clientId, request.getRegisterClientDetails(), request.getRegisterClientAddress(), request.getRegisterClientAccountInfo());
        return ResponseEntity.ok(isClientCreated);
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
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Boolean> deleteClient(@PathVariable Long clientId) {
        boolean isClientDeleted = clientService.deleteFullClient(clientId);
        return ResponseEntity.ok(isClientDeleted);
    }

}



















