# Sử dụng một nền tảng Java 21 gọn nhẹ
FROM openjdk:21-jdk-slim

# Tạo một thư mục tên 'app' bên trong container
WORKDIR /app

# Sao chép file .jar từ thư mục target vào thư mục 'app' và đổi tên thành app.jar
COPY target/*.jar app.jar

# Lệnh sẽ được chạy khi container khởi động: "java -jar app.jar"
ENTRYPOINT ["java", "-jar", "app.jar"]
