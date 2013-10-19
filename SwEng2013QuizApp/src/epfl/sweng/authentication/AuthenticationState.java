package epfl.sweng.authentication;

/**
 *
 * @author MathieuMonney
 *
 */
public enum AuthenticationState {
    UNAUTHENTICATED, TOKEN, TEQUILA, CONFIRMATION, AUTHENTICATED;
    
    
    private static AuthenticationState current;
    
    private AuthenticationState() {
        
    }
    
    public static void setState(AuthenticationState newState) {
        current = newState;
    }
    
    public static AuthenticationState getState() {
        return current;
    }
}
