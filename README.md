# cssThemeMerge

![Latest Build](https://github.com/sebdroid/cssThemeMerge/workflows/Java%20CI%20with%20Maven/badge.svg)

Remove duplicate elements and rules from css files to allow efficient theme switching. Made to allow the following use case:
```css
/* Default CSS Theme */

@media (prefers-color-scheme: dark) {
  /* Alternative CSS theme */
}
```
Usage:
```bash
java -jar cssThemeMerge.jar -d=light.css -a=dark.css -o=output.css
```
