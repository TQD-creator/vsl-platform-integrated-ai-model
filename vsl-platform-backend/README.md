# VSL Platform Backend (Vietnamese Sign Language)

> **Capstone Project**: Hệ thống nền tảng chuyển đổi văn bản sang thủ ngữ và ngược lại, tích hợp AI nhận diện cử chỉ và quản lý từ điển.

## 📋 Giới thiệu

Đây là Core Backend API của hệ thống VSL Platform. Hệ thống cung cấp các dịch vụ:
- **Authentication**: Đăng nhập, Đăng ký, JWT, Phân quyền (Admin/User/Guest).
- **Dictionary Management**: Tra cứu từ điển, quản lý video thủ ngữ (kết hợp PostgreSQL & Elasticsearch).
- **AI Integration**: Cầu nối (Gateway) nhận diện cử chỉ tay (Landmarks) và chuyển đổi thành văn bản tiếng Việt có dấu.
- **CMS**: Quản lý người dùng, duyệt đóng góp từ cộng đồng.

## 🛠 Tech Stack

- **Java Development Kit (JDK)**: 21 (LTS)
- **Framework**: Spring Boot 3.3
- **Database**: PostgreSQL 16
- **Search Engine**: Elasticsearch 8.11
- **Build Tool**: Maven
- **Deployment**: Docker & Docker Compose

## 🚀 Hướng dẫn Cài đặt & Chạy (Local)

### 1. Yêu cầu tiên quyết
- Đã cài đặt Java 21.
- Đã cài đặt Docker Desktop (Khuyên dùng) hoặc PostgreSQL & Elasticsearch cài rời.
- Đã chạy **Python AI Service** ở port `5000` và `5001`.

### 2. Cấu hình
File cấu hình chính nằm tại: `src/main/resources/application.properties`.
Mặc định hệ thống sẽ kết nối đến:
- DB: `jdbc:postgresql://localhost:5432/vsl_db`
- Elastic: `localhost:9200`

### 3. Chạy ứng dụng
Mở terminal tại thư mục gốc của dự án:

```bash
# Bước 1: Build dự án
./mvnw clean install

# Bước 2: Chạy
./mvnw spring-boot:run