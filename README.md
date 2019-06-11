# spring-boot-starter-logging-ndjson
by Eric Goetschalckx

Provides NDJSON Structured JSON logs for Spring Boot applications.

## Minimum Requirements
Requires `spring-boot-starter-web` `1.4.0.RELEASE` or higher at runtime.

## Spring Profiles
Use the following Spring profile(s) to enable the various logging output options.

- `default` or `log-dev`
    - "Standard" Spring logging, colorized, pretty-printed, etc...
- `log-ndjson`
    - NDJSON Structured logs to the console.
    - Intended option for containerized deployments.
  
The problem with this approach is that if you already use a custom Spring Profile besides default, you will get no logs, which is less than ideal.

The following configuration properties are available for managing logging settings:

```yaml
# Logging settings 
logging:
  
  # Newline-delimited JSON Logging Settings
  ndjson:
      
    # Enable JSON pretty-print
    # Optional (default is false)
    pretty-print: false
```
