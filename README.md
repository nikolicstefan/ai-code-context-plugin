# AI Code Context

Minimal IntelliJ IDEA plugin for building AI prompts from selected code context.

## Features

- Adds an editor context-menu action: `Build AI Prompt from Selection`
- Reads the currently selected code from the editor
- Includes the current file name as context
- Builds a structured AI prompt
- Copies the prompt to the clipboard
- Shows IDE notifications for success and missing selection

## Example Output

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


## Why this is AI-related

The plugin does not call an LLM directly. Instead, it focuses on preparing IDE-specific context for AI assistants and coding agents.

This is useful because selected code, file names, and structured prompts can help external AI tools produce more relevant explanations and suggestions.

## Technologies

- Java
- IntelliJ Platform SDK
- Gradle

## Project Structure

```text
src/main/java/com/nikolicstefan/aicodecontext/
└── BuildPromptFromSelectionAction.java

src/main/resources/META-INF/
└── plugin.xml
```

## Running

```bash
./gradlew runIde
```

This starts a sandbox IntelliJ IDEA instance with the plugin installed.

## Usage

1. Open a file in the sandbox IDE.
2. Select a piece of code.
3. Right-click inside the editor.
4. Choose `Build AI Prompt from Selection`.
5. Paste the copied prompt into an AI assistant.

## Notes

This is a small proof-of-concept plugin focused on IDE context extraction and prompt construction.