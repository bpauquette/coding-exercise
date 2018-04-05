#!C:/Strawberry/perl/bin/perl.exe
use strict;
use warnings;
use CGI;
use CGI::Log;
# warn user (from perspective of caller)
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
print { $OUTFILE } "Here is your sign......\n"
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

# read the CGI params
#my @names = $q->param;
#my $name="";
#foreach $name ( @names ) { 
#  if ( $name =~ /\_/ ) { 
#    next; 
#  } else { 
#    print "".$name."\t=\t".$q->param($name) . "\n"; 
#  }
#}
my $search = $q->param("search");
my $description = $q->param("description");
my $datepicker =$q->param("datepicker");
  
   
# Create inline java object that imports other java classes  
my $java_obj = Driver->new();
if (!defined($search)) {
  print { $OUTFILE } "Search is undefined\n"
  or croak "Cannot write to $files: $OS_ERROR";

  $java_obj->setSearchForText("");
  my $json=$java_obj->getSearchResult();
  print $json;
} elsif (defined($search) && $search ne "")
{ Log->debug("We received a search parameter of ".$search);	
  $java_obj->setSearchForText($search);
  my $json=$java_obj->getSearchResult();
  print $json;
} elsif($description && $datepicker) {
       Log->debug("Create parms received");	
       $java_obj->setDescription($description);
       $java_obj->setInputDate($datepicker);
       $java_obj->createAppointment();
#       my $polite=false;
#       if (not($polite)) {
         print $q->redirect('http://localhost');
#       } else {
#         print $q->header("text/html");
#         print $q->start_html('Appointment Confirmed');
#         print $q->h1("Thank you!");
#         print $q->p("Your request to add appointment for $description on $datepicker was processed successfully.");
#         print $q->p('<a href="index.html">Click your heels together three times... There is no place like home</a>');
#         print $q->end_html;
#       }
} else {
    print { $OUTFILE } "Invalid Input\n"
    or croak "Cannot write to $files: $OS_ERROR";
    my( $name, $value );
    foreach $name ( $q->param ) {
      print { $OUTFILE } "$name:"
      or croak "Cannot write to $files: $OS_ERROR";
   
      foreach $value ( $q->param( $name ) ) {
          print { $OUTFILE }"|$value|\n"
           or croak "Cannot write to $files: $OS_ERROR";
      }
    }
    dumphtml();
}   
close $OUTFILE
or croak "Cannot close $files: $OS_ERROR";

sub dumphtml {
    my $template = snag('index.html');
    print $q->header("text/html");
    print $q->$template;
}

sub snag {
  local $/ = undef;
  my $template_file = shift;
  open F, $template_file or die "can't open $template_file";
  my $template = <F>;
  close F;
  $template;
}   

 




