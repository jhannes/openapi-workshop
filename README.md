# OpenAPI workshop

This is the source code for the Rubics workshop on OpenAPI with React and Typescript by Johannes Brodwall.

The project contains two modules:

* The Java code for the backend. You will not need to examine on this for the workshop, as there will be a server running on https://openapi-workshop.azurewebsites.net
* The React code for the client. This is where the work will be happening

## Workshop

### 1. Verify that you can access the backend 

First, make sure that you can access the backend.

1. Go to https://openapi-workshop.azurewebsites.net
2. Click Authorize. This will open a new tab to log you on with Active Directory. Use your normal Active Directory account
3. When you return to the OpenAPI spec page, click on the "GET /user/current" operation and Try it out. Verify that you get back your own identity


### 2. Start the frontend and authorize

In the `react-frontend` directory:

1. Run `npm install` to download dependencies
2. Run `npm start` to start the development server with Parcel
3. Go to `http://localhost:1234` (NB: it's important to run on this exact port as the backend requires it)
4. Click "Login" to log in
5. Verify that you are logged in with the correct name

### 3. Exercise

In the workshop, we will be adding a field to the Pet-data structure

1. As a warm-up: Update the pet listing `react-client/src/pets/ListPets.tsx` to display more of the Pet properties
2. The workshop facilitator will demonstrate adding the new field to the API and backend and deploy a new backend version
3. The workshop facilitator will publish a new API version
4. Update the API by running `npm install -P @jhannes/openapi-workshop`. **This requires you to restart Parcel**
5. The NewPet view will now fail to compile as there is a missing field
6. Update `react-client/src/pets/NewPet.tsx` and `react-client/src/pets/ListPets.tsx` to submit and retrieve the new field


## Components

### React app

The frontend app is created with React 17.0.3 and built with Parcel. It uses Typescript 4.2.4. The front-end routing is done with React Router 5.2.0

The API to the Petstore app is build using [openapi-generator-typescript-fetch-api](https://github.com/jhannes/openapi-generator-typescript-fetch-api) and is distributed as the NPM package `@jhannes/openapi-workshop`

It has a few helper functions:

* `useLoader` - handles the state of an asynchronous loading operation, such as an API call
* `useSubmit` - handles the state of an asynchronous submit operation
* `useLocalStorage` - stores a normal React state hook to `localStorage`

## Java backend

The Java backend is created as a self-contained Jetty-server which implements the Petstore sample app. The controllers are implemented with [action-controller](https://github.com/jhannes/action-controller) and the database interaction with [fluent-jdbc](https://github.com/jhannes/fluent-jdbc). The Java DTOs are generated using [openapi-generator-java-annotationfree](https://github.com/jhannes/openapi-generator-java-annotationfree). The serialization is done with [jsonbuddy](https://github.com/anders88/jsonbuddy)

### Deployment

Requires Azure Account and [Azure CLI tools](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)

1. Login to Azure CLI: `az login`
2. Deploy to your personal AppService: `mvn azure-webapp:deploy`
3Deploy production: `mvn azure-webapp:deploy -DopenapiWorkshop.azure.appName=openapi-workshop`
