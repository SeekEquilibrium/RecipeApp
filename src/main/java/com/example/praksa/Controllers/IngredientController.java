package com.example.praksa.Controllers;

import com.example.praksa.DTOs.IngredientDTO;
import com.example.praksa.Models.Ingredient;
import com.example.praksa.Repositories.IngredientRepository;
import com.example.praksa.Services.IngredientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/ingredient")
public class IngredientController {

    private final IngredientService ingredientService ;


    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Create a new ingredient",httpMethod = "POST")
    public ResponseEntity<Ingredient> addIngredient(String requestIngredient) throws Exception {
        Ingredient ingredient = ingredientService.create(requestIngredient);
        return new ResponseEntity<>(ingredient, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "Gets a list of all ingredients",httpMethod = "GET")
    public ResponseEntity<?> getAllIngredients() throws Exception{
        List<IngredientDTO> ingredientList = ingredientService.getAll();
        return ResponseEntity.ok(ingredientList);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Deletes an ingredient with the given id",httpMethod = "DELETE")
    public ResponseEntity<?> deleteIngredient(@PathVariable Long id) throws Exception {
        ingredientService.deleteIngredient(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
