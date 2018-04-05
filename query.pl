#!C:/Strawberry/perl/bin/perl.exe
use strict;
use warnings;
use CGI;

# do your database stuff here

my $cgi = CGI->new;
my $searchForText = $cgi->param("searchForText");
my $result = "<p>You searched for ".$searchForText."</p>"; 

print $cgi->header('text/html');
print $result;