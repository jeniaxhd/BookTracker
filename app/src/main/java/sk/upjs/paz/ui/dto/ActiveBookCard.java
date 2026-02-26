package sk.upjs.paz.ui.dto;

public record ActiveBookCard(
        long bookId,
        String title,
        String authorsText,
        String genreName,
        int totalPages,
        int currentPage,
        int totalMinutes,
        String coverPath
) {}