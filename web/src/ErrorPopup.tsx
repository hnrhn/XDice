import React from "react";

export function ErrorPopup(props: {error: string}) {
    return (
        <div id={"errorPopup"}>
            {props.error}
        </div>
    );
}