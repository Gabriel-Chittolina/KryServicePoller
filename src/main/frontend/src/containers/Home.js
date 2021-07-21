import React from "react";
import "./Home.css";

export default function Home() {
  return (
    <div className="Home">
      <div className="lander">
        <h1>Kry Service Poller by Gabriel Chittolina</h1>
        <p className="text-muted">A poller for Kry's services created with <a href="https://reactjs.org/">React</a> and <a href="https://vertx.io/">Vert.x 4!</a></p>
      </div>
    </div>
  );
}