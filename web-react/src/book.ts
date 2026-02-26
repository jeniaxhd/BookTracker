export type AuthorRef = {
    id: number;
    name: string;
};

export type GenreRef = {
    id: number;
    name: string;
};

export type Book = {
    id: number;
    title: string;
    authors: AuthorRef[];
    genre: GenreRef[];
    year?: number | null;
    pages?: number;
    description?: string | null;
    coverPath?: string | null;
};
