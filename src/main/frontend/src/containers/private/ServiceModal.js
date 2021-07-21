import React, { useState, useEffect } from "react";
import { Button, Modal } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import LoaderButton from "../../components/LoaderButton";

export function ServiceModal(props) {
    const { service, modalShow, onClose, onSubmit, isLoading } = props;

    const [serviceName, setServiceName] = useState('');
    const [serviceUrl, setServiceUrl] = useState('');

    function validateForm() {
        return (
            (serviceName.length > 0 && serviceUrl.length > 0) && 
            (serviceName !== '' && serviceUrl !== '')
        );
    }

    useEffect(() => {
        if(service.service_name && service.service_url) {
            setServiceName(service.service_name);
            setServiceUrl(service.service_url);
        } else {
            setServiceName('');
            setServiceUrl('');
        }
    }, [service])

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit({ 
            service_name: serviceName, 
            service_url: serviceUrl,
            service_id: service.service_id
        })
    }

    return (
        <Modal
            show={modalShow}
            onHide={onClose}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
        >
            <Modal.Header closeButton>

                {!!service === false ?
                    <Modal.Title id="contained-modal-title-vcenter">
                        Add a new Service
                    </Modal.Title>
                    :
                    <Modal.Title id="contained-modal-title-vcenter">
                        Update Service
                    </Modal.Title>
                }

            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group controlId="service_name" size="lg">
                        <Form.Label>Service name</Form.Label>
                        <Form.Control
                            autoFocus
                            type="text"
                            value={serviceName}
                            onChange={e => setServiceName(e.target.value)}
                        />
                    </Form.Group>
                    <Form.Group controlId="service_url" size="lg">
                        <Form.Label>Service URL</Form.Label>
                        <Form.Control
                            type="text"
                            value={serviceUrl}
                            onChange={e => setServiceUrl(e.target.value)}
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
                        Save
                    </LoaderButton>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={onClose}>Close</Button>
            </Modal.Footer>
        </Modal>
    );
}