function consultaInicial (res, dados) {
  let index = auxiliaConsulta(dados);

  res.render('index', index)
}

function auxiliaConsulta (dados) {
  let index = {
    titulo: '',
    tabela: ''
  }

  const tst = dados.campeonatos
  .reduce((acc, dado) => {
    return [...acc, ...dado.classificacao]
  }, [])

const nomeTimes = tst
  .map(time => time.nome)
const nomeTimesUnicos = nomeTimes
  .filter((item, pos) => {
    return nomeTimes.indexOf(item) == pos
  })

const tst2 = nomeTimesUnicos
  .map(time => {
    return tst
      .filter(timeResultados => timeResultados.nome === time) 
      .reduce((acc, timeAux) => ({
        nome: time,
        PG: acc.PG + timeAux.PG,
        J: acc.J + timeAux.J,
        V: acc.V + timeAux.V,
        E: acc.E + timeAux.E,
        D: acc.D + timeAux.D,
        GP: acc.GP + timeAux.GP,
        GC: acc.GC + timeAux.GC,
        SG: acc.SG + timeAux.SG,
        APR: acc.SG + timeAux.SG,
      }), {
        nome: time,
        PG: 0,
        J: 0,
        V: 0,
        E: 0,
        D: 0,
        GP: 0,
        GC: 0,
        SG: 0,
        APR: 0,
        MGS: 0,
        MGF: 0
      })
  })
  .map(time => ({
    ...time,
    APR: (time.PG / (time.J * 3) * 100).toFixed(1),
    MGS: (time.GC / time.J).toFixed(1),
    MGF: (time.GP / time.J).toFixed(1)
  }))
  .sort((a, b) => {
    return b.APR - a.APR
  })

  
  index.tabela = tst2;

  return index;
}

export {consultaInicial}
