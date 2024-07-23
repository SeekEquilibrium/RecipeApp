package com.example.praksa.Controllers;

import com.example.praksa.DTOs.CountCategoryDTO;
import com.example.praksa.DTOs.RecipeResponseDTO;
import com.example.praksa.Services.RecipeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    private  final RecipeService recipeService;

    public AdminController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping(value = "/countAllCategories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Retrieves name and count of  recipes for all categories ",httpMethod = "GET")
    public ResponseEntity<?> countCategoryAll(){
        List<CountCategoryDTO> list = recipeService.countCategoryAll();
        return ResponseEntity.ok(list);
    }
    @GetMapping(value = "/countSpecificCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Retrieves the number of recipes for specific category ",httpMethod = "GET")
    public ResponseEntity<CountCategoryDTO> countCategorySpecific(@RequestParam String categoryName) throws Exception {
        CountCategoryDTO response = recipeService.countCategorySpecific(categoryName);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/countSpecificDate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Retrieves the number of recipes created on the specific date",httpMethod = "GET")
    public ResponseEntity<?> countDateSpecific(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        Integer response=recipeService.countDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/retriveSpecificDate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Retrieves  recipes created on the specific date",httpMethod = "GET")
    public ResponseEntity<?> retrieveDateSpecific(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        List<RecipeResponseDTO> recipeList=recipeService.retrieveDate(date);
        return ResponseEntity.ok(recipeList);
    }

}
