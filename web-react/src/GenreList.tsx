import { useEffect, useState } from "react";
import { restURL } from "./config";
import type { Genre } from "./genre";

export function GenreList() {
    const [genres, setGenres] = useState<Genre[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch(`${restURL}/api/genres`)
            .then((r) => (r.ok ? r.json() : Promise.reject(`HTTP ${r.status}`)))
            .then((data) => setGenres(data as Genre[]))
            .catch((e) => setError(String(e)));
    }, []);

    if (error) return <div style={{ padding: 16 }}>ERROR: {error}</div>;

    return (
        <div style={{ padding: 16 }}>
            <h2>Genres</h2>
            <ul>
                {genres.map((g) => (
                    <li key={g.id}>
                        {g.id}: {g.name}
                    </li>
                ))}
            </ul>
        </div>
    );
}
