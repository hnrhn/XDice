import React from "react";

export default function NavBar(props: {changePageTo: Function}) {
    return (
        <nav>
            <img style={{height: "4rem", objectFit: "contain"}} src={"logo192.png"} alt="XDice logo by Janina Franck - janinafranck.com" title="XDice logo by Janina Franck - janinafranck.com" />
            <span>XDice</span>
            <div className={"navLink"} onClick={() => props.changePageTo("home")}>Home</div>
            <div className={"navLink"} onClick={() => props.changePageTo("room")}>Room</div>
        </nav>
    );
}