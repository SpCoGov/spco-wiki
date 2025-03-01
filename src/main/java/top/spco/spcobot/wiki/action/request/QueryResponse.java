package top.spco.spcobot.wiki.action.request;

import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import top.spco.spcobot.wiki.ActionResponse;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public class QueryResponse extends ActionResponse<QueryRequest, Set<QuerySubmodule<?>>> implements Iterable<QueryResponse> {
    QueryResponse first;
    QueryResponse next;

    public QueryResponse(QueryRequest request, Response response) {
        super(request, response);
    }

    @Override
    public Set<QuerySubmodule<?>> parse() {
        super.parse();
        return request.getSubmodules();
    }

    @NotNull
    @Override
    public Iterator<QueryResponse> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<QueryResponse> {
        QueryResponse current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public QueryResponse next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            QueryResponse result = current;
            current = current.next;
            return result;
        }
    }
}