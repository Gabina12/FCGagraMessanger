package ge.bondx.fcgagramessanger.service;

/**
 * Created by Admin on 4/23/2017.
 */

public class ServiceUrl {
    public static final String REST_SERVICE_URL = "http://messiging.bondx.ge/api/";
    public static final String CONTACTS = REST_SERVICE_URL + "Contacts";
    public static final String AUTH = REST_SERVICE_URL + "Auth/GetUser";
    public static final String TOKEN = REST_SERVICE_URL + "Messiging/RefreshToken";
    public static final String MESSAGES = REST_SERVICE_URL + "History/GetMessages";
    public static final String SEND_MESSAGE = REST_SERVICE_URL + "Messiging/SendMessage";
}