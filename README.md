# Azure Blob Document Organizer

## Project Overview

Azure Blob Document Organizer is a cloud-based document management system that uses **Azure Blob Storage** and **Azure Blob Index Tags** to organize and search documents efficiently.

Users can upload documents along with metadata such as Department, Subject, Year, and Type. The metadata is stored as Blob Index Tags, allowing documents to be searched based on their tags.

## Problem Statement

Managing a large number of documents using normal folders and filenames can make it difficult and time-consuming to find the required files.

Our solution provides a cloud-based approach where documents are stored in Azure Blob Storage and organized using metadata-based Blob Index Tags.

## Our Solution

We developed a web-based document organization system that:

- Uploads documents to Azure Blob Storage.
- Adds metadata using Blob Index Tags.
- Searches documents using tag values.
- Displays stored documents through a web interface.
- Allows users to download documents.
- Provides an option to delete documents.
- Uses Azure Blob Storage directly for document storage and tag-based searching.

## Technologies Used

### Frontend
- HTML
- CSS
- JavaScript

### Backend
- Java
- Spring Boot
- REST APIs
- Maven

### Cloud
- Microsoft Azure
- Azure Blob Storage
- Azure Blob Index Tags

### Testing
- Postman
- Azure Portal
- Web Browser

## Key Features

### 1. Document Upload

Users can upload a document and provide metadata:

- Department
- Subject
- Year
- Type

Example:

```text
Department = CSE
Subject = Java
Year = 2026
Type = Notes