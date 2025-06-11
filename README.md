# Rutherford Diagnostics API Documentation

## Local Base URL
```
http://localhost:8080
```

## API Endpoints

### Patients

#### Get All Patients
```
GET /patients
```
Response: Array of Patient objects

#### Get Patient by ID
```
GET /patients/{id}
```
Response: Patient object

### Tests

#### Get All Tests
```
GET /tests
```
Response: Array of Test objects

#### Get Test by ID
```
GET /tests/{id}
```
Response: Test object

## Data Models

### Patient
```json
{
  "id": 1,
  "name": "Max",
  "species": "Dog",
  "breed": "Labrador",
  "dateOfBirth": "2020-01-01",
  "ownerName": "John Doe",
  "ownerContact": "123-456-7890",
  "tests": [
    {
      "id": 1,
      "name": "Blood Test",
      "datePerformed": "2024-05-14",
      "parameters": []
    }
  ]
}
```

### Test
```json
{
  "id": 1,
  "name": "Blood Test",
  "datePerformed": "2024-05-14",
  "patient": {
    "id": 1,
    "name": "Max",
    "species": "Dog",
    "breed": "Labrador",
    "dateOfBirth": "2020-01-01",
    "ownerName": "John Doe",
    "ownerContact": "123-456-7890"
  },
  "parameters": [
    {
      "id": 1,
      "name": "Hemoglobin",
      "unit": "g/dL",
      "referenceMin": 12.0,
      "referenceMax": 18.0,
      "history": []
    }
  ]
}
```

### Parameter
```json
{
  "id": 1,
  "name": "Hemoglobin",
  "unit": "g/dL",
  "referenceMin": 12.0,
  "referenceMax": 18.0,
  "test": {
    "id": 1,
    "name": "Blood Test",
    "datePerformed": "2024-05-14"
  },
  "history": [
    {
      "id": 1,
      "resultDate": "2024-05-14",
      "value": 15.0
    }
  ]
}
```

### ResultHistory
```json
{
  "id": 1,
  "resultDate": "2024-05-14",
  "value": 15.0,
  "parameter": {
    "id": 1,
    "name": "Hemoglobin",
    "unit": "g/dL",
    "referenceMin": 12.0,
    "referenceMax": 18.0
  }
}
```

## Notes

1. All dates are in ISO 8601 format (YYYY-MM-DD)
2. All IDs are auto-generated Long values
3. Nested objects may be returned with fewer fields to prevent circular references
4. All endpoints return JSON responses

## Error Responses

```json
{
  "timestamp": "2024-05-14T10:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with id: 1",
  "path": "/patients/1"
}
```
