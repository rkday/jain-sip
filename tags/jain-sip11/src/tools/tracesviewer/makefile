ROOT=../../..
include $(ROOT)/build-config

EXTRA_LIBS=$(XERCES_HOME)/xerces.jar

all:	
	$(JAVAC) *.java

viewer:
	$(JAVA) tools.tracesviewer.TracesViewer -server_file ./server_3pcc_log.txt

dagan:
	$(JAVA) tools.tracesviewer.TracesViewer -debug_file ../../examples/proxy/debug/debug_log.txt

herbivore:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 129.6.55.182 -stackId nist-proxy

baboon:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 129.6.55.62 -stackId nist-proxy

local:
	$(JAVA) tools.tracesviewer.TracesViewer -rmihost 127.0.0.1 -stackId nist-proxy

clean: emacsclean
	\rm  -f *.class
	\rm -f $(DESTINATION)/tools/tracesviewer/*.class
