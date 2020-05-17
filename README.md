# Phrases service

Phrases service for phrases project, this microservice provides the phrases for the project and all the sources needed.

### Prerequisites

To run this microservice use the following maven environment variables:

```
-DconfigUrl
-Dspring.profiles.active
```

Examples:

```
Localhost url
-DconfigUrl=http://localhost:1111

Docker container url
-DconfigUrl=http://config-server:1111

Spring active profile for DEV
-Dspring.profiles.active=dev

Sring active profile for PROD
-Dspring.profiles.active=prod
```