package com.civdevops.helloworld.service;


import static com.civdevops.helloworld.Constants.INVALID_INPUT;

public final class GreetingService {

    public String greet(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(INVALID_INPUT);
        }

        String normalizedName = name.strip();
        char firstCharacter = normalizedName.charAt(0);

        boolean isValid = (firstCharacter >= 'A' && firstCharacter <= 'M') || (firstCharacter >= 'a' && firstCharacter <= 'm');

        if (!isValid) throw new IllegalArgumentException(INVALID_INPUT);

        return "Hello " + Character.toUpperCase(firstCharacter) + normalizedName.substring(1);
    }
}
