package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstudioMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_VER_TODO = 1;
	private static final int OPCION_CREAR = 2;
	private static final int OPCION_EDITAR = 3;
	private static final int OPCION_ELIMINAR = 4;
	private static final int OPCION_BUSCAR = 5;
	private static final int OPCION_CONTAR = 6;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void iniciarMenu(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuMotorPersistencia();
				int opcion = leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					isValid = true;
					break;
				case PERSISTENCIA_MARIADB:
					estudioInputAdapterCli.setStudyOutputPortInjection("MARIA");
					menuOpciones(estudioInputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					estudioInputAdapterCli.setStudyOutputPortInjection("MONGO");
					menuOpciones(estudioInputAdapterCli, keyboard);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuOpciones();
				int opcion = leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
					isValid = true;
					break;
				case OPCION_VER_TODO:
					estudioInputAdapterCli.historial();
					break;
				case OPCION_CREAR:
					crearEstudio(estudioInputAdapterCli, keyboard);
					break;
				case OPCION_EDITAR:
					editarEstudio(estudioInputAdapterCli, keyboard);
					break;
				case OPCION_ELIMINAR:
					eliminarEstudio(estudioInputAdapterCli, keyboard);
					break;
				case OPCION_BUSCAR:
					buscarEstudio(estudioInputAdapterCli, keyboard);
					break;
				case OPCION_CONTAR:
					contarEstudios(estudioInputAdapterCli);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InputMismatchException e) {
				log.warn("Solo se permiten números.");
				keyboard.nextLine(); // Clear buffer
			}
		} while (!isValid);
	}

	private void crearEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el CC de la persona:");
			Integer personId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el ID de la profesión:");
			Integer professionId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese la fecha de graduación (formato: yyyy-MM-dd):");
			String dateStr = keyboard.nextLine();
			LocalDate graduationDate = null;
			try {
				graduationDate = LocalDate.parse(dateStr, DATE_FORMATTER);
			} catch (DateTimeParseException e) {
				log.warn("Formato de fecha inválido. Use yyyy-MM-dd");
				return;
			}
			
			System.out.println("Ingrese el nombre de la universidad:");
			String universityName = keyboard.nextLine();

			Person person = new Person();
			person.setIdentification(personId);

			Profession profession = new Profession();
			profession.setIdentification(professionId);

			Study study = new Study();
			study.setPerson(person);
			study.setProfession(profession);
			study.setGraduationDate(graduationDate);
			study.setUniversityName(universityName);

			Study createdStudy = estudioInputAdapterCli.crearEstudio(study);
			if (createdStudy != null) {
				System.out.println("Estudio creado exitosamente: " + createdStudy);
			} else {
				System.out.println("Error al crear el estudio. Verifique que la persona y profesión existan.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void editarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el CC de la persona del estudio a editar:");
			Integer personId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el ID de la profesión del estudio a editar:");
			Integer professionId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese la nueva fecha de graduación (formato: yyyy-MM-dd):");
			String dateStr = keyboard.nextLine();
			LocalDate graduationDate = null;
			try {
				graduationDate = LocalDate.parse(dateStr, DATE_FORMATTER);
			} catch (DateTimeParseException e) {
				log.warn("Formato de fecha inválido. Use yyyy-MM-dd");
				return;
			}
			
			System.out.println("Ingrese el nuevo nombre de la universidad:");
			String universityName = keyboard.nextLine();

			Person person = new Person();
			person.setIdentification(personId);

			Profession profession = new Profession();
			profession.setIdentification(professionId);

			Study study = new Study();
			study.setPerson(person);
			study.setProfession(profession);
			study.setGraduationDate(graduationDate);
			study.setUniversityName(universityName);

			Study editedStudy = estudioInputAdapterCli.editarEstudio(personId, professionId, study);
			if (editedStudy != null) {
				System.out.println("Estudio editado exitosamente: " + editedStudy);
			} else {
				System.out.println("Error al editar el estudio. Verifique que exista y que la persona y profesión sean válidas.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void eliminarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el CC de la persona del estudio a eliminar:");
			Integer personId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el ID de la profesión del estudio a eliminar:");
			Integer professionId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			Boolean deleted = estudioInputAdapterCli.eliminarEstudio(personId, professionId);
			if (deleted) {
				System.out.println("Estudio eliminado exitosamente");
			} else {
				System.out.println("Error al eliminar el estudio. Puede que no exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void buscarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el CC de la persona del estudio a buscar:");
			Integer personId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el ID de la profesión del estudio a buscar:");
			Integer professionId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			EstudioModelCli estudio = estudioInputAdapterCli.buscarEstudio(personId, professionId);
			if (estudio != null) {
				System.out.println("Estudio encontrado: " + estudio);
			} else {
				System.out.println("Estudio no encontrado");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void contarEstudios(EstudioInputAdapterCli estudioInputAdapterCli) {
		Integer count = estudioInputAdapterCli.contarEstudios();
		System.out.println("Total de estudios: " + count);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_VER_TODO + " para ver todos los estudios");
		System.out.println(OPCION_CREAR + " para crear un estudio");
		System.out.println(OPCION_EDITAR + " para editar un estudio");
		System.out.println(OPCION_ELIMINAR + " para eliminar un estudio");
		System.out.println(OPCION_BUSCAR + " para buscar un estudio");
		System.out.println(OPCION_CONTAR + " para contar estudios");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

	private int leerOpcion(Scanner keyboard) {
		try {
			System.out.print("Ingrese una opción: ");
			return keyboard.nextInt();
		} catch (InputMismatchException e) {
			log.warn("Solo se permiten números.");
			keyboard.nextLine(); // Clear buffer
			return leerOpcion(keyboard);
		}
	}
}

