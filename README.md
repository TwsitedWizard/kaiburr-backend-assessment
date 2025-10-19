# Kaiburr Full-Stack Assessment: Backend API

This repository contains the Java Spring Boot backend application for the Kaiburr assessment (Tasks 1, 2, and 4). This RESTful API allows for creating, retrieving, deleting, and executing shell command tasks.

---

## Prerequisites

To build and run this project locally, you will need the following software installed:

* Java Development Kit (JDK) 17
* Apache Maven
* MongoDB
* Docker Desktop (for later tasks)

---

## Build and Run Instructions

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/TwsitedWizard/kaiburr-backend-assessment.git](https://github.com/TwsitedWizard/kaiburr-backend-assessment.git)
    cd kaiburr-backend-assessment/task-api
    ```

2.  **Ensure MongoDB is running:**
    Make sure your local MongoDB server is active on the default port `27017`.

3.  **Run the application using Maven:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

---

## API Usage

The following endpoints are available to interact with the API.

### 1. Create a Task

* **Method:** `PUT`
* **URL:** `/tasks/`
* **Description:** Creates a new task. The task object is sent in the request body.
* **Request Body Example:**
    ```json
    {
        "name": "List Files Task",
        "owner": "Saran",
        "command": "ls -l"
    }
    ```
* **Success Response (200 OK):**
    ```json
    {
        "id": "6715a8c8e4b0c8e8e8e8e8e8",
        "name": "List Files Task",
        "owner": "Saran",
        "command": "ls -l",
        "taskExecutions": null
    }
    ```

### 2. Get All Tasks

* **Method:** `GET`
* **URL:** `/tasks/`
* **Description:** Retrieves a list of all tasks.
* **Success Response (200 OK):**
    ```json
    [
        {
            "id": "6715a8c8e4b0c8e8e8e8e8e8",
            "name": "List Files Task",
            "owner": "Saran",
            "command": "ls -l",
            "taskExecutions": null
        }
    ]
    ```

### 3. Get a Single Task by ID

* **Method:** `GET`
* **URL:** `/tasks/{id}`
* **Description:** Retrieves a single task by its unique ID. Returns a 404 error if not found.
* **Success Response (200 OK):**
    ```json
    {
        "id": "6715a8c8e4b0c8e8e8e8e8e8",
        "name": "List Files Task",
        "owner": "Saran",
        "command": "ls -l",
        "taskExecutions": null
    }
    ```

### 4. Find Tasks by Name

* **Method:** `GET`
* **URL:** `/tasks/find/by-name/{name}`
* **Description:** Finds all tasks whose name contains the given string. Returns a 404 error if none are found.
* **Success Response (200 OK):**
    ```json
    [
        {
            "id": "6715a8c8e4b0c8e8e8e8e8e8",
            "name": "List Files Task",
            "owner": "Saran",
            "command": "ls -l",
            "taskExecutions": null
        }
    ]
    ```

### 5. Execute a Task

* **Method:** `PUT`
* **URL:** `/tasks/execute/{id}`
* **Description:** Executes the shell command associated with the task ID and stores the execution history.
* **Success Response (200 OK):**
    ```
    Exit Code: 0
    Output:
    total 8
    -rw-r--r-- 1 user 197121 430 Oct 19 01:30 pom.xml
    drwxr-xr-x 1 user 197121   0 Oct 19 01:30 src
    ```

### 6. Delete a Task

* **Method:** `DELETE`
* **URL:** `/tasks/{id}`
* **Description:** Deletes a task by its ID.
* **Success Response:** `204 No Content`

---

## Task 1 Screenshots
Screenshot 1: Creating a New Task
This image shows a PUT request to the /tasks/ endpoint. The JSON body contains the details for a new task. The server responds with 200 OK and returns the created task, now including its unique ID.
<img width="1912" height="1078" alt="SS1" src="https://github.com/user-attachments/assets/4ec2a1dc-70e4-4e2b-ab3a-fc0ff7e1bc42" />


Screenshot 2: Getting All Tasks
This image shows a GET request to the /tasks/ endpoint. The server responds with 200 OK and a JSON array containing all the tasks currently in the database.
<img width="1911" height="1078" alt="SS2" src="https://github.com/user-attachments/assets/cb1a5ec9-d281-4cb9-84aa-0b426bab1030" />

Screenshot 3
: Getting a Task by ID
This image shows a GET request to the /tasks/{id} endpoint, using the specific ID of the task created earlier. The server responds with 200 OK and returns the single, requested task object.
<img width="1911" height="1075" alt="SS3" src="https://github.com/user-attachments/assets/bed2ad56-f3fc-442e-a5d7-c9384c451318" />


Screenshot 4: Executing a Task
This image shows a PUT request to the /tasks/execute/{id} endpoint. The server executes the command associated with the task (echo Hello Springboot) and responds with 200 OK, showing the command's exit code and its standard output.
<img width="1912" height="1076" alt="SS4" src="https://github.com/user-attachments/assets/404a9d75-4b80-44dd-afc8-5782c2c3b33b" />

Screenshot 5: Deleting a Task
This image shows a DELETE request to the /tasks/{id} endpoint. The server successfully deletes the task and responds with a 204 No Content status, as expected for a successful deletion.
<img width="1911" height="1076" alt="SS5" src="https://github.com/user-attachments/assets/6635196b-7e20-4850-8c0d-b05adafcbea1" />


---

## Task 2: Containerization and Deployment

This section details the steps to containerize the application with Docker and deploy it to a Kubernetes cluster.

### Build and Push the Docker Image

1.  Navigate to the `task-api` directory which contains the `Dockerfile`.
2.  Build the Docker image:
    ```bash
    # Replace 'saran39' with your Docker Hub username
    docker build -t saran39/kaiburr-task-app:latest .
    ```
3.  Log in to Docker Hub and push the image:
    ```bash
    docker login
    docker push saran39/kaiburr-task-app:latest
    ```

### Deploy to Kubernetes

1.  Ensure your Kubernetes cluster (e.g., from Docker Desktop) is running.
2.  Apply all the manifest files from the `task-api` directory:
    ```bash
    kubectl apply -f mongodb-statefulset.yaml
    kubectl apply -f rbac.yaml
    kubectl apply -f deployment.yaml
    kubectl apply -f service.yaml
    ```
3.  Verify that the pods are running:
    ```bash
    kubectl get pods
    ```
4.  The application will be accessible at `http://localhost:30080`.

### Task 2 Screenshots
