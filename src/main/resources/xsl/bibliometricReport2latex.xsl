<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
                xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
                xmlns:mods="http://www.loc.gov/mods/v3" exclude-result-prefixes="xsl xalan i18n mabxml">

  <xsl:output method="text" omit-xml-declaration="yes"
              encoding="UTF-8"/>

  <xsl:variable name="name">
    <xsl:value-of select="bibliometricAnalysis/author/@name"/>
  </xsl:variable>

  <xsl:template match="/">
    \documentclass[ %
    version=3.01b, %
    oneside, %
    BCOR=0mm, DIV=14, %
    captions=tableheading, %
    %toc=flat, %
    headsepline=true,%
    %draft=true, %
    parskip=half*]{scrartcl}

    \usepackage[english,ngerman]{babel}
    \usepackage[utf8]{inputenc}
    \usepackage{graphicx}

    \KOMAoptions{DIV=last}
    \renewcommand{\baselinestretch}{1.25}

    \newcommand{\mycaption}[1]{\renewcommand{\baselinestretch}{1.0}
    \caption{#1} \renewcommand{\baselinestretch}{1.25}}

    \usepackage{hyperref}
    \hypersetup{colorlinks=true,urlcolor=blue,linkcolor=blue,citecolor=red,bookmarksopen=true,bookmarksnumbered=true, pdfstartview=FitH, pdfmenubar=true, pdftoolbar=true, pdftitle={Bibliometric
    Report}, pdfauthor={AG Forschung \&amp; Innovation}}
    \setkomafont{caption}{\sffamily\footnotesize}
    \setkomafont{captionlabel}{\usekomafont{caption}\bfseries}

    \pagestyle{headings}

    \newcommand{\rb}[1]{\raisebox{1.5ex}[-1.5ex]{#1}}

    \begin{document}
    <xsl:apply-templates select="bibliometricAnalysis"/>
    \end{document}
  </xsl:template>

  <xsl:template match="bibliometricAnalysis">
    \begin{titlepage}
    \center
    \vspace*{1.5cm}
    {\Huge\textbf Bibliometrische
    Analyse von \\
    <xsl:value-of select="$name"/>
    }

    \vspace{1cm}

    Erstellt am \today \\[3cm]

    auf der Basis der in der
    Universitätsbibliographie verzeichneten
    Publikationen\\[2cm]

    <xsl:text>
		{\textbf\Large AG Forschung \&amp; Innovation}\\[0.25cm]
		</xsl:text>
    Universitätsbibliothek Duisburg-Essen\\
    \href{mailto:test@example.net}{ag-forschung.ub@uni-due.de}\\[1cm]

    Den
    Publikationen auf der Spur\\[1cm]
    \includegraphics[width=0.4\textwidth]{../../bibliometrie_logo}
    \end{titlepage}
    \tableofcontents
    \section{Disclaimer}
    Diese
    bibliometrische Analyse zu den Publikationen von
    <xsl:value-of select="$name"/>
    und deren Rezeption in Form von Zitierungen bezieht sich
    ausschließlich auf die wissenschaftlichen Veröffentlichungen, die
    <xsl:value-of select="$name"/>
    in der Universitätsbibliographie Duisburg-Essen verzeichnet sind.

    Zitationsangaben für Artikel in wissenschaftlichen Zeitschriften,
    Konferenzbänden oder Buchkapiteln werden dabei der Zitationsdatenbank
    \href{https://www.scopus.com}{Scopus} entnommen.

    Die Datenbank
    \href{https://www.scopus.com}{Scopus} enthält nur einen
    Teilbereich des
    Publikationswesens (in erster Linie
    Zeitschriftenartikel),
    Publikationen in anderen Medien (zum Beispiel
    in Form von Monographien)
    werden bei den Zitationsanalysen nicht
    berücksichtigt. Für eine
    umfassende Bewertung der wissenschaftlichen
    Arbeit sollte diese Analyse
    daher durch Peer-Review-Verfahren und
    alternative Metriken ergänzt
    werden.
    \pagebreak

    \section{Zusammenfassung}
    \setcounter{page}{1}
    \renewcommand{\thepage}{\arabic{page}}

    <xsl:apply-templates select="generalStatistics"/>


    <xsl:if test="citationStatistics">
      <xsl:variable name="partWithCitationData">
        <xsl:value-of select="citationStatistics/numberOfDocumentsWithCitationData/. div number(generalStatistics/totalNumberOfPublications/.)"/>
      </xsl:variable>
      Davon sind
      <xsl:value-of select="format-number($partWithCitationData,'0.00%')"/>, das entspricht

      <xsl:value-of select="citationStatistics/numberOfDocumentsWithCitationData/."/>
      Publikationen in der Zitationsdatenbank
      <xsl:choose>
        <xsl:when test="citationStatistics/@source = 'scopus'">\href{www.scopus.com}{Scopus}</xsl:when>
        <xsl:otherwise>XXX</xsl:otherwise>
      </xsl:choose>
      gelistet und insgesamt
      <xsl:value-of select="citationStatistics/totalCitations/."/>
      mal zitiert wurden. Dies entspricht einer durchschnittlichen
      Zitationsrate von
      <xsl:value-of
              select='format-number(citationStatistics/totalCitations/. div number(citationStatistics/numberOfDocumentsWithCitationData/.),"#0.0")'/>.

      <xsl:choose>
        <xsl:when test="$partWithCitationData &lt; 0.6">
          \textbf{Warnung:} Es werden nur
          <xsl:value-of select="format-number($partWithCitationData,'0.00%')"/> der in der Universitätsbibliographie verzeichneten Publikation in der Zitationsdatenbank nachgewiesen.
          Die berechneten bibliometrischen Kennzahlen sind somit nicht repäsentativ für den Forschungsoutput von <xsl:value-of select="$name"/>.
          Ein solch geringer Anteil weist außerdem darauf hin, dass in dem Fachgebiet andere Publikationskanäle eine große Rolle spielen.
          Die in der Zitationsdatenbank gelisteten Publikationen können somit durchaus in anderen Medien eine deutliche Resonanz haben, sodass die Zitationszahlen nur einen Teil des wissenschaftlichen
          Impact widerspiegeln.
          Diese Analyse sollte daher unbedingt um eine qualitative Analyse der anderen Publikationsmedien ergänzt werden.
        </xsl:when>
        <xsl:when test="$partWithCitationData &gt; 0.6 and $partWithCitationData &lt; 0.8">
          \textbf{Achtung:} Es werden nur
          <xsl:value-of select="format-number($partWithCitationData,'0.00%')"/> der in der Universitätsbibliographie verzeichneten Publikation in der Zitationsdatenbank nachgewiesen.
          Die berechneten Kennzahlen sind somit mit Vorsicht zu sehen und spiegeln nur einen Teilaspekt des wissenschaftlichen Schaffens von <xsl:value-of select="$name"/>.
          Der Report sollte um eine Analyse der übrigen Publikationen ergänzt werden.
        </xsl:when>
        <xsl:otherwise>
          Es werden nur
          <xsl:value-of select="format-number($partWithCitationData,'0.00%')"/> der in der Universitätsbibliographie verzeichneten Publikation in der Zitationsdatenbank nachgewiesen.
          Die Zitationsanalyse analysiert daher einen Großteil des wissenschaftlichen Outputs von <xsl:value-of select="$name"/>.
        </xsl:otherwise>
      </xsl:choose>
      \subsection{Bibliometrische Faktoren im Überblick}
      \begin{tabular}{l l
      l}
      <xsl:choose>
        <xsl:when test="citationStatistics/@source = 'scopus'">

          Gesamtzahl der Publikationen in \href{www.scopus.com}{Scopus} : &amp; $N_P$ = &amp;
          <xsl:value-of select="citationStatistics/numberOfDocumentsWithCitationData/./."/>\\
          Gesamtzahl der Zitationen laut \href{www.scopus.com}{Scopus} : &amp; $N_Z$ = &amp;
          <xsl:value-of select="citationStatistics/totalCitations/."/>\\
        </xsl:when>
        <xsl:otherwise>
          Gesamtzahl der Publikationen : &amp; $N_P$ = &amp;
          <xsl:value-of select="citationStatistics/numberOfDocumentsWithCitationData/."/>\\
          Gesamtzahl der Zitationen : &amp; $N_Z$ = &amp;
          <xsl:value-of select="citationStatistics/totalCitations/."/>\\
        </xsl:otherwise>
      </xsl:choose>

      Durchschnittliche Anzahl Zitationen pro Publication: &amp; CpP = &amp;
      <xsl:value-of select='format-number(number(publication/averageCitationPerPublication/.),"#0.0")'/>
      \\
      <xsl:if test="citationStatistics/uncited">
        Anteil nicht-zitierter Publikationen: &amp; $P_{\mathrm uncited}$ = &amp;
        <xsl:value-of select='format-number(citationStatistics/uncited/. div number(citationStatistics/numberOfDocumentsWithCitationData/.),"0.00%")'/>
        \\
      </xsl:if>
      <xsl:if test="citationStatistics/hIndex">
        Hirsch-Index &amp; $h$ = &amp;
        <xsl:value-of select="citationStatistics/hIndex/."/>
        \\
      </xsl:if>
      \end{tabular}
    </xsl:if>

    \pagebreak
    \section{Im Einzelnen:}
    \subsection{Zitationsrate}
    Die Zitationsrate (Citation per Publication, $CpP$) ist ein Maß für
    die wissenschaftliche
    Wahrnehmung der Publikationen bspw. eines
    Wissenschaftlers, einer
    Arbeitsgruppe oder einer Institution. Durch die
    Berechnung des
    Quotienten aus der Anzahl der Zitationen ($C$) und der
    Anzahl der
    Publikationen ($P$) ermöglicht die so ermittelte
    Zitationsrate einen
    relativen Vergleich der wissenschaftlichen
    Wahrnehmung.

    Als arithmetisches Mittel ist diese Kennzahl sensitiv
    gegenüber
    Ausreißern und berücksichtigt weder die Varianz noch die
    Verteilung
    der Zitationsanzahl einzelner Publikationen innerhalb des
    gewählten
    Zeitraums. Ebenso beeinflussen die Datenquelle und damit auch
    welche
    und wie viele Publikationen in die Berechnung einbezogen werden
    die
    Zitationsrate.

    Für
    <xsl:value-of select="$name"/>
    konnte bei einer Datengrundlage von
    <xsl:value-of select="citationStatistics/numberOfDocumentsWithCitationData/."/>
    Publikationen und
    <xsl:value-of select="citationStatistics/totalCitations/."/>
    Zitationen eine Zitationsrate von
    <xsl:value-of
            select='format-number(citationStatistics/totalCitations/. div number(citationStatistics/numberOfDocumentsWithCitationData/.),"#0.0")'/>
    CPP für den Zeitraum
    <xsl:value-of select="generalStatistics/startYear/."/>
    bis
    <xsl:value-of select="generalStatistics/endYear/."/>
    ermittelt werden.

    <xsl:choose>
      <xsl:when test="citationStatistics/uncited">
        \subsection{Anteil nicht zitierter Publikationen}
        Um zu
        berücksichtigen, dass einige Publikationen, die in die
        Zitationsratenberechnung einbezogen wurden, nicht zitiert wurden,
        wird ergänzend die Kennzahl Uncitedness ($P_{\mathrm uncited}$)
        erhoben. Im Gegensatz zur Zitationsrate stellt diese ein Maß für die
        fehlende Wahrnehmung von Publikationen dar, indem der Anteil der
        nicht zitierten Publikationen an allen berücksichtigen Publikationen
        (P) bestimmt wird. Im Fall von
        <xsl:value-of select="$name"/>
        wurde ein Anteil von
        <xsl:value-of select='format-number(citationStatistics/uncited/. div number(citationStatistics/numberOfDocumentsWithCitationData/.),"0.00%")'/>
        seiner Publikationen nicht zitiert.

        Nach Eugene Garfield basiert die
        Uncitedness von Publikationen nicht
        lediglich auf wissenschaftlicher
        Irrelevanz einer Veröffentlichung,
        sondern vermehrt auf Ignoranz oder
        auf mangelnder Auffindbarkeit
        durch die Fachcommunity. Ebenso werden
        Publikationen aufgrund eines
        hohen Bekanntheitsgrades weniger oder
        sogar gar nicht zitiert, weil
        lediglich die Namensnennung des
        Urhebers erfolgt und damit keine
        expliziten Literaturangaben gemacht
        werden.
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="citationStatistics/hIndex">
        \subsection{h-Index}
        Als Kennzahl des wissenschaftlichen Einflusses
        einer Autorin/eines
        Autors kann ergänzend der {\em h-Index}
        (Hirsch-Index, $h$)
        betrachtet werden. Anhand der abwärts bezüglich
        ihrer
        Zitationshäufigkeit sortierten Artikel einer Autorin/eines
        Autors,
        ist der h-Index als der Wert ablesbar, bei dem die Rangnummer
        der
        Sortierung mit der Anzahl der Zitationen (C) übereinstimmt. Es
        ist
        jedoch zu berücksichtigen, dass der h-Index je nach Fachdisziplin
        sehr stark variieren kann und damit ein interdisziplinärer
        Forschervergleich nicht möglich ist.

        Außerdem eignet sich ein
        Vergleich mittels h-Index erst ab einer gewissen
        Publikationsanzahl,
        die auch vom Alter der/des zu betrachtenden
        Wissenschaftlerin/Wissenschaftlers abhängt und mit dem Alter
        zunimmt. Ein hoher Wert wird mit einem großen wissenschaftlichen
        Einfluss gleichgesetzt.

        <xsl:value-of select="$name"/>
        hat einen h-Index von
        <xsl:value-of select="citationStatistics/hIndex/."/>, das heißt,
        <xsl:value-of select="citationStatistics/hIndex/."/>
        seiner Publikationen wurden jeweils mindestens
        <xsl:value-of select="citationStatistics/hIndex/."/>
        mal zitiert.
      </xsl:when>
    </xsl:choose>
    \section{Zeitlicher Verlauf der Publikationen und Zitationen}
    In Abbildung \ref{fig:pubsPerYear} wird der zeitliche Verlauf des Publikationsverhaltens von
    <xsl:value-of select="$name"/> dargestellt.

    \begin{figure}
    \includegraphics[width=0.9\textwidth]{pubsPerYear}
    \caption{Zeitlicher Verlauf der Publikationstätigkeit von <xsl:value-of select="$name"/>.}
    \label{fig:pubsPerYear}
    \end{figure}
    \section{Publikationsliste}
    \begin{itemize}
    <xsl:apply-templates select="mods:collection/mods:mods"/>
    \end{itemize}
    \end{document}
  </xsl:template>

  <xsl:template match="generalStatistics">
    <xsl:value-of select="$name"/>
    hat im Zeitraum von
    <xsl:value-of select="startYear"/>
    bis
    <xsl:value-of select="endYear/."/>
    insgesamt
    <xsl:value-of select="totalNumberOfPublications/."/>
    Beiträge verfasst, die in der Universitätsbibliographie verzeichnet
    sind.
    Die Verteilung auf verschiedene Kommunikationstypen ist in Abbildung \ref{fig:pubsPerType} dargestellt.

    \begin{figure}
    \includegraphics[width=0.3\textwidth]{pubsPerType}
    \caption{Verteilung der Medienart der in der Universitätsbibliographie verzeichneten Publikationen von <xsl:value-of select="$name"/>.}
    \label{fig:pubsPerType}
    \end{figure}
  </xsl:template>

  <xsl:template match="mods:mods">
    \item{
    <xsl:apply-templates select="mods:name"/>
    <xsl:choose>
      <xsl:when test="mods:identifier[@type='doi']">
        <xsl:apply-templates select="mods:identifier[@type='doi']"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="mods:titleInfo"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mods:relatedItem"/>
    <xsl:apply-templates select="mods:originInfo"/>; \textbf{Zitationen:}
    <xsl:choose><xsl:when test="/bibliometricAnalysis/citationStatistics/@source = 'scopus'">\href{<xsl:value-of select="citationInformation/citationLink/."/>}{<xsl:value-of select="citationInformation/count/."/>.}
      </xsl:when><xsl:otherwise>.</xsl:otherwise></xsl:choose>}
  </xsl:template>

  <!-- ========== DOI ========== -->
  <xsl:template match="mods:identifier[@type='doi']">
    \href{http://dx.doi.org/
    <xsl:value-of select="."/>
    }{
    <xsl:apply-templates select="../mods:titleInfo"/>
    }
  </xsl:template>

  <!-- ========== Titel ========== -->
  <xsl:template match="mods:titleInfo">
    <xsl:apply-templates select="mods:nonSort"/>
    <xsl:apply-templates select="mods:title"/>
    <xsl:apply-templates select="mods:subTitle"/>
    <xsl:text>;</xsl:text>
  </xsl:template>


  <!-- ========== Führende Artikel: Der, Die, Das ========== -->
  <xsl:template match="mods:nonSort">
    <xsl:value-of select="text()"/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <!-- ========== Haupttitel ========== -->
  <xsl:template match="mods:title">
    <xsl:value-of select="."/>
  </xsl:template>


  <!-- ========== Untertitel ========== -->
  <xsl:template match="mods:subTitle">
    <xsl:variable name="lastCharOfTitle"
                  select="substring(../mods:title,string-length(../mods:title))"/>
    <xsl:if test="translate($lastCharOfTitle,'?!.:,-;','.......') != '.'">
      <xsl:text> :</xsl:text>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:value-of select="text()"/>
  </xsl:template>

  <!-- ========== Personenname ========== -->
  <xsl:template match="mods:name">
    <xsl:value-of select="mods:displayForm/."/>
    <xsl:choose>
      <xsl:when test="position() != last()">
        <xsl:text>; </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>: </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="mods:originInfo">
    \textbf{(
    <xsl:value-of select="substring(mods:dateIssued/.,1,4)"/>
    )}
  </xsl:template>

  <xsl:template match="mods:relatedItem[@type='host']">
    <xsl:choose>
      <xsl:when test="mods:identifier[@type='issn']">
        <xsl:apply-templates select="mods:identifier[@type='issn']"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="mods:titleInfo"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mods:part"/>
  </xsl:template>

  <!-- ========== Band/Jahrgang, Heftnummer, Seitenangaben ========== -->
  <xsl:template match="mods:part">
    <xsl:apply-templates select="mods:detail[@type='volume']"/>
    <xsl:apply-templates select="mods:detail[@type='issue']"/>
    <xsl:apply-templates select="mods:detail[@type='page']"/>
    <xsl:apply-templates select="mods:extent[@unit='pages']"/>
  </xsl:template>

  <!-- ========== Band/Jahrgang ========== -->
  <xsl:template match="mods:detail[@type='volume']">
    <xsl:text>, Vol. </xsl:text>
    <xsl:value-of select="mods:number/."/>
  </xsl:template>

  <!-- ========== Heftnummer ========== -->
  <xsl:template match="mods:detail[@type='issue']">
    <xsl:text>, Iss. </xsl:text>
    \textit{
    <xsl:value-of select="mods:number"/>
    }
  </xsl:template>

  <!-- ========== Einzelne Seite ========== -->
  <xsl:template match="mods:detail[@type='page']">
    <xsl:if test="../mods:detail[not(@type='page')]">
      ,
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:value-of select="mods:number"/>
  </xsl:template>

  <!-- ========== Seiten von-bis ========== -->
  <xsl:template match="mods:extent[@unit='pages']">
    <xsl:apply-templates select="mods:start|mods:end"/>
  </xsl:template>

  <xsl:template match="mods:start">
    <xsl:text>, S. </xsl:text>
    <xsl:value-of select="text()"/>
  </xsl:template>

  <xsl:template match="mods:end">
    <xsl:text> -- </xsl:text>
    <xsl:value-of select="text()"/>
  </xsl:template>

</xsl:stylesheet>