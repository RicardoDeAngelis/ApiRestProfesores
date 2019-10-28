package com.profesores.service;

import java.util.List;

import com.profesores.model.SocialMedia;
import com.profesores.model.TeacherSocialMedia;



public interface SocialMediaService {
	void saveSocialMedia(SocialMedia socialMedia);
	
	void deleteSocialMediaById(Long id);
	
	void updateSocialMedia(SocialMedia socialMedia);
	
	List<SocialMedia> findAllSocialMedias();
	
	SocialMedia findById(Long idSocialMedia);
	
	SocialMedia findByName(String name);
	
	TeacherSocialMedia findSocialMediaByIdAndName(Long idSocialMedia, String nickname);
	TeacherSocialMedia findSocialMediaByIdTeacherAndIdSocialMedia(Long idTeacher, Long idSocialMedia);
}
