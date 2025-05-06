# News Application API Documentation

This document provides detailed information about all API endpoints exposed by the News Application, including example request/response payloads and instructions for testing.

## Table of Contents

1. [Authentication](#authentication)
2. [Users](#users)
3. [Categories](#categories)
4. [Tags](#tags)
5. [Articles](#articles)
6. [Comments](#comments)
7. [Replies](#replies)

## Base URL

All endpoints are relative to the base URL: `http://localhost:8080/api`
At the first run the project will seed an admin check you database for username and password

## Authentication

### Login

Authenticates a user and returns a JWT token.

- **URL**: `/auth/login`
- **Method**: `POST`
- **Authentication**: None
- **Request Body**:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjoiUk9MRV9BRE1JTiIsImlhdCI6MTY0MTY1MzIxMSwiZXhwIjoxNjQxNzM5NjExfQ.example-token",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### Register

Registers a new user with READER role by default.

- **URL**: `/auth/register`
- **Method**: `POST`
- **Authentication**: None
- **Request Body**:

```json
{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com",
  "firstName": "New",
  "lastName": "User",
  "phone": "1234567890"
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 4,
  "username": "newuser",
  "email": "newuser@example.com",
  "firstName": "New",
  "lastName": "User",
  "phone": "1234567890",
  "profilePic": null,
  "role": "READER",
  "isActive": true,
  "createdAt": "2025-05-06T10:30:45.123Z",
  "updatedAt": "2025-05-06T10:30:45.123Z"
}
```

## Users

### Get All Users

Returns a list of all users (Admin only).

- **URL**: `/users`
- **Method**: `GET`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "firstName": "Admin",
    "lastName": "User",
    "phone": "1234567890",
    "profilePic": null,
    "role": "ADMIN",
    "isActive": true,
    "createdAt": "2025-05-06T10:00:00.000Z",
    "updatedAt": "2025-05-06T10:00:00.000Z"
  },
  {
    "id": 2,
    "username": "writer",
    "email": "writer@example.com",
    "firstName": "Writer",
    "lastName": "User",
    "phone": "0987654321",
    "profilePic": null,
    "role": "WRITER",
    "isActive": true,
    "createdAt": "2025-05-06T10:15:00.000Z",
    "updatedAt": "2025-05-06T10:15:00.000Z"
  }
]
```

### Get User by ID

Returns details for a specific user (Admin or self only).

- **URL**: `/users/{id}`
- **Method**: `GET`
- **Authentication**: Required (ADMIN role or owner)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 2,
  "username": "writer",
  "email": "writer@example.com",
  "firstName": "Writer",
  "lastName": "User",
  "phone": "0987654321",
  "profilePic": null,
  "role": "WRITER",
  "isActive": true,
  "createdAt": "2025-05-06T10:15:00.000Z",
  "updatedAt": "2025-05-06T10:15:00.000Z"
}
```

### Get Current User Profile

Returns the profile of the currently authenticated user.

- **URL**: `/users/profile`
- **Method**: `GET`
- **Authentication**: Required
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 3,
  "username": "reader",
  "email": "reader@example.com",
  "firstName": "Reader",
  "lastName": "User",
  "phone": "5555555555",
  "profilePic": null,
  "role": "READER",
  "isActive": true,
  "createdAt": "2025-05-06T10:20:00.000Z",
  "updatedAt": "2025-05-06T10:20:00.000Z"
}
```

### Update User

Updates a user's information (Admin or self only).

- **URL**: `/users/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN role or owner)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "firstName": "Updated",
  "lastName": "Name",
  "phone": "9999999999",
  "profilePic": "profile-image.jpg"
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 3,
  "username": "reader",
  "email": "reader@example.com",
  "firstName": "Updated",
  "lastName": "Name",
  "phone": "9999999999",
  "profilePic": "profile-image.jpg",
  "role": "READER",
  "isActive": true,
  "createdAt": "2025-05-06T10:20:00.000Z",
  "updatedAt": "2025-05-06T11:30:45.123Z"
}
```

### Change User Role

Changes a user's role (Admin only).

- **URL**: `/users/{id}/role`
- **Method**: `PATCH`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Parameters**:
  - `role`: ADMIN, WRITER, or READER

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 3,
  "username": "reader",
  "email": "reader@example.com",
  "firstName": "Updated",
  "lastName": "Name",
  "phone": "9999999999",
  "profilePic": "profile-image.jpg",
  "role": "WRITER",
  "isActive": true,
  "createdAt": "2025-05-06T10:20:00.000Z",
  "updatedAt": "2025-05-06T12:30:45.123Z"
}
```

### Delete User

Deletes a user (Admin or self only).

- **URL**: `/users/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN role or owner)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Categories

### Get All Categories

Returns a list of all categories.

- **URL**: `/categories`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
[
  {
    "id": 1,
    "name": "Technology",
    "description": "Articles about technology trends and innovations",
    "createdAt": "2025-05-06T09:00:00.000Z",
    "updatedAt": "2025-05-06T09:00:00.000Z"
  },
  {
    "id": 2,
    "name": "Health",
    "description": "Health and wellness articles",
    "createdAt": "2025-05-06T09:15:00.000Z",
    "updatedAt": "2025-05-06T09:15:00.000Z"
  }
]
```

### Get Category by ID

Returns details for a specific category.

- **URL**: `/categories/{id}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 1,
  "name": "Technology",
  "description": "Articles about technology trends and innovations",
  "createdAt": "2025-05-06T09:00:00.000Z",
  "updatedAt": "2025-05-06T09:00:00.000Z"
}
```

### Create Category

Creates a new category (Admin only).

- **URL**: `/categories`
- **Method**: `POST`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "name": "Science",
  "description": "Scientific discoveries and research"
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 3,
  "name": "Science",
  "description": "Scientific discoveries and research",
  "createdAt": "2025-05-06T13:30:45.123Z",
  "updatedAt": "2025-05-06T13:30:45.123Z"
}
```

### Update Category

Updates a category (Admin only).

- **URL**: `/categories/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "name": "Science & Research",
  "description": "Scientific discoveries, research, and innovations"
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 3,
  "name": "Science & Research",
  "description": "Scientific discoveries, research, and innovations",
  "createdAt": "2025-05-06T13:30:45.123Z",
  "updatedAt": "2025-05-06T14:15:22.456Z"
}
```

### Delete Category

Deletes a category (Admin only).

- **URL**: `/categories/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Tags

