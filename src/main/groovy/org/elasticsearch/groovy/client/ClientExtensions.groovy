/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.groovy.client

import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.count.CountRequest
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.explain.ExplainRequest
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.get.MultiGetRequest
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptRequest
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptResponse
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequest
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequest
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse
import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.action.percolate.MultiPercolateRequest
import org.elasticsearch.action.percolate.MultiPercolateResponse
import org.elasticsearch.action.percolate.PercolateRequest
import org.elasticsearch.action.percolate.PercolateResponse
import org.elasticsearch.action.search.ClearScrollRequest
import org.elasticsearch.action.search.ClearScrollResponse
import org.elasticsearch.action.search.MultiSearchRequest
import org.elasticsearch.action.search.MultiSearchResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchScrollRequest
import org.elasticsearch.action.suggest.SuggestRequest
import org.elasticsearch.action.suggest.SuggestResponse
import org.elasticsearch.action.termvector.MultiTermVectorsRequest
import org.elasticsearch.action.termvector.MultiTermVectorsResponse
import org.elasticsearch.action.termvector.TermVectorRequest
import org.elasticsearch.action.termvector.TermVectorResponse
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.AdminClient
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.elasticsearch.common.settings.Settings

/**
 * {@code ClientExtensions} provides extensions to the Elasticsearch {@link Client} to enable Groovy-friendly
 * requests.
 * <p />
 * This enables support for using {@link Closure}s to configure (and execute) the various action requests. For example:
 * <pre>
 * IndexResponse indexResponse = client.index {
 *     index "index-name"
 *     type "type-name"
 *     id "id-value"
 *     source {
 *         name "kimchy"
 *         timestamp = new Date()
 *         nested {
 *             other = 1.23
 *             data {
 *                 count = 1234
 *                 values = ["abc", "def"]
 *             }
 *         }
 *     }
 * }.response
 * </pre>
 * The above code would create an {@link IndexRequest}, call {@link IndexRequest#index(String) index("index-name")},
 * {@link IndexRequest#type(String) type("type-name")}, {@link IndexRequest#id(String) id("id-value")}, and {@link
 * IndexRequest#source source(Closure)}.
 */
class ClientExtensions extends AbstractClientExtensions {
    /**
     * Get the admin client that can be used to perform administrative operations.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @return Always {@link Client#admin()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static AdminClient getAdmin(Client self) {
        self.admin()
    }

    /**
     * Get the client {@link Settings}.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @return Always {@link Client#settings()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static Settings getSettings(Client self) {
        self.settings()
    }

    // REQUEST/RESPONSE

    /**
     * Index a document associated with a given index and type, then get the future result.
     * <p/>
     * The id is optional, if it is not provided, one will be generated automatically.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link IndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndexResponse> index(Client self, Closure requestClosure) {
        doRequest(self, Requests.indexRequest(), requestClosure, self.&index)
    }

    /**
     * Executes a bulk of index / delete operations.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link BulkRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<BulkResponse> bulk(Client self, Closure requestClosure) {
        doRequest(self, new BulkRequest(), requestClosure, self.&bulk)
    }

    /**
     * Updates a document based on a script.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link UpdateRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<UpdateResponse> update(Client self, Closure requestClosure) {
        doRequest(self, new UpdateRequest(), requestClosure, self.&update)
    }

    /**
     * Deletes a document from the index based on the index, type and id.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link DeleteRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<DeleteResponse> delete(Client self, Closure requestClosure) {
        doRequest(self, new DeleteRequest(), requestClosure, self.&delete)
    }

    /**
     * Deletes all documents from one or more indices based on a query.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link DeleteByQueryRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<DeleteByQueryResponse> deleteByQuery(Client self, Closure requestClosure) {
        doRequest(self, Requests.deleteByQueryRequest(), requestClosure, self.&deleteByQuery)
    }

    /**
     * Gets a document from the index based on the index, type and id.
     * <p />
     * Note: Get retrievals are performed in real time.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link GetRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<GetResponse> get(Client self, Closure requestClosure) {
        // index is expected to be set by the closure
        doRequest(self, Requests.getRequest(null), requestClosure, self.&get)
    }

    /**
     * Multi-get documents. This provides the mechanism to perform bulk requests (as opposed to bulk indexing) to avoid
     * unnecessary back-and-forth requests.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link MultiGetRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<MultiGetResponse> multiGet(Client self, Closure requestClosure) {
        doRequest(self, new MultiGetRequest(), requestClosure, self.&multiGet)
    }

    /**
     * Request suggestion matching for a specific query.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link SuggestRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<SuggestResponse> suggest(Client self, Closure requestClosure) {
        doRequest(self, new SuggestRequest(), requestClosure, self.&suggest)
    }

    /**
     * Search across one or more indices and one or more types with a query.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link SearchRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<SearchResponse> search(Client self, Closure requestClosure) {
        doRequest(self, Requests.searchRequest(), requestClosure, self.&search)
    }

    /**
     * Perform multiple search requests similar to multi-get.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link MultiSearchRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<MultiSearchResponse> multiSearch(Client self, Closure requestClosure) {
        doRequest(self, new MultiSearchRequest(), requestClosure, self.&multiSearch)
    }

    /**
     * Request a count of documents matching a specified query.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link CountRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<CountResponse> count(Client self, Closure requestClosure) {
        doRequest(self, Requests.countRequest(), requestClosure, self.&count)
    }

    /**
     * A search scroll request to continue searching a previous scrollable search request.
     * <p />
     * Note: The {@link SearchResponse} will contain a new ID to use for subsequent requests.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link SearchScrollRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<SearchResponse> searchScroll(Client self, Closure requestClosure) {
        doRequest(self, new SearchScrollRequest(), requestClosure, self.&searchScroll)
    }

    /**
     * Clears the search contexts associated with specified Scroll IDs.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link ClearScrollRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<ClearScrollResponse> clearScroll(Client self, Closure requestClosure) {
        doRequest(self, new ClearScrollRequest(), requestClosure, self.&clearScroll)
    }

    /**
     * An action that is the term vectors for a specific document.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link TermVectorRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<TermVectorResponse> termVector(Client self, Closure requestClosure) {
        // index, type and id are expected to be set by the closure
        doRequest(self, new TermVectorRequest(null, null, null), requestClosure, self.&termVector)
    }

    /**
     * Multi-get term vectors.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link MultiTermVectorsRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<MultiTermVectorsResponse> multiTermVectors(Client self, Closure requestClosure) {
        doRequest(self, new MultiTermVectorsRequest(), requestClosure, self.&multiTermVectors)
    }

    /**
     * Percolates a requesting the matching documents.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link PercolateRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<PercolateResponse> percolate(Client self, Closure requestClosure) {
        doRequest(self, new PercolateRequest(), requestClosure, self.&percolate)
    }

    /**
     * Performs multiple percolate requests.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link MultiPercolateRequest}.
     * @return Never {@code null}.
     */
    static ListenableActionFuture<MultiPercolateResponse> multiPercolate(Client self, Closure requestClosure) {
        doRequest(self, new MultiPercolateRequest(), requestClosure, self.&multiPercolate)
    }

    /**
     * Computes a score explanation for the specified request.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link ExplainRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ExplainResponse> explain(Client self, Closure requestClosure) {
        doRequest(self, new ExplainRequest(null, null, null), requestClosure, self.&explain)
    }

    /**
     * Put (set/add) the indexed script.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link PutIndexedScriptRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutIndexedScriptResponse> putIndexedScript(Client self, Closure requestClosure) {
        doRequest(self, new PutIndexedScriptRequest(), requestClosure, self.&putIndexedScript)
    }

    /**
     * Get an indexed script.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link GetIndexedScriptRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetIndexedScriptResponse> getIndexedScript(Client self, Closure requestClosure) {
        doRequest(self, new GetIndexedScriptRequest(), requestClosure, self.&getIndexedScript)
    }

    /**
     * Delete an indexed script.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param requestClosure The map-like closure that configures the {@link DeleteIndexedScriptRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteIndexedScriptResponse> deleteIndexedScript(Client self,
                                                                                   Closure requestClosure) {
        doRequest(self, new DeleteIndexedScriptRequest(), requestClosure, self.&deleteIndexedScript)
    }

    /**
     * A more like this action to search for documents that are "like" a specific document.
     *
     * @param self The {@code this} reference for the {@link Client}
     * @param index The index to load the document(s) from
     * @param requestClosure The map-like closure that configures the {@link MoreLikeThisRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null} except {@code index}
     */
    static ListenableActionFuture<SearchResponse> moreLikeThis(Client self, String index, Closure requestClosure) {
        // the only one that _requires_ the index as a parameter/constructor arg
        doRequest(self, Requests.moreLikeThisRequest(index), requestClosure, self.&moreLikeThis)
    }
}