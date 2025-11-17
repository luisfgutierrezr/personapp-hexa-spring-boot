package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelefonoMenu {

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

	public void iniciarMenu(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
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
					telefonoInputAdapterCli.setPhoneOutputPortInjection("MARIA");
					menuOpciones(telefonoInputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					telefonoInputAdapterCli.setPhoneOutputPortInjection("MONGO");
					menuOpciones(telefonoInputAdapterCli, keyboard);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
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
					telefonoInputAdapterCli.historial();
					break;
				case OPCION_CREAR:
					crearTelefono(telefonoInputAdapterCli, keyboard);
					break;
				case OPCION_EDITAR:
					editarTelefono(telefonoInputAdapterCli, keyboard);
					break;
				case OPCION_ELIMINAR:
					eliminarTelefono(telefonoInputAdapterCli, keyboard);
					break;
				case OPCION_BUSCAR:
					buscarTelefono(telefonoInputAdapterCli, keyboard);
					break;
				case OPCION_CONTAR:
					contarTelefonos(telefonoInputAdapterCli);
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

	private void crearTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
		try {
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el número de teléfono:");
			String number = keyboard.nextLine();
			
			System.out.println("Ingrese la operadora:");
			String company = keyboard.nextLine();
			
			System.out.println("Ingrese el CC del dueño:");
			Integer ownerId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			Person owner = new Person();
			owner.setIdentification(ownerId);

			Phone phone = new Phone();
			phone.setNumber(number);
			phone.setCompany(company);
			phone.setOwner(owner);

			Phone createdPhone = telefonoInputAdapterCli.crearTelefono(phone);
			if (createdPhone != null) {
				System.out.println("Teléfono creado exitosamente: " + createdPhone);
			} else {
				System.out.println("Error al crear el teléfono. Verifique que el dueño exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void editarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
		try {
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el número de teléfono a editar:");
			String number = keyboard.nextLine();
			
			System.out.println("Ingrese la nueva operadora:");
			String company = keyboard.nextLine();
			
			System.out.println("Ingrese el nuevo CC del dueño:");
			Integer ownerId = keyboard.nextInt();
			keyboard.nextLine(); // Clear buffer

			Person owner = new Person();
			owner.setIdentification(ownerId);

			Phone phone = new Phone();
			phone.setNumber(number);
			phone.setCompany(company);
			phone.setOwner(owner);

			Phone editedPhone = telefonoInputAdapterCli.editarTelefono(number, phone);
			if (editedPhone != null) {
				System.out.println("Teléfono editado exitosamente: " + editedPhone);
			} else {
				System.out.println("Error al editar el teléfono. Verifique que exista y que el dueño sea válido.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void eliminarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
		try {
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el número de teléfono a eliminar:");
			String number = keyboard.nextLine();

			Boolean deleted = telefonoInputAdapterCli.eliminarTelefono(number);
			if (deleted) {
				System.out.println("Teléfono eliminado exitosamente");
			} else {
				System.out.println("Error al eliminar el teléfono. Puede que no exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void buscarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
		try {
			keyboard.nextLine(); // Clear buffer
			
			System.out.println("Ingrese el número de teléfono a buscar:");
			String number = keyboard.nextLine();

			TelefonoModelCli telefono = telefonoInputAdapterCli.buscarTelefono(number);
			if (telefono != null) {
				System.out.println("Teléfono encontrado: " + telefono);
			} else {
				System.out.println("Teléfono no encontrado");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida. Inténtelo de nuevo.");
			keyboard.nextLine(); // Clear buffer
		}
	}

	private void contarTelefonos(TelefonoInputAdapterCli telefonoInputAdapterCli) {
		Integer count = telefonoInputAdapterCli.contarTelefonos();
		System.out.println("Total de teléfonos: " + count);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_VER_TODO + " para ver todos los teléfonos");
		System.out.println(OPCION_CREAR + " para crear un teléfono");
		System.out.println(OPCION_EDITAR + " para editar un teléfono");
		System.out.println(OPCION_ELIMINAR + " para eliminar un teléfono");
		System.out.println(OPCION_BUSCAR + " para buscar un teléfono");
		System.out.println(OPCION_CONTAR + " para contar teléfonos");
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

