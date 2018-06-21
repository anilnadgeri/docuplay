# Docusign client with Java

Repository: https://github.com/anilnadgeri/docuplay.git


## Introduction

Docusign client with Java, springboot and Docusign client SDK

## Installation

Requirements: Java 8

Download or clone this repository. Then:

1. Create private.key and public.key files in project home directory. This is the key configured in your Docusign account
2. Modify account specifc settings in DocuPlayController::initializeDocusignClient. These include integrator key, redirecURI and impersonated user's guid
3. Hit http://localhost:8080/docu-play/documents to send sample documents for signature

