package com.profesores.cont;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.profesores.model.Course;
import com.profesores.model.SocialMedia;
import com.profesores.model.Teacher;
import com.profesores.model.TeacherSocialMedia;
import com.profesores.service.SocialMediaService;
import com.profesores.service.TeacherService;
import util.CustomErrorType;

@Controller
@RequestMapping(value="/v1")
public class TeacherController {
	
	@Autowired
	private TeacherService _teacherService;
	
	@Autowired
	private SocialMediaService _socialMediaService;
		
	//CREATE
	@RequestMapping(value = "/teachers", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> createTeacher(@RequestBody Teacher teacher,  UriComponentsBuilder uriComponentsBuilder) {
        
		if (_teacherService.findByName(teacher.getName()) != null) {
            //logger.error("Unable to create. A User with name {} already exist", user.getName());
            return new ResponseEntity(new CustomErrorType("Unable to create. A teacher with name " + 
            		teacher.getName() + " already exist."),HttpStatus.CONFLICT);
        }
        
        _teacherService.saveTeacher(teacher);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponentsBuilder.path("/v1/teacher/{id}").buildAndExpand(teacher.getIdTeacher()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
			
	/**
	//GET 
	@RequestMapping(value = "/teachers", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<List<Teacher>> getTeachers(@RequestParam(value="name", required=false) String name){
		List<Teacher> teachers = new ArrayList<Teacher>();
		
		if (name == null) {
			teachers = _teacherService.findAllTeachers();
	        if (teachers.isEmpty()) {
	            return new ResponseEntity(HttpStatus.NO_CONTENT);
	            // You many decide to return HttpStatus.NOT_FOUND
	        }
		   
			return new ResponseEntity<List<Teacher>>(teachers, HttpStatus.OK);
		} else {
			Teacher teacher = _teacherService.findByName(name);
			if (teacher == null) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
			
			teachers.add(teacher);
			return new ResponseEntity<List<Teacher>>(teachers, HttpStatus.OK);
		}
		
		**/
	
	//GET 
		@RequestMapping(value = "/teachers", method = RequestMethod.GET, headers = "Accept=application/json")
	    public ResponseEntity<List<Teacher>> getTeachers(@RequestParam(value = "name",required = false) String name){
			List<Teacher> teachers = new ArrayList<Teacher>();
			
			
			if (name != null) {
				Teacher teacher = _teacherService.findByName(name);
		        if (teacher == null) {
		            return new ResponseEntity(HttpStatus.NOT_FOUND);
		            // You many decide to return HttpStatus.NOT_FOUND
		        }
		        teachers.add(teacher);
		        
			}
			
			if (name == null) {
				teachers = _teacherService.findAllTeachers();
		        if (teachers.isEmpty()) {
		            return new ResponseEntity(HttpStatus.NO_CONTENT);
		            // You many decide to return HttpStatus.NOT_FOUND
		        }
		        
			}
			
			return new ResponseEntity<List<Teacher>>(teachers, HttpStatus.OK);

			
	    }

	
	//FIND BY ID
	@RequestMapping(value = "/teachers/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<Teacher> getCourseById(@PathVariable("id") Long id){
		Teacher teacher = _teacherService.findById(id);
        if (teacher == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
    }
	
	//DELETE
	@RequestMapping(value = "/teachers/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Long id) {
		Teacher teacher = _teacherService.findById(id);
        if (teacher == null) {
        	
            return new ResponseEntity(new CustomErrorType("Unable to delete. teacher with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        
        _teacherService.deleteTeacherById(id);
        return new ResponseEntity<Teacher>(HttpStatus.NO_CONTENT);
    }
	

	public static final String TEACHER_UPLOADED_FOLDER ="images/teachers/";
	//CREATE TEACHER IMAGE
	@RequestMapping(value="/teachers/images", method = RequestMethod.POST, headers=("content-type=multipart/form-data"))
	public ResponseEntity<byte[]> uploadTeacherImage(@RequestParam("id_teacher") Long idTeacher, 
			@RequestParam("file") MultipartFile multipartFile, 
			UriComponentsBuilder componentsBuilder){
		if (idTeacher == null) {
			return new ResponseEntity(new CustomErrorType("Please set id_teacher"), HttpStatus.NO_CONTENT);
		}
		
		if (multipartFile.isEmpty()) {
			return new ResponseEntity(new CustomErrorType("Please select a file to upload"), HttpStatus.NO_CONTENT);
		}
		
		Teacher teacher = _teacherService.findById(idTeacher);
		if (teacher == null) {
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + idTeacher + " not dfound"), HttpStatus.NOT_FOUND);
		}
		
		if (teacher.getAvatar() != null || !teacher.getAvatar().isEmpty() ) {
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (f.exists()) {
				f.delete();
			}
		}
		
		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String dateName = dateFormat.format(date);
			
			String fileName = String.valueOf(idTeacher) + "-pictureTaecher-" + dateName + "." + multipartFile.getContentType().split("/")[1];
			teacher.setAvatar(TEACHER_UPLOADED_FOLDER + fileName);
			
			byte[] bytes = multipartFile.getBytes();
			Path path = Paths.get(TEACHER_UPLOADED_FOLDER + fileName);
			Files.write(path, bytes);
			
			_teacherService.updateTeacher(teacher);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error during upload: " + multipartFile.getOriginalFilename()),HttpStatus.CONFLICT);
		}
	}
	
	
	//GET IMAGE
	@RequestMapping(value="/teachers/{id_teacher}/images", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getTeacherImage(@PathVariable("id_teacher") Long idTeacher){
		if (idTeacher == null) {
			 return new ResponseEntity(new CustomErrorType("Please set id_teacher "), HttpStatus.NO_CONTENT);
		}
		
		Teacher teacher = _teacherService.findById(idTeacher);
		if (teacher == null) {
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + idTeacher + " not found"), HttpStatus.NOT_FOUND);
		}
		
		try {
			
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (!f.exists()) {
				return new ResponseEntity(new CustomErrorType("Image not found"),HttpStatus.CONFLICT);
			}
			
			byte[] image = Files.readAllBytes(path);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error to show image"),HttpStatus.CONFLICT);
		}
	
	}
	//DELETE
	@RequestMapping(value="/teachers/{id_teacher}/images", method = RequestMethod.DELETE,headers = "Accept=application/json")
	public ResponseEntity<?> deleteTeacherImage(@PathVariable("id_teacher") Long idTeacher){
		if (idTeacher == null) {
			 return new ResponseEntity(new CustomErrorType("Please set id_teacher "), HttpStatus.NO_CONTENT);
		}
		
		Teacher teacher = _teacherService.findById(idTeacher);
		if (teacher == null) {
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + idTeacher + " not found"), HttpStatus.NOT_FOUND);
		}
		
		if (teacher.getAvatar().isEmpty() || teacher.getAvatar() == null) {
			 return new ResponseEntity(new CustomErrorType("This Teacher dosen't have image assigned"), HttpStatus.NO_CONTENT);
		}
		
		String fileName = teacher.getAvatar();
		Path path = Paths.get(fileName);
		File file = path.toFile();
		if (file.exists()) {
			file.delete();
		}
		
		teacher.setAvatar("");
		_teacherService.updateTeacher(teacher);
		
		return new ResponseEntity<Teacher>(HttpStatus.NO_CONTENT);
	}
	//upddate
	@RequestMapping(value = "teachers/socialMedias", method = RequestMethod.PATCH, headers = "Accept=application/json")
	public ResponseEntity<?> assignTeacherSocialMedia(@RequestBody Teacher teacher, UriComponentsBuilder uriComponentsBuilder) {
	 
		if (teacher.getIdTeacher() == null)
	  	{
	    ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(new CustomErrorType("We need also id_teacher, id_social_media and nickname"), HttpStatus.NOT_FOUND);
		return responseEntity;
	  	}
	  
	  Teacher teacherFound = _teacherService.findById(teacher.getIdTeacher());
	  if (teacherFound == null) {
	    return new ResponseEntity(new CustomErrorType("The id_teacher " + teacher.getIdTeacher() + " not found."), HttpStatus.NOT_FOUND);
	  }
	  
	  if (teacher.getTeacherSocialMedias().size() == 0) {
	    return new ResponseEntity(new CustomErrorType("We need almost id_teacher, id_social_media and nickname"), HttpStatus.NOT_FOUND);
	  } else {
	    Iterator<TeacherSocialMedia> i = teacher.getTeacherSocialMedias().iterator();
	    while (i.hasNext()) {
	      TeacherSocialMedia teacherSocialMedia = i.next();
	      if (teacherSocialMedia.getSocialMedia().getIdSocialMedia() == null || teacherSocialMedia.getNickname() == null) {
	        return new ResponseEntity(new CustomErrorType("We need almost id_teacher, id_social_media and nickname"), HttpStatus.NOT_FOUND);
	      } else {
	        TeacherSocialMedia tsmAux = _socialMediaService.findSocialMediaByIdTeacherAndIdSocialMedia(
	            teacher.getIdTeacher(),
	            teacherSocialMedia.getSocialMedia().getIdSocialMedia());
	        
	        if (tsmAux != null && teacherSocialMedia.getSocialMedia().getIdSocialMedia() == tsmAux.getSocialMedia().getIdSocialMedia()
	            && teacherSocialMedia.getNickname().equals(tsmAux.getNickname())) {
	          return new ResponseEntity(new CustomErrorType("The information entered for id social media and nickname is the same as the current one. Please, update these values."), HttpStatus.NOT_FOUND);
	        } else {
	          SocialMedia socialMedia = _socialMediaService.findById(teacherSocialMedia.getSocialMedia().getIdSocialMedia());
	          if (socialMedia == null) {
	            return new ResponseEntity(new CustomErrorType("The id social media " + teacherSocialMedia.getSocialMedia().getIdSocialMedia() + " not found."), HttpStatus.NOT_FOUND);
	          }
	          
	          teacherSocialMedia.setSocialMedia(socialMedia);
	          teacherSocialMedia.setTeacher(teacherFound);
	          
	          if (tsmAux == null) {
	            teacherFound.getTeacherSocialMedias().add(teacherSocialMedia);
	          } else {
	            LinkedList<TeacherSocialMedia> teacherSocialMedias = new LinkedList<>();
	            teacherSocialMedias.addAll(teacherFound.getTeacherSocialMedias());
	            for (int j = 0; j < teacherSocialMedias.size(); j++) {
	              TeacherSocialMedia tSM = teacherSocialMedias.get(j);
	              if (teacherSocialMedia.getTeacher().getIdTeacher() == tSM.getTeacher().getIdTeacher()
	                && teacherSocialMedia.getSocialMedia().getIdSocialMedia() == tSM.getSocialMedia().getIdSocialMedia()) {
	                  tSM.setNickname(teacherSocialMedia.getNickname());
	                  teacherSocialMedias.set(j, tSM);
	                } else {
	                  teacherSocialMedias.set(j, tSM);
	              }
	            }
	            
	            teacherFound.getTeacherSocialMedias().clear();
	            teacherFound.getTeacherSocialMedias().addAll(teacherSocialMedias);
	          }
	        }
	        
	      }
	      
	    }
	  }
	  _teacherService.updateTeacher(teacherFound);
	  return new ResponseEntity<Teacher>(teacherFound, HttpStatus.OK);
	}
}










