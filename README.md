# Wizard Backend

## Endpoints

### GET /annotationGroup?projectId=id
Gets an overview of the annotation groups belonging to a project.

#### Response

```json
{
  "groups": [
    {
      "id": "0123456789abcdef12345678",
      "name": "name of first group"
    },
    {
      "id": "abcdef0123456789abcdef12",
      "name": "name of other group"
    }
  ]
}

```

### POST /annotationGroup
Creates a new annotation group.

#### Request
```json
{
  "projectId": 12345,
  "annotations": [42, 1337],
  "name": "my new group"
}

```

#### Response
```json
{
  "groupId": "0123456789abcdef12345678"
}

```

### Post /startAnalysis

Endpoint: `POST /startAnalysis`

#### Request
```JSON
{
    "groupId": "0123456789abcdef12345678",
    "analysis": ["he", "rgb"]
}
```

#### Response: 202 Accepted
