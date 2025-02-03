# Passwordless Authentication with Passkeys

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?&style=flat&logo=kotlin&logoColor=white)


This is a simple web server that demonstrates the use of the WebAuthn API. It provides a simple web page that allows users to register and authenticate using a Passkey.
## Demo

![Demo](demo.gif)

## Brief Overview

### 🔑 Registration Ceremony
- The server generates and stores a random challenge to prevent replay attacks
- Client provides public key and credential ID, which are stored securely in the userStore

### ✅ Authentication Ceremony
- Server issues a fresh challenge for each authentication attempt
- Client must provide:
   - Original challenge (prevents replay attacks)
   - Signed challenge (proves possession of private key)
   - Client data JSON (determine the current state or flow of the WebAuthn ceremony)
   - Authenticator data (cryptographic metadata)
- Server verifies the signature using the stored public key and combined authenticator/client data

```mermaid
sequenceDiagram
    participant C as Client
    participant S as Server
    
    %% Registration Flow
    Note over C,S: Registration Ceremony
    C->>+S: Register(username)
    S-->>-C: Return challenge
    
    C->>+S: Complete Registration(publicKey, credentialId)
    S->>S: Store UserData
    S-->>-C: Registration Successful
    
    %% Authentication Flow
    Note over C,S: Authentication Ceremony
    C->>+S: Start Authentication(username)
    S-->>-C: Return challenge & credentialId
    
    C->>+S: Authenticate(signedChallenge)
    S->>S: Verify signature with stored publicKey
    alt Invalid Signature
        S-->>C: Authentication Failed
    else Valid Signature
        S-->>C: Authentication Successful
    end
```

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```



