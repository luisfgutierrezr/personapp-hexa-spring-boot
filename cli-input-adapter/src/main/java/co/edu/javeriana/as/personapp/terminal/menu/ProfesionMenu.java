package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfesionMenu {

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

	public void iniciarMenu(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
					profesionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
					menuOpciones(profesionInputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					profesionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
					menuOpciones(profesionInputAdapterCli, keyboard);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
					profesionInputAdapterCli.historial();
					break;
				case OPCION_CREAR:
					crearProfesion(profesionInputAdapterCli, keyboard);
					break;
				case OPCION_EDITAR:
					editarProfesion(profesionInputAdapterCli, keyboard);
					break;
				case OPCION_ELIMINAR:
					eliminarProfesion(profesionInputAdapterCli, keyboard);
					break;
				case OPCION_BUSCAR:
					buscarProfesion(profesionInputAdapterCli, keyboard);
					break;
				case OPCION_CONTAR:
					contarProfesiones(profesionInputAdapterCli);
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

	private void crearProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el ID de la profesión:");
			Integer id = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el nombre de la profesión:");
			String name = keyboard.nextLine();
			
			System.out.println("Ingrese la descripción de la profesión:");
			String description = keyboard.nextLine();

			Profession profession = new Profession();
			profession.setIdentification(id);
			profession.setName(name);
			profession.setDescription(description);

			Profession createdProfession = profesionInputAdapterCli.crearProfesion(profession);
			if (createdProfession != null) {
				System.out.println("Profesión creada exitosamente: " + createdProfession);
			} else {
				System.out.println("Error al crear la profesión");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void editarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el ID de la profesión a editar:");
			Integer id = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el nuevo nombre de la profesión:");
			String name = keyboard.nextLine();
			
			System.out.println("Ingrese la nueva descripción de la profesión:");
			String description = keyboard.nextLine();

			Profession profession = new Profession();
			profession.setIdentification(id);
			profession.setName(name);
			profession.setDescription(description);

			Profession editedProfession = profesionInputAdapterCli.editarProfesion(id, profession);
			if (editedProfession != null) {
				System.out.println("Profesión editada exitosamente: " + editedProfession);
			} else {
				System.out.println("Error al editar la profesión. Puede que no exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void eliminarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el ID de la profesión a eliminar:");
			Integer id = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			Boolean deleted = profesionInputAdapterCli.eliminarProfesion(id);
			if (deleted) {
				System.out.println("Profesión eliminada exitosamente");
			} else {
				System.out.println("Error al eliminar la profesión. Puede que no exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void buscarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
		try {
			System.out.println("Ingrese el ID de la profesión a buscar:");
			Integer id = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			ProfesionModelCli profesion = profesionInputAdapterCli.buscarProfesion(id);
			if (profesion != null) {
				System.out.println("Profesión encontrada: " + profesion);
			} else {
				System.out.println("Profesión no encontrada");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void contarProfesiones(ProfesionInputAdapterCli profesionInputAdapterCli) {
		Integer count = profesionInputAdapterCli.contarProfesiones();
		System.out.println("Total de profesiones: " + count);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_VER_TODO + " para ver todas las profesiones");
		System.out.println(OPCION_CREAR + " para crear una profesión");
		System.out.println(OPCION_EDITAR + " para editar una profesión");
		System.out.println(OPCION_ELIMINAR + " para eliminar una profesión");
		System.out.println(OPCION_BUSCAR + " para buscar una profesión");
		System.out.println(OPCION_CONTAR + " para contar profesiones");
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

