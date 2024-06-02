package com.br.multicloudecore.awsmodule.exceptions;

public class AwsCredentialsException extends RuntimeException {


  public AwsCredentialsException(String message) {
    super(message);
  }


  public AwsCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}
