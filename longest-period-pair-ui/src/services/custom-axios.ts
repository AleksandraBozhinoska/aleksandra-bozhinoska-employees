import axios from 'axios';

const instance = axios.create();

instance.interceptors.request.use((request) => {
  if (request.method === 'get' || request.method === 'GET') {
    request.data = {};
  }

  return request;
});

instance.defaults.headers.common = {
  Accept: 'application/json'
};

export default instance;
