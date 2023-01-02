package com.example.catalogservice.controller;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/catalog-service")
@Controller
@Slf4j
public class CatalogController {

    private CatalogService catalogService;
    private Environment env;

    public CatalogController(CatalogService catalogService, Environment env) {
        this.catalogService = catalogService;
        this.env = env;
    }
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s",env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getCatalogs() {
        List<CatalogEntity> catalogs = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        catalogs.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
