package sk.catheaven.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Container for syntax errors. Provides storage for two types of error:
 *     Line errors: errors described by line of code number and associated error message.
 *  Message errors: errors described more generally just by a message.
 * @author catlord
 */
public class SyntaxErrorsContainer {
    private final List<Tuple<Integer, String>> lineErrors = new ArrayList<>();
    private final List<String> messageErrors = new ArrayList<>();
    
    /**
     * Adds error message to list
     * @param lineNumber Line number containing error.
     * @param errorMessage Error message.
     */
    public void addError(int lineNumber, String errorMessage){
        lineErrors.add(new Tuple<>(lineNumber, errorMessage));
    }
    public void addError(String errorMessage){
        messageErrors.add(errorMessage);
    }
    
    public void addAllLineErrors(List<Tuple<Integer, String>> errors){
        lineErrors.addAll(errors);
    }
    public void addAllMessageErrors(List<String> errorMessages){
        messageErrors.addAll(errorMessages);
    }
    
    public List<Tuple<Integer, String>> getLineErrors(){
        return lineErrors;
    }
    public List<String> getMessageErrors(){
        return messageErrors;
    }
    
    public void clear() {
        lineErrors.clear();
        messageErrors.clear();
    }
}
