# Dummy lambda

This is a very dumb lambda used to test the performance on several layers of AWS lambdas calling each other. The lambda can only do one of two things: 

- Handle a request, sleeping for a specified amount of milliseconds before returning a dumb "hello world" JSON response. The time it sleeps is configured through and environment variable you need to set. The variable is called `WAIT_TIME`
- Sending a new request to a URL you configure in the environment variable. The environment variable is called `PROXY_URL`.

If non of the two environment variables `WAIT_TIME` or `PROXY_URL` are set the request will fail!

You build the artifact by running `./gradlew shadowJar`. This places a file in the project under `build/libs/dummy-lambda.jar`. This file you manually upload to AWS and connect with a public API Gateway in AWS. Once that is done you get an endpoint and should be able to execute the lambda function.
