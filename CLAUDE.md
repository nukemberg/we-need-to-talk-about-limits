# CLAUDE.md

## Project Overview

This is a Slidev presentation project. Slidev is a markdown-based slides maker and presenter designed for developers that supports Vue components, code highlighting, animations, and various interactive features.

## Content
This is a presentation for DevOpsDays TLV conference. The target audience is DevOps + backend engineers and managers.
The presentation is 20 minutes long

## Development Commands

- **Install dependencies**: `pnpm install`
- **Start development server**: `pnpm dev` (opens at http://localhost:3030)
- **Build for production**: `pnpm build`
- **Export presentation**: `pnpm export`

## Project Structure

- **[slides.md](slides.md)**: Main presentation content (markdown-based slides with frontmatter configuration)
- **[components/](components/)**: Custom Vue components (e.g., Counter.vue) used within slides
- **[snippets/](snippets/)**: External code snippets that can be embedded in slides using `<<< @/snippets/filename.ts#snippet` syntax
- **[pages/](pages/)**: Additional slide pages that can be imported using `src: ./pages/filename.md` in frontmatter

## Slide Development

### Slide Syntax
docs: https://sli.dev/guide/syntax
components: https://sli.dev/builtin/components
config: https://sli.dev/custom/

- Slides use markdown with YAML frontmatter for configuration
- Slides are separated by `---` dividers
- Theme is configured in the frontmatter (currently using 'seriph' theme)
- Each slide can have its own frontmatter for layout, transitions, and other settings

### Vue Integration
- Vue components can be used directly in slides
- Inline `<script setup>` blocks are supported within slides and affect only the current page
- Custom components from [components/](components/) directory are auto-imported

### Key Features
- Code blocks support syntax highlighting, line highlighting, and TypeScript hover with twoslash
- Animations: `v-click`, `v-motion`, `v-mark` directives for interactive elements
- Diagrams: Mermaid and PlantUML support directly in markdown
- Monaco Editor: Add `{monaco}` or `{monaco-run}` to code blocks for in-slide editing/execution
- External snippets: Reference code from [snippets/](snippets/) directory using `<<< @/snippets/file.ts#snippet`
