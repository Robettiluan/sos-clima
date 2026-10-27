# SOS Clima — Alertas Climáticos e Defesa Civil Comunitária

Aplicativo Android nativo para acompanhar o risco de chuvas fortes e ventanias, registrar ocorrências no bairro e consultar telefones e orientações de emergência mesmo sem internet.

**Tema do catálogo:** 16 — SOS Alertas Climáticos e Defesa Civil Comunitária
**Disciplina:** Programação para Aplicações e Dispositivos Móveis

## Integrantes

- Luan Robetti

## O problema

Em eventos climáticos extremos, a população costuma não saber o nível de risco do dia, não tem onde anotar e acompanhar problemas do bairro (ruas alagadas, árvores caídas) e, sem internet, perde o acesso aos telefones de emergência. O SOS Clima reúne essas três coisas em um app simples.

## Funcionalidades (MVP)

| Módulo | O que faz | Tecnologia |
|---|---|---|
| **Alertas** | Previsão de 7 dias da cidade escolhida, com nível de alerta (sem alerta / atenção / alerta alto) e tela de detalhes com recomendações | Retrofit + API Open-Meteo |
| **Ocorrências** | Cadastro, edição, exclusão e filtro de ocorrências (alagamento, árvore caída, deslizamento...), com marcação de resolvida | Room (CRUD com Flow) |
| **Emergência** | Telefones da Defesa Civil, Bombeiros, SAMU e Polícia com discagem direta e guia de primeiros cuidados — funciona offline | Conteúdo local |
| **Ajustes** | Tema escuro e cidade monitorada, salvos entre aberturas do app | DataStore |

Confirmações com `AlertDialog`: excluir ocorrência, limpar ocorrências resolvidas e descartar alterações não salvas no formulário.

## Arquitetura

MVVM em camadas, com fluxo de dados unidirecional (UDF):

```
UI (Jetpack Compose)  ──eventos──▶  ViewModel  ──▶  Repository  ──▶  Data Source
        ▲                              │                                ├─ Room (OcorrenciaDao)
        └──────── StateFlow<UiState> ◀─┘                                ├─ Retrofit (OpenMeteoApi)
                                                                        └─ DataStore (preferências)
```

- **UI:** só desenha o estado e repassa eventos. Cada tela tem uma função `Route` (conecta o ViewModel) e uma função `Screen` sem estado (state hoisting).
- **ViewModel:** expõe `StateFlow` imutável; não recebe `Context`. Estados de rede modelados com `sealed interface AlertasUiState { Loading, Success, Error }`.
- **Repository:** única porta de acesso aos dados; executa o trabalho com `Dispatchers.IO`.
- **Injeção de dependência:** manual, em `di/AppContainer.kt`, com uma fábrica única de ViewModels em `di/AppViewModelProvider.kt`.

### Estrutura de pastas

```
app/src/main/java/com/sosclima/app/
├── data/
│   ├── local/         # Room (Entity, DAO, Database), DataStore, guia offline
│   ├── remote/        # Retrofit (OpenMeteoApi) e DTOs
│   └── repository/    # OcorrenciaRepository, PrevisaoRepository, PreferenciasRepository
├── domain/            # Modelos e regras (nível de alerta, formatação)
├── di/                # AppContainer e fábrica de ViewModels
└── ui/
    ├── navigation/    # NavHost, rotas e NavigationBar
    ├── alertas/       # Lista + detalhe (API)
    ├── ocorrencias/   # Lista + formulário (Room)
    ├── emergencia/    # Guia offline
    ├── ajustes/       # Preferências
    ├── components/    # AlertDialog, estados de carregando/erro/vazio
    └── theme/         # Material 3
```

### Bibliotecas

Kotlin 2.0 · Jetpack Compose (BOM 2024.09) · Material 3 · Navigation Compose · Lifecycle ViewModel · Room 2.6 (KSP) · DataStore Preferences · Retrofit 2.11 + Gson · OkHttp · Kotlin Coroutines/Flow

### API

[Open-Meteo](https://open-meteo.com) — gratuita, sem chave de acesso. Endpoint usado:

```
GET https://api.open-meteo.com/v1/forecast?latitude=-23.55&longitude=-46.63
    &daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,
           precipitation_probability_max,wind_speed_10m_max
    &timezone=America/Sao_Paulo&forecast_days=7
```

O nível de alerta é calculado pelo app (chuva ≥ 50 mm ou vento ≥ 60 km/h = alerta alto; chuva ≥ 20 mm, vento ≥ 40 km/h ou chance de chuva ≥ 80% = atenção). Não substitui os avisos oficiais da Defesa Civil.

## Como executar

1. Instale o **Android Studio** (Koala ou mais recente) com JDK 17.
2. `git clone <url-do-repositorio>` e abra a pasta no Android Studio (**File > Open**).
3. Aguarde o Gradle sincronizar (baixa as dependências na primeira vez).
4. Crie um emulador com Android 8.0 (API 26) ou superior, ou conecte um celular com depuração USB.
5. Clique em **Run ▶**.

Pela linha de comando: `./gradlew assembleDebug` (o APK fica em `app/build/outputs/apk/debug/`).

Requisitos: minSdk 26, targetSdk 34, acesso à internet para o módulo de alertas.
