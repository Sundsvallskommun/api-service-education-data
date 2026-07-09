package se.sundsvall.educationdata.integration.susanavet;

public record SusaPage<E>(
	E entity,
	int totalPages) {}
