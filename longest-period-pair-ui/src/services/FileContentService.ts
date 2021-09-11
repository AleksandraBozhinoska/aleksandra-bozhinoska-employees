import { AxiosResponse } from 'axios';
import { PairPerProject } from '../models/PairPerProject';
import axios from './CustomAxios';

const getFileContent = (fileName: string): Promise<AxiosResponse<PairPerProject[]>> => {
    return axios.get<PairPerProject[]>(`http://localhost:8080/api/attachments/${fileName}`);
};

export default getFileContent;
