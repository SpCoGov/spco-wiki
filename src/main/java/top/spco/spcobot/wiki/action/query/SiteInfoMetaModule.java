package top.spco.spcobot.wiki.action.query;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;

public class SiteInfoMetaModule extends QueryMetaModule<JsonObject> {
    public SiteInfoMetaModule(QueryRequest request) {
        super(request, "si", "siteinfo");
        addQueryParameter("siprop","autocreatetempuser|dbrepllag|defaultoptions|extensions|extensiontags|fileextensions|functionhooks|general|interwikimap|languages|languagevariants|libraries|magicwords|namespacealiases|namespaces|protocols|restrictions|rightsinfo|showhooks|skins|specialpagealiases|statistics|uploaddialog|usergroups|variables");
    }

    @Override
    public void parse(QueryResponse response) {
        result = response.getResponseBodyJson();
    }
}
