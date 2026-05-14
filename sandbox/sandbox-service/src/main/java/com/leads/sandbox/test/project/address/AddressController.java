package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.implService.AddressTypeServiceImpl;
import com.leads.sandbox.test.project.address.implService.CountryServiceImpl;
import com.leads.sandbox.test.project.address.implService.DivisionServiceImpl;
import com.leads.sandbox.test.project.address.query.*;
import com.leads.sandbox.test.project.address.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(
        origins = "*",
        allowCredentials = "false",
        allowedHeaders = "*",
        methods = {
                RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT,
        }
)
public class AddressController {
    private final CountryService countryService;
    private final DivisionService divisionService;
    private final DistrictService districtService;
    private final ThanaService thanaService;
    private final AddressTypeService addressTypeService;

    public AddressController(DivisionServiceImpl divisionService, CountryServiceImpl countryService, DistrictService districtService, ThanaService thanaService,  AddressTypeServiceImpl addressTypeService) {
        this.divisionService = divisionService;
        this.countryService = countryService;
        this.districtService = districtService;
        this.thanaService = thanaService;
        this.addressTypeService = addressTypeService;
    }
    @GetMapping("/types")
    public ResponseEntity<List<RetrieveAddressType>> retrieveAllTypes() {
        return ResponseEntity.ok(addressTypeService.retrieveFindAll());
    }

    @GetMapping("/countries")
    public ResponseEntity<List<RetrieveCountry>> retrieveAllCountries() {
        return ResponseEntity.ok(countryService.retrieveFindAll());
    }

    @GetMapping("/divisions/{countryId}")
    public ResponseEntity<List<RetrieveDivision>> retrieveAllDivisions(@PathVariable Long countryId) {
        return ResponseEntity.ok(divisionService.findByCountryId(countryId));
    }

    @GetMapping("/districts/{divisionId}")
    public ResponseEntity<List<RetrieveDistrict>> retrieveAllDistricts(@PathVariable Long divisionId) {
        return ResponseEntity.ok(districtService.findDistrictByDivisionId(divisionId));
    }

    @GetMapping("/thanas/{districtId}")
    public ResponseEntity<List<RetrieveThana>> retrieveAllThanas(@PathVariable Long districtId) {
        return ResponseEntity.ok(thanaService.findThanasByDistrictId(districtId));
    }

    @GetMapping("/type/{addressTypeId}")
    public ResponseEntity<RetrieveAddressType> retrieveAddressType(@PathVariable Long addressTypeId) {
        return ResponseEntity.ok(addressTypeService.retrieveAddressType(addressTypeId));
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<RetrieveCountry> retrieveCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(countryService.retrieveCountry(countryId));
    }

    @GetMapping("/division/{divisionId}")
    public ResponseEntity<RetrieveDivision> retrieveDivision(@PathVariable Long divisionId) {
        return ResponseEntity.ok(divisionService.retrieveDivision(divisionId));
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<RetrieveDistrict> retrieveDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(districtService.retrieveDistrict(districtId));
    }

    @GetMapping("/thana/{thanaId}")
    public ResponseEntity<RetrieveThana> retrieveThana(@PathVariable Long thanaId) {
        return ResponseEntity.ok(thanaService.retrieveThana(thanaId));
    }

}
