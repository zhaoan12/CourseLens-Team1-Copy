# CourseLens

CourseLens is a web application that provides live University of Toronto course enrollment statistics. It helps students track course availability and predict which courses are likely to fill up soon, empowering them to make informed decisions about their academic schedule.

## Features

*   **Live Enrollment Data:** View up-to-the-minute enrollment numbers for any UofT course.
*   **Historical Data Visualization:** See historical enrollment trends for courses using Chart.js.
*   **Closure Prediction:** A machine learning model predicts courses that are likely to close within 24 hours.
*   **Personalized Watchlist:** Students can add courses to a personal watchlist to receive notifications.
*   **User-Friendly Interface:** A clean and intuitive UI built with Angular.

## Tech Stack

*   **Frontend:** Angular, Chart.js
*   **Backend:** Spring Boot, Java
*   **Machine Learning:** scikit-learn, ONNX Runtime
*   **Database:** PostgreSQL (or another relational database)

## Getting Started

### Prerequisites

*   Node.js and npm
*   Angular CLI
*   Java 17 or higher
*   Maven
*   Python 3.8 or higher

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/zhaoan12/(name of repo)
    cd courselens
    ```

2.  **Backend Setup:**
    ```bash
    cd backend
    mvn install
    # Set up your database configuration in application.properties
    mvn spring-boot:run
    ```

3.  **Frontend Setup:**
    ```bash
    cd ../frontend
    npm install
    ng serve
    ```

4.  **ML Model Setup:**
    The machine learning model is trained separately. See the `ml/` directory for instructions on training and exporting the model.

## Project Structure

```
courselens/
├── backend/                  # Spring Boot application
│   ├── src/
│   └── pom.xml
├── frontend/                 # Angular application
│   ├── src/
│   └── package.json
├── ml/                       # scikit-learn model and training scripts
│   ├── data/
│   ├── notebooks/
│   └── scripts/
└── README.md
```
