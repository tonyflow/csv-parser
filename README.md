# CSV Parser
## Description
This is a CSV parser including some special case handling
### Before you use the API
Make sure you have appropriately defined the configuration properties in `parser.conf`. For the time being, there are
only 3 configuration properties
```
csv {
   quoting-string = "
   line-separator = \n
   field-delimiter = ,
   has-header = false
}
```
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
Run the `ParserApp` class or the tests included in the test package

## Missing 
- Some test cases have not been implemented yet and thus are ignore. Only the description has been specified.
- The parser has not been tested with ***really*** large files. The largest file it has been tested with is 10 MB 
and the performance was quite acceptable. The file is not included in the repository for size purposes.
- The parser does not handle quoted string. The implementation ***is*** handling special cases of quotes cells but will
 not take into account simple fields in quotes.
 - Special cases in quotes cells will maintain the quotes after parsing i.e. The `LazyList` results will include
 the quotes. 
- Multiple character encodings are not supported.
- More thorough testing.
- Appropriate header handling. For now the header is just another entry in the `LazyList`.