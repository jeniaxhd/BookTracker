
import { useEffect, useState } from "react";
import { restURL } from "./config";
import type { Author } from "./author";

export function AuthorList() {
    const [authors, setAuthors] = useState<Author[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch(`${restURL}/api/authors`)
            .then((r) => (r.ok ? r.json() : Promise.reject(`HTTP ${r.status}`)))
            .then((data) => setAuthors(data as Author[]))
            .catch((e) => setError(String(e)));
    }, []);

    if (error) return <div style={{ padding: 16 }}>ERROR: {error}</div>;

    return (
        <div style={{ padding: 16 }}>
            <h2>Authors</h2>
            <table border={1} cellPadding={8}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Country</th>
                    </tr>
                </thead>
                <tbody>
                    {authors.map((a) => (
                        <tr key={a.id}>
                            <td>{a.id}</td>
                            <td>{a.name}</td>
                            <td>{a.country ?? "-"}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
