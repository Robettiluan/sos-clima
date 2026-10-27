package com.sosclima.app.data.local

/** Conteúdo fixo, disponível mesmo sem internet. */
data class ContatoEmergencia(val nome: String, val numero: String, val descricao: String)

data class OrientacaoEmergencia(val titulo: String, val passos: List<String>)

object GuiaEmergencia {

    val contatos = listOf(
        ContatoEmergencia("Defesa Civil", "199", "Alagamentos, deslizamentos, desabamentos"),
        ContatoEmergencia("Corpo de Bombeiros", "193", "Resgates, incêndios, pessoas ilhadas"),
        ContatoEmergencia("SAMU", "192", "Atendimento médico de urgência"),
        ContatoEmergencia("Polícia Militar", "190", "Segurança e apoio em emergências"),
    )

    val orientacoes = listOf(
        OrientacaoEmergencia(
            "Enchentes e alagamentos",
            listOf(
                "Desligue a energia elétrica e o gás se a água começar a entrar em casa.",
                "Não entre em água de enchente: ela pode esconder buracos, correnteza e esgoto.",
                "Não dirija em ruas alagadas; poucos centímetros de água podem arrastar um carro.",
                "Suba para um local alto e espere o resgate se não for possível sair com segurança.",
            ),
        ),
        OrientacaoEmergencia(
            "Deslizamentos de terra",
            listOf(
                "Fique atento a rachaduras em paredes, portas emperrando e árvores ou postes inclinando.",
                "Ao notar esses sinais durante a chuva, saia de casa imediatamente.",
                "Avise os vizinhos e ligue 199 para a Defesa Civil.",
            ),
        ),
        OrientacaoEmergencia(
            "Ventos fortes e queda de árvores",
            listOf(
                "Fique longe de árvores, placas, outdoors e redes elétricas.",
                "Nunca toque em fios caídos; acione a companhia de energia ou os bombeiros.",
                "Feche portas e janelas e fique em um cômodo sem janelas grandes.",
            ),
        ),
        OrientacaoEmergencia(
            "Tempestades com raios",
            listOf(
                "Procure abrigo em uma construção fechada ou dentro de um carro.",
                "Evite campos abertos, piscinas, praias e locais elevados.",
                "Desligue aparelhos elétricos da tomada.",
            ),
        ),
    )
}
