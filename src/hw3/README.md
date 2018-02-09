# Assignment 3
## Variant 2
The task is to change the solution of Assignment 1 so that UniqueWordsChecker will be loaded
dynamically from JAR file through custom ClassLoader.

The rest of the program is the same: it gets a list of text resources as an input and 
checks uniqueness of each word. Each resource should be processes in a separate thread.
Text contains only Cyrillic symbols and numbers. All errors in the text should be correctly processed.

Alternative check: uniqueness of first 10 symbols of a word.

## Source Code
The source code related to the threads and the app itself is located in the `main` directory. 
* Package `readers` contains implementations of resource readers.
* Package `utils` contains tools for working with text. 
* Package `workers` contains workers that process the resources.

* Directory `resources` contains some test text files.

* Directory `lib` contains JAR files with workers.