### Get All Tags

Returns a list of all tags.

- **URL**: `/tags`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
[
  {
    "id": 1,
    "name": "AI",
    "createdAt": "2025-05-06T09:30:00.000Z",
    "updatedAt": "2025-05-06T09:30:00.000Z"
  },
  {
    "id": 2,
    "name": "Climate",
    "createdAt": "2025-05-06T09:35:00.000Z",
    "updatedAt": "2025-05-06T09:35:00.000Z"
  }
]
```

### Get Tag by ID

Returns details for a specific tag.

- **URL**: `/tags/{id}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 1,
  "name": "AI",
  "createdAt": "2025-05-06T09:30:00.000Z",
  "updatedAt": "2025-05-06T09:30:00.000Z"
}
```

### Create Tag

Creates a new tag (Admin or Writer role).

- **URL**: `/tags`
- **Method**: `POST`
- **Authentication**: Required (ADMIN or WRITER role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "name": "Robotics"
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 3,
  "name": "Robotics",
  "createdAt": "2025-05-06T15:10:33.789Z",
  "updatedAt": "2025-05-06T15:10:33.789Z"
}
```

### Update Tag

Updates a tag (Admin only).

- **URL**: `/tags/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "name": "Robotics & Automation"
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 3,
  "name": "Robotics & Automation",
  "createdAt": "2025-05-06T15:10:33.789Z",
  "updatedAt": "2025-05-06T15:45:22.123Z"
}
```

### Delete Tag

Deletes a tag (Admin only).

- **URL**: `/tags/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN role)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Articles

### Get All Articles

Returns a paginated list of all articles.

- **URL**: `/articles`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "content": [
    {
      "id": 1,
      "title": "The Future of AI in Healthcare",
      "content": "Article content goes here...",
      "description": "How AI is transforming the healthcare industry",
      "featuredImage": "ai-healthcare.jpg",
      "status": "PUBLISHED",
      "views": 150,
      "author": {
        "id": 2,
        "username": "writer",
        "email": "writer@example.com"
      },
      "category": {
        "id": 1,
        "name": "Technology"
      },
      "createdAt": "2025-05-06T11:00:00.000Z",
      "updatedAt": "2025-05-06T11:30:00.000Z",
      "tags": [
        {
          "id": 1,
          "name": "AI"
        },
        {
          "id": 4,
          "name": "Healthcare"
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 45,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "first": true,
  "empty": false
}
```

### Get Published Articles

Returns a paginated list of published articles.

- **URL**: `/articles/published`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Similar to "Get All Articles" but with only published articles

### Get Article by ID

Returns details for a specific article.

- **URL**: `/articles/{id}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 1,
  "title": "The Future of AI in Healthcare",
  "content": "Article content goes here with full details...",
  "description": "How AI is transforming the healthcare industry",
  "featuredImage": "ai-healthcare.jpg",
  "status": "PUBLISHED",
  "views": 151,
  "author": {
    "id": 2,
    "username": "writer",
    "email": "writer@example.com",
    "firstName": "Writer",
    "lastName": "User"
  },
  "category": {
    "id": 1,
    "name": "Technology",
    "description": "Articles about technology trends and innovations"
  },
  "createdAt": "2025-05-06T11:00:00.000Z",
  "updatedAt": "2025-05-06T11:30:00.000Z",
  "tags": [
    {
      "id": 1,
      "name": "AI"
    },
    {
      "id": 4,
      "name": "Healthcare"
    }
  ],
  "images": [
    {
      "id": 1,
      "image": "ai-robot-surgery.jpg",
      "description": "AI-assisted robotic surgery",
      "articleId": 1,
      "createdAt": "2025-05-06T11:15:00.000Z",
      "updatedAt": "2025-05-06T11:15:00.000Z"
    }
  ]
}
```

### Get Articles by Author

Returns a paginated list of articles by a specific author.

- **URL**: `/articles/author/{authorId}`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Similar to "Get All Articles" but filtered by author

### Get Articles by Category

Returns a paginated list of articles in a specific category.

- **URL**: `/articles/category/{categoryId}`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Similar to "Get All Articles" but filtered by category

### Search Articles

Searches articles by keyword.

- **URL**: `/articles/search`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `keyword`: Search term
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Similar to "Get All Articles" but filtered by search term

### Get Top Articles

Returns the top N articles by view count.

- **URL**: `/articles/top`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `count`: Number of articles to return (default: 5)

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Array of the most-viewed articles

### Create Article

Creates a new article (Admin or Writer role).

- **URL**: `/articles`
- **Method**: `POST`
- **Authentication**: Required (ADMIN or WRITER role)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "title": "Climate Change: Latest Research",
  "content": "Detailed article content goes here...",
  "description": "Summary of the latest climate change research",
  "featuredImage": "climate-research.jpg",
  "status": "DRAFT",
  "categoryId": 2,
  "tagIds": [2, 5]
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 3,
  "title": "Climate Change: Latest Research",
  "content": "Detailed article content goes here...",
  "description": "Summary of the latest climate change research",
  "featuredImage": "climate-research.jpg",
  "status": "DRAFT",
  "views": 0,
  "author": {
    "id": 2,
    "username": "writer",
    "email": "writer@example.com",
    "firstName": "Writer",
    "lastName": "User"
  },
  "category": {
    "id": 2,
    "name": "Science & Research",
    "description": "Scientific discoveries, research, and innovations"
  },
  "createdAt": "2025-05-06T16:30:45.123Z",
  "updatedAt": "2025-05-06T16:30:45.123Z",
  "tags": [
    {
      "id": 2,
      "name": "Climate"
    },
    {
      "id": 5,
      "name": "Research"
    }
  ],
  "images": []
}
```

### Update Article

Updates an existing article (Admin or article author only).

- **URL**: `/articles/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN role or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "title": "Climate Change: Latest Scientific Research",
  "content": "Updated article content goes here...",
  "description": "Comprehensive summary of the latest climate change research",
  "featuredImage": "climate-research-updated.jpg",
  "status": "PUBLISHED",
  "categoryId": 2,
  "tagIds": [2, 5, 6]
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated article object

### Update Article Status

Updates an article's status (Admin or article author only).

- **URL**: `/articles/{id}/status`
- **Method**: `PATCH`
- **Authentication**: Required (ADMIN role or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Query Parameters**:
  - `status`: DRAFT, PUBLISHED, or ARCHIVED

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated article object

### Delete Article

Deletes an article (Admin or article author only).

- **URL**: `/articles/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN role or article author)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Comments

### Get Comments by Article

Returns a paginated list of comments for a specific article.

- **URL**: `/comments/article/{articleId}`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "content": [
    {
      "id": 1,
      "comment": "Great article! Very informative.",
      "likes": 5,
      "status": 1,
      "articleId": 1,
      "user": {
        "id": 3,
        "username": "reader",
        "email": "reader@example.com"
      },
      "email": "reader@example.com",
      "createdAt": "2025-05-06T12:00:00.000Z",
      "updatedAt": "2025-05-06T12:00:00.000Z",
      "replies": [
        {
          "id": 1,
          "content": "Thank you for your feedback!",
          "likes": 2,
          "status": 1,
          "commentId": 1,
          "user": {
            "id": 2,
            "username": "writer",
            "email": "writer@example.com"
          },
          "email": "writer@example.com",
          "parentReplyId": null,
          "createdAt": "2025-05-06T12:30:00.000Z",
          "updatedAt": "2025-05-06T12:30:00.000Z"
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 5,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 5,
  "first": true,
  "empty": false
}
```

### Get Comment by ID

Returns details for a specific comment.

- **URL**: `/comments/{id}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 1,
  "comment": "Great article! Very informative.",
  "likes": 5,
  "status": 1,
  "articleId": 1,
  "user": {
    "id": 3,
    "username": "reader",
    "email": "reader@example.com",
    "firstName": "Reader",
    "lastName": "User"
  },
  "email": "reader@example.com",
  "createdAt": "2025-05-06T12:00:00.000Z",
  "updatedAt": "2025-05-06T12:00:00.000Z",
  "replies": [
    {
      "id": 1,
      "content": "Thank you for your feedback!",
      "likes": 2,
      "status": 1,
      "commentId": 1,
      "user": {
        "id": 2,
        "username": "writer",
        "email": "writer@example.com"
      },
      "email": "writer@example.com",
      "parentReplyId": null,
      "createdAt": "2025-05-06T12:30:00.000Z",
      "updatedAt": "2025-05-06T12:30:00.000Z"
    }
  ]
}
```

### Create Comment

Creates a new comment (authenticated users only).

- **URL**: `/comments`
- **Method**: `POST`
- **Authentication**: Required
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "comment": "This is my comment on the article.",
  "articleId": 1
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 6,
  "comment": "This is my comment on the article.",
  "likes": 0,
  "status": 1,
  "articleId": 1,
  "user": {
    "id": 3,
    "username": "reader",
    "email": "reader@example.com",
    "firstName": "Reader",
    "lastName": "User"
  },
  "email": "reader@example.com",
  "createdAt": "2025-05-06T17:45:33.789Z",
  "updatedAt": "2025-05-06T17:45:33.789Z",
  "replies": []
}
```

### Update Comment

Updates a comment (Admin, comment author, or article author only).

- **URL**: `/comments/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN, comment author, or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "comment": "Updated comment text."
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated comment object

