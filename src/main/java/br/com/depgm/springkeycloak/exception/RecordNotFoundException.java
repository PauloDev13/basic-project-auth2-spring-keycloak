package br.com.depgm.springkeycloak.exception;

public class RecordNotFoundException extends RuntimeException{

  public RecordNotFoundException(String param) {
    super("Registro não encontrado com o ID: " + param);
  }
}
