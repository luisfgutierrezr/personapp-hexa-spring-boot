package co.edu.javeriana.as.personapp.terminal.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	@Autowired
	private PersonaMapperCli personaMapperCli;

	@Autowired
	private ProfesionMapperCli profesionMapperCli;

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		EstudioModelCli estudioModelCli = new EstudioModelCli();
		estudioModelCli.setPerson(study.getPerson() != null ? personaMapperCli.fromDomainToAdapterCli(study.getPerson()) : null);
		estudioModelCli.setProfession(study.getProfession() != null ? profesionMapperCli.fromDomainToAdapterCli(study.getProfession()) : null);
		estudioModelCli.setGraduationDate(study.getGraduationDate());
		estudioModelCli.setUniversityName(study.getUniversityName());
		return estudioModelCli;
	}
}

