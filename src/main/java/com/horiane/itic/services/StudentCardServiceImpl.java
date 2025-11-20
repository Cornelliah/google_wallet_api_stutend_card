package com.horiane.itic.services;

import java.util.UUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.security.PrivateKey;
import java.time.LocalDate;

import com.horiane.itic.dtos.StudentCardRequestDto;
import com.horiane.itic.dtos.StudentCardResponseDto;
import com.horiane.itic.models.StudentCard;
import com.horiane.itic.repository.StudentCardRepository;

import com.google.auth.oauth2.ServiceAccountCredentials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentCardServiceImpl implements StudentCardService {

    private final StudentCardRepository repository;

    @Value("${google.wallet.class-id}")
    private String studentCardClassId;

    @Value("${google.wallet.issuer-id}")
    private String issuerId;

    private static final String SERVICE_ACCOUNT_FILE = "/service-account.json";

    @Override
    public StudentCardResponseDto createStudentCard(StudentCardRequestDto request) {

        try {
        	         
            ServiceAccountCredentials credentials = loadServiceAccountCredentials();
            PrivateKey privateKey = credentials.getPrivateKey();
            String serviceAccountEmail = credentials.getClientEmail();
            
          
            String classId = issuerId + "." + studentCardClassId;

            createOrUpdateClass(classId, credentials);

            String objectSuffix = studentCardClassId + "-" + UUID.randomUUID();
            String objectId = issuerId + "." + objectSuffix;

            Map<String, Object> payload = buildWalletPayload(request, classId, objectId);

            
           
            String signedJwt = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .claim("iss", serviceAccountEmail)
                    .claim("aud", "google")
                    .claim("typ", "savetowallet")
                    .claim("payload", payload)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
            
          
             
           
            String saveUrl = "https://pay.google.com/gp/v/save/" + signedJwt;

            
            StudentCard card = StudentCard.builder()
                    .objectId(objectId)
                    .studentId(request.getStudentId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .program(request.getProgram())
                    .year(request.getYear())
                    .photoUrl(request.getPhotoUrl())
                    .saveUrl(saveUrl)
                    .expirationDate(LocalDate.parse(request.getExpirationDate()).atStartOfDay())
                    .build();

            repository.save(card);

           
            return StudentCardResponseDto.builder()
                    .objectId(objectId)
                    .saveUrl(saveUrl)
                    .message("Carte étudiante créée avec succès")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de la création de la carte étudiante", e);
            throw new RuntimeException("Impossible de créer la carte étudiante", e);
        }
    }
    
    
    

    private ServiceAccountCredentials loadServiceAccountCredentials() throws IOException {
    	
        InputStream stream = getClass().getResourceAsStream(SERVICE_ACCOUNT_FILE);
        if (stream == null) {
            throw new RuntimeException("Fichier service-account.json introuvable dans resources");
        }
        return ServiceAccountCredentials.fromStream(stream);
    }

    private void createOrUpdateClass (String classId, ServiceAccountCredentials credentials) throws IOException {
        String url = "https://walletobjects.googleapis.com/walletobjects/v1/genericClass/" + classId;

        // Token OAuth2 signé par service-account.json
        String accessToken = credentials.createScoped(
                List.of("https://www.googleapis.com/auth/wallet_object.issuer")
        ).refreshAccessToken().getTokenValue();

        // Définition du template dans la Class
        Map<String, Object> classPayload = Map.of(
                "id", classId,
                "issuerName", "ITIC Paris",
                "reviewStatus", "UNDER_REVIEW",
                "cardTemplateOverride", Map.of(
                        "cardRowTemplateInfos", List.of(
                                Map.of(
                                        "firstValue", Map.of(
                                                "defaultValue", Map.of("language", "fr", "value", "Clé")
                                        ),
                                        "secondValue", Map.of(
                                                "defaultValue", Map.of("language", "fr", "value", "Valeur")
                                        )
                                )
                        )
                )
        );

        // Vérifie si la class existe
        var request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        var client = java.net.http.HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // important !
            throw new RuntimeException("La requête GET pour vérifier la class a été interrompue", ie);
        }


        if (response.statusCode() == 404) {
            // Create the class
            System.out.println("Class not found → creating it…");

            var createRequest = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://walletobjects.googleapis.com/walletobjects/v1/genericClass"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(
                            new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(classPayload)
                    ))
                    .build();

            try {
                client.send(createRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("La requête POST pour créer la class a été interrompue", ie);
            }

        } else {
            System.out.println("Class already exists → OK");
        }
    }

    
    
    
    
    private Map<String, Object> buildWalletPayload(StudentCardRequestDto req,  String classId, String objectId) {
        
    	Map<String, Object> genericObject = new HashMap<>();
    	
    	
    	Map<String, Object> cardTemplateOverride = new HashMap<>();

    	Map<String, Object> row = Map.of(
    		    "firstValue", Map.of(
    		        "defaultValue", Map.of(
    		            "language", "fr",
    		            "value", "Filière"
    		        )
    		    ),
    		    "secondValue", Map.of(
    		        "defaultValue", Map.of(
    		            "language", "fr",
    		            "value", req.getProgram()
    		        )
    		    )
    		);

    		cardTemplateOverride.put("cardRowTemplateInfos", List.of(row));
    		genericObject.put("cardTemplateOverride", cardTemplateOverride);


    	genericObject.put("classId", classId);
        genericObject.put("id", objectId);
        
        genericObject.put("cardTitle", Map.of(
        	    "defaultValue", Map.of(
        	        "language", "fr",
        	        "value", "Carte Étudiante")
        	)) ;
           
        genericObject.put("header", Map.of(
        	    "defaultValue", Map.of(
        	        "language", "fr",
        	        "value",  req.getFirstName() + " " + req.getLastName()
        	    )
        	));
        genericObject.put("subheader", Map.of(
        	    "defaultValue", Map.of(
        	        "language", "fr",
        	        "value",  req.getEmail()
        	    )
        	));
        
        
        genericObject.put("barcode", Map.of("type", "QR_CODE", "value", req.getStudentId()));

        
        genericObject.put("textModulesData", List.of(
        	    Map.of("header", "Année", "body", req.getYear(), "language", "fr")
        	));
    
        
        genericObject.put("hexBackgroundColor", "#012087"); 
       
        
        genericObject.put("heroImage", Map.of(
                "sourceUri", Map.of(
                        "uri", "https://github.com/Cornelliah/assets_itic/blob/main/banner.png?raw=true",
                        "description", "Bannière"
                )
        ));
        
        genericObject.put("logo", Map.of(
        	    "sourceUri", Map.of(
        	        "uri", "https://github.com/Cornelliah/assets_itic/blob/main/logo1.png?raw=true",
        	        "description", "Logo ITIC Paris"
        	    )
        	));
        
  
        Map<String, Object> payload = new HashMap<>();
        payload.put("genericObjects", List.of(genericObject));
        return payload;
        
        
    }
}
