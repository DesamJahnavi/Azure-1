import os

folder = "azure-demo-200-files"
os.makedirs(folder, exist_ok=True)

departments = ["CSE", "ECE", "EEE", "MECH"]
subjects = ["Java", "Python", "Cloud", "DBMS", "AI"]
types = ["Notes", "Assignment", "LabRecord", "QuestionPaper"]

for i in range(1, 201):
    department = departments[(i - 1) % len(departments)]
    subject = subjects[(i - 1) % len(subjects)]
    year = 2026
    file_type = types[(i - 1) % len(types)]

    filename = f"document_{i:03d}.txt"

    content = f"""Azure Document Organizer Demo File

Document ID: {i}
Department: {department}
Subject: {subject}
Year: {year}
Type: {file_type}

This file is part of the 200-document dataset
used to demonstrate Azure Blob Storage and
Azure Blob Index Tags.
"""

    with open(os.path.join(folder, filename), "w", encoding="utf-8") as file:
        file.write(content)

print("200 files created successfully.")