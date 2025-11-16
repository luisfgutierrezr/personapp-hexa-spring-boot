package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudiosRequest;
import co.edu.javeriana.as.personapp.model.response.EstudiosResponse;

@Mapper
public class EstudiosMapperRest {
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	
	public EstudiosResponse fromDomainToAdapterRestMaria(Study study) {
		return fromDomainToAdapterRest(study, "MariaDB");
	}
	
	public EstudiosResponse fromDomainToAdapterRestMongo(Study study) {
		return fromDomainToAdapterRest(study, "MongoDB");
	}
	
	public EstudiosResponse fromDomainToAdapterRest(Study study, String database) {
		Integer personId = study.getPerson() != null ? study.getPerson().getIdentification() : null;
		Integer professionId = study.getProfession() != null ? study.getProfession().getIdentification() : null;
		String graduationDateStr = study.getGraduationDate() != null 
			? study.getGraduationDate().format(DATE_FORMATTER) 
			: null;
		
		return new EstudiosResponse(
				personId != null ? personId.toString() : "",
				professionId != null ? professionId.toString() : "",
				graduationDateStr,
				study.getUniversityName() != null ? study.getUniversityName() : "",
				database,
				"OK");
	}

	public Study fromAdapterToDomain(EstudiosRequest request, Person person, Profession profession) {
		if (request == null) {
			return null;
		}
		
		if (person == null) {
			throw new IllegalArgumentException("La persona es requerida");
		}
		
		if (profession == null) {
			throw new IllegalArgumentException("La profesión es requerida");
		}
		
		// Convertir graduationDate (String) a LocalDate
		LocalDate graduationDate = null;
		if (request.getGraduationDate() != null && !request.getGraduationDate().isEmpty()) {
			try {
				graduationDate = LocalDate.parse(request.getGraduationDate(), DATE_FORMATTER);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("La fecha de graduación debe estar en formato YYYY-MM-DD: " + request.getGraduationDate(), e);
			}
		}
		
		return new Study(
			person,
			profession,
			graduationDate,
			request.getUniversityName()
		);
	}
}

