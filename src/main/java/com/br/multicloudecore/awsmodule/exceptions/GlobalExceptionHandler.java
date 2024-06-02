package com.br.multicloudecore.awsmodule.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.br.multicloudecore.util.ConstantsAWSMS.ERROR_MESSAGE_ATTRIBUTE;
import static com.br.multicloudecore.util.ConstantsAWSMS.ERROR_VIEW_NAME;

/**
 * GlobalExceptionHandler is a class that handles and manages exceptions
 * occurred throughout the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles an exception of type SentimentAnalysisException by creating and
   * returning a ModelAndView object with an error view.
   * The error message from the exception is added as a model attribute.
   *
   * @param ex the SentimentAnalysisException to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(AwsCredentialsException.class)
  public ModelAndView handleSentimentAnalysisException(AwsCredentialsException ex) {
    ModelAndView modelAndView = new ModelAndView(ERROR_VIEW_NAME);
    modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles an exception by creating a ModelAndView object with an error view,
   * setting the error message as a model attribute and returning the ModelAndView object.
   *
   * @param ex the exception to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(Exception.class)
  public ModelAndView handleException(Exception ex) {
    ModelAndView modelAndView = new ModelAndView(ERROR_VIEW_NAME);
    modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }
}
