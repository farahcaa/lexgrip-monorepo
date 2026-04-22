package com.lexgrip.app.platform.service.application.languages;

import com.lexgrip.app.platform.service.model.common.Colors;
import com.lexgrip.app.platform.service.model.languages.LanguageDTO;
import com.lexgrip.app.platform.service.model.languages.LanguageEntity;
import com.lexgrip.app.platform.service.model.languages.LanguageRepository;
import com.lexgrip.app.platform.service.model.languages.LanguageMapper;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.common.api.model.ApiError;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageRepository languageRepository, LanguageMapper languageMapper){
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    public ApiResponse<List<LanguageDTO>> getLanguages(UserEntity user) {
        List<LanguageEntity> languageEntities = languageRepository.findAllByUser(user);

        if (languageEntities == null){
            return new ApiResponse<>(true,null,new ApiError("204","no content", HttpStatus.NO_CONTENT));
        }

        return new ApiResponse<>(true, languageMapper.toListLanguageDTO(languageEntities), null);
    }

    public ApiResponse<String> createLanguage(String languageName, UserEntity user) {
        LanguageEntity languageEntity = new LanguageEntity();
        languageEntity.setName(languageName).setUser(user);

        Colors[] values = Colors.values();
        Colors randomColor = values[new Random().nextInt(values.length)];

        languageEntity.setColor(randomColor);
      languageRepository.save(languageEntity);
      return new ApiResponse<>(true,"success",null);
    }

    public ApiResponse<String> deleteLanguage(UUID id, UserEntity user){

        LanguageEntity languageEntity = languageRepository.findByIdAndUser(id, user);
        if(languageEntity == null){
            return new ApiResponse<>(false,null,new ApiError("404","failure to delete language", HttpStatus.NOT_FOUND));
        }
        languageRepository.delete(languageEntity);
        return new ApiResponse<>(true,"success",null);
    }
}
