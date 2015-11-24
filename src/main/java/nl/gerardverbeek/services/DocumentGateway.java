package nl.gerardverbeek.services;

/**
 * Created by gerardverbeek on 08/11/15.
 */
public interface DocumentGateway {

    boolean sendDocument(String endPoint, Object documentToSend);

}
