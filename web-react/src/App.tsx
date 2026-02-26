import { useState } from "react";
import { BookList } from "./BookList";
import { AuthorList } from "./AuthorList";
import { GenreList } from "./GenreList";

type Page = "books" | "authors" | "genres";

export default function App() {
    const [page, setPage] = useState<Page>("books");

    return (
        <div>
            <div style={{ padding: 16, display: "flex", gap: 8 }}>
                <button onClick={() => setPage("books")}>Books</button>
                <button onClick={() => setPage("authors")}>Authors</button>
                <button onClick={() => setPage("genres")}>Genres</button>
            </div>

            {page === "books" && <BookList />}
            {page === "authors" && <AuthorList />}
            {page === "genres" && <GenreList />}
        </div>
    );
}
