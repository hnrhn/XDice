import React from "react";

export default function Home() {
    return (
        <div className={"centered"}>
            <div>
                <h1>Welcome to XDice</h1>
                <p>XDice is your smart, configurable dice-roller, ready to apply some randomness to any situation.</p>

                <h2>Features</h2>
                <ul>
                    <li>Default dice-type shorthand</li>
                    <li>Success counting</li>
                    <li>Total calculation</li>
                    <li>Exploding dice</li>
                    <li>Auto-success support</li>
                    <li>Discord integration</li>
                    <li>A sassy personality</li>
                </ul>

                <h2>Detail</h2>
                <p>At its most basic, XDice is a standard dice roller. Send a command like <u>/roll 5d6</u> and it will respond with the result of rolling five 6-sided dice.</p>
                <p>However, most games roll the same type of dice far more often than others, so XDice started as a way to reduce roll commands to a more streamlined form.</p>
                <p>For example, by setting <i>d10</i> as the default die type for a Room, <u>/roll 1d10</u> can be reduced all the way down to simply <u>/r</u>.</p>
                <p>That's 80% more bullet per bullet!</p>
            </div>
        </div>
    )
}