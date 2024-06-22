export interface HistoricalDataPoint {
    date: string; // Assuming date is a string in ISO format
    enrollment: number;
}

export interface Course {
    id: string;
    title: string;
    code: string;
    description: string;
    currentEnrollment: number;
    capacity: number;
    historicalData: HistoricalDataPoint[];
}
