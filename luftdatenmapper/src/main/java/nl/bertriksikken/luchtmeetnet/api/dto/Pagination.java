package nl.bertriksikken.luchtmeetnet.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Pagination {

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("next_page")
    private int nextPage;

    @JsonProperty("prev_page")
    private int prevPage;

    @JsonProperty("last_page")
    private int lastPage;

    @JsonProperty("first_page")
    private int firstPage;

    @JsonProperty("page_list")
    private int[] pages;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int[] getPages() {
        return pages.clone();
    }

}
