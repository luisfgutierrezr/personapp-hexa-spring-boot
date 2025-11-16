package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()+"", 
				person.getGender().toString(), 
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		if (request == null) {
			return null;
		}
		
		// Convertir dni (String) a identification (Integer)
		Integer identification = null;
		try {
			identification = Integer.parseInt(request.getDni());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("El DNI debe ser un número válido: " + request.getDni(), e);
		}
		
		// Convertir age (String) a age (Integer)
		Integer age = null;
		if (request.getAge() != null && !request.getAge().isEmpty()) {
			try {
				age = Integer.parseInt(request.getAge());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("La edad debe ser un número válido: " + request.getAge(), e);
			}
		}
		
		// Convertir sex (String) a gender (Gender enum)
		Gender gender = parseGender(request.getSex());
		
		return new Person(
			identification,
			request.getFirstName(),
			request.getLastName(),
			gender,
			age,
			null,
			null
		);
	}
	
	private Gender parseGender(String sex) {
		if (sex == null || sex.isEmpty()) {
			throw new IllegalArgumentException("El sexo no puede estar vacío");
		}
		
		String sexUpper = sex.toUpperCase().trim();
		switch (sexUpper) {
			case "M":
			case "MALE":
				return Gender.MALE;
			case "F":
			case "FEMALE":
				return Gender.FEMALE;
			case "O":
			case "OTHER":
				return Gender.OTHER;
			default:
				throw new IllegalArgumentException("Valor de sexo no válido: " + sex + ". Valores permitidos: M, F, O, MALE, FEMALE, OTHER");
		}
	}
		
}
