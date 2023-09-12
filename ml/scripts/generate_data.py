import pandas as pd
import numpy as np
import os

# Configuration
NUM_COURSES = 200
NUM_DAYS = 90
OUTPUT_DIR = "ml/data"
OUTPUT_FILE = os.path.join(OUTPUT_DIR, "course_enrollment_data.csv")

def generate_enrollment_data():
    """
    Generates a synthetic dataset of course enrollment statistics over time.
    """
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)

    data = []
    course_ids = [f"CSC{i:03}" for i in range(1, NUM_COURSES + 1)]

    for course_id in course_ids:
        capacity = np.random.randint(25, 200)
        # Start with a low initial enrollment
        initial_enrollment = np.random.randint(0, int(capacity * 0.2))

        # Simulate enrollment trend (e.g., logistic growth)
        growth_rate = np.random.uniform(0.05, 0.15)

        enrollment_trend = [initial_enrollment]
        for day in range(1, NUM_DAYS):
            # Sigmoid-like growth that slows as it approaches capacity
            growth_factor = 1 / (1 + np.exp(-growth_rate * (day - NUM_DAYS / 2)))
            current_enrollment = int(initial_enrollment + (capacity - initial_enrollment) * growth_factor)

            # Add some daily random fluctuation
            high_fluctuation = max(1, int(capacity * 0.02))
            fluctuation = np.random.randint(-high_fluctuation, high_fluctuation)
            current_enrollment += fluctuation

            # Ensure enrollment doesn't exceed capacity or drop below zero
            current_enrollment = max(0, min(capacity, current_enrollment))
            enrollment_trend.append(current_enrollment)

        for day, enrollment in enumerate(enrollment_trend):
            # Add a feature indicating if the course is likely to close soon
            # For this mock data, let's say it's likely to close if enrollment is >95% of capacity
            will_close = 1 if (enrollment / capacity) > 0.95 else 0

            data.append({
                "course_id": course_id,
                "date": pd.to_datetime("2023-09-01") + pd.to_timedelta(day, unit='d'),
                "enrollment": enrollment,
                "capacity": capacity,
                "day_of_week": (pd.to_datetime("2023-09-01") + pd.to_timedelta(day, unit='d')).dayofweek,
                "days_since_start": day,
                "will_close_in_24h": will_close # Target variable
            })

    df = pd.DataFrame(data)
    df.to_csv(OUTPUT_FILE, index=False)
    print(f"Successfully generated synthetic data and saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    generate_enrollment_data()
