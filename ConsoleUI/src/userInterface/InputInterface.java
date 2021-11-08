package userInterface;

import javafx.util.Pair;

public interface InputInterface {
    public int getUserSelectionFromMenu();
    public void checkValidationOfFile();
    public void unmarshellXmlFileToObjects();
    public void loadFile();

}