### Like Comment

Increments the like count for a comment.

- **URL**: `/comments/{id}/like`
- **Method**: `POST`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated comment object with incremented likes

### Update Comment Status

Updates a comment's status (Admin or article author only).

- **URL**: `/comments/{id}/status`
- **Method**: `PATCH`
- **Authentication**: Required (ADMIN role or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Query Parameters**:
  - `status`: 1 (Active) or 0 (Inactive)

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated comment object

### Delete Comment

Deletes a comment (Admin, comment author, or article author only).

- **URL**: `/comments/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN, comment author, or article author)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Replies

### Get Replies by Comment

Returns a paginated list of replies for a specific comment.

- **URL**: `/replies/comment/{commentId}`
- **Method**: `GET`
- **Authentication**: None
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field (default: "createdAt,desc")

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "content": [
    {
      "id": 1,
      "content": "Thank you for your feedback!",
      "likes": 2,
      "status": 1,
      "commentId": 1,
      "user": {
        "id": 2,
        "username": "writer",
        "email": "writer@example.com"
      },
      "email": "writer@example.com",
      "parentReplyId": null,
      "createdAt": "2025-05-06T12:30:00.000Z",
      "updatedAt": "2025-05-06T12:30:00.000Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

### Get Replies by Parent Reply

Returns a list of replies for a parent reply (for nested replies).

- **URL**: `/replies/parent/{parentReplyId}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
[
  {
    "id": 3,
    "content": "I agree with this point.",
    "likes": 1,
    "status": 1,
    "commentId": 1,
    "user": {
      "id": 4,
      "username": "reader2",
      "email": "reader2@example.com"
    },
    "email": "reader2@example.com",
    "parentReplyId": 1,
    "createdAt": "2025-05-06T14:15:00.000Z",
    "updatedAt": "2025-05-06T14:15:00.000Z"
  }
]
```

### Get Reply by ID

Returns details for a specific reply.

- **URL**: `/replies/{id}`
- **Method**: `GET`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:

```json
{
  "id": 1,
  "content": "Thank you for your feedback!",
  "likes": 2,
  "status": 1,
  "commentId": 1,
  "user": {
    "id": 2,
    "username": "writer",
    "email": "writer@example.com",
    "firstName": "Writer",
    "lastName": "User"
  },
  "email": "writer@example.com",
  "parentReplyId": null,
  "createdAt": "2025-05-06T12:30:00.000Z",
  "updatedAt": "2025-05-06T12:30:00.000Z"
}
```

### Create Reply

Creates a new reply to a comment or another reply (authenticated users only).

- **URL**: `/replies`
- **Method**: `POST`
- **Authentication**: Required
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "content": "This is my reply to the comment.",
  "commentId": 1,
  "parentReplyId": null
}
```

- **Success Response**:
  - **Code**: 201 Created
  - **Content**:

```json
{
  "id": 4,
  "content": "This is my reply to the comment.",
  "likes": 0,
  "status": 1,
  "commentId": 1,
  "user": {
    "id": 3,
    "username": "reader",
    "email": "reader@example.com",
    "firstName": "Reader",
    "lastName": "User"
  },
  "email": "reader@example.com",
  "parentReplyId": null,
  "createdAt": "2025-05-06T18:20:15.456Z",
  "updatedAt": "2025-05-06T18:20:15.456Z"
}
```

### Update Reply

Updates a reply (Admin, reply author, or article author only).

- **URL**: `/replies/{id}`
- **Method**: `PUT`
- **Authentication**: Required (ADMIN, reply author, or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Request Body**:

```json
{
  "content": "Updated reply text."
}
```

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated reply object

### Like Reply

Increments the like count for a reply.

- **URL**: `/replies/{id}/like`
- **Method**: `POST`
- **Authentication**: None

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated reply object with incremented likes

### Update Reply Status

Updates a reply's status (Admin or article author only).

- **URL**: `/replies/{id}/status`
- **Method**: `PATCH`
- **Authentication**: Required (ADMIN role or article author)
- **Headers**:
  - `Authorization: Bearer {token}`
- **Query Parameters**:
  - `status`: 1 (Active) or 0 (Inactive)

- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Updated reply object

### Delete Reply

Deletes a reply (Admin, reply author, or article author only).

- **URL**: `/replies/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (ADMIN, reply author, or article author)
- **Headers**:
  - `Authorization: Bearer {token}`

- **Success Response**:
  - **Code**: 204 No Content

## Testing the API

### Prerequisites

1. Make sure you have the following tools installed:
   - [Postman](https://www.postman.com/downloads/) or any API testing tool
   - [PostgreSQL](https://www.postgresql.org/download/) database
   - Java 17 or later

2. Set up the database:
   - Create a PostgreSQL database named `newsapp`
   - Update database credentials in `application.properties` if necessary

3. Run the application:
   - Use your IDE or run `mvn spring-boot:run` from the command line
   - The application will start on `http://localhost:8080`

### Testing Workflow

Here's a recommended workflow for testing the API:

1. **Register Users**:
   - Register an admin, writer, and reader using the `/api/auth/register` endpoint
   - Update the roles of admin and writer using the admin account

2. **Authenticate Users**:
   - Use the `/api/auth/login` endpoint to get JWT tokens for each user
   - Save these tokens for subsequent requests

3. **Create Categories and Tags**:
   - Use the admin token to create categories and tags
   - Verify they appear in the GET endpoints

4. **Create Articles**:
   - Use the writer token to create articles
   - Update article status to PUBLISHED
   - Verify they appear in the public endpoints

5. **Test Comment and Reply Functionality**:
   - Use the reader token to create comments on articles
   - Use the writer token to reply to comments
   - Test the like functionality
   - Test updating and deleting comments/replies

### Example Testing Scenarios

#### 1. Register and Login Flow

1. Register three users (admin, writer, reader)
2. Login with admin credentials
3. Change the roles of other users
4. Login with writer and reader credentials

#### 2. Content Management Flow

1. Create categories and tags (as admin)
2. Create articles in draft status (as writer)
3. Update articles to published status
4. Verify articles appear in the public endpoints

#### 3. Interaction Flow

1. View published articles (as reader)
2. Comment on an article (as reader)
3. Reply to the comment (as writer)
4. Like the comment and reply
5. Delete the reply (as writer)

#### 4. Admin Management Flow

1. View all users (as admin)
2. Update a user's role (as admin)
3. Delete a user (as admin)
4. View all articles, comments, and replies

### Postman Collection

You can import the following example requests into Postman to test the API:

1. **Authentication Requests**:
   - Register Admin
   - Register Writer
   - Register Reader
   - Login as Admin
   - Login as Writer
   - Login as Reader

2. **User Management Requests**:
   - Get All Users
   - Change User Role
   - Update User
   - Delete User

3. **Category and Tag Requests**:
   - Create Category
   - Get All Categories
   - Create Tag
   - Get All Tags

4. **Article Requests**:
   - Create Article
   - Get All Articles
   - Get Published Articles
   - Get Article by ID
   - Update Article
   - Update Article Status
   - Delete Article

5. **Comment and Reply Requests**:
   - Create Comment
   - Get Comments by Article
   - Like Comment
   - Create Reply
   - Get Replies by Comment
   - Like Reply

This comprehensive testing approach will ensure that all endpoints and functionality in your News Application API are working as expected.