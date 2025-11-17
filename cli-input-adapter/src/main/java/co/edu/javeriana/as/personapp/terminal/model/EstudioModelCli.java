package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	private PersonaModelCli person;
	private ProfesionModelCli profession;
	private LocalDate graduationDate;
	private String universityName;
	
	@Override
	public String toString() {
		return "EstudioModelCli{" +
				"person=" + (person != null ? person.getCc() + " - " + person.getNombre() + " " + person.getApellido() : "null") +
				", profession=" + (profession != null ? profession.getId() + " - " + profession.getName() : "null") +
				", graduationDate=" + graduationDate +
				", universityName='" + universityName + '\'' +
				'}';
	}
}

