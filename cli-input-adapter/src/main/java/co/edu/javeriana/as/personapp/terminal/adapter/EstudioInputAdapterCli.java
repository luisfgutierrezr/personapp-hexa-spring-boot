package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	PersonInputPort personInputPort;
	ProfessionInputPort professionInputPort;
	StudyInputPort studyInputPort;

	public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
			studyInputPort = new StudyUseCase(studyOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
			studyInputPort = new StudyUseCase(studyOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		log.info("Into historial EstudiosEntity in Input Adapter");
		studyInputPort.findAll().stream()
			.map(estudioMapperCli::fromDomainToAdapterCli)
			.forEach(System.out::println);
	}

	public Study crearEstudio(Study study) {
		log.info("Into crearEstudio in Input Adapter");
		try {
			// Validate that the person exists
			if (study.getPerson() != null && study.getPerson().getIdentification() != null) {
				Person person = personInputPort.findOne(study.getPerson().getIdentification());
				if (person == null) {
					log.warn("Person with identification " + study.getPerson().getIdentification() + " does not exist");
					return null;
				}
				study.setPerson(person);
			} else {
				log.warn("Study must have a person");
				return null;
			}

			// Validate that the profession exists
			if (study.getProfession() != null && study.getProfession().getIdentification() != null) {
				Profession profession = professionInputPort.findOne(study.getProfession().getIdentification());
				if (profession == null) {
					log.warn("Profession with identification " + study.getProfession().getIdentification() + " does not exist");
					return null;
				}
				study.setProfession(profession);
			} else {
				log.warn("Study must have a profession");
				return null;
			}

			return studyInputPort.create(study);
		} catch (NoExistException e) {
			log.warn("Error creating study: " + e.getMessage());
			return null;
		}
	}

	public Study editarEstudio(Integer personIdentification, Integer professionIdentification, Study study) {
		log.info("Into editarEstudio in Input Adapter");
		try {
			// Validate that the person exists
			if (study.getPerson() != null && study.getPerson().getIdentification() != null) {
				Person person = personInputPort.findOne(study.getPerson().getIdentification());
				if (person == null) {
					log.warn("Person with identification " + study.getPerson().getIdentification() + " does not exist");
					return null;
				}
				study.setPerson(person);
			}

			// Validate that the profession exists
			if (study.getProfession() != null && study.getProfession().getIdentification() != null) {
				Profession profession = professionInputPort.findOne(study.getProfession().getIdentification());
				if (profession == null) {
					log.warn("Profession with identification " + study.getProfession().getIdentification() + " does not exist");
					return null;
				}
				study.setProfession(profession);
			}

			return studyInputPort.edit(personIdentification, professionIdentification, study);
		} catch (NoExistException e) {
			log.warn("Error editing study: " + e.getMessage());
			return null;
		}
	}

	public Boolean eliminarEstudio(Integer personIdentification, Integer professionIdentification) {
		log.info("Into eliminarEstudio in Input Adapter");
		try {
			return studyInputPort.drop(personIdentification, professionIdentification);
		} catch (NoExistException e) {
			log.warn("Study does not exist: " + e.getMessage());
			return false;
		}
	}

	public EstudioModelCli buscarEstudio(Integer personIdentification, Integer professionIdentification) {
		log.info("Into buscarEstudio in Input Adapter");
		try {
			Study study = studyInputPort.findOne(personIdentification, professionIdentification);
			return study != null ? estudioMapperCli.fromDomainToAdapterCli(study) : null;
		} catch (NoExistException e) {
			log.warn("Study not found: " + e.getMessage());
			return null;
		}
	}

	public List<EstudioModelCli> buscarTodos() {
		log.info("Into buscarTodos EstudiosEntity in Input Adapter");
		return studyInputPort.findAll().stream()
			.map(estudioMapperCli::fromDomainToAdapterCli)
			.collect(Collectors.toList());
	}

	public Integer contarEstudios() {
		log.info("Into contarEstudios in Input Adapter");
		return studyInputPort.count();
	}
}

