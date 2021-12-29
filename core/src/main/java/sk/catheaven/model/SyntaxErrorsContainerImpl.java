package sk.catheaven.model;

import sk.catheaven.core.SyntaxErrorsContainer;
import sk.catheaven.core.Tuple;

import java.util.ArrayList;
import java.util.List;


/**
 * Container for syntax errors. Provides storage for two types of error:
 *     Line errors: errors described by line of code number and associated error message.
 *  Message errors: errors described more generally just by a message.
 * @author catlord
 */
public class SyntaxErrorsContainerImpl implements SyntaxErrorsContainer {
    private final List<Tuple<Integer, String>> lineErrors = new ArrayList<>();
    private final List<String> messageErrors = new ArrayList<>();
    
    /**
     * Adds error message to list
     * @param lineNumber Line number containing error.
     * @param errorMessage Error message.
     */
    @Override
    public void addError(int lineNumber, String errorMessage){
        lineErrors.add(new TupleImpl<>(lineNumber, errorMessage));
    }
    @Override
    public void addError(String errorMessage){
        messageErrors.add(errorMessage);
    }
    
    @Override
    public void addAllLineErrors(List<Tuple<Integer, String>> errors){
        lineErrors.addAll(errors);
    }
    @Override
    public void addAllMessageErrors(List<String> errorMessages){
        messageErrors.addAll(errorMessages);
    }
    
    @Override
    public List<Tuple<Integer, String>> getLineErrors(){
        return lineErrors;
    }
    @Override
    public List<String> getMessageErrors(){
        return messageErrors;
    }
    
    @Override
    public void clear() {
        lineErrors.clear();
        messageErrors.clear();
    }
    
    @Override
    public int size() {
        return lineErrors.size() + messageErrors.size();
    }
    
    @Override
    public boolean isEmpty() {
        return lineErrors.isEmpty() && messageErrors.isEmpty();
    }
}
