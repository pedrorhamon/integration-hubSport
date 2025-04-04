package com.starking.hubsport.util;

public class HubspotUrls {

    public static final String BASE_API_URL = "https://api.hubapi.com";
    public static final String CONTACTS_ENDPOINT = BASE_API_URL + "/crm/v3/objects/contacts";
    public static final String CONTACT_BY_ID_ENDPOINT = CONTACTS_ENDPOINT + "/%s";
    public static final String TOKEN_ENDPOINT = BASE_API_URL + "/oauth/v1/token";
    public static final String AUTHORIZE_URL = "https://app.hubspot.com/oauth/authorize";
    public static final String CREATE_CONTACT = "created contact";
    public static final String CONTACT_BY_ID = "contact by ID";


    private HubspotUrls() {
    }
}

