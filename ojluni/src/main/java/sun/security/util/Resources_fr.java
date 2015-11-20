/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.security.util;

/**
 * <p> This class represents the <code>ResourceBundle</code>
 * for javax.security.auth and sun.security.
 *
 */
public class Resources_fr extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // shared (from jarsigner)
        {"SPACE", " "},
        {"2SPACE", "  "},
        {"6SPACE", "      "},
        {"COMMA", ", "},
        // shared (from keytool)
        {"NEWLINE", "\n"},
        {"STAR",
                "*******************************************"},
        {"STARNN",
                "*******************************************\n\n"},

        // keytool: Help part
        {".OPTION.", " [OPTION]..."},
        {"Options.", "Options :"},
        {"Use.keytool.help.for.all.available.commands",
                 "Utiliser \"keytool -help\" pour toutes les commandes disponibles"},
        {"Key.and.Certificate.Management.Tool",
                 "Outil de gestion de certificats et de cl\u00E9s"},
        {"Commands.", "Commandes :"},
        {"Use.keytool.command.name.help.for.usage.of.command.name",
                "Utiliser \"keytool -command_name -help\" pour la syntaxe de command_name"},
        // keytool: help: commands
        {"Generates.a.certificate.request",
                "G\u00E9n\u00E8re une demande de certificat"}, //-certreq
        {"Changes.an.entry.s.alias",
                "Modifie l'alias d'une entr\u00E9e"}, //-changealias
        {"Deletes.an.entry",
                "Supprime une entr\u00E9e"}, //-delete
        {"Exports.certificate",
                "Exporte le certificat"}, //-exportcert
        {"Generates.a.key.pair",
                "G\u00E9n\u00E8re une paire de cl\u00E9s"}, //-genkeypair
        {"Generates.a.secret.key",
                "G\u00E9n\u00E8re une cl\u00E9 secr\u00E8te"}, //-genseckey
        {"Generates.certificate.from.a.certificate.request",
                "G\u00E9n\u00E8re le certificat \u00E0 partir d'une demande de certificat"}, //-gencert
        {"Generates.CRL", "G\u00E9n\u00E8re la liste des certificats r\u00E9voqu\u00E9s (CRL)"}, //-gencrl
        {"Imports.entries.from.a.JDK.1.1.x.style.identity.database",
                "Importe les entr\u00E9es \u00E0 partir d'une base de donn\u00E9es d'identit\u00E9s de type JDK 1.1.x"}, //-identitydb
        {"Imports.a.certificate.or.a.certificate.chain",
                "Importe un certificat ou une cha\u00EEne de certificat"}, //-importcert
        {"Imports.one.or.all.entries.from.another.keystore",
                "Importe une entr\u00E9e ou la totalit\u00E9 des entr\u00E9es depuis un autre fichier de cl\u00E9s"}, //-importkeystore
        {"Clones.a.key.entry",
                "Clone une entr\u00E9e de cl\u00E9"}, //-keyclone
        {"Changes.the.key.password.of.an.entry",
                "Modifie le mot de passe de cl\u00E9 d'une entr\u00E9e"}, //-keypasswd
        {"Lists.entries.in.a.keystore",
                "R\u00E9pertorie les entr\u00E9es d'un fichier de cl\u00E9s"}, //-list
        {"Prints.the.content.of.a.certificate",
                "Imprime le contenu d'un certificat"}, //-printcert
        {"Prints.the.content.of.a.certificate.request",
                "Imprime le contenu d'une demande de certificat"}, //-printcertreq
        {"Prints.the.content.of.a.CRL.file",
                "Imprime le contenu d'un fichier de liste des certificats r\u00E9voqu\u00E9s (CRL)"}, //-printcrl
        {"Generates.a.self.signed.certificate",
                "G\u00E9n\u00E8re un certificat auto-sign\u00E9"}, //-selfcert
        {"Changes.the.store.password.of.a.keystore",
                "Modifie le mot de passe de banque d'un fichier de cl\u00E9s"}, //-storepasswd
        // keytool: help: options
        {"alias.name.of.the.entry.to.process",
                "nom d'alias de l'entr\u00E9e \u00E0 traiter"}, //-alias
        {"destination.alias",
                "alias de destination"}, //-destalias
        {"destination.key.password",
                "mot de passe de la cl\u00E9 de destination"}, //-destkeypass
        {"destination.keystore.name",
                "nom du fichier de cl\u00E9s de destination"}, //-destkeystore
        {"destination.keystore.password.protected",
                "mot de passe du fichier de cl\u00E9s de destination prot\u00E9g\u00E9"}, //-destprotected
        {"destination.keystore.provider.name",
                "nom du fournisseur du fichier de cl\u00E9s de destination"}, //-destprovidername
        {"destination.keystore.password",
                "mot de passe du fichier de cl\u00E9s de destination"}, //-deststorepass
        {"destination.keystore.type",
                "type du fichier de cl\u00E9s de destination"}, //-deststoretype
        {"distinguished.name",
                "nom distinctif"}, //-dname
        {"X.509.extension",
                "extension X.509"}, //-ext
        {"output.file.name",
                "nom du fichier de sortie"}, //-file and -outfile
        {"input.file.name",
                "nom du fichier d'entr\u00E9e"}, //-file and -infile
        {"key.algorithm.name",
                "nom de l'algorithme de cl\u00E9"}, //-keyalg
        {"key.password",
                "mot de passe de la cl\u00E9"}, //-keypass
        {"key.bit.size",
                "taille en bits de la cl\u00E9"}, //-keysize
        {"keystore.name",
                "nom du fichier de cl\u00E9s"}, //-keystore
        {"new.password",
                "nouveau mot de passe"}, //-new
        {"do.not.prompt",
                "ne pas inviter"}, //-noprompt
        {"password.through.protected.mechanism",
                "mot de passe via m\u00E9canisme prot\u00E9g\u00E9"}, //-protected
        {"provider.argument",
                "argument du fournisseur"}, //-providerarg
        {"provider.class.name",
                "nom de la classe de fournisseur"}, //-providerclass
        {"provider.name",
                "nom du fournisseur"}, //-providername
        {"provider.classpath",
                "variable d'environnement CLASSPATH du fournisseur"}, //-providerpath
        {"output.in.RFC.style",
                "sortie au style RFC"}, //-rfc
        {"signature.algorithm.name",
                "nom de l'algorithme de signature"}, //-sigalg
        {"source.alias",
                "alias source"}, //-srcalias
        {"source.key.password",
                "mot de passe de la cl\u00E9 source"}, //-srckeypass
        {"source.keystore.name",
                "nom du fichier de cl\u00E9s source"}, //-srckeystore
        {"source.keystore.password.protected",
                "mot de passe du fichier de cl\u00E9s source prot\u00E9g\u00E9"}, //-srcprotected
        {"source.keystore.provider.name",
                "nom du fournisseur du fichier de cl\u00E9s source"}, //-srcprovidername
        {"source.keystore.password",
                "mot de passe du fichier de cl\u00E9s source"}, //-srcstorepass
        {"source.keystore.type",
                "type du fichier de cl\u00E9s source"}, //-srcstoretype
        {"SSL.server.host.and.port",
                "Port et h\u00F4te du serveur SSL"}, //-sslserver
        {"signed.jar.file",
                "fichier JAR sign\u00E9"}, //=jarfile
        {"certificate.validity.start.date.time",
                "date/heure de d\u00E9but de validit\u00E9 du certificat"}, //-startdate
        {"keystore.password",
                "mot de passe du fichier de cl\u00E9s"}, //-storepass
        {"keystore.type",
                "type du fichier de cl\u00E9s"}, //-storetype
        {"trust.certificates.from.cacerts",
                "certificats s\u00E9curis\u00E9s issus de certificats CA"}, //-trustcacerts
        {"verbose.output",
                "sortie en mode verbose"}, //-v
        {"validity.number.of.days",
                "nombre de jours de validit\u00E9"}, //-validity
        {"Serial.ID.of.cert.to.revoke",
                 "ID de s\u00E9rie du certificat \u00E0 r\u00E9voquer"}, //-id
        // keytool: Running part
        {"keytool.error.", "erreur keytool : "},
        {"Illegal.option.", "Option non admise :  "},
        {"Illegal.value.", "Valeur non admise : "},
        {"Unknown.password.type.", "Type de mot de passe inconnu : "},
        {"Cannot.find.environment.variable.",
                "Variable d'environnement introuvable : "},
        {"Cannot.find.file.", "Fichier introuvable : "},
        {"Command.option.flag.needs.an.argument.", "L''option de commande {0} requiert un argument."},
        {"Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.",
                "Avertissement\u00A0: les mots de passe de cl\u00E9 et de banque distincts ne sont pas pris en charge pour les fichiers de cl\u00E9s d''acc\u00E8s PKCS12. La valeur {0} sp\u00E9cifi\u00E9e par l''utilisateur est ignor\u00E9e."},
        {".keystore.must.be.NONE.if.storetype.is.{0}",
                "-keystore doit \u00EAtre d\u00E9fini sur NONE si -storetype est {0}"},
        {"Too.many.retries.program.terminated",
                 "Trop de tentatives, fin du programme"},
        {".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}",
                "Les commandes -storepasswd et -keypasswd ne sont pas prises en charge si -storetype est d\u00E9fini sur {0}"},
        {".keypasswd.commands.not.supported.if.storetype.is.PKCS12",
                "Les commandes -keypasswd ne sont pas prises en charge si -storetype est d\u00E9fini sur PKCS12"},
        {".keypass.and.new.can.not.be.specified.if.storetype.is.{0}",
                "Les commandes -keypass et -new ne peuvent pas \u00EAtre sp\u00E9cifi\u00E9es si -storetype est d\u00E9fini sur {0}"},
        {"if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified",
                "si -protected est sp\u00E9cifi\u00E9, -storepass, -keypass et -new ne doivent pas \u00EAtre indiqu\u00E9s"},
        {"if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified",
                "Si -srcprotected est indiqu\u00E9, les commandes -srcstorepass et -srckeypass ne doivent pas \u00EAtre sp\u00E9cifi\u00E9es"},
        {"if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified",
                "Si le fichier de cl\u00E9s n'est pas prot\u00E9g\u00E9 par un mot de passe, les commandes -storepass, -keypass et -new ne doivent pas \u00EAtre sp\u00E9cifi\u00E9es"},
        {"if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified",
                "Si le fichier de cl\u00E9s source n'est pas prot\u00E9g\u00E9 par un mot de passe, les commandes -srcstorepass et -srckeypass ne doivent pas \u00EAtre sp\u00E9cifi\u00E9es"},
        {"Illegal.startdate.value", "Valeur de date de d\u00E9but non admise"},
        {"Validity.must.be.greater.than.zero",
                "La validit\u00E9 doit \u00EAtre sup\u00E9rieure \u00E0 z\u00E9ro"},
        {"provName.not.a.provider", "{0} n''est pas un fournisseur"},
        {"Usage.error.no.command.provided", "Erreur de syntaxe\u00A0: aucune commande fournie"},
        {"Source.keystore.file.exists.but.is.empty.", "Le fichier de cl\u00E9s source existe mais il est vide : "},
        {"Please.specify.srckeystore", "Indiquez -srckeystore"},
        {"Must.not.specify.both.v.and.rfc.with.list.command",
                "-v et -rfc ne doivent pas \u00EAtre sp\u00E9cifi\u00E9s avec la commande 'list'"},
        {"Key.password.must.be.at.least.6.characters",
                "Un mot de passe de cl\u00E9 doit comporter au moins 6 caract\u00E8res"},
        {"New.password.must.be.at.least.6.characters",
                "Le nouveau mot de passe doit comporter au moins 6 caract\u00E8res"},
        {"Keystore.file.exists.but.is.empty.",
                "Fichier de cl\u00E9s existant mais vide : "},
        {"Keystore.file.does.not.exist.",
                "Le fichier de cl\u00E9s n'existe pas : "},
        {"Must.specify.destination.alias", "L'alias de destination doit \u00EAtre sp\u00E9cifi\u00E9"},
        {"Must.specify.alias", "L'alias doit \u00EAtre sp\u00E9cifi\u00E9"},
        {"Keystore.password.must.be.at.least.6.characters",
                "Un mot de passe de fichier de cl\u00E9s doit comporter au moins 6 caract\u00E8res"},
        {"Enter.keystore.password.", "Entrez le mot de passe du fichier de cl\u00E9s :  "},
        {"Enter.source.keystore.password.", "Entrez le mot de passe du fichier de cl\u00E9s source\u00A0:  "},
        {"Enter.destination.keystore.password.", "Entrez le mot de passe du fichier de cl\u00E9s de destination\u00A0:  "},
        {"Keystore.password.is.too.short.must.be.at.least.6.characters",
         "Le mot de passe du fichier de cl\u00E9s est trop court : il doit comporter au moins 6 caract\u00E8res"},
        {"Unknown.Entry.Type", "Type d'entr\u00E9e inconnu"},
        {"Too.many.failures.Alias.not.changed", "Trop d'erreurs. Alias non modifi\u00E9"},
        {"Entry.for.alias.alias.successfully.imported.",
                 "L''entr\u00E9e de l''alias {0} a \u00E9t\u00E9 import\u00E9e."},
        {"Entry.for.alias.alias.not.imported.", "L''entr\u00E9e de l''alias {0} n''a pas \u00E9t\u00E9 import\u00E9e."},
        {"Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.",
                 "Probl\u00E8me lors de l''import de l''entr\u00E9e de l''alias {0}\u00A0: {1}.\nL''entr\u00E9e de l''alias {0} n''a pas \u00E9t\u00E9 import\u00E9e."},
        {"Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled",
                 "Commande d''import ex\u00E9cut\u00E9e\u00A0: {0} entr\u00E9es import\u00E9es, \u00E9chec ou annulation de {1} entr\u00E9es"},
        {"Warning.Overwriting.existing.alias.alias.in.destination.keystore",
                 "Avertissement\u00A0: l''alias {0} existant sera remplac\u00E9 dans le fichier de cl\u00E9s d''acc\u00E8s de destination"},
        {"Existing.entry.alias.alias.exists.overwrite.no.",
                 "L''alias d''entr\u00E9e {0} existe d\u00E9j\u00E0. Voulez-vous le remplacer ? [non]\u00A0:  "},
        {"Too.many.failures.try.later", "Trop d'erreurs. R\u00E9essayez plus tard"},
        {"Certification.request.stored.in.file.filename.",
                "Demande de certification stock\u00E9e dans le fichier <{0}>"},
        {"Submit.this.to.your.CA", "Soumettre \u00E0 votre CA"},
        {"if.alias.not.specified.destalias.srckeypass.and.destkeypass.must.not.be.specified",
            "si l'alias n'est pas sp\u00E9cifi\u00E9, destalias, srckeypass et destkeypass ne doivent pas \u00EAtre sp\u00E9cifi\u00E9s"},
        {"Certificate.stored.in.file.filename.",
                "Certificat stock\u00E9 dans le fichier <{0}>"},
        {"Certificate.reply.was.installed.in.keystore",
                "R\u00E9ponse de certificat install\u00E9e dans le fichier de cl\u00E9s"},
        {"Certificate.reply.was.not.installed.in.keystore",
                "R\u00E9ponse de certificat non install\u00E9e dans le fichier de cl\u00E9s"},
        {"Certificate.was.added.to.keystore",
                "Certificat ajout\u00E9 au fichier de cl\u00E9s"},
        {"Certificate.was.not.added.to.keystore",
                "Certificat non ajout\u00E9 au fichier de cl\u00E9s"},
        {".Storing.ksfname.", "[Stockage de {0}]"},
        {"alias.has.no.public.key.certificate.",
                "{0} ne poss\u00E8de pas de cl\u00E9 publique (certificat)"},
        {"Cannot.derive.signature.algorithm",
                "Impossible de d\u00E9duire l'algorithme de signature"},
        {"Alias.alias.does.not.exist",
                "L''alias <{0}> n''existe pas"},
        {"Alias.alias.has.no.certificate",
                "L''alias <{0}> ne poss\u00E8de pas de certificat"},
        {"Key.pair.not.generated.alias.alias.already.exists",
                "Paire de cl\u00E9s non g\u00E9n\u00E9r\u00E9e, l''alias <{0}> existe d\u00E9j\u00E0"},
        {"Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for",
                "G\u00E9n\u00E9ration d''une paire de cl\u00E9s {1} de {0} bits et d''un certificat auto-sign\u00E9 ({2}) d''une validit\u00E9 de {3} jours\n\tpour : {4}"},
        {"Enter.key.password.for.alias.", "Entrez le mot de passe de la cl\u00E9 pour <{0}>"},
        {".RETURN.if.same.as.keystore.password.",
                "\t(appuyez sur Entr\u00E9e s'il s'agit du mot de passe du fichier de cl\u00E9s) :  "},
        {"Key.password.is.too.short.must.be.at.least.6.characters",
                "Le mot de passe de la cl\u00E9 est trop court : il doit comporter au moins 6 caract\u00E8res"},
        {"Too.many.failures.key.not.added.to.keystore",
                "Trop d'erreurs. Cl\u00E9 non ajout\u00E9e au fichier de cl\u00E9s"},
        {"Destination.alias.dest.already.exists",
                "L''alias de la destination <{0}> existe d\u00E9j\u00E0"},
        {"Password.is.too.short.must.be.at.least.6.characters",
                "Le mot de passe est trop court : il doit comporter au moins 6 caract\u00E8res"},
        {"Too.many.failures.Key.entry.not.cloned",
                "Trop d'erreurs. Entr\u00E9e de cl\u00E9 non clon\u00E9e"},
        {"key.password.for.alias.", "mot de passe de cl\u00E9 pour <{0}>"},
        {"Keystore.entry.for.id.getName.already.exists",
                "L''entr\u00E9e de fichier de cl\u00E9s d''acc\u00E8s pour <{0}> existe d\u00E9j\u00E0"},
        {"Creating.keystore.entry.for.id.getName.",
                "Cr\u00E9ation d''une entr\u00E9e de fichier de cl\u00E9s d''acc\u00E8s pour <{0}>..."},
        {"No.entries.from.identity.database.added",
                "Aucune entr\u00E9e ajout\u00E9e \u00E0 partir de la base de donn\u00E9es d'identit\u00E9s"},
        {"Alias.name.alias", "Nom d''alias : {0}"},
        {"Creation.date.keyStore.getCreationDate.alias.",
                "Date de cr\u00E9ation : {0,date}"},
        {"alias.keyStore.getCreationDate.alias.",
                "{0}, {1,date}, "},
        {"alias.", "{0}, "},
        {"Entry.type.type.", "Type d''entr\u00E9e\u00A0: {0}"},
        {"Certificate.chain.length.", "Longueur de cha\u00EEne du certificat : "},
        {"Certificate.i.1.", "Certificat[{0,number,integer}]:"},
        {"Certificate.fingerprint.SHA1.", "Empreinte du certificat (SHA1) : "},
        {"Keystore.type.", "Type de fichier de cl\u00E9s : "},
        {"Keystore.provider.", "Fournisseur de fichier de cl\u00E9s : "},
        {"Your.keystore.contains.keyStore.size.entry",
                "Votre fichier de cl\u00E9s d''acc\u00E8s contient {0,number,integer} entr\u00E9e"},
        {"Your.keystore.contains.keyStore.size.entries",
                "Votre fichier de cl\u00E9s d''acc\u00E8s contient {0,number,integer} entr\u00E9es"},
        {"Failed.to.parse.input", "L'analyse de l'entr\u00E9e a \u00E9chou\u00E9"},
        {"Empty.input", "Entr\u00E9e vide"},
        {"Not.X.509.certificate", "Pas un certificat X.509"},
        {"alias.has.no.public.key", "{0} ne poss\u00E8de pas de cl\u00E9 publique"},
        {"alias.has.no.X.509.certificate", "{0} ne poss\u00E8de pas de certificat X.509"},
        {"New.certificate.self.signed.", "Nouveau certificat (auto-sign\u00E9) :"},
        {"Reply.has.no.certificates", "La r\u00E9ponse n'a pas de certificat"},
        {"Certificate.not.imported.alias.alias.already.exists",
                "Certificat non import\u00E9, l''alias <{0}> existe d\u00E9j\u00E0"},
        {"Input.not.an.X.509.certificate", "L'entr\u00E9e n'est pas un certificat X.509"},
        {"Certificate.already.exists.in.keystore.under.alias.trustalias.",
                "Le certificat existe d\u00E9j\u00E0 dans le fichier de cl\u00E9s d''acc\u00E8s sous l''alias <{0}>"},
        {"Do.you.still.want.to.add.it.no.",
                "Voulez-vous toujours l'ajouter ? [non] :  "},
        {"Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.",
                "Le certificat existe d\u00E9j\u00E0 dans le fichier de cl\u00E9s d''acc\u00E8s CA syst\u00E8me sous l''alias <{0}>"},
        {"Do.you.still.want.to.add.it.to.your.own.keystore.no.",
                "Voulez-vous toujours l'ajouter \u00E0 votre fichier de cl\u00E9s ? [non] :  "},
        {"Trust.this.certificate.no.", "Faire confiance \u00E0 ce certificat ? [non] :  "},
        {"YES", "OUI"},
        {"New.prompt.", "Nouveau {0} : "},
        {"Passwords.must.differ", "Les mots de passe doivent diff\u00E9rer"},
        {"Re.enter.new.prompt.", "Indiquez encore le nouveau {0} : "},
        {"Re.enter.new.password.", "Ressaisissez le nouveau mot de passe : "},
        {"They.don.t.match.Try.again", "Ils sont diff\u00E9rents. R\u00E9essayez."},
        {"Enter.prompt.alias.name.", "Indiquez le nom d''alias {0} :  "},
        {"Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry.",
                 "Saisissez le nom du nouvel alias\t(ou appuyez sur Entr\u00E9e pour annuler l'import de cette entr\u00E9e)\u00A0:  "},
        {"Enter.alias.name.", "Indiquez le nom d'alias :  "},
        {".RETURN.if.same.as.for.otherAlias.",
                "\t(appuyez sur Entr\u00E9e si le r\u00E9sultat est identique \u00E0 <{0}>)"},
        {".PATTERN.printX509Cert",
                "Propri\u00E9taire : {0}\nEmetteur : {1}\nNum\u00E9ro de s\u00E9rie : {2}\nValide du : {3} au : {4}\nEmpreintes du certificat :\n\t MD5:  {5}\n\t SHA1 : {6}\n\t SHA256 : {7}\n\t Nom de l''algorithme de signature : {8}\n\t Version : {9}"},
        {"What.is.your.first.and.last.name.",
                "Quels sont vos nom et pr\u00E9nom ?"},
        {"What.is.the.name.of.your.organizational.unit.",
                "Quel est le nom de votre unit\u00E9 organisationnelle ?"},
        {"What.is.the.name.of.your.organization.",
                "Quel est le nom de votre entreprise ?"},
        {"What.is.the.name.of.your.City.or.Locality.",
                "Quel est le nom de votre ville de r\u00E9sidence ?"},
        {"What.is.the.name.of.your.State.or.Province.",
                "Quel est le nom de votre \u00E9tat ou province ?"},
        {"What.is.the.two.letter.country.code.for.this.unit.",
                "Quel est le code pays \u00E0 deux lettres pour cette unit\u00E9 ?"},
        {"Is.name.correct.", "Est-ce {0} ?"},
        {"no", "non"},
        {"yes", "oui"},
        {"y", "o"},
        {".defaultValue.", "  [{0}]:  "},
        {"Alias.alias.has.no.key",
                "L''alias <{0}> n''est associ\u00E9 \u00E0 aucune cl\u00E9"},
        {"Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key",
                 "L''entr\u00E9e \u00E0 laquelle l''alias <{0}> fait r\u00E9f\u00E9rence n''est pas une entr\u00E9e de type cl\u00E9 priv\u00E9e. La commande -keyclone prend uniquement en charge le clonage des cl\u00E9s priv\u00E9es"},

        {".WARNING.WARNING.WARNING.",
            "*****************  WARNING WARNING WARNING  *****************"},
        {"Signer.d.", "Signataire n\u00B0%d :"},
        {"Timestamp.", "Horodatage :"},
        {"Signature.", "Signature :"},
        {"CRLs.", "Listes des certificats r\u00E9voqu\u00E9s (CRL) :"},
        {"Certificate.owner.", "Propri\u00E9taire du certificat : "},
        {"Not.a.signed.jar.file", "Fichier JAR non sign\u00E9"},
        {"No.certificate.from.the.SSL.server",
                "Aucun certificat du serveur SSL"},

        {".The.integrity.of.the.information.stored.in.your.keystore.",
            "* L'int\u00E9grit\u00E9 des informations stock\u00E9es dans votre fichier de cl\u00E9s  *\n* n'a PAS \u00E9t\u00E9 v\u00E9rifi\u00E9e. Pour cela, *\n* vous devez fournir le mot de passe de votre fichier de cl\u00E9s.                  *"},
        {".The.integrity.of.the.information.stored.in.the.srckeystore.",
            "* L'int\u00E9grit\u00E9 des informations stock\u00E9es dans le fichier de cl\u00E9s source  *\n* n'a PAS \u00E9t\u00E9 v\u00E9rifi\u00E9e. Pour cela, *\n* vous devez fournir le mot de passe de votre fichier de cl\u00E9s source.                  *"},

        {"Certificate.reply.does.not.contain.public.key.for.alias.",
                "La r\u00E9ponse au certificat ne contient pas de cl\u00E9 publique pour <{0}>"},
        {"Incomplete.certificate.chain.in.reply",
                "Cha\u00EEne de certificat incompl\u00E8te dans la r\u00E9ponse"},
        {"Certificate.chain.in.reply.does.not.verify.",
                "La cha\u00EEne de certificat de la r\u00E9ponse ne concorde pas : "},
        {"Top.level.certificate.in.reply.",
                "Certificat de niveau sup\u00E9rieur dans la r\u00E9ponse :\n"},
        {".is.not.trusted.", "... non s\u00E9curis\u00E9. "},
        {"Install.reply.anyway.no.", "Installer la r\u00E9ponse quand m\u00EAme ? [non] :  "},
        {"NO", "NON"},
        {"Public.keys.in.reply.and.keystore.don.t.match",
                "Les cl\u00E9s publiques de la r\u00E9ponse et du fichier de cl\u00E9s ne concordent pas"},
        {"Certificate.reply.and.certificate.in.keystore.are.identical",
                "La r\u00E9ponse au certificat et le certificat du fichier de cl\u00E9s sont identiques"},
        {"Failed.to.establish.chain.from.reply",
                "Impossible de cr\u00E9er une cha\u00EEne \u00E0 partir de la r\u00E9ponse"},
        {"n", "n"},
        {"Wrong.answer.try.again", "R\u00E9ponse incorrecte, recommencez"},
        {"Secret.key.not.generated.alias.alias.already.exists",
                "Cl\u00E9 secr\u00E8te non g\u00E9n\u00E9r\u00E9e, l''alias <{0}> existe d\u00E9j\u00E0"},
        {"Please.provide.keysize.for.secret.key.generation",
                "Indiquez -keysize pour la g\u00E9n\u00E9ration de la cl\u00E9 secr\u00E8te"},

        {"Extensions.", "Extensions\u00A0: "},
        {".Empty.value.", "(Valeur vide)"},
        {"Extension.Request.", "Demande d'extension :"},
        {"PKCS.10.Certificate.Request.Version.1.0.Subject.s.Public.Key.s.format.s.key.",
                "Demande de certificat PKCS #10 (version 1.0)\nSujet : %s\nCl\u00E9 publique : format %s pour la cl\u00E9 %s\n"},
        {"Unknown.keyUsage.type.", "Type keyUsage inconnu : "},
        {"Unknown.extendedkeyUsage.type.", "Type extendedkeyUsage inconnu : "},
        {"Unknown.AccessDescription.type.", "Type AccessDescription inconnu : "},
        {"Unrecognized.GeneralName.type.", "Type GeneralName non reconnu : "},
        {"This.extension.cannot.be.marked.as.critical.",
                 "Cette extension ne peut pas \u00EAtre marqu\u00E9e comme critique. "},
        {"Odd.number.of.hex.digits.found.", "Nombre impair de chiffres hexad\u00E9cimaux trouv\u00E9 : "},
        {"Unknown.extension.type.", "Type d'extension inconnu : "},
        {"command.{0}.is.ambiguous.", "commande {0} ambigu\u00EB :"},

        // policytool
        {"Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.",
                "Avertissement\u00A0: il n''existe pas de cl\u00E9 publique pour l''alias {0}. V\u00E9rifiez que le fichier de cl\u00E9s d''acc\u00E8s est correctement configur\u00E9."},
        {"Warning.Class.not.found.class", "Avertissement : classe introuvable - {0}"},
        {"Warning.Invalid.argument.s.for.constructor.arg",
                "Avertissement\u00A0: arguments non valides pour le constructeur\u00A0- {0}"},
        {"Illegal.Principal.Type.type", "Type de principal non admis : {0}"},
        {"Illegal.option.option", "Option non admise : {0}"},
        {"Usage.policytool.options.", "Syntaxe : policytool [options]"},
        {".file.file.policy.file.location",
                "  [-file <file>]    emplacement du fichier de r\u00E8gles"},
        {"New", "Nouveau"},
        {"Open", "Ouvrir"},
        {"Save", "Enregistrer"},
        {"Save.As", "Enregistrer sous"},
        {"View.Warning.Log", "Afficher le journal des avertissements"},
        {"Exit", "Quitter"},
        {"Add.Policy.Entry", "Ajouter une r\u00E8gle"},
        {"Edit.Policy.Entry", "Modifier une r\u00E8gle"},
        {"Remove.Policy.Entry", "Enlever une r\u00E8gle"},
        {"Edit", "Modifier"},
        {"Retain", "Conserver"},

        {"Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes",
            "Avertissement : il se peut que le nom de fichier contienne des barres obliques inverses avec caract\u00E8re d'\u00E9chappement. Il n'est pas n\u00E9cessaire d'ajouter un caract\u00E8re d'\u00E9chappement aux barres obliques inverses. L'outil proc\u00E8de \u00E0 l'\u00E9chappement si n\u00E9cessaire lorsqu'il \u00E9crit le contenu des r\u00E8gles dans la zone de stockage persistant).\n\nCliquez sur Conserver pour garder le nom saisi ou sur Modifier pour le remplacer."},

        {"Add.Public.Key.Alias", "Ajouter un alias de cl\u00E9 publique"},
        {"Remove.Public.Key.Alias", "Enlever un alias de cl\u00E9 publique"},
        {"File", "Fichier"},
        {"KeyStore", "Fichier de cl\u00E9s"},
        {"Policy.File.", "Fichier de r\u00E8gles :"},
        {"Could.not.open.policy.file.policyFile.e.toString.",
                "Impossible d''ouvrir le fichier de r\u00E8gles\u00A0: {0}: {1}"},
        {"Policy.Tool", "Policy Tool"},
        {"Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.",
                "Des erreurs se sont produites \u00E0 l'ouverture de la configuration de r\u00E8gles. Pour plus d'informations, consultez le journal des avertissements."},
        {"Error", "Erreur"},
        {"OK", "OK"},
        {"Status", "Statut"},
        {"Warning", "Avertissement"},
        {"Permission.",
                "Droit :                                                       "},
        {"Principal.Type.", "Type de principal :"},
        {"Principal.Name.", "Nom de principal :"},
        {"Target.Name.",
                "Nom de cible :                                                    "},
        {"Actions.",
                "Actions :                                                             "},
        {"OK.to.overwrite.existing.file.filename.",
                "Remplacer le fichier existant {0} ?"},
        {"Cancel", "Annuler"},
        {"CodeBase.", "Base de code :"},
        {"SignedBy.", "Sign\u00E9 par :"},
        {"Add.Principal", "Ajouter un principal"},
        {"Edit.Principal", "Modifier un principal"},
        {"Remove.Principal", "Enlever un principal"},
        {"Principals.", "Principaux :"},
        {".Add.Permission", "  Ajouter un droit"},
        {".Edit.Permission", "  Modifier un droit"},
        {"Remove.Permission", "Enlever un droit"},
        {"Done", "Termin\u00E9"},
        {"KeyStore.URL.", "URL du fichier de cl\u00E9s :"},
        {"KeyStore.Type.", "Type du fichier de cl\u00E9s :"},
        {"KeyStore.Provider.", "Fournisseur du fichier de cl\u00E9s :"},
        {"KeyStore.Password.URL.", "URL du mot de passe du fichier de cl\u00E9s :"},
        {"Principals", "Principaux"},
        {".Edit.Principal.", "  Modifier un principal :"},
        {".Add.New.Principal.", "  Ajouter un principal :"},
        {"Permissions", "Droits"},
        {".Edit.Permission.", "  Modifier un droit :"},
        {".Add.New.Permission.", "  Ajouter un droit :"},
        {"Signed.By.", "Sign\u00E9 par :"},
        {"Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name",
            "Impossible de sp\u00E9cifier un principal avec une classe g\u00E9n\u00E9rique sans nom g\u00E9n\u00E9rique"},
        {"Cannot.Specify.Principal.without.a.Name",
            "Impossible de sp\u00E9cifier un principal sans nom"},
        {"Permission.and.Target.Name.must.have.a.value",
                "Le droit et le nom de cible doivent avoir une valeur"},
        {"Remove.this.Policy.Entry.", "Enlever cette r\u00E8gle ?"},
        {"Overwrite.File", "Remplacer le fichier"},
        {"Policy.successfully.written.to.filename",
                "R\u00E8gle \u00E9crite dans {0}"},
        {"null.filename", "nom de fichier NULL"},
        {"Save.changes.", "Enregistrer les modifications ?"},
        {"Yes", "Oui"},
        {"No", "Non"},
        {"Policy.Entry", "R\u00E8gle"},
        {"Save.Changes", "Enregistrer les modifications"},
        {"No.Policy.Entry.selected", "Aucune r\u00E8gle s\u00E9lectionn\u00E9e"},
        {"Unable.to.open.KeyStore.ex.toString.",
                "Impossible d''ouvrir le fichier de cl\u00E9s d''acc\u00E8s : {0}"},
        {"No.principal.selected", "Aucun principal s\u00E9lectionn\u00E9"},
        {"No.permission.selected", "Aucun droit s\u00E9lectionn\u00E9"},
        {"name", "nom"},
        {"configuration.type", "type de configuration"},
        {"environment.variable.name", "Nom de variable d'environnement"},
        {"library.name", "nom de biblioth\u00E8que"},
        {"package.name", "nom de package"},
        {"policy.type", "type de r\u00E8gle"},
        {"property.name", "nom de propri\u00E9t\u00E9"},
        {"Principal.List", "Liste de principaux"},
        {"Permission.List", "Liste de droits"},
        {"Code.Base", "Base de code"},
        {"KeyStore.U.R.L.", "URL du fichier de cl\u00E9s :"},
        {"KeyStore.Password.U.R.L.", "URL du mot de passe du fichier de cl\u00E9s :"},


        // javax.security.auth.PrivateCredentialPermission
        {"invalid.null.input.s.", "entr\u00E9es NULL non valides"},
        {"actions.can.only.be.read.", "les actions sont accessibles en lecture uniquement"},
        {"permission.name.name.syntax.invalid.",
                "syntaxe de nom de droit [{0}] non valide : "},
        {"Credential.Class.not.followed.by.a.Principal.Class.and.Name",
                "Classe Credential non suivie d'une classe et d'un nom de principal"},
        {"Principal.Class.not.followed.by.a.Principal.Name",
                "Classe de principal non suivie d'un nom de principal"},
        {"Principal.Name.must.be.surrounded.by.quotes",
                "Le nom de principal doit \u00EAtre indiqu\u00E9 entre guillemets"},
        {"Principal.Name.missing.end.quote",
                "Guillemet fermant manquant pour le nom de principal"},
        {"PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value",
                "La classe de principal PrivateCredentialPermission ne peut pas \u00EAtre une valeur g\u00E9n\u00E9rique (*) si le nom de principal n'est pas une valeur g\u00E9n\u00E9rique (*)"},
        {"CredOwner.Principal.Class.class.Principal.Name.name",
                "CredOwner :\n\tClasse de principal = {0}\n\tNom de principal = {1}"},

        // javax.security.auth.x500
        {"provided.null.name", "nom NULL fourni"},
        {"provided.null.keyword.map", "mappage de mots-cl\u00E9s NULL fourni"},
        {"provided.null.OID.map", "mappage OID NULL fourni"},

        // javax.security.auth.Subject
        {"invalid.null.AccessControlContext.provided",
                "AccessControlContext NULL fourni non valide"},
        {"invalid.null.action.provided", "action NULL fournie non valide"},
        {"invalid.null.Class.provided", "classe NULL fournie non valide"},
        {"Subject.", "Objet :\n"},
        {".Principal.", "\tPrincipal : "},
        {".Public.Credential.", "\tInformations d'identification et de connexion publiques : "},
        {".Private.Credentials.inaccessible.",
                "\tInformations d'identification et de connexion priv\u00E9es inaccessibles\n"},
        {".Private.Credential.", "\tInformations d'identification et de connexion priv\u00E9es : "},
        {".Private.Credential.inaccessible.",
                "\tInformations d'identification et de connexion priv\u00E9es inaccessibles\n"},
        {"Subject.is.read.only", "Sujet en lecture seule"},
        {"attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set",
                "tentative d'ajout d'un objet qui n'est pas une instance de java.security.Principal dans un ensemble de principaux du sujet"},
        {"attempting.to.add.an.object.which.is.not.an.instance.of.class",
                "tentative d''ajout d''un objet qui n''est pas une instance de {0}"},

        // javax.security.auth.login.AppConfigurationEntry
        {"LoginModuleControlFlag.", "LoginModuleControlFlag : "},

        // javax.security.auth.login.LoginContext
        {"Invalid.null.input.name", "Entr\u00E9e NULL non valide : nom"},
        {"No.LoginModules.configured.for.name",
         "Aucun LoginModule configur\u00E9 pour {0}"},
        {"invalid.null.Subject.provided", "sujet NULL fourni non valide"},
        {"invalid.null.CallbackHandler.provided",
                "CallbackHandler NULL fourni non valide"},
        {"null.subject.logout.called.before.login",
                "sujet NULL - Tentative de d\u00E9connexion avant la connexion"},
        {"unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor",
                "impossible d''instancier LoginModule {0} car il ne fournit pas de constructeur sans argument"},
        {"unable.to.instantiate.LoginModule",
                "impossible d'instancier LoginModule"},
        {"unable.to.instantiate.LoginModule.",
                "impossible d'instancier LoginModule\u00A0: "},
        {"unable.to.find.LoginModule.class.",
                "classe LoginModule introuvable : "},
        {"unable.to.access.LoginModule.",
                "impossible d'acc\u00E9der \u00E0 LoginModule : "},
        {"Login.Failure.all.modules.ignored",
                "Echec de connexion : tous les modules ont \u00E9t\u00E9 ignor\u00E9s"},

        // sun.security.provider.PolicyFile

        {"java.security.policy.error.parsing.policy.message",
                "java.security.policy : erreur d''analyse de {0} :\n\t{1}"},
        {"java.security.policy.error.adding.Permission.perm.message",
                "java.security.policy : erreur d''ajout de droit, {0} :\n\t{1}"},
        {"java.security.policy.error.adding.Entry.message",
                "java.security.policy : erreur d''ajout d''entr\u00E9e :\n\t{0}"},
        {"alias.name.not.provided.pe.name.", "nom d''alias non fourni ({0})"},
        {"unable.to.perform.substitution.on.alias.suffix",
                "impossible d''effectuer une substitution pour l''alias, {0}"},
        {"substitution.value.prefix.unsupported",
                "valeur de substitution, {0}, non prise en charge"},
        {"LPARAM", "("},
        {"RPARAM", ")"},
        {"type.can.t.be.null","le type ne peut \u00EAtre NULL"},

        // sun.security.provider.PolicyParser
        {"keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore",
                "Impossible de sp\u00E9cifier keystorePasswordURL sans indiquer aussi le fichier de cl\u00E9s"},
        {"expected.keystore.type", "type de fichier de cl\u00E9s attendu"},
        {"expected.keystore.provider", "fournisseur de fichier de cl\u00E9s attendu"},
        {"multiple.Codebase.expressions",
                "expressions Codebase multiples"},
        {"multiple.SignedBy.expressions","expressions SignedBy multiples"},
        {"SignedBy.has.empty.alias","SignedBy poss\u00E8de un alias vide"},
        {"can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name",
                "impossible de sp\u00E9cifier le principal avec une classe g\u00E9n\u00E9rique sans nom g\u00E9n\u00E9rique"},
        {"expected.codeBase.or.SignedBy.or.Principal",
                "codeBase, SignedBy ou Principal attendu"},
        {"expected.permission.entry", "entr\u00E9e de droit attendue"},
        {"number.", "nombre "},
        {"expected.expect.read.end.of.file.",
                "attendu [{0}], lu [fin de fichier]"},
        {"expected.read.end.of.file.",
                "attendu [;], lu [fin de fichier]"},
        {"line.number.msg", "ligne {0} : {1}"},
        {"line.number.expected.expect.found.actual.",
                "ligne {0} : attendu [{1}], trouv\u00E9 [{2}]"},
        {"null.principalClass.or.principalName",
                "principalClass ou principalName NULL"},

        // sun.security.pkcs11.SunPKCS11
        {"PKCS11.Token.providerName.Password.",
                "Mot de passe PKCS11 Token [{0}] : "},

        /* --- DEPRECATED --- */
        // javax.security.auth.Policy
        {"unable.to.instantiate.Subject.based.policy",
                "impossible d'instancier les r\u00E8gles bas\u00E9es sur le sujet"}
    };


    /**
     * Returns the contents of this <code>ResourceBundle</code>.
     *
     * <p>
     *
     * @return the contents of this <code>ResourceBundle</code>.
     */
    public Object[][] getContents() {
        return contents;
    }
}

