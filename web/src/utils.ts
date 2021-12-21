import React from "react";

export function keyWasEnter(e: React.KeyboardEvent) {
    return e.code === "Enter";
}