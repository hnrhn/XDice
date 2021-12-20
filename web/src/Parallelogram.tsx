import React, {MouseEventHandler} from "react";

export function Parallelogram(props: {content: string, color: string, onClick: MouseEventHandler}) {
    return (
        <div className={"parallelogram"} onClick={props.onClick} style={({"--box-shadow-color": props.color} as React.CSSProperties)}>
            <div>
                {props.content}
            </div>
        </div>
    )
}