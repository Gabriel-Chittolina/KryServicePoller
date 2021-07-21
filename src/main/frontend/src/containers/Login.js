import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import LoaderButton from "../components/LoaderButton";
import "./Login.css";
import { useAppContext } from "../libs/contextLib";
import { useHistory } from "react-router-dom";
import { onError } from "../libs/errorLib";
import { useFormFields } from "../libs/hooksLib";
import { API } from '../api/api';

export default function Login() {
	const history = useHistory();
	const { userHasAuthenticated } = useAppContext();
	const [isLoading, setIsLoading] = useState(false);
	const [fields, handleFieldChange] = useFormFields({
		username: "",
		password: ""
	  });

	function validateForm() {
		return fields.username.length > 0 && fields.password.length > 0;
	}

	function handleSuccessfulAuth(data) {
		userHasAuthenticated(true);
		history.push("/private/dashboard");
	}

	function handleFailedAuth(error) {
		onError(error);
		setIsLoading(false);
	}

	async function handleSubmit(event) {
		event.preventDefault();
		setIsLoading(true);

		try {
			const dataForm = new FormData();
			dataForm.append('username', fields.username);
			dataForm.append('password', fields.password);

			await API.post('/login', 
				dataForm
				).then(response => {
					handleSuccessfulAuth(response);
				});
		} catch(error){
			handleFailedAuth(error.response);
		}
	}

	return (
		<div className="Login">
			<Form onSubmit={handleSubmit}>
				<Form.Group size="lg" controlId="username">
					<Form.Label>Username</Form.Label>
					<Form.Control
						autoFocus
						type="text"
						value={fields.username}
						onChange={handleFieldChange}
					/>
				</Form.Group>
				<Form.Group size="lg" controlId="password">
					<Form.Label>Password</Form.Label>
					<Form.Control
						type="password"
						value={fields.password}
						onChange={handleFieldChange}
					/>
				</Form.Group>
				<LoaderButton
					block
					size="lg"
					type="submit"
					isLoading={isLoading}
					disabled={!validateForm()}
				>
					Login
				</LoaderButton>
			</Form>
		</div>
	);
}