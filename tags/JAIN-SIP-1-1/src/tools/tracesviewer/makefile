ROOT=../../..
include $(ROOT)/build-config

#EXTRA_LIBS=$(XERCES_HOME)/xerces.jar

all:	
	$(JAVAC) *.java
	rm -rf $(DESTINATION)/tools/tracesviewer/images; cp -r ./images $(DESTINATION)/tools/tracesviewer

viewer:
	$(JAVA) tools.tracesviewer.TracesViewer -server_file ./debug/server_3pcc_log.txt

dagan:
	$(JAVA) tools.tracesviewer.TracesViewer -debug_file ../../examples/proxy/debug/debug_log.txt

herbivore:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 129.6.55.182 -stackId nist-proxy

baboon:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 129.6.55.62 -stackId nist-proxy

local:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 127.0.0.1 -stackId nist-proxy

server:
	touch daemon-logs.txt
	$(JAVA) tools.tracesviewer.TracesViewer -daemon -server_file daemon-logs.txt -port 5500

clean: emacsclean
	\rm  -f *.class
	\rm -f $(DESTINATION)/tools/tracesviewer/*.class
