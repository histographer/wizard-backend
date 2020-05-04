# Wizard Backend
This is the backend/middleware component of the Wizard application. TODO link to more info

## Getting started
### Prerequisites
In order to build the application, you need Docker and Docker Compose. For development and testing, you also need Apache Maven 3 and JDK 1.8.

### How to set environment variables
- [Windows](https://www.techjunkie.com/environment-variables-windows-10/)
- [Mac](https://apple.stackexchange.com/questions/106778)
- Linux:
    - Open `/etc/environment` in a text editor
    - For each variable, add a line of the form `export VARIABLE_NAME=value`
    - Run `source /etc/environment` in a terminal
    - The variables should now be set for that terminal session.
    They will be set globally whenever the system reboots. 

### Installing
First, copy the contents file `.env.sample` into a file with the name `.env`, modifying the values of the environment variables as necessary. Then run the commands `docker network create wizard` and `docker-compose -f docker-compose.dev.yml up --build -d`. A development container will become available at `http://localhost:8080`, or whatever port you configured in `.env`.

## Testing
### Preparing to run the integration tests
Before you can run the integration tests, you have to set some environment variables:

```
WIZARD_TEST_MONGODB_HOST: localhost
WIZARD_TEST_MONGODB_PORT: 27019
WIZARD_TEST_MONGODB_USERNAME: test_user
WIZARD_TEST_MONGODB_PASSWORD: test_password
WIZARD_TEST_MONGODB_DATABASE: wizard_test
WIZARD_TEST_TOMCAT_PROTOCOL: http
WIZARD_TEST_TOMCAT_HOST: localhost
WIZARD_TEST_TOMCAT_PORT: 8082

```

The port numbers can be changed, but the rest should not be changed.

In addition, you need to copy the contents of the file `.analysis_test.env.sample` into a file with the name `.analysis_test.env`.

### Running the tests
The integration tests will only work if the application is already running. You can start it in a test container
with the command `./integration_test_setup.sh`, and stop it with the command `./integration_test_teardown.sh`.
You may need to add the prefix `sudo -E` to these commands.

To run the unit tests and integration tests:

```console
mvn verify

```

To run only the unit tests:

```console
mvn test

```

To run only the integration tests:

```console
mvn -Dskip.unit.tests=true verify

```

To run the style tests:

```console
mvn checkstyle:check

```

## Deployment
TODO

## Documentation
The API is described in [API.md](API.md).

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md).

## License
TODO

## Acknowledgements
TODO
