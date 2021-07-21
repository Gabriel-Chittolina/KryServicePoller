import React, { useState, useEffect } from "react";
import Table from 'react-bootstrap/Table'
import Container from 'react-bootstrap/Container'
import Button from 'react-bootstrap/Button'
import axios from 'axios';
import { useHistory } from "react-router-dom";
import { format } from "date-fns";

import { ServiceModal } from "./ServiceModal";
import { useAppContext } from "../../libs/contextLib";
import { 
    deleteService,
    fetchServices, 
    saveService, 
    updateService, 
    updateStatusService, 
} from '../../api/api';

import "./Dashboard.css";

export default function Dashboard(props) {
    const { isAuthenticated } = useAppContext();
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitLoading, setSubmitLoading] = useState(false);
    const [modalShow, setModalShow] = useState(false);
    const [services, setServices] = useState([]);
    const [modalService, setModalService] = useState({});

    const history = useHistory();

    console.log('Dashboard', { services });

    useEffect(() => {
        getServices();
    }, []);

    useEffect(() => {
        if(services) {
           setTimeout(() => {
                const servicesPromices = services.map(serv => {
                    return updateStatusService({ service_id: serv.service_id })
                        .then((res) => {
                            return ({...serv, ...res.data[0]})
                        }).catch(e => {
                            return serv;
                        });
                });

                Promise.all(servicesPromices)
                    .then(servs => {
                        setServices(servs)
                    })
                    .catch(console.error);
            }, 5000);
        }
    }, [services])

    async function getServices() {
        setIsLoading(true);
        
        try {
            const response = await fetchServices();
            setServices(response.data);
        } catch(e) {
            console.error(`[Dashboard]`, e);
        } finally {
            setIsLoading(false);
        }
    };

    const openModal = (service = {}) => {
        setModalService(service);
        setModalShow(true);
    } 

    const handleClose = () => {
        setModalShow(false);
        setModalService({});
    }

    const handleDelete = (service_id) => {
        setSubmitLoading(true);

        deleteService({ service_id })
            .then(() => {
                getServices();
            })
            .catch(e => {
                console.error(e);
            })
            .finally(() => {
                setSubmitLoading(false);
            });
    }

    const handleSubmit = (values) => {
        const { service_name, service_url, service_id } = values;
        setSubmitLoading(true);
        
        if(values.service_id) {
            // edit
            updateService({ service_name, service_url, service_id })
                .then(reponse => {
                    // Success
                    handleClose();
                    getServices();
                })
                .catch(e => {
                    console.error(e)
                })
                .finally(() => {
                    setSubmitLoading(false);
                    
                });
        } else {
            // save
            saveService({ service_name, service_url })
                .then(reponse => {
                    // Success
                    handleClose();
                    getServices();
                })
                .catch(e => {
                    console.error(e)
                })
                .finally(() => {
                    setSubmitLoading(false);
                });
        }
    }

    if(!isAuthenticated) {
        history.push("/login");
    }

    return (
        <>
            <Container style={{ marginTop: '100px' }}>
                <Button 
                    variant="secondary" 
                    disable={!isSubmitLoading}
                    style={{ float: 'right', margin: '20px' }} 
                    onClick={() => openModal()}
                >
                    Add a new Service
                </Button>
                {!isLoading ? <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>URL</th>
                            <th>Creation Date</th>
                            <th>Last Update</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {services.map((currentService) => {
                            return (
                                <tr key={currentService.service_id}>
                                    <td>{currentService.service_name}</td>
                                    <td>{currentService.service_url}</td>
                                    <td>{format(new Date(currentService.created_at), 'MM/dd/yyyy HH:mm:ss')}</td>
                                    <td>{format(new Date(currentService.updated_at), 'MM/dd/yyyy HH:mm:ss')}</td>
                                    <td>{currentService.service_status}</td>
                                    <td>
                                        <Button 
                                            variant="primary" 
                                            disable={!isSubmitLoading}
                                            onClick={() => openModal(currentService)}
                                        >
                                            Update
                                        </Button>
                                        <Button 
                                            variant="danger"
                                            disable={!isSubmitLoading}
                                            onClick={() => handleDelete(currentService.service_id)}
                                        >
                                            Delete
                                        </Button>
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table> : "LOADING..."}
            </Container>

            <ServiceModal 
                modalShow={modalShow} 
                onClose={handleClose} 
                service={modalService} 
                isLoading={isSubmitLoading}
                onSubmit={handleSubmit}
            />
        </>
    )
}