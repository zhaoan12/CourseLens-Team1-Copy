import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.metrics import f1_score, classification_report
import onnx
import onnxmltools
from skl2onnx.common.data_types import FloatTensorType
import os

# Configuration
DATA_FILE = "ml/data/course_enrollment_data.csv"
MODEL_OUTPUT_DIR = "backend/src/main/resources/ml"
MODEL_OUTPUT_FILE = os.path.join(MODEL_OUTPUT_DIR, "course_prediction_model.onnx")

def train_and_export_model():
    """
    Loads data, trains a gradient boosting model, and exports it to ONNX format.
    """
    if not os.path.exists(MODEL_OUTPUT_DIR):
        os.makedirs(MODEL_OUTPUT_DIR)

    # 1. Load Data
    df = pd.read_csv(DATA_FILE)
    df['date'] = pd.to_datetime(df['date'])

    # 2. Feature Engineering
    df['enrollment_ratio'] = df['enrollment'] / df['capacity']
    df['days_until_end'] = (df['date'].max() - df['date']).dt.days

    # Lag features - enrollment in previous days
    df = df.sort_values(by=['course_id', 'date'])
    for lag in [1, 3, 7]:
        df[f'enrollment_lag_{lag}'] = df.groupby('course_id')['enrollment'].shift(lag)

    # Rolling window features
    df['enrollment_rol_mean_7'] = df.groupby('course_id')['enrollment'].transform(
        lambda x: x.rolling(7, min_periods=1).mean()
    )
    df['enrollment_rol_std_7'] = df.groupby('course_id')['enrollment'].transform(
        lambda x: x.rolling(7, min_periods=1).std()
    )

    df = df.dropna()

    # 3. Define Features (X) and Target (y)
    features = [
        'enrollment_ratio',
        'days_until_end',
        'day_of_week',
        'days_since_start',
        'enrollment_lag_1',
        'enrollment_lag_3',
        'enrollment_lag_7',
        'enrollment_rol_mean_7',
        'enrollment_rol_std_7'
    ]
    target = 'will_close_in_24h'

    X = df[features].astype(np.float32)
    y = df[target]

    # 4. Split Data
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )

    # 5. Train Model
    print("Training Gradient Boosting Classifier...")
    model = GradientBoostingClassifier(
        n_estimators=100,
        learning_rate=0.1,
        max_depth=3,
        random_state=42
    )
    model.fit(X_train, y_train)

    # 6. Evaluate Model
    y_pred = model.predict(X_test)
    f1 = f1_score(y_test, y_pred)
    print(f"\nModel F1 Score: {f1:.2f}")
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred))

    # 7. Export to ONNX
    print(f"\nExporting model to ONNX format at {MODEL_OUTPUT_FILE}...")
    initial_type = [('float_input', FloatTensorType([None, len(features)]))]
    onnx_model = onnxmltools.convert_sklearn(model, initial_types=initial_type)

    with open(MODEL_OUTPUT_FILE, "wb") as f:
        f.write(onnx_model.SerializeToString())

    print("Model successfully exported.")

if __name__ == "__main__":
    train_and_export_model()
