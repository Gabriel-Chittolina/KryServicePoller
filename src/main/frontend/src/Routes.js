import React from "react";
import { Route, Switch } from "react-router-dom";
import Home from "./containers/Home";
import Login from "./containers/Login";
import NotFound from "./containers/NotFound";
import Signup from "./containers/Signup";
import Dashboard from "./containers/private/Dashboard";

export default function Routes() {
	return (
		<Switch>
			<Route exact path="/">
				<Home />
			</Route>
			<Route exact path="/login">
				<Login />
			</Route>
			<Route exact path="/signup">
				<Signup />
			</Route>
			<Route exact path="/private/dashboard">
				<Dashboard />
			</Route>
			{/* Finally, catch all unmatched routes */}
			<Route>
				<NotFound />
			</Route>
		</Switch>
	);
}