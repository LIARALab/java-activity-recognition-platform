package org.liara.api.documentation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.request.parser.APIDocumentedRequestParser;
import org.liara.api.request.parser.APIRequestParser;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER) 
public class RequestConfigurationOperationReaderPlugin implements OperationBuilderPlugin  
{
  @Override
  public void apply(@NonNull final OperationContext context) {
    final Optional<ParametersFromConfiguration> optionalAnnotation = context.findAnnotation(ParametersFromConfiguration.class);

    if (optionalAnnotation.isPresent()) { 
      final ParametersFromConfiguration annotation = optionalAnnotation.get();
      final CollectionRequestConfiguration<?> configuration = CollectionRequestConfiguration.fromRawClass(annotation.value());
      final List<APIRequestParser<?>> parsers = new ArrayList<>();
      
      if (annotation.cursorable()) {
        parsers.add(configuration.createCursorParser());
      }
      
      if (annotation.filterable()) {
        parsers.add(configuration.createFilterParser());
      }
      
      if (annotation.orderable()) {
        parsers.add(configuration.createOrderingParser());
      }
      
      if (annotation.groupable()) {
        parsers.add(configuration.createGroupingParser());
      }
      
      final List<Parameter> parameters = new LinkedList<>();
      
      parsers.stream().filter(
        x -> x instanceof APIDocumentedRequestParser
      ).forEach(
        x -> parameters.addAll(
          ((APIDocumentedRequestParser) x).getHandledParametersDocumentation()
        )
      );
      
      context.operationBuilder().parameters(parameters);
    }
  }

  @Override
  public boolean supports(@NonNull final DocumentationType documentationType) {
    return DocumentationType.SWAGGER_2.equals(documentationType); 
  }
}