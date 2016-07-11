<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ResourceType SYSTEM "http://www.holobloc.com/xml/LibraryElement.dtd" >
<ResourceType Name="GRAPH_RESOURCE" Comment="Resource for displaying a 3-layer (background, edges, nodes) graph" >
  <Identification Standard="IEC 61499" Classification="HMI" Function="Display" Type="Directed/Undirected graph" />
  <VersionInfo Organization="Holobloc Inc" Version="0.5" Author="JHC" Date="2006-08-23" Remarks="Moved to package fb.rt.gui" />
  <VersionInfo Organization="Holobloc Inc" Version="0.4" Author="JHC" Date="2006-08-18" Remarks="Moved all parameters to GRAPH file." />
  <VersionInfo Organization="Holobloc Inc" Version="0.3" Author="JHC" Date="2006-08-16" Remarks="Changed EDGES to GRAPH, deleted IMG." />
  <VersionInfo Organization="Holobloc Inc" Version="0.2" Author="JHC" Date="2006-08-04" Remarks="Added EDGES." />
  <VersionInfo Organization="Holobloc Inc" Version="0.1" Author="JHC" Date="2006-07-27" Remarks="Added FONT." />
  <VersionInfo Organization="Holobloc Inc" Version="0.0" Author="JHC" Date="2006-07-22" />
  <CompilerInfo header="package fb.rt.gui; import java.awt.*;" >
    <Compiler Language="Java" Vendor="Sun" Product="JDK" Version="1.5.0" />
  </CompilerInfo>
  <VarDeclaration Name="GRAPH" Type="WSTRING" Comment="Location relative to FBDK/src of XML file containing graph elements, e.g., &#34;batch/MPP_GRAPH.xml&#34;" />
  <FBNetwork >
    <FB Name="START" Type="E_RESTART" x="11.111111" y="11.111111" />
  </FBNetwork>
</ResourceType>
