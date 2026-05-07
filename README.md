# NPC AI Mod

Este mod é uma implementação Fabric de um NPC com inventário, comportamento e comandos.

## Compatibilidade

- O projeto está configurado para Fabric.
- As versões de Minecraft, Yarn, Fabric Loader e Fabric API são controladas em `gradle.properties`.
- O plugin Fabric Loom é carregado via `buildscript` em `build.gradle`.

## Atualização para a versão mais recente

1. Atualize `gradle.properties` com a versão mais recente de Minecraft, Yarn mappings, Fabric Loader e Fabric API.
2. Atualize o `classpath "net.fabricmc:fabric-loom:<versão>"` em `build.gradle` caso seja necessário.
3. Execute `./gradlew clean build` para verificar a compilação.

## Nota sobre Forge

- Este projeto atualmente suporta apenas Fabric.
- Para compatibilidade com Forge, é necessário portar ou criar uma versão separada do mod para o ambiente Forge.
