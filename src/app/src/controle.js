function consultaInicial (res, dados) {
  let index = auxiliaConsulta(dados);

  res.render('index', index)
}

function auxiliaConsulta (dados) {
  let index = {
    titulo: '',
    tabela: ''
  }

  for (let i = 0; i < dados.campeonatos.length; ++i) {
    index.titulo = dados.campeonatos[i].name
    index.tabela = dados.campeonatos[i].classificacao
  }

  return index;
}

export {consultaInicial}
