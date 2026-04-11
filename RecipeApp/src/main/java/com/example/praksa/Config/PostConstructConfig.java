package com.example.praksa.Config;

import com.example.praksa.Models.*;
import com.example.praksa.Repositories.postgres.IngredientRepository;
import com.example.praksa.Repositories.postgres.RecipeCategoryRepository;
import com.example.praksa.Repositories.postgres.RecipeRepository;
import com.example.praksa.Repositories.postgres.RoleRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import com.example.praksa.Repositories.neo4j.UserNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PostConstructConfig {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAppRepository userAppRepository;
    private final UserNodeRepository userNodeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeRepository recipeRepository;

    public PostConstructConfig(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserAppRepository userAppRepository, UserNodeRepository userNodeRepository, IngredientRepository ingredientRepository, RecipeCategoryRepository recipeCategoryRepository, RecipeRepository recipeRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAppRepository = userAppRepository;
        this.userNodeRepository = userNodeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.recipeRepository = recipeRepository;
    }
    @PostConstruct
    public void create(){
        if(roleRepository.findAll().isEmpty()){
            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");
            roleRepository.save(roleUser);
            roleRepository.save(roleAdmin);

            UserApp admin1 = new UserApp("Jovan", "Jovanovic","admin@gmail.com", passwordEncoder.encode("admin"),123 , Gender.MALE,new Adress("Grobljanska1","Ruma","Srbija"),true);
            admin1.setRole(roleAdmin);
            userAppRepository.save(admin1);
            UserNode userNode = new UserNode("Jovan", "Jovanovic","admin@gmail.com", passwordEncoder.encode("admin"),123);
            userNodeRepository.save(userNode);


            UserApp user1 = new UserApp("Nikola", "Slavnic","nikola@gmail.com", passwordEncoder.encode("nikola"),123 , Gender.MALE,new Adress("Grobljanska2","Ruma","Srbija"),true);
            user1.setRole(roleUser);
            UserNode userNode1 = new UserNode("Nikola", "Slavnic","nikola@gmail.com",passwordEncoder.encode("nikola"),123);
            userAppRepository.save(user1);
            userNodeRepository.save(userNode1);
        }

        Ingredient ingredient1 = new Ingredient("so");
        Ingredient ingredient2 = new Ingredient("biber");
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);

        RecipeCategory recipeCategory1 = new RecipeCategory("Italian","Food originating from Italian cuisine");
        RecipeCategory recipeCategory2 = new RecipeCategory("Indian","Food originating from Indian cuisine");
        recipeCategoryRepository.save(recipeCategory1);
        recipeCategoryRepository.save(recipeCategory2);

        if (recipeRepository.findByName("Spaghetti Bolognese") == null) {
            Ingredient salt = ingredientRepository.findByName("so");
            Ingredient pepper = ingredientRepository.findByName("biber");
            RecipeCategory italian = recipeCategoryRepository.getByName("Italian");

            Recipe spaghettibolognese = new Recipe();
            spaghettibolognese.setName("Spaghetti Bolognese");
            spaghettibolognese.setPreparation("Cook spaghetti according to package directions. Brown ground beef in a pan, add tomato sauce, simmer for 20 minutes. Season and serve over pasta.");
            spaghettibolognese.setServings("4");
            spaghettibolognese.setRecipeCategory(italian);
            spaghettibolognese.setCreatedDateTime(LocalDateTime.now());
            spaghettibolognese.addRecipeIngredient(new RecipeIngredient("1 tsp", salt, spaghettibolognese));
            spaghettibolognese.addRecipeIngredient(new RecipeIngredient("1/2 tsp", pepper, spaghettibolognese));
            recipeRepository.save(spaghettibolognese);
        }

        if (recipeRepository.findByName("Chicken Tikka Masala") == null) {
            Ingredient salt = ingredientRepository.findByName("so");
            Ingredient pepper = ingredientRepository.findByName("biber");
            RecipeCategory indian = recipeCategoryRepository.getByName("Indian");

            Recipe chickenTikkaMasala = new Recipe();
            chickenTikkaMasala.setName("Chicken Tikka Masala");
            chickenTikkaMasala.setPreparation("Marinate chicken in yogurt and spices, grill until charred. Simmer in a creamy tomato-based sauce with garlic, ginger, and garam masala. Serve with rice or naan.");
            chickenTikkaMasala.setServings("4");
            chickenTikkaMasala.setRecipeCategory(indian);
            chickenTikkaMasala.setCreatedDateTime(LocalDateTime.now());
            chickenTikkaMasala.addRecipeIngredient(new RecipeIngredient("1 tsp", salt, chickenTikkaMasala));
            chickenTikkaMasala.addRecipeIngredient(new RecipeIngredient("1 tsp", pepper, chickenTikkaMasala));
            recipeRepository.save(chickenTikkaMasala);
        }

    }
}
