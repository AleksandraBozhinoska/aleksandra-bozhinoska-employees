import * as React from 'react';
import FileUpload from '../file-upload/FileUpload';
import './App.css';

const App = (): JSX.Element => {
  return (
    <div className="app">
      <header className="app-header">
        <h5>Welcome to Longest Period Pair App!</h5>
      </header>
      <FileUpload></FileUpload>
    </div>
  );
}

export default App;
