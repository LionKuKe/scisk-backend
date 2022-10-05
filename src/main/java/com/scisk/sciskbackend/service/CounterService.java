package com.scisk.sciskbackend.service;

public interface CounterService {
    /**
     * Récupère l'index actuel du document de nom collectionName, l'incrémente et l'enregistre, et le retourne
     * @param collectionName Le nom du document dont on veut le prochain index
     * @return Le prochaine index
     */
    public long getNextSequence(String collectionName);

    /**
     * Retourne le code du prochain document d'une collection
     * @param collectionName La collection dont on veut le prochain document
     * @return Le code du prochain document
     */
    public String getNextCodeOfCollection(String collectionName);
}
