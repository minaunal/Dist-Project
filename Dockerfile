# Maven ve Java için temel imajı kullanıyoruz
FROM maven:3.9.3-eclipse-temurin-17 as builder

# Çalışma dizinini belirliyoruz
WORKDIR /app

# Proje dosyalarını kopyalıyoruz
COPY . .

# Maven ile uygulamayı derliyoruz ve bağımlılıkları indiriyoruz
RUN mvn clean package -DskipTests

# Çalışan uygulama katmanını oluşturuyoruz
FROM eclipse-temurin:17-jdk

# Çalışma dizinini belirtiyoruz
WORKDIR /app

# Builder aşamasında üretilen JAR dosyasını kopyalıyoruz
COPY --from=builder /app/target/*.jar app.jar

# Uygulama portunu açıyoruz
EXPOSE 7000

# Uygulamayı başlatıyoruz
ENTRYPOINT ["java", "-jar", "app.jar"]