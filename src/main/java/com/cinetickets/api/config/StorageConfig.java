// package com.cinetickets.api.config;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.s3.S3Client;

// @Configuration
// public class StorageConfig {

//     @Value("${aws.s3.region}")
//     private String awsRegion;

//     @Value("${aws.credentials.access-key:}")
//     private String accessKey;

//     @Value("${aws.credentials.secret-key:}")
//     private String secretKey;

//     /**
//      * Configuración del cliente S3 para almacenamiento de archivos
//      */
//     @Bean
//     public S3Client s3Client() {
//         if (accessKey.isEmpty() || secretKey.isEmpty()) {
//             // Si no se proporcionan credenciales, usar el proveedor de credenciales predeterminado
//             return S3Client.builder()
//                     .region(Region.of(awsRegion))
//                     .build();
//         } else {
//             // Si se proporcionan credenciales, usar credenciales estáticas
//             AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
//             return S3Client.builder()
//                     .region(Region.of(awsRegion))
//                     .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
//                     .build();
//         }
//     }
// }