Steps to run the application:
Sample steps to be followed for UNIX/Windows/Linux based machines.

1: Allow application to connect to AWS S3
Option 1:
- Create new File 'aws_cred' in the root folder.
- Update the content as
    [default]
    aws_access_key_id=key
    aws_secret_access_key=secret
- Replace Key and secret with your AWS account credentials which has access to
    S3 bucket.
- set AWS_CREDENTIAL_PROFILES_FILE=./aws_cred - to tell application to read
    credentials from that file.

Option 2:
-  Export access credentials.

Option 3:
- Giving IAM permission role to access all s3 resources to the EC@ machine to which
    the application is deployed with which above steps can be avoided.

2: Provide appropriate bucket name in the application.properties file under src/resource folder
    [So that code is loosely coupled]

3: Build the application using
./gradlew clean build --stacktrace

4: Run Application
java -jar build/libs/FileUploaderService-0.0.1-SNAPSHOT.jar


Endpoint Details (When running locally)

POST http://localhost:8080/files/
     file - formData

     Response: { id: "id" } [CREATED]

GET http://localhost:8080/files/
     file - formData

     Response: All files in Zip format [OK}

GET http://localhost:8080/files/{id}/
     file - formData

     Response: downloads a file [OK]

DELETE http://localhost:8080/files/{id}/
     file - formData

     Response: Deletes a file [204 no content]


Updating Environment varibales:
On Linux, macOS, or Unix, use export :

export AWS_CREDENTIAL_PROFILES_FILE=./aws_cred
export AWS_S3_BUCKET = bucket name

On Windows, use set :

set AWS_S3_BUCKET = bucket name
set AWS_CREDENTIAL_PROFILES_FILE=./aws_cred