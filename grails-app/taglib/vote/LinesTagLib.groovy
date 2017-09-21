package vote

class LinesTagLib {
    static defaultEncodeAs = 'raw'
    //static encodeAsForTags = [tagName: 'raw']

    def lines = { attrs, body ->
        out << attrs['string'].encodeAsHTML().replace('\n', '<br/>\n')
    }

}
