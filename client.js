const id = Math.floor(Math.random() * 1000);

let fetchLib = require("node-fetch-commonjs");

const solicitarRecurso_1 = async () => {
  const url = "http://localhost:8080/solicita_recurso_um";
  const params = { id: id };
  const options = {
    method: 'POST',
    body: JSON.stringify( params )  
  };
  await fetchLib(url, options)
    .then((response) => response.json())
    .then((json) => console.log(json));
};

const solicitarRecurso_2 = async () => {
  const url = "http://localhost:8080/solicita_recurso_dois";
  await fetchLib(url)
    .then((response) => response.json())
    .then((json) => console.log(json));
};
const liberarRecurso_1 = async () => {
  const url = "http://localhost:8080/libera_recurso_um";
  await fetchLib(url)
    .then((response) => response.json())
    .then((json) => console.log(json));
};
const liberarRecurso_2 = async () => {
  const url = "http://localhost:8080/libera_recurso_dois";
  await fetchLib(url)
    .then((response) => response.json())
    .then((json) => console.log(json));
};
// create a menu with 4 options with switch case
const menu = async () => {
  let prompt = require("prompt");
  prompt.start();
  console.log("Selecione uma opção abaixo:");
  console.log();
  console.log("+---+------------------------+");
  console.log("+ 1 +    Acessar recurso 1   +");
  console.log("+---+------------------------+");
  console.log("+ 2 +    Acessar recurso 2   +");
  console.log("+---+------------------------+");
  console.log("+ 3 +    Liberar recurso 1   +");
  console.log("+---+------------------------+");
  console.log("+ 4 +    Liberar recurso 2   +");
  console.log("+---+------------------------+");
  console.log();
  prompt.get(["option"], function (err, result) {
    switch (result.option) {
      case "1":
        solicitarRecurso_1();
        menu();
        break;
      case "2":
        solicitarRecurso_2();
        menu();
        break;
      case "3":
        liberarRecurso_1();
        menu();
        break;
      case "4":
        liberarRecurso_2();
        menu();
        break;
      default:
        console.log("Opção inválida");
        menu();
    }
  });
};
menu();
