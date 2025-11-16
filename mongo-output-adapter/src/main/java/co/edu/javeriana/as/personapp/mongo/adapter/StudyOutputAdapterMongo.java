package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoWriteException;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {
	
	@Autowired
    private EstudiosRepositoryMongo estudiosRepositoryMongo;
	
	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;
	
	private String buildStudyId(Integer personIdentification, Integer professionIdentification) {
		return personIdentification + "-" + professionIdentification;
	}
	
	@Override
	public Study save(Study study) {
		log.debug("Into save on Adapter MongoDB");
		try {
			EstudiosDocument persistedEstudio = estudiosRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
			return estudiosMapperMongo.fromAdapterToDomain(persistedEstudio);
		} catch (MongoWriteException e) {
			log.warn(e.getMessage());
			return study;
		}		
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete on Adapter MongoDB");
		String id = buildStudyId(personIdentification, professionIdentification);
		estudiosRepositoryMongo.deleteById(id);
		return estudiosRepositoryMongo.findById(id).isEmpty();
	}

	@Override
	public List<Study> find() {
		log.debug("Into find on Adapter MongoDB");
		return estudiosRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById on Adapter MongoDB");
		String id = buildStudyId(personIdentification, professionIdentification);
		if (estudiosRepositoryMongo.findById(id).isEmpty()) {
			return null;
		} else {
			return estudiosMapperMongo.fromAdapterToDomain(estudiosRepositoryMongo.findById(id).get());
		}
	}

}

