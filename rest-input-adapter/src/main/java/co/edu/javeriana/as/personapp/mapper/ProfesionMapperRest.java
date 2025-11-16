package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper
public class ProfesionMapperRest {
	
	public ProfesionResponse fromDomainToAdapterRestMaria(Profession profession) {
		return fromDomainToAdapterRest(profession, "MariaDB");
	}
	
	public ProfesionResponse fromDomainToAdapterRestMongo(Profession profession) {
		return fromDomainToAdapterRest(profession, "MongoDB");
	}
	
	public ProfesionResponse fromDomainToAdapterRest(Profession profession, String database) {
		return new ProfesionResponse(
				profession.getIdentification()+"", 
				profession.getName(), 
				profession.getDescription() != null ? profession.getDescription() : "",
				database,
				"OK");
	}

	public Profession fromAdapterToDomain(ProfesionRequest request) {
		if (request == null) {
			return null;
		}
		
		// Convertir id (String) a identification (Integer)
		Integer identification = null;
		try {
			identification = Integer.parseInt(request.getId());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("El ID debe ser un número válido: " + request.getId(), e);
		}
		
		if (request.getName() == null || request.getName().isEmpty()) {
			throw new IllegalArgumentException("El nombre de la profesión no puede estar vacío");
		}
		
		Profession profession = new Profession();
		profession.setIdentification(identification);
		profession.setName(request.getName());
		profession.setDescription(request.getDescription());
		return profession;
	}
}

