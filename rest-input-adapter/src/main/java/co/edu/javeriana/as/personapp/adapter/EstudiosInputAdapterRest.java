package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
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
import co.edu.javeriana.as.personapp.mapper.EstudiosMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudiosRequest;
import co.edu.javeriana.as.personapp.model.response.EstudiosResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudiosInputAdapterRest {

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
	private EstudiosMapperRest estudiosMapperRest;

	PersonInputPort personInputPort;
	ProfessionInputPort professionInputPort;
	StudyInputPort studyInputPort;

	private String setOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
			studyInputPort = new StudyUseCase(studyOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
			studyInputPort = new StudyUseCase(studyOutputPortMongo);
			return DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<EstudiosResponse> historial(String database) {
		log.info("Into historial EstudiosEntity in Input Adapter");
		try {
			setOutputPortInjection(database);
			String db = setOutputPortInjection(database);
			return studyInputPort.findAll().stream()
					.map(study -> db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
						? estudiosMapperRest.fromDomainToAdapterRestMaria(study)
						: estudiosMapperRest.fromDomainToAdapterRestMongo(study))
					.collect(Collectors.toList());
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<EstudiosResponse>();
		}
	}

	public EstudiosResponse crearEstudios(EstudiosRequest request) {
		try {
			setOutputPortInjection(request.getDatabase());
			
			Integer personId = Integer.parseInt(request.getPersonId());
			Integer professionId = Integer.parseInt(request.getProfessionId());
			
			Person person = personInputPort.findOne(personId);
			if (person == null) {
				return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: Persona no encontrada");
			}
			
			Profession profession = professionInputPort.findOne(professionId);
			if (profession == null) {
				return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: Profesión no encontrada");
			}
			
			Study study = studyInputPort.create(estudiosMapperRest.fromAdapterToDomain(request, person, profession));
			String db = setOutputPortInjection(request.getDatabase());
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? estudiosMapperRest.fromDomainToAdapterRestMaria(study)
				: estudiosMapperRest.fromDomainToAdapterRestMongo(study);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: " + e.getMessage());
		} catch (NumberFormatException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: ID inválido");
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error inesperado al crear estudios", e);
			return new EstudiosResponse("", "", "", "", request.getDatabase(), "ERROR: " + e.getMessage());
		}
	}

	public EstudiosResponse obtenerEstudiosPorId(String database, Integer personId, Integer professionId) {
		try {
			setOutputPortInjection(database);
			Study study = studyInputPort.findOne(personId, professionId);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? estudiosMapperRest.fromDomainToAdapterRestMaria(study)
				: estudiosMapperRest.fromDomainToAdapterRestMongo(study);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", database, "ERROR: " + e.getMessage());
		}
	}

	public EstudiosResponse actualizarEstudios(String database, Integer personId, Integer professionId, EstudiosRequest request) {
		try {
			setOutputPortInjection(database);
			
			Person person = personInputPort.findOne(personId);
			if (person == null) {
				return new EstudiosResponse("", "", "", "", database, "ERROR: Persona no encontrada");
			}
			
			Profession profession = professionInputPort.findOne(professionId);
			if (profession == null) {
				return new EstudiosResponse("", "", "", "", database, "ERROR: Profesión no encontrada");
			}
			
			Study study = estudiosMapperRest.fromAdapterToDomain(request, person, profession);
			Study updatedStudy = studyInputPort.edit(personId, professionId, study);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? estudiosMapperRest.fromDomainToAdapterRestMaria(updatedStudy)
				: estudiosMapperRest.fromDomainToAdapterRestMongo(updatedStudy);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse("", "", "", "", database, "ERROR: " + e.getMessage());
		}
	}

	public EstudiosResponse eliminarEstudios(String database, Integer personId, Integer professionId) {
		try {
			setOutputPortInjection(database);
			Boolean deleted = studyInputPort.drop(personId, professionId);
			if (deleted) {
				return new EstudiosResponse(personId.toString(), professionId.toString(), "", "", database, "DELETED");
			} else {
				return new EstudiosResponse(personId.toString(), professionId.toString(), "", "", database, "ERROR: No se pudo eliminar");
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse(personId.toString(), professionId.toString(), "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new EstudiosResponse(personId.toString(), professionId.toString(), "", "", database, "ERROR: " + e.getMessage());
		}
	}

}

