package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainCustomAliasEntity;

/**
 * Port for validating and managing custom aliases.
 */
public interface DomainInputPortAliasService {

    /**
     * Validates if a custom alias meets format and reserved word rules.
     * @param alias the alias string to validate
     * @return true if valid, false otherwise
     */
    boolean validateAlias(String alias);

    /**
     * Reserves a custom alias for a given owner.
     * @param alias the alias value
     * @param owner the user who owns the alias
     * @return the reserved alias entity
     */
    DomainCustomAliasEntity reserveAlias(String alias, String owner);

    /**
     * Releases a previously reserved alias.
     * @param aliasId the identifier of the alias to release
     */
    void releaseAlias(String aliasId);

    /**
     * Checks if an alias matches any reserved patterns or words.
     * @param alias the alias value to check
     * @return true if it is reserved, false otherwise
     */
    boolean checkReservedWords(String alias);
}
