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
      "name": "Complete Blood Count"
    },
    {
      "id": 23,
      "name": "Chemistry Panel"
    }
  ]
}
```

### Test Summary
```json
{
  "id": 22,
  "name": "Complete Blood Count"
}
```

### Test Detail (with time-series parameter data)
```json
{
  "id": 64,
  "name": "Complete Blood Count",
  "patient": {
    "id": 28,
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
      "id": 292,
      "value": 14.5,
      "datePerformed": "2024-03-14"
    },
    {
      "id": 293,
      "value": 13.8,
      "datePerformed": "2024-02-14"
    },
    {
      "id": 294,
      "value": 15.2,
      "datePerformed": "2024-01-12"
    },
    {
      "id": 295,
      "value": 15.2,
      "datePerformed": "2023-09-24"
    },
    {
      "id": 296,
      "value": 13.1,
      "datePerformed": "2022-04-21"
    }
  ]
}
```

## Data Structure Notes

### Test Structure - **IMPORTANT FOR FRONTEND**
- **Tests no longer have a single `datePerformed` field**
- Each test has shared properties: `parameterName`, `unit`, `referenceMin`, `referenceMax`
- All parameters within a test share these properties (e.g., all measurements are of the same type like "Hemoglobin" in "g/dL")
- **Individual parameters contain `id`, `value`, and `datePerformed`** (the actual measurement date)

### Time-Series Data for Charts
- **Each parameter represents a single measurement taken on a specific date**
- Multiple parameters per test create a time series for graphing
- Dates span multiple years for meaningful trend analysis
- **Perfect for plotting parameter values over time**

### Test Types
The system supports various diagnostic test types:
- **Complete Blood Count**: Hemoglobin measurements (g/dL)
- **Chemistry Panel**: Glucose measurements (mg/dL)
- **Thyroid Panel**: T4 measurements (ug/dL)  
- **Liver Function**: ALT measurements (U/L)

### Frontend Implementation Notes
1. **Chart Data**: Use `parameters` array to plot time-series graphs
   - X-axis: `datePerformed` values
   - Y-axis: `value` values
   - Reference lines: `referenceMin` and `referenceMax`

2. **Test Summaries**: No longer include dates (tests group measurements by type)

3. **Patient History**: Shows test types without dates (dates are at parameter level)

## Notes

1. All dates are in ISO 8601 format (YYYY-MM-DD)
2. All IDs are auto-generated Long values
3. Nested objects may be returned with fewer fields to prevent circular references
4. All endpoints return JSON responses
5. **Parameter dates are individual measurement dates, not test dates**
6. Each parameter represents a single measurement value taken on a specific date
7. **Tests group related measurements by parameter type (e.g., all Hemoglobin readings)**

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

## Sample API Calls for Frontend Development

### Get all patients
```bash
curl http://localhost:8080/patient
```

### Get patient with diagnostic history
```bash
curl http://localhost:8080/patient/1
```

### Get all tests (summary)
```bash
curl http://localhost:8080/test
```

### Get test with time-series data for charting
```bash
curl http://localhost:8080/test/64
```

This structure enables the frontend to create meaningful time-series charts showing how patient parameters change over time!
