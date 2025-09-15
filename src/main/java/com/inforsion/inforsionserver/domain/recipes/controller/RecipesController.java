package com.inforsion.inforsionserver.domain.recipes.controller;

import com.inforsion.inforsionserver.domain.recipes.Dto.request.RecipesRequestDto;
import com.inforsion.inforsionserver.domain.recipes.Dto.response.RecipesResponseDto;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import com.inforsion.inforsionserver.domain.recipes.service.RecipesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recipes", description = "레시피 관리 API")
@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipesController {

    private final RecipesService recipesService;

    // 레시피 조회
    @Operation(
            summary = "레시피 목록 조회",
            description = "레시피를 조회하기 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<Page<RecipesResponseDto>> findRecipes(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable Integer storeId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RecipesResponseDto> response = recipesService.findRecipes(storeId, pageable);
        return ResponseEntity.ok(response);
    }

    // 레시피 상세 조회, 재료도 같이 조회 가능해야함
    @Operation(
            summary = "레시피 상세 조회",
            description = "레시피를 상세 조회하기 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipesResponseDto> getRecipeDetail(@PathVariable("recipeId") Integer recipeId){
        RecipesEntity entity = recipesService.getRecipeDetail(recipeId);
        return ResponseEntity.ok(RecipesResponseDto.fromEntity(entity));
    }


    @Operation(
            summary = "레시피 수정",
            description = "레시피를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 수정 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{recipesId}")
    public ResponseEntity<RecipesResponseDto> updateRecipe(
            @Parameter(description = "레시피 ID", required = true, example = "1")
            @PathVariable Integer recipesId,
            @Parameter(description = "레시피 수정 요청 데이터", required = true)
            @Valid @RequestBody RecipesRequestDto recipesRequestDto
    ) {
        RecipesResponseDto updated = recipesService.updateRecipe(recipesId, recipesRequestDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "레시피 삭제",
            description = "지정된 레시피를 삭제합니다. 삭제된 레시피는 복구할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "레시피 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{recipesId}")
    public ResponseEntity<Void> deleteRecipe(
            @Parameter(description = "삭제할 레시피 ID", required = true, example = "1")
            @PathVariable("recipesId") Integer recipesId
    ) {
        recipesService.deleteRecipe(recipesId);
        return ResponseEntity.noContent().build();
    }

    // 레시피 생성
    @Operation(
            summary = "레시피 생성",
            description = "레시피를 생성합니다. 매장의 주문 관리를 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "레시피 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<RecipesResponseDto> createRecipes(
            @Parameter(description = "레시피 생성 요청 데이터", required = true)
            @Valid @RequestBody RecipesRequestDto recipesRequestDto
    ) {
        RecipesResponseDto created = recipesService.createRecipes(recipesRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
