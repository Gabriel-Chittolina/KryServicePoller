import axios from 'axios';

export const API = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true,
  timeout: 10000,
})

export function fetchServices(){ 
  return API.get('/services')
};
export function saveService({ service_name, service_url }){
  const dataForm = new FormData();
  dataForm.append('service_name', service_name);
  dataForm.append('service_url', service_url);

  return API.post('/service', dataForm);
}
export function updateService({ service_name, service_url, service_id }){ 
  const dataForm = new FormData();
  dataForm.append('service_name', service_name);
  dataForm.append('service_url', service_url);
  dataForm.append('service_id', service_id);
  
  return API.put('/service', dataForm);
}

export function deleteService({ service_id }){ 
  const dataForm = new FormData();
  dataForm.append('service_id', service_id);
  
  return API.delete('/service', { data: dataForm });
}

export function updateStatusService({ service_id }){ 
  return API.get(`/service/status`, { params: {service_id} });
}