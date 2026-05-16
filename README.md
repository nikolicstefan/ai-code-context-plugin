# AI Code Context

Minimal IntelliJ IDEA plugin for building AI prompts from selected code context
and sending them to a local Ollama model.

## Features

- Adds an editor context-menu action: `Build AI Prompt from Selection`
- Adds an editor context-menu action: `Explain Selection with Local AI`
- Reads the currently selected code from the editor
- Includes the current file name as context
- Builds a structured AI prompt
- Copies the prompt to the clipboard
- Sends prompts to a locally running Ollama model
- Displays AI-generated explanations inside the IDE
- Shows IDE notifications for success and missing selection

## Example Prompt

````text
Explain the following code from file `Test.java`.

Focus on:
- purpose
- important implementation details
- possible issues or improvements

```text
public class Test {
    public static void main(String[] args) {
        int x = 5;
        System.out.println(x * 2);
    }
}

```
````

## Technologies

- Java
- IntelliJ Platform SDK
- Gradle
- Ollama (local LLM integration)

## Project Structure

```text
src/main/java/com/nikolicstefan/aicodecontext/
├── BuildPromptFromSelectionAction.java
└── ExplainSelectionAction.java

src/main/resources/META-INF/
└── plugin.xml
```

## Running

```bash
./gradlew runIde
```

This starts a sandbox IntelliJ IDEA instance with the plugin installed.

## Ollama Setup

To use the local AI explanation feature, install Ollama and run a local model:

```bash
ollama run llama3.2:1b
```

## Usage

### Build AI Prompt from Selection

1. Open a file in the sandbox IDE.
2. Select a piece of code.
3. Right-click inside the editor.
4. Choose `Build AI Prompt from Selection`.
5. Paste the copied prompt into an AI assistant.

### Explain Selection with Local AI

1. Start Ollama locally.
2. Open a file in the sandbox IDE.
3. Select a piece of code.
4. Right-click inside the editor.
5. Choose `Explain Selection with Local AI`.
6. Wait for the generated explanation dialog.

## Notes

This is a small proof-of-concept plugin focused on IDE context extraction,
prompt construction, and lightweight local AI integration.
