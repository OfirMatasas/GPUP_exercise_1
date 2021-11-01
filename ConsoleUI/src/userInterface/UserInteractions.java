package userInterface;

import target.Graph;
import target.Target;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;

public class UserInteractions implements OutputInterface, InputInterface {
    Scanner scanner = new Scanner(System.in);
    Graph targetsGraph;

    public void SystemExecute()
    {
        System.out.println("Welcome to our project!");
        int userSelection = 0;

        do {
            PrintMenu();
            userSelection = getUserSelectionFromMenu();

            switch (userSelection)
            {
                case 1:
                {
                    ///
                    break;
                }
                case 2:
                {
                    ///
                    break;
                }
                case 3:
                {
                    PrintTargetInformation();
                    break;
                }

            }
            System.out.println("1. Load system details from file.");
            System.out.println("2. Show graph details.");
            System.out.println("3. Show target details.");
            System.out.println("4. Find connection between 2 targets.");
            System.out.println("5. Execute task.");
            System.out.println("6. Exit.");

        }while (userSelection != 0);
    }

    @Override
    public void PrintTargetInformation()
    {
        if(targetsGraph.getGraphTargets().size() == 0)
        {
            System.out.println("There're no targets on the graph!");
            System.out.println("Please load a graph from file.");
            return;
        }

        System.out.print("Enter target's name: ");
        String targetName = getUserTargetSelection();
        if(targetsGraph.getGraphTargets().containsKey(targetName))
        {
            Target selectedTarget = targetsGraph.getGraphTargets().get(targetName);
            System.out.println("Target's name: " + targetName);
            System.out.println("Target's property: " + selectedTarget.getTargetProperty());

            PrintDependsOnTargets(selectedTarget);
            PrintRequiredForTargets(selectedTarget);
            PrintTargetExtraInformation(selectedTarget);
        }

    }

    @Override
    public void PrintDependsOnTargets(Target target) {
        if(target.getDependsOnTargets().size() == 0)
            System.out.println("The target has no depends-on-targets.");
        else
        {
            System.out.println("List of depends-on-targets: ");
            for(Target currentTarget : target.getDependsOnTargets())
                System.out.print(currentTarget.getTargetName() + " ");
        }
    }

    @Override
    public void PrintRequiredForTargets(Target target) {
        if(target.getRequireForTargets().size() == 0)
            System.out.println("The target has no required-for-targets.");
        else
        {
            System.out.println("List of required-for-targets: ");
            for(Target currentTarget : target.getRequireForTargets())
                System.out.print(currentTarget.getTargetName() + " ");
        }
    }

    @Override
    public void PrintTargetExtraInformation(Target target) {
        if(target.getExtraInformation() == null)
            System.out.println("There's no extra information about the selected target.");
        else
            System.out.println("Target's extra information: " + target.getExtraInformation());
    }

    @Override
    public int getUserSelectionFromMenu() {
        try{
            int selection = scanner.nextInt();
            return selection;
        }
        catch(ArithmeticException ex)
        {
            PrintExceptionInformation("You entered non-integer. Please try again.");
            return 0;
        }
    }

    @Override
    public String getUserTargetSelection() {
        String targetName = scanner.nextLine();
        return targetName;
    }


    @Override
    public void PrintMenu() {
        System.out.println("Please choose from the options below:");
        System.out.println("1. Load system details from file.");
        System.out.println("2. Show graph details.");
        System.out.println("3. Show target details.");
        System.out.println("4. Find connection between 2 targets.");
        System.out.println("5. Execute task.");
        System.out.println("6. Exit.");
    }

    @Override
    public void PrintGraphInformation() {

    }

    @Override
    public void PrintTargetConnectionStatus(String target1, String target2) {

    }

    @Override
    public void PrintExceptionInformation(String message) {

    }
}
