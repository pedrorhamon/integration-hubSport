package com.starking.hubsport.util;

public class ErrorMessages {

    public static final String CONTACT_NOT_FOUND = "Contato com ID %d não encontrado";
    public static final String CONTACT_NOT_FOUND_HUBSPORT = "Contato com ID {} não encontrado no HubSpot. Ignorando.";
    public static final String TOKEN_REFRESH_ERROR = "Erro ao renovar token";
    public static final String TOKEN_EXCHANGE_ERROR = "Falha ao trocar o código por token.";
    public static final String NO_REFRESH_TOKEN = "Nenhum refresh token disponível";
    public static final String INVALID_HUBSPOT_RESPONSE = "Resposta inválida do HubSpot. Token nulo.";
    public static final String FETCH_CONTACT_ERROR = "Erro no contato";
    public static final String ERRO_SEARCH_CONTACT_HUBSPORT = "Erro ao buscar contato do HubSpot";

    private ErrorMessages() {
    }
}