# vim: noet: softtabstop=0

xmldir = $(prefix)/dom/components/ExamplePythonComponent/
dist_xml_DATA = ../ExamplePythonComponent.prf.xml ../ExamplePythonComponent.scd.xml ../ExamplePythonComponent.spd.xml
domdir = $(prefix)/dom/components/ExamplePythonComponent/python/

distclean-local:
	rm -f config.*
	rm -rf autom4te.cache
	rm -f acinclude.m4
	rm -f aclocal.m4
	rm -f configure
	rm -f depcomp
	rm -f install-sh
	rm -f ltmain.sh
	rm -f Makefile.in
	rm -f missing
	rm -rf .deps

# Sources are auto-included from a file generated by the REDHAWK IDE. You may
# remove the following lines if you wish to manually control these options.
include $(srcdir)/Makefile.am.ide
nobase_dist_dom_SCRIPTS = $(redhawk_SCRIPTS_auto)
nobase_dist_dom_DATA = $(redhawk_DATA_auto)
