package epfl.sweng.patterns;

import epfl.sweng.servercomm.ServerCommunication;

/**
 * @author lseguy
 *
 */
public class CheckProxyHelper implements ICheckProxyHelper {

    @Override
    public Class<?> getServerCommunicationClass() {
        return ServerCommunication.class;
    }

    @Override
    public Class<?> getProxyClass() {
        return Proxy.class;
    }

}
