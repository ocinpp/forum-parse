# forum-parse
Get and parse forum data


## Main Parsers

- BForumTopicParser - for parsing the forum starting with "B"
- DForumTopicParser - for parsing the forum starting with "D"

### Setup

- PAGE_PARAM - the parameter name of the parameter used in the forum to go to different pages 
- FORUM_NAME - the forum name to be added in the output
- FORUM_CHARSET - charset of the forum (e.g. utf-8, Big5-HKSCS)
- USER_AGENT - user agent string to be used when getting the page from the forum
- SLEEP_BETWEEN_TOPICS - time to wait between each topic retrieval (in seconds)
- SLEEP_BETWEEN_PAGES - time to wait between each page retrieval (in seconds)

## External libraries

### jsoup
[jsoup](https://jsoup.org/) is used for parsing the HTML

### jackson-databind
[jackson-databind](https://github.com/FasterXML/jackson-databind/) is used to convert the objects to JSON

