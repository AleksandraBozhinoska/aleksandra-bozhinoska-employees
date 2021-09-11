import { AxiosResponse } from 'axios';
import { ParsedLine } from 'models/pair-per-project';
import axios from './custom-axios';

const getFileContent = (fileName: string): Promise<AxiosResponse<ParsedLine[]>> => {
    return axios.get<ParsedLine[]>(`http://localhost:8080/api/attachments/${fileName}`);
};

export default getFileContent;
