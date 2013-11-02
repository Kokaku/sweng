package epfl.sweng.patterns;

/**
 * Helper class for checking the correct implementation of the Proxy Design Pattern.
 */
public interface ICheckProxyHelper {
    /**
     * Get the class that implements the communication with the server.
     * @return the class that implements the communication with the server.
     */
    Class<?> getServerCommunicationClass();

    /**
     * Get the class that implements the proxy.
     * @return the class that implements the proxy.
     */
    Class<?> getProxyClass();
}
