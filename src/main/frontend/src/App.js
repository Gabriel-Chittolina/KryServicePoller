import React, { useState, useEffect } from "react";
import Navbar from "react-bootstrap/Navbar";
import { useHistory } from "react-router-dom";
import Nav from "react-bootstrap/Nav";
import { LinkContainer } from "react-router-bootstrap";

import "./App.css";
import Routes from "./Routes";
import { AppContext } from "./libs/contextLib";
import { onError } from "./libs/errorLib";
import { API } from "./api/api"

function App() {
  const history = useHistory();
  const [isAuthenticated, userHasAuthenticated] = useState(false);
  const [isAuthenticating, setIsAuthenticating] = useState(true);

  useEffect(() => {
    API.get('/logged_in')
      .then(response => {
        userHasAuthenticated(true)
        history.push("/private/dashboard");
      }).catch(error => {
        userHasAuthenticated(false)
      });
    setIsAuthenticating(false);
  }, [history]);

  async function handleLogout(event) {
    await API.delete('/logout')
      .catch(error => {
        onError(error);
      });
    userHasAuthenticated(false);
    history.push("/login");
  }

  return (
    !isAuthenticating && (
      <div className="App container py-3">
        <Navbar collapseOnSelect bg="light" expand="md" className="mb-3">
          <LinkContainer to="/">
            <Navbar.Brand className="font-weight-bold text-muted">
              Kry Service Poller
            </Navbar.Brand>
          </LinkContainer>
          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Nav activeKey={window.location.pathname}>
              {isAuthenticated ? (
                <>
                  <LinkContainer to="/private/dashboard">
                    <Nav.Link>Dashboard</Nav.Link>
                  </LinkContainer>
                  <Nav.Link onClick={handleLogout}>Logout</Nav.Link>
                </>
              ) : (
                <>
                  <LinkContainer to="/signup">
                    <Nav.Link>Signup</Nav.Link>
                  </LinkContainer>
                  <LinkContainer to="/login">
                    <Nav.Link>Login</Nav.Link>
                  </LinkContainer>
                </>
              )}
            </Nav>
          </Navbar.Collapse>
        </Navbar>
        <AppContext.Provider value={{ isAuthenticated, userHasAuthenticated }}>
          <Routes />
        </AppContext.Provider>
      </div>
    )
  );
}

export default App;