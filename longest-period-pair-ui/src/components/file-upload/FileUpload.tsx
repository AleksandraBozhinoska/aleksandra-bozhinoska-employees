import * as React from 'react';
import axios from 'axios';
import { useState } from 'react';
import { FilePond } from 'react-filepond'
import Dropdown, { Option } from 'react-dropdown';
import { FilePondErrorDescription, FilePondFile } from 'filepond';
import { ParsedLine } from 'models/pair-per-project';
import getFileContent from 'services/file-content-service';

import "filepond/dist/filepond.min.css";
import "./FileUpload.css";
import 'react-dropdown/style.css';

const FileUpload = (): JSX.Element => {

  const options = [
    'yyyy-MM-dd',
    'dd/MM/yyyy',
    'dd-MM-yyyy',
    'MM-dd-yyyy',
    'MM/dd/yyyy'
  ];

  const [file, setFile] = useState<FilePondFile>();
  const [models, setModels] = useState<ParsedLine[]>();
  const [pattern, setPattern] = useState<string | Option | undefined>(options[0]);
  const [error, setError] = useState<string | undefined>();

  const onProcessFile = (error: FilePondErrorDescription | null, file: FilePondFile) => {

    if(error) {
      setError('An error occurred in the attachment upload. Please check the date formatter and try again.');
      setFile(undefined);
      return;
    }
    
    setFile(file);
    setError(undefined);

    getFileContent(file.filename)
      .then((res) => setModels(res.data))
      .catch((err) => {
        if (err && !axios.isCancel(err)) {
          console.warn('An error occurred while trying to load processed file content.');
        }
      });
  };

  const onRemoveFile = () => {
    setFile(undefined);
    setError(undefined);
  };

  const onProcessStart = () => {
    setError(undefined);
    setFile(undefined);
  };

  const onSelect = (val: Option) => {
    console.log(`New date pattern selected: ${val.value}`);
    setPattern(val.value);
  };

  const renderTableData = (): JSX.Element => {
    return <table>
      <thead>
      <tr>
        <th>Employee ID #1</th>
        <th>Employee ID #2</th>
        <th>Project ID</th>
        <th>Days worked</th>
      </tr>
      </thead>
      <tbody>
        {models?.sort((a, b) => b.daysWorked - a.daysWorked)
          .map(model => <tr key={`${model.employeeIdFirst}-${model.employeeIdSecond}-${model.projectId}`}>
          <td>{model.employeeIdFirst}</td>
          <td>{model.employeeIdSecond}</td>
          <td>{model.projectId}</td>
          <td>{model.daysWorked}</td>
        </tr>
        )}
      </tbody>
    </table>
  };

    return (
    <div className="content-wrapper">
      <div className="dropdown-wrapper">
        <label>Select a date formatter:</label>
        <Dropdown 
          options={options} 
          onChange={onSelect} 
          value={pattern} />
      </div>
      <div>
        <FilePond
          onaddfilestart={onProcessStart}
          onprocessfile={onProcessFile}
          onremovefile={onRemoveFile}
          server={{ 
            url: 'http://localhost:8080/api/attachments',
            process: `/upload?pattern=${pattern}`,
            revert: null,
            restore: null,
            load: null,
            fetch: null
          }}
          name="file"
          labelIdle='Drag & Drop the file to parse with the selected formatter or <span class="filepond--label-action">Browse</span>' 
        >
        </FilePond>
        {file && renderTableData()}
        {error && <div className="error-wrapper">{error}</div>}
      </div>
    </div>);
}

export default FileUpload;