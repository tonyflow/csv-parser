# CSV Parser
## Description
This is a CSV parser including some special case handling
### Examples of special cases
- Absent elements: `,a,b,,,,c,`
- Field delimiters in quoted cells: `a,"b,c,d",e`
- New line characters in quoted cells: 
```csv
a,"a split
cell",b,"something else"
```
- Part of field appears as quoted `"abc,"onetwo,three,doremi` 

## How to run
Run the main class or the tests included in the test package

## Missing
- The field delimiter, line separator and quoting string are configurable but have no default values.
Their values have to explicitly declared in the `parser.conf`. If not then the results cannot be deterministic. 
- Some test cases have not been implemented yet, so it is just the description that's been specified
- The parser has not been tested with ***really*** large files. The largest file it has been tested with is 10 MB 
and the performance was quite acceptable. The file is not included in the repository for size purposes.
- The parser does not handle quoted string. The implementation ***is*** handling special cases of quotes cells but will
 not take into account simple fields in quotes.
 - Special cases in quotes cells will maintain the quotes after parsing i.e. The `LazyList` results will include
 the quotes. 
