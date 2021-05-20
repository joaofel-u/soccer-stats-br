# UFSC - CTC - INE - INE5646 Programação para Web :: Exercício App Atletas

A aplicação mostra na tela uma relação de atletas (nome e altura) cadastradas em uma equipe. O usuário pode pesquisar os atletas que possuem uma altura mínima (informada pelo usuário).

## Objetivo do Exercício

Mostrar uma aplicação cujas páginas são geradas no lado servidor e que utiliza o framework [Express](http://expressjs.com/),  a biblioteca [pug](https://pugjs.org/api/getting-started.html) e os estilos CSS do framework [Bulma](https://bulma.io/).

## Instruções

Depois de baixar/clonar o respositório, digite

`npm install`

para instalar os módulos JavaScript utilizados pela aplicação.

### Durante o desenvolvimento

Digite

`npm start`

e a aplicação estará disponível na porta 3000.Para acessar, use o navegador e digite o endereço `https://localhost:3000`

Sempre que um arquivo for salvo a aplicação irá reiniciar automaticamente (graças ao pacote **nodemon**).

### Em produção

Para executar a aplicação em produção (melhor desempenho) digite

`npm run build`

e depois

`node build/app.js`
