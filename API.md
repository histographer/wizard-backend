# API documentation

## GET /analysisInformation?analysisId=[id]
Gets information about a single analysis. The status can be "pending", "success", or "failure". 

### Example response body

```json
{
  "groupName": "name of the annotation group",
  "analysisId": "0123456789abcdef12345678",
  "annotationGroupId": "abcdef0123456789abcdef12",
  "status": "success"
}

```

## GET /analysisInformation?annotationGroupId=[id]
Gets information about all the analyses associated with an annotation group.

### Example response body

```json
{
  "groupName": "name of the group",
  "analyses": [
    {
      "groupName": "name of the group",
      "analysisId": "0123456789abcdef12345678",
      "annotationGroupId": "abcdef0123456789abcdef12",
      "status": "success"
    },
    {
      "groupName": "name of the group",
      "analysisId": "9876543210fedcba98765432",
      "annotationGroupId": "abcdef0123456789abcdef12",
      "status": "pending"
    }
  ]
}

```

## GET /annotationGroup?projectId=id
Gets an overview of the annotation groups belonging to a project.

### Example response body

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

## POST /annotationGroup
Creates a new annotation group.

Response status: 201 Created.

### Example request body
```json
{
  "projectId": 12345,
  "annotations": [42, 1337],
  "name": "my new group"
}

```

### Example response body
```json
{
  "groupId": "0123456789abcdef12345678"
}

```

## POST /startAnalysis

Starts an analysis.

Response status: 202 Accepted.

### Example request body
```json
{
  "groupId": "0123456789abcdef12345678",
  "analysis": ["he", "rgb"]
}

```

### Example response body
```json
{
  "analysisId": "abcdef0123456789abcdef12"
}

```

## GET /analysisResult?groupId=[id]
Gets the analysis results for an annotation group.

### Example response body
```json
{
"groupId": "1",
"annotations": [
  {
    "annotationId": 1,
    "results": [
      {
        "components": [
          {
            "name": "H",
            "components": [
              {
                "name": "mean",
                "val": -0.333
              },
              {
                "name": "std",
                "val": -0.333
              }
            ]
          },
          {
            "name": "E",
            "components": [
              {
                "name": "mean",
                "val": -0.333
              },
              {
                "name": "std",
                "val": -0.333
              }
            ]
          }
        ],
        "name": "he"
      }
    ]
  }
],
"groupName": "name of the group"
}

```

## POST /analysisResult
For use by the analysis component. Creates the results of an analysis.

Response status: 201 Created.

### Example request body
```json
{
"analysisId": "0123456789abcdef12345678",
"annotations": [
  {
    "annotationId": 1064743,
    "results": [
      {
        "name": "HE",
        "components": [
          {
            "name": "H",
            "components": [
              {
                "name": "mean",
                "val": -0.44739692704068174
              },
              {
                "name": "std",
                "val": 0.08928628449947514
              }
            ]
          }
        ]
      }
    ]
  }
]
}

```

## GET /exportAnalysisResults?groupId=[id]
Exports the analysis results of a given annotation group. The optional query parameter `analyzeType` specifies which type of analysis should be included. If it's absent, all analysis types will be included.

### Example response body
```json
{
  "data": "<some csv data>"
}

```

## GET /availableAnalysisTypes
Gets the names of the types of analysis that are available.

### Example response body
```json
{
  "analysisTypes": ["he", "hsv"]
}

```
