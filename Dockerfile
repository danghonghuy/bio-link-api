# --- GIAI ĐOẠN 1: BUILD DỰ ÁN ---
# Sử dụng một image có sẵn Maven và JDK 21 để làm môi trường build
FROM maven:3.9-eclipse-temurin-21 AS build

# Tạo thư mục làm việc
WORKDIR /workspace/app

# Sao chép file pom.xml trước để tận dụng cache của Docker
COPY pom.xml .

# Sao chép toàn bộ mã nguồn còn lại
COPY src ./src

# Chạy lệnh Maven để build dự án và tạo ra file .jar
RUN mvn install -DskipTests


# --- GIAI ĐOẠN 2: CHẠY ỨNG DỤNG ---
# Sử dụng một image Java 21 gọn nhẹ để chạy
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Sao chép file .jar đã được build từ giai đoạn 'build' sang đây
COPY --from=build /workspace/app/target/*.jar app.jar

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]