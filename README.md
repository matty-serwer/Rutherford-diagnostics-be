# Rutherford Diagnostics API Documentation

## Local Base URL
```
http://localhost:8080
```

## API Endpoints

### Patients

#### Get All Patients
```
GET /patient
```
Response: Array of Patient objects

#### Get Patient by ID
```
GET /patient/{id}
```
Response: Patient object with diagnostic history

### Tests

#### Get All Tests
```
GET /test
```
Response: Array of Test summary objects

#### Get Test by ID
```
GET /test/{id}
```
Response: Test object with full details

## Data Models

### Patient Summary
```json
{
  "id": 1,
  "name": "Walter",
  "species": "Dog",
  "breed": "Labrador",
  "dateOfBirth": "2018-05-20",
  "ownerName": "Jane Doe",
  "ownerContact": "555-1234"
}
```

### Patient Detail (with diagnostic history)
```json
{
  "id": 1,
  "name": "Walter",
  "species": "Dog",
  "breed": "Labrador",
  "dateOfBirth": "2018-05-20",
  "ownerName": "Jane Doe",
  "ownerContact": "555-1234",
  "diagnosticHistory": [
    {
      "id": 22,
      "name": "Complete Blood Count",
      "datePerformed": "2024-03-14"
    },
    {
      "id": 23,
      "name": "Chemistry Panel",
      "datePerformed": "2024-02-14"
    }
  ]
}
```

### Test Summary
```json
{
  "id": 22,
  "name": "Complete Blood Count",
  "datePerformed": "2024-03-14"
}
```

### Test Detail
```json
{
  "id": 22,
  "name": "Complete Blood Count",
  "datePerformed": "2024-03-14",
  "patient": {
    "id": 10,
    "name": "Walter",
    "species": "Dog",
    "breed": "Labrador",
    "dateOfBirth": "2018-05-20",
    "ownerName": "Jane Doe",
    "ownerContact": "555-1234"
  },
  "parameterName": "Hemoglobin",
  "unit": "g/dL",
  "referenceMin": 12.0,
  "referenceMax": 18.0,
  "parameters": [
    {
      "id": 106,
      "value": 14.5
    },
    {
      "id": 107,
      "value": 13.8
    },
    {
      "id": 108,
      "value": 15.2
    }
  ]
}
```

## Data Structure Notes

### Test Structure
- Each test has shared properties: `parameterName`, `unit`, `referenceMin`, `referenceMax`
- All parameters within a test share these properties (e.g., all measurements are of the same type like "Hemoglobin" in "g/dL")
- Individual parameters only contain `id` and `value` (the actual measurement)

### Test Types
The system supports various diagnostic test types:
- **Complete Blood Count**: Hemoglobin measurements
- **Chemistry Panel**: Glucose measurements  
- **Thyroid Panel**: T4 measurements
- **Liver Function**: ALT measurements

## Notes

1. All dates are in ISO 8601 format (YYYY-MM-DD)
2. All IDs are auto-generated Long values
3. Nested objects may be returned with fewer fields to prevent circular references
4. All endpoints return JSON responses
5. Parameter properties (name, unit, reference ranges) are shared at the test level
6. Each parameter represents a single measurement value of the test's parameter type

## Error Responses

```json
{
  "timestamp": "2024-05-14T10:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with id: 1",
  "path": "/patient/1"
}
```
