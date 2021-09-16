package sk.catheaven;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.instructions.Instruction;

import java.util.List;

/**
 * Main application window. Application entry.
 * @author catlord
 */
@SpringBootApplication
public class Launcher {
	private static final Logger log = LogManager.getLogger();

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(Launcher.class, args);

		StringBuilder beans = new StringBuilder("====== Available beans ======\n");
		for (String beanName : context.getBeanDefinitionNames())
			beans.append("     ").append(beanName).append("\n");
		log.debug(beans);

		instructionsCheck(context.getBean(Instruction[].class));
		CPUCheck(context.getBean(CPU.class));
	}

	static void instructionsCheck(Instruction[] instructions) {
		for (Instruction instruction : instructions) {
			log.debug("{}: type {} with {} fields",
					instruction.getMnemo(), instruction.getType().getLabel(), instruction.getFields().size());
		}
		log.debug("Successfully loaded {} instructions\n", instructions.length);
	}

	static void CPUCheck(CPU cpu) {
		for (Component component: cpu.getComponents().values())
			log.debug("{}\n{}\n {} input(s) | {} output(s) | {} selector(s)",
					component.getLabel(), component,
					component.getInputs().size(), component.getOutputs().size(), component.getSelectors().size()
			);

		log.debug("Successfully initiated CPU with {} components and {} connectors\n",
				cpu.getComponents().size(), cpu.getConnectors().size());

		for (String sourceLabel: cpu.getConnectors().keySet())
			log.debug("Component `{}` has {} connections",
					sourceLabel, cpu.getConnectors().get(sourceLabel).size());

		int phaseIndex = 1;
		for (List<Component> phase : cpu.getPhases())
			log.debug("Phase {}: {} Components", phaseIndex++, phase.size());

		//cpu.getRegisters();
	}
}