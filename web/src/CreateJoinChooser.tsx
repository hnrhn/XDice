import React from "react";
import {Parallelogram} from "./Parallelogram";

export default function CreateJoinChooser(props: {createRoomPath: string, joinRoomPath: string, changePageTo: Function}) {
    return (
        <div className={"centered"}>
            <Parallelogram content={"Join"} color={"hotpink"} onClick={() => props.changePageTo(props.joinRoomPath)} />
            &nbsp;
            <Parallelogram content={"Create"} color={"darkorange"} onClick={() => props.changePageTo(props.createRoomPath)} />
        </div>
    );
}