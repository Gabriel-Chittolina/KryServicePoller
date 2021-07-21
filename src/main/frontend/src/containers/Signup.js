import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import { useHistory } from "react-router-dom";
import LoaderButton from "../components/LoaderButton";
import { useFormFields } from "../libs/hooksLib";
import { onError } from "../libs/errorLib";
import "./Signup.css";
import axios from 'axios';

export default function Signup() {
  const [fields, handleFieldChange] = useFormFields({
    username: "",
    password: "",
    confirmPassword: ""
  });
  const history = useHistory();
  const [isLoading, setIsLoading] = useState(false);

  function validateForm() {
    return (
      fields.username.length > 0 &&
      fields.password.length > 0 &&
      fields.password === fields.confirmPassword
    );
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setIsLoading(true);

    try {
        const dataForm = new FormData();
        dataForm.append('username', fields.username);
        dataForm.append('password', fields.password);

        await axios.post('http://localhost:8080/api/register', 
            dataForm, 
            { headers: { 'Accept': 'application/json', 'Content-Type': 'multipart/form-data' } }
            ).then(response => {
                if(response.status === 201){
                    alert('Registered successfully. Now you can login!');
                    history.push("/login");
                } else{
                    alert(response.data);
                    setIsLoading(false);
                }
            });
    } catch(error){
        onError(error);
		setIsLoading(false);
    }
  }


  return (
    <Form onSubmit={handleSubmit}>
    <Form.Group controlId="username" size="lg">
        <Form.Label>username</Form.Label>
        <Form.Control
        autoFocus
        type="text"
        value={fields.username}
        onChange={handleFieldChange}
        />
    </Form.Group>
    <Form.Group controlId="password" size="lg">
        <Form.Label>Password</Form.Label>
        <Form.Control
        type="password"
        value={fields.password}
        onChange={handleFieldChange}
        />
    </Form.Group>
    <Form.Group controlId="confirmPassword" size="lg">
        <Form.Label>Confirm Password</Form.Label>
        <Form.Control
        type="password"
        onChange={handleFieldChange}
        value={fields.confirmPassword}
        />
    </Form.Group>
    <LoaderButton
        block
        size="lg"
        type="submit"
        variant="success"
        isLoading={isLoading}
        disabled={!validateForm()}
    >
        Signup
    </LoaderButton>
    </Form>
  );
}