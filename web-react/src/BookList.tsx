import { useEffect, useState } from "react";
import { restURL } from "./config";
import type { Book } from "./book";

export function BookList() {
    const [books, setBooks] = useState<Book[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch(`${restURL}/api/books`)
            .then((r) => {
                if (!r.ok) throw new Error(`HTTP ${r.status}`);
                return r.json();
            })
            .then((data) => setBooks(data as Book[]))
            .catch((e) => setError(String(e)));
    }, []);

    if (error) return <div style={{ padding: 16 }}>ERROR: {error}</div>;

    return (
        <div style={{ padding: 16 }}>
            <h2>Books</h2>
            <table border={1} cellPadding={8}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Authors</th>
                        <th>Genres</th>
                        <th>Year</th>
                        <th>Pages</th>
                    </tr>
                </thead>
                <tbody>
                    {books.map((b) => (
                        <tr key={b.id}>
                            <td>{b.id}</td>
                            <td>{b.title}</td>
                            <td>{b.authors?.map((a) => a.name).join(", ") || "-"}</td>
                            <td>{b.genre?.map((g) => g.name).join(", ") || "-"}</td>
                            <td>{b.year ?? "-"}</td>
                            <td>{b.pages ?? "-"}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
