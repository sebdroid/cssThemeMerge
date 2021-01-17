# cssThemeMerge

Remove duplicate elements and rules from css files to allow efficient theme switching. Made to allow the following use case:
```css
/* Default CSS Theme */

@media (prefers-color-scheme: dark) {
  /* Alternative CSS theme */
}
```