package com.profesores.dao;

import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.profesores.model.Teacher;
import com.profesores.model.TeacherSocialMedia;



@Repository
@Transactional
public class TeacherDaoImpl extends AbstractSession implements TeacherDao {
	
	public void saveTeacher(Teacher teacher) {
		// TODO Auto-generated method stub
		getSession().persist(teacher);
	}

	public void deleteTeacherById(Long idTeacher) {
		// TODO Auto-generated method stub
		Teacher teacher = findById(idTeacher);
		if (teacher != null) {
			
			Iterator<TeacherSocialMedia> i =  teacher.getTeacherSocialMedias().iterator();
			while (i.hasNext()) {
				TeacherSocialMedia teacherSocialMedia = i.next();
				i.remove();
				getSession().delete(teacherSocialMedia);
			}
			teacher.getTeacherSocialMedias().clear();
			getSession().delete(teacher);
		}
	}

	public void updateTeacher(Teacher teacher) {
		// TODO Auto-generated method stub
		getSession().update(teacher);
	}

	public List<Teacher> findAllTeachers() {
		// TODO Auto-generated method stub
		return getSession().createQuery("from Teacher").list();
	}

	public Teacher findById(Long idTeacher) {
		// TODO Auto-generated method stub
		return (Teacher) getSession().get(Teacher.class, idTeacher);
	}

	public Teacher findByName(String name) {
		// TODO Auto-generated method stub
		return (Teacher) getSession().createQuery(
				"from Teacher where name = :name")
				.setParameter("name", name).uniqueResult();
	}

	@Override
	public TeacherSocialMedia findSocialMediaByIdAndName(Long idSocialMedia, String nickname) {
		// TODO Auto-generated method stub
		List<Object[]> objects = getSession().createQuery(
				"from TeacherSocialMedia tsm join tsm.socialMedia sm "
				+ "where sm.idSocialMedia = :idSocialMedia and tsm.nickname = :nickname")
				.setParameter("idSocialMedia", idSocialMedia)
				.setParameter("nickname", nickname).list();
		
		if (objects.size() > 0) {
			for (Object[] objects2 : objects) {
				for (Object object : objects2) {
					if (object instanceof TeacherSocialMedia) {
						return (TeacherSocialMedia) object;
					}
				}
			}
		}
		
		return null;

}
}