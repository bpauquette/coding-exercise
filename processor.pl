#!C:/Strawberry/perl/bin/perl.exe
use strict;
use warnings;
use CGI;
use CGI::Log;
use Carp;
use Perl::Critic;
BEGIN {
        $ENV{CLASSPATH} .= "C:\\jars\\Appointments.jar;C:\\jars\\sqlite-jdbc-3.21.0.jar;C:\\jars\\gson-2.8.2.jar";
}

# declare variables
my $files = 'mylog.log';
my $OS_ERROR="";


# check if the file exists
if (-f $files) {
    unlink $files
        or croak "Cannot delete $files: $!";
}

# use a variable for the file handle
my $OUTFILE;

# use the three arguments version of open
# and check for errors
open $OUTFILE, '>>', $files
    or croak "Cannot open $files: $OS_ERROR";

# you can check for errors (e.g., if after opening the disk gets full)
print { $OUTFILE } "Call results are here......\n"
    or croak "Cannot write to $files: $OS_ERROR";

    
my $q = new CGI;
use Inline Java => <<'END', AUTOSTUDY => 1 ;
import com.pauquette.appointments.model.AppointmentsDAO;

public class Driver {
	private AppointmentsDAO dao;
	private String searchForText;
	private String description;

	private String inputDate;

	public Driver() {
		dao=AppointmentsDAO.getInstance();
		if (dao.checkPopulated()) {
			// NOOP - The database has some existing tables
		} else {
			// First time initialization of an empty database
			dao.initializeDataBase();
		}
	}

	public String getDescription() {
		return description;
	}

	public String getInputDate() {
		return inputDate;
	}
	public String getSearchForText() {
		return searchForText;
	}
	
	/* Returns JSON string for searchResults */
	public Object getSearchResult() {
		if (getSearchForText()==null||getSearchForText().isEmpty()) {
			return dao.toJson(dao.getAllAppointments());
		} else {
		    return dao.toJson(dao.getAppointmentsContaining(getSearchForText()));
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	public void setSearchForText(String searchForText) {
		this.searchForText = searchForText;
	}
	
	public void createAppointment() {
		dao.createAppointment(getDescription(), getInputDate(), 60);
	}
}
END
if(!$q->param()) {
	print { $OUTFILE } "There were no parameters\n";
	dumphtml();
} else {
    my $search = $q->param("search");
    my $description = $q->param("description");
    my $datepicker =$q->param("datepicker");  
    # Create inline java object that imports other java classes  
    my $java_obj = Driver->new();
    if (defined($search)) {
      print { $OUTFILE } "Search is defined and contains $search\n"
      or croak "Cannot write to $files: $OS_ERROR";
      if ($search =~ /^ *$/) {
      	$search='';
      }
      $java_obj->setSearchForText($search);
      my $json=$java_obj->getSearchResult();
      print $q->header("text/json");
      print $json;
    } elsif(defined($description) && defined($datepicker)) {
       Log->debug("Create parms received were $description and $datepicker");	
       $java_obj->setDescription($description);
       $java_obj->setInputDate($datepicker);
       $java_obj->createAppointment();
       print $q->redirect('http://localhost');
    } else {
        dumphtml();
    }
}   

close $OUTFILE
or croak "Cannot close $files: $OS_ERROR";

sub dumphtml {
    my $template = snag('index.html');
    print $q->header("text/html");
    print $template;
}

sub snag {
  local $/ = undef;
  my $template_file = shift;
  open F, $template_file or die "can't open $template_file";
  my $template = <F>;
  close F;
  $template;
}   



