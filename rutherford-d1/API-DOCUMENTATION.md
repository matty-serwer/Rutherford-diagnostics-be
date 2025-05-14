# Rutherford Diagnostics API Documentation

## Overview

The Rutherford Diagnostics API provides access to patient records, diagnostic tests, and test results for veterinary diagnostic purposes. This RESTful API enables clinicians to retrieve patient information, view diagnostic test details, and track test results over time.

## Base URL

```
http://localhost:8080
```

## Authentication

Currently, the API is open and does not require authentication. JWT authentication will be implemented in future versions.

## Endpoints

### Patients

#### Get All Patients

Retrieves a list of all patients with basic information.

- **URL**: `/patients`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content Example**:
  ```json
  [
    {
      "id": 1,
      "name": "Fido",
      "species": "Dog",
      "breed": "Labrador",
      "dateOfBirth": "2018-05-20",
      "ownerName": "Jane Doe",
      "ownerContact": "555-1234"
    },
    {
      "id": 2,
      "name": "Whiskers",
      "species": "Cat",
      "breed": "Siamese",
      "dateOfBirth": "2019-03-15",
      "ownerName": "John Smith",
      "ownerContact": "555-5678"
    }
  ]
  ```

#### Get Patient Details

Retrieves detailed information about a specific patient, including their diagnostic history.

- **URL**: `/patients/{id}`
- **Method**: `GET`
- **URL Parameters**:
  - `id` (required): The ID of the patient
- **Success Response**:
  - **Code**: 200 OK
  - **Content Example**:
  ```json
  {
    "id": 1,
    "name": "Fido",
    "species": "Dog",
    "breed": "Labrador",
    "dateOfBirth": "2018-05-20",
    "ownerName": "Jane Doe",
    "ownerContact": "555-1234",
    "diagnosticHistory": [
      {
        "id": 1,
        "name": "Blood Test",
        "datePerformed": "2023-05-10"
      },
      {
        "id": 2,
        "name": "X-Ray",
        "datePerformed": "2023-05-15"
      }
    ]
  }
  ```
- **Error Response**:
  - **Code**: 404 Not Found
  - **Content**: Empty

### Diagnostic Tests

#### Get All Tests

Retrieves a list of all available diagnostic tests.

- **URL**: `/tests`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content Example**:
  ```json
  [
    {
      "id": 1,
      "name": "Blood Test",
      "datePerformed": "2023-05-10"
    },
    {
      "id": 2,
      "name": "X-Ray",
      "datePerformed": "2023-05-15"
    }
  ]
  ```

#### Get Test Details

Retrieves detailed information about a specific diagnostic test, including parameters and result history.

- **URL**: `/tests/{id}`
- **Method**: `GET`
- **URL Parameters**:
  - `id` (required): The ID of the test
- **Success Response**:
  - **Code**: 200 OK
  - **Content Example**:
  ```json
  {
    "id": 1,
    "name": "Blood Test",
    "datePerformed": "2023-05-10",
    "patient": {
      "id": 1,
      "name": "Fido",
      "species": "Dog",
      "breed": "Labrador",
      "dateOfBirth": "2018-05-20",
      "ownerName": "Jane Doe",
      "ownerContact": "555-1234"
    },
    "parameters": [
      {
        "id": 1,
        "name": "Hemoglobin",
        "unit": "g/dL",
        "referenceMin": 12.0,
        "referenceMax": 18.0,
        "history": [
          {
            "resultDate": "2023-05-09",
            "value": 15.2
          },
          {
            "resultDate": "2023-05-10",
            "value": 16.1
          }
        ]
      }
    ]
  }
  ```
- **Error Response**:
  - **Code**: 404 Not Found
  - **Content**: Empty

## Data Models

### Patient

```json
{
  "id": "Long",
  "name": "String",
  "species": "String",
  "breed": "String",
  "dateOfBirth": "LocalDate",
  "ownerName": "String",
  "ownerContact": "String"
}
```

### Test

```json
{
  "id": "Long",
  "name": "String",
  "datePerformed": "LocalDate",
  "patient": "Patient",
  "parameters": ["Parameter"]
}
```

### Parameter

```json
{
  "id": "Long",
  "name": "String",
  "unit": "String",
  "referenceMin": "Double",
  "referenceMax": "Double",
  "history": ["ResultHistory"]
}
```

### ResultHistory

```json
{
  "resultDate": "LocalDate",
  "value": "Double"
}
```

## Status Codes

- `200 OK` - Request succeeded
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server encountered an error

## Additional Resources

- Interactive API documentation is available at `/swagger-ui`
- OpenAPI specification is available at `/api-docs` 