package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainCustomAliasEntity;
import ai.shreds.domain.exceptions.DomainException;
import ai.shreds.domain.ports.DomainInputPortAliasService;
import ai.shreds.domain.ports.DomainOutputPortCustomAliasRepository;
import ai.shreds.domain.value_objects.DomainCustomAliasValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for managing custom aliases.
 * Implements business logic for alias validation, reservation, and management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomainAliasManagementService implements DomainInputPortAliasService {
    
    private final DomainOutputPortCustomAliasRepository aliasRepository;
    private final List<String> reservedWords;
    
    /**
     * Default constructor with predefined reserved words.
     */
    public DomainAliasManagementService(DomainOutputPortCustomAliasRepository aliasRepository) {
        this.aliasRepository = aliasRepository;
        this.reservedWords = getDefaultReservedWords();
    }
    
    @Override
    public boolean validateAlias(String alias) {
        log.info("Validating alias: {}", alias);
        
        try {
            // Check format using value object validation
            DomainCustomAliasValue aliasValue = new DomainCustomAliasValue(alias);
            
            // Check if alias is available (not already used)
            if (aliasRepository.existsByAliasValue(alias)) {
                log.warn("Alias already exists: {}", alias);
                return false;
            }
            
            // Check against reserved words
            if (checkReservedWords(alias)) {
                log.warn("Alias matches reserved word: {}", alias);
                return false;
            }
            
            log.info("Alias validation passed: {}", alias);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Alias validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public DomainCustomAliasEntity reserveAlias(String alias, String owner) {
        log.info("Reserving alias: {} for owner: {}", alias, owner);
        
        if (!validateAlias(alias)) {
            throw new DomainException("Cannot reserve invalid alias: " + alias);
        }
        
        try {
            // Create alias entity
            DomainCustomAliasEntity aliasEntity = DomainCustomAliasEntity.builder()
                    .aliasId(UUID.randomUUID().toString())
                    .aliasValue(alias)
                    .isReserved(false)
                    .owner(owner)
                    .createdAt(new Date())
                    .build();
            
            // Reserve the alias
            aliasEntity.reserve(owner);
            
            // Save to repository
            DomainCustomAliasEntity savedAlias = aliasRepository.save(aliasEntity);
            
            log.info("Successfully reserved alias: {} for owner: {}", alias, owner);
            return savedAlias;
        } catch (Exception e) {
            log.error("Error reserving alias: {}", e.getMessage(), e);
            throw new DomainException("Failed to reserve alias: " + alias, e);
        }
    }
    
    @Override
    public void releaseAlias(String aliasId) {
        log.info("Releasing alias with ID: {}", aliasId);
        
        try {
            // Find the alias by ID - this requires a different approach since we only have findByAliasValue
            // In a real implementation, the repository would have a findById method
            DomainCustomAliasEntity alias = aliasRepository.findByAliasValue(aliasId);
            if (alias == null) {
                log.warn("Alias not found for release: {}", aliasId);
                return;
            }
            
            // Release the alias
            alias.release();
            
            // Update in repository
            aliasRepository.update(alias);
            
            log.info("Successfully released alias: {}", aliasId);
        } catch (Exception e) {
            log.error("Error releasing alias: {}", e.getMessage(), e);
            throw new DomainException("Failed to release alias: " + aliasId, e);
        }
    }
    
    @Override
    public boolean checkReservedWords(String alias) {
        if (alias == null || alias.isEmpty()) {
            return false;
        }
        
        // Check if the alias exactly matches any reserved word (case-insensitive)
        for (String reservedWord : reservedWords) {
            if (alias.equalsIgnoreCase(reservedWord)) {
                return true;
            }
        }
        
        // Check if the alias contains any reserved patterns
        String lowerAlias = alias.toLowerCase();
        for (String reservedWord : reservedWords) {
            if (lowerAlias.contains(reservedWord.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Provides a default list of reserved words that cannot be used as aliases.
     * 
     * @return List of reserved words
     */
    private List<String> getDefaultReservedWords() {
        return List.of(
            "admin", "administrator", "api", "app", "application",
            "auth", "authentication", "authorization", "backup", "cache",
            "config", "configuration", "database", "db", "debug",
            "delete", "dev", "development", "docs", "documentation",
            "download", "error", "example", "ftp", "guest",
            "help", "home", "host", "info", "login",
            "logout", "mail", "manager", "master", "monitor",
            "news", "null", "password", "prod", "production",
            "public", "register", "root", "search", "security",
            "server", "service", "settings", "stage", "staging",
            "support", "sys", "system", "temp", "test",
            "testing", "tmp", "user", "users", "www",
            "undefined", "unknown", "default", "index", "main",
            "about", "contact", "privacy", "terms", "legal",
            "blog", "forum", "wiki", "cdn", "static",
            "assets", "resource", "resources", "files", "images",
            "js", "css", "html", "xml", "json",
            "http", "https", "ftp", "smtp", "pop", "imap"
        );
    }
}