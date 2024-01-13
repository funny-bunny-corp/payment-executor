package com.paymentic.infra.ce;

import com.paymentic.infra.ce.CExtensions.Audience;
import com.paymentic.infra.ce.CExtensions.EventContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExtensionsBuilder {
  private final Map<String,Object> extensions = new HashMap<>();

  public ExtensionsBuilder audience(Audience audience){
    Objects.requireNonNull(audience,"Audience must be informed");
    this.extensions.put(CExtensions.AUDIENCE.extensionName(), audience.audienceName());
    return this;
  }

  public ExtensionsBuilder tags(List<String> tags){
    Objects.requireNonNull(tags,"Tags must be informed");
    this.extensions.put(CExtensions.TAGS.extensionName(), tags.stream().reduce((w1, w2) -> w1 + "," + w2).get());
    return this;
  }

  public ExtensionsBuilder eventContext(EventContext context){
    Objects.requireNonNull(context,"Event Context must be informed");
    this.extensions.put(CExtensions.EVENT_CONTEXT.extensionName(), context.eventContextName());
    return this;
  }
  public Map<String,Object> build(){
    return Map.copyOf(this.extensions);
  }

}
