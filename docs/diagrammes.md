# Diagrammes complets — CRM Desktop Commercial

---

## 1. MCD — Modèle Conceptuel de Données (Merise)

Le MCD représente les entités métier, leurs attributs et les associations de façon conceptuelle, selon la notation Merise.

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                        │
│   ┌─────────────────┐          ┌──────────────┐          ┌──────────────────────────┐ │
│   │   ENTERPRISE    │          │   possède    │          │       COMMERCIAL         │ │
│   │─────────────────│  1,1     │              │  1,N     │──────────────────────────│ │
│   │ # id            │─────────▶│              │─────────▶│ # id                     │ │
│   │   nom           │          └──────────────┘          │   nom                    │ │
│   │   adresse       │                                     │   prenom                 │ │
│   │   code_postal   │                                     │   mail                   │ │
│   │   ville         │                                     │   telephone              │ │
│   │   telephone     │                                     │   date_embauche          │ │
│   │   email         │                                     │   ca                     │ │
│   │   siret         │                                     │   objectif_ca            │ │
│   └─────────────────┘                                     └──────────────────────────┘ │
│                                                                                        │
│   ┌─────────────────────────────────┐          ┌──────────┐          ┌──────────────┐ │
│   │             CLIENT              │          │  passe   │          │    DEVIS     │ │
│   │─────────────────────────────────│  1,1     │          │  0,N     │──────────────│ │
│   │ # id                            │─────────▶│          │─────────▶│ # id         │ │
│   │   societe                       │          └──────────┘          │   reference  │ │
│   │   nom                           │                                │   date_creat.│ │
│   │   prenom                        │          ┌──────────┐          │   montant    │ │
│   │   email                         │          │    a     │  0,N     │   statut     │ │
│   │   telephone                     │  1,1     │          │─────────▶│   descriptn  │ │
│   │   adresse                       │─────────▶│          │          └──────────────┘ │
│   │   code_postal                   │          └──────────┘                           │
│   │   ville                         │                    │                            │
│   │   pays                          │                    │ 0,N                        │
│   │   date_inscription              │          ┌─────────────────────────────────┐   │
│   │   statut (*)                    │          │           COMMENTAIRE           │   │
│   │   date_derniere_modification    │          │─────────────────────────────────│   │
│   │   segment (**)                  │          │ # id                            │   │
│   │   source_acquisition (***)      │          │   commentaire                   │   │
│   └─────────────────────────────────┘          │   date_commentaire              │   │
│                                                │   date_derniere_modification    │   │
│                                                └─────────────────────────────────┘   │
│                                                                                        │
│  (*) StatutClient : CONTACTER | PROSPECT | DEVIS_ENVOYE | CLIENT                      │
│  (**) SegmentClient : AUTO_ENTREPRISE | TPE | PME | LIBERAL | GRAND_COMPTE | PARTICULIER│
│  (***) SourceAcquisitionClient : RESEAU | EMAIL | TEL | RECOMMANDATION | ANNONCE |    │
│                                  SITE_WEB | AUTRE                                     │
│  StatutDevis : ENVOYE | ACCEPTE | REFUSE                                              │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

**Tableau des cardinalités :**

| Association | Entité gauche | Cardinalité gauche | Cardinalité droite | Entité droite |
| ----------- | ------------- | ------------------ | ------------------ | ------------- |
| possède     | ENTERPRISE    | 1,1                | 1,N                | COMMERCIAL    |
| passe       | CLIENT        | 1,1                | 0,N                | DEVIS         |
| a           | CLIENT        | 1,1                | 0,N                | COMMENTAIRE   |

---

## 2. MLD — Modèle Logique de Données

Le MLD traduit le MCD en schéma relationnel avec clés primaires (PK) et clés étrangères (FK).

```
enterprise (
    id          : INTEGER  PK  AUTOINCREMENT,
    nom         : TEXT     NOT NULL,
    adresse     : TEXT     NOT NULL,
    code_postal : TEXT     NOT NULL,
    ville       : TEXT     NOT NULL,
    telephone   : TEXT     NOT NULL,
    email       : TEXT     NOT NULL,
    siret       : TEXT     NOT NULL
)

commercial (
    id            : INTEGER  PK  AUTOINCREMENT,
    id_enterprise : INTEGER  FK → enterprise(id)  NOT NULL  ON DELETE CASCADE,
    nom           : TEXT     NOT NULL,
    prenom        : TEXT     NOT NULL,
    mail          : TEXT     NOT NULL,
    telephone     : TEXT     NOT NULL,
    date_embauche : TEXT     NOT NULL,
    ca            : REAL     NOT NULL,
    objectif_ca   : REAL     NOT NULL
)

client (
    id                         : INTEGER  PK  AUTOINCREMENT,
    societe                    : TEXT     NOT NULL,
    nom                        : TEXT     NOT NULL,
    prenom                     : TEXT     NOT NULL,
    email                      : TEXT,
    telephone                  : TEXT,
    adresse                    : TEXT,
    code_postal                : TEXT,
    ville                      : TEXT,
    pays                       : TEXT,
    date_inscription           : TEXT,
    statut                     : TEXT     NOT NULL,
    date_derniere_modification : TEXT     NOT NULL,
    segment                    : TEXT,
    source_acquisition         : TEXT
)

devis (
    id            : INTEGER  PK  AUTOINCREMENT,
    id_client     : INTEGER  FK → client(id)  NOT NULL  ON DELETE CASCADE,
    reference     : TEXT     NOT NULL  UNIQUE,
    date_creation : TEXT     NOT NULL,
    montant       : REAL     NOT NULL,
    statut        : TEXT     NOT NULL,
    description   : TEXT
)

commentaire (
    id                         : INTEGER  PK  AUTOINCREMENT,
    id_client                  : INTEGER  FK → client(id)  NOT NULL  ON DELETE CASCADE,
    commentaire                : TEXT     NOT NULL,
    date_commentaire           : TEXT     NOT NULL,
    date_derniere_modification : TEXT
)
```

**Schéma des dépendances :**

```
enterprise ──(1,N)──▶ commercial
client     ──(0,N)──▶ devis         (ON DELETE CASCADE)
client     ──(0,N)──▶ commentaire   (ON DELETE CASCADE)
```

---

## 3. MPD — Modèle Physique de Données (SQLite)

Le MPD correspond aux scripts SQL réels utilisés dans `InitDB.java` pour créer la base SQLite.

```sql
-- Table enterprise
CREATE TABLE IF NOT EXISTS enterprise (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nom         TEXT    NOT NULL,
    adresse     TEXT    NOT NULL,
    code_postal TEXT    NOT NULL,
    ville       TEXT    NOT NULL,
    telephone   TEXT    NOT NULL,
    email       TEXT    NOT NULL,
    siret       TEXT    NOT NULL
);

-- Table commercial
CREATE TABLE IF NOT EXISTS commercial (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    id_enterprise INTEGER NOT NULL,
    nom           TEXT    NOT NULL,
    prenom        TEXT    NOT NULL,
    mail          TEXT    NOT NULL,
    telephone     TEXT    NOT NULL,
    date_embauche TEXT    NOT NULL,
    ca            REAL    NOT NULL,
    objectif_ca   REAL    NOT NULL,
    FOREIGN KEY (id_enterprise) REFERENCES enterprise(id) ON DELETE CASCADE
);

-- Table client
CREATE TABLE IF NOT EXISTS client (
    id                         INTEGER PRIMARY KEY AUTOINCREMENT,
    societe                    TEXT    NOT NULL,
    nom                        TEXT    NOT NULL,
    prenom                     TEXT    NOT NULL,
    email                      TEXT,
    telephone                  TEXT,
    adresse                    TEXT,
    code_postal                TEXT,
    ville                      TEXT,
    pays                       TEXT,
    date_inscription           TEXT,
    statut                     TEXT    NOT NULL,
    date_derniere_modification TEXT    NOT NULL,
    segment                    TEXT,
    source_acquisition         TEXT
);

-- Table devis
CREATE TABLE IF NOT EXISTS devis (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    id_client     INTEGER NOT NULL,
    reference     TEXT    NOT NULL UNIQUE,
    date_creation TEXT    NOT NULL,
    montant       REAL    NOT NULL,
    statut        TEXT    NOT NULL,
    description   TEXT,
    FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE CASCADE
);

-- Table commentaire
CREATE TABLE IF NOT EXISTS commentaire (
    id                         INTEGER PRIMARY KEY AUTOINCREMENT,
    id_client                  INTEGER NOT NULL,
    commentaire                TEXT    NOT NULL,
    date_commentaire           TEXT    NOT NULL,
    date_derniere_modification TEXT,
    FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE CASCADE
);
```

---

## 4. Diagramme de classes UML — Couche Modèle et données

Ce diagramme couvre toutes les entités métier, les enums, les interfaces et implémentations DAO.

```mermaid
classDiagram
    direction TB

    %% ─── ENTITÉS ───
    class Enterprise {
        +Integer id
        +String nom
        +String adresse
        +String codePostal
        +String ville
        +String telephone
        +String email
        +String siret
    }

    class Commercial {
        +Integer id
        +Integer idEntreprise
        +String nom
        +String prenom
        +String mail
        +String telephone
        +LocalDate dateEmbauche
        +Integer ca
        +Integer objectifCa
    }

    class Client {
        +Integer id
        +String societe
        +String nom
        +String prenom
        +String email
        +String telephone
        +String adresse
        +String codePostal
        +String ville
        +String pays
        +LocalDate dateInscription
        +StatutClient statut
        +LocalDate dateDerniereModification
        +SegmentClient segment
        +SourceAcquisitionClient sourceAcquisition
        +List~Commentaire~ commentaires
    }

    class Devis {
        +Integer id
        +Integer idClient
        +String reference
        +LocalDate dateCreation
        +Double montant
        +StatutDevis statut
        +String description
    }

    class Commentaire {
        +Integer id
        +Integer idClient
        +String commentaire
        +LocalDate date
        +LocalDate dateDerniereModification
    }

    %% ─── ENUMS ───
    class StatutClient {
        <<enumeration>>
        CONTACTER
        PROSPECT
        DEVIS_ENVOYE
        CLIENT
    }

    class StatutDevis {
        <<enumeration>>
        ENVOYE
        ACCEPTE
        REFUSE
    }

    class SegmentClient {
        <<enumeration>>
        AUTO_ENTREPRISE
        TPE
        PME
        LIBERAL
        GRAND_COMPTE
        PARTICULIER
    }

    class SourceAcquisitionClient {
        <<enumeration>>
        RESEAU
        EMAIL
        TEL
        RECOMMANDATION
        ANNONCE
        SITE_WEB
        AUTRE
    }

    %% ─── INTERFACES DAO ───
    class IEnterpriseDAO {
        <<interface>>
        +saveEnterprise(Enterprise) void
        +getEnterprise() Enterprise
        +updateEnterprise(Enterprise) int
    }

    class ICommercialDAO {
        <<interface>>
        +saveCommercial(Commercial) void
        +getCommercial() Commercial
        +updateCommercial(Commercial) int
    }

    class IClientDAO {
        <<interface>>
        +saveClient(Client) void
        +findAll() List~Client~
        +getClientById(Integer) Client
        +updateClient(Client) int
        +deleteClientById(Integer) int
        +getClientWithCommentsById(Integer) Client
    }

    class IDevisDao {
        <<interface>>
        +findAll() List~Devis~
        +saveDevis(Devis) void
        +getDevisByClientId(Integer) List~Devis~
        +deleteDevisById(Integer) int
        +updateDevis(Devis) int
    }

    class ICommentaireDAO {
        <<interface>>
        +saveCommentaire(Commentaire) void
        +getCommentairesByClientId(Integer) List~Commentaire~
        +deleteCommentaireById(Integer) int
        +updateCommentaire(Commentaire) int
    }

    %% ─── IMPLÉMENTATIONS DAO ───
    class EnterpriseDAO {
        +saveEnterprise(Enterprise) void
        +getEnterprise() Enterprise
        +updateEnterprise(Enterprise) int
    }

    class CommercialDAO {
        +saveCommercial(Commercial) void
        +getCommercial() Commercial
        +updateCommercial(Commercial) int
    }

    class ClientDAO {
        +saveClient(Client) void
        +findAll() List~Client~
        +getClientById(Integer) Client
        +updateClient(Client) int
        +deleteClientById(Integer) int
        +getClientWithCommentsById(Integer) Client
    }

    class DevisDAO {
        +findAll() List~Devis~
        +saveDevis(Devis) void
        +getDevisByClientId(Integer) List~Devis~
        +deleteDevisById(Integer) int
        +updateDevis(Devis) int
    }

    class CommentaireDAO {
        +saveCommentaire(Commentaire) void
        +getCommentairesByClientId(Integer) List~Commentaire~
        +deleteCommentaireById(Integer) int
        +updateCommentaire(Commentaire) int
    }

    %% ─── UTIL ───
    class DataBaseUtil {
        <<utility>>
        -String URL
        +getConnection() Connection
    }

    class InitDB {
        <<utility>>
        +createTable() void
    }

    %% ─── RELATIONS ───
    Enterprise "1" --> "1..*" Commercial : possède
    Client "1" --> "0..*" Devis : passe
    Client "1" --> "0..*" Commentaire : a

    Client --> StatutClient
    Client --> SegmentClient
    Client --> SourceAcquisitionClient
    Devis --> StatutDevis

    EnterpriseDAO ..|> IEnterpriseDAO
    CommercialDAO ..|> ICommercialDAO
    ClientDAO ..|> IClientDAO
    DevisDAO ..|> IDevisDao
    CommentaireDAO ..|> ICommentaireDAO

    EnterpriseDAO --> DataBaseUtil : utilise
    CommercialDAO --> DataBaseUtil : utilise
    ClientDAO --> DataBaseUtil : utilise
    DevisDAO --> DataBaseUtil : utilise
    CommentaireDAO --> DataBaseUtil : utilise
    InitDB --> DataBaseUtil : utilise

    EnterpriseDAO --> Enterprise : mappe
    CommercialDAO --> Commercial : mappe
    ClientDAO --> Client : mappe
    DevisDAO --> Devis : mappe
    CommentaireDAO --> Commentaire : mappe
```

---

## 5. Diagramme de classes UML — Couche Service et Validation

Ce diagramme couvre les services, les validateurs, les règles de validation et les exceptions.

```mermaid
classDiagram
    direction TB

    %% ─── INTERFACES SERVICE ───
    class IEnterpriseService {
        <<interface>>
        +saveEnterprise(Enterprise) void
        +getEnterprise() Enterprise
        +updateEnterprise(Enterprise) void
    }

    class ICommercialService {
        <<interface>>
        +saveCommercial(Commercial) void
        +getCommercial() Commercial
        +updateCommercial(Commercial) void
    }

    class IClientService {
        <<interface>>
        +saveClient(Client) void
        +findAll() List~Client~
        +getClientById(Integer) Client
        +deleteClientById(Integer) void
        +updateClient(Client) void
        +getClientWithCommentsById(Integer) Client
    }

    class IDevisService {
        <<interface>>
        +saveDevis(Devis) void
        +getDevisByClientId(Integer) List~Devis~
        +deleteDevisById(Integer) void
        +updateDevis(Devis) void
    }

    class ICommentaireService {
        <<interface>>
        +save(Commentaire) void
        +getCommentaireByClientId(Integer) List~Commentaire~
        +deleteCommentaireById(Integer) void
        +updateCommentaire(Commentaire) void
    }

    %% ─── IMPLÉMENTATIONS SERVICE ───
    class EnterpriseService {
        -IEnterpriseDAO enterpriseDAO
        +saveEnterprise(Enterprise) void
        +getEnterprise() Enterprise
        +updateEnterprise(Enterprise) void
    }

    class CommercialService {
        -ICommercialDAO commercialDAO
        +saveCommercial(Commercial) void
        +getCommercial() Commercial
        +updateCommercial(Commercial) void
    }

    class ClientService {
        -IClientDAO clientDAO
        +saveClient(Client) void
        +findAll() List~Client~
        +getClientById(Integer) Client
        +deleteClientById(Integer) void
        +updateClient(Client) void
        +getClientWithCommentsById(Integer) Client
    }

    class DevisService {
        -IDevisDAO devisDAO
        +saveDevis(Devis) void
        +getDevisByClientId(Integer) List~Devis~
        +deleteDevisById(Integer) void
        +updateDevis(Devis) void
    }

    class CommentaireService {
        -ICommentaireDAO commentaireDAO
        +save(Commentaire) void
        +getCommentaireByClientId(Integer) List~Commentaire~
        +deleteCommentaireById(Integer) void
        +updateCommentaire(Commentaire) void
    }

    %% ─── VALIDATION — RÈGLES ───
    class ValidationRule {
        <<interface>>
        +validate(String) String
    }

    class MandatoryRule {
        <<utility>>
        +mandatory(String) ValidationRule
    }

    class LengthRule {
        <<utility>>
        +length(int, int, String) ValidationRule
        +maxLength(int, String) ValidationRule
    }

    class RegexRule {
        <<utility>>
        +regex(String, String) ValidationRule
    }

    class AmountRule {
        <<utility>>
        +isValidAmount(String, String) ValidationRule
    }

    class SiretRule {
        <<utility>>
        +siret(String) ValidationRule
    }

    class LuhnValidator {
        <<utility>>
        +isSiretValidForLuhn(String) boolean
    }

    class ValidationPattern {
        <<utility>>
        EMAIL_PATTERN String
        PHONE_PATTERN String
        DATE_PATTERN String
        AMOUNT_PATTERN String
        POSTAL_CODE_PATTERN String
    }

    %% ─── VALIDATION — VALIDATEURS ───
    class ClientValidator {
        <<utility>>
        +validateClient(Client) void
    }

    class DevisValidator {
        <<utility>>
        +validateDevis(Devis) void
    }

    class CommercialValidator {
        <<utility>>
        +validateCommercial(Commercial) void
    }

    class EnterpriseValidator {
        <<utility>>
        +validateEnterprise(Enterprise) void
    }

    class Assertion {
        <<utility>>
        +assertNotNull(Object, String) void
        +assertIntegerPositiveAndNotNull(Integer, String) void
    }

    %% ─── EXCEPTIONS ───
    class ClientNotFoundException { <<exception>> }
    class ClientAssertException { <<exception>> }
    class DevisNotFoundException { <<exception>> }
    class DevisAssertException { <<exception>> }
    class CommercialNotFoundException { <<exception>> }
    class CommercialAssertException { <<exception>> }
    class EnterpriseNotFoundException { <<exception>> }
    class EnterpriseAssertException { <<exception>> }
    class CommentaireNotFoundException { <<exception>> }
    class BadRequestException { <<exception>> }
    class AppAssertException { <<exception>> }

    %% ─── RELATIONS SERVICE ───
    EnterpriseService ..|> IEnterpriseService
    CommercialService ..|> ICommercialService
    ClientService ..|> IClientService
    DevisService ..|> IDevisService
    CommentaireService ..|> ICommentaireService

    ClientService --> ClientValidator : valide avec
    DevisService --> DevisValidator : valide avec
    CommercialService --> CommercialValidator : valide avec
    EnterpriseService --> EnterpriseValidator : valide avec
    ClientService --> Assertion : vérifie
    DevisService --> Assertion : vérifie
    CommercialService --> Assertion : vérifie
    EnterpriseService --> Assertion : vérifie
    CommentaireService --> Assertion : vérifie

    ClientService --> ClientNotFoundException : lève
    ClientValidator --> ClientAssertException : lève
    DevisService --> DevisNotFoundException : lève
    DevisValidator --> DevisAssertException : lève
    CommercialService --> CommercialNotFoundException : lève
    CommercialValidator --> CommercialAssertException : lève
    EnterpriseService --> EnterpriseNotFoundException : lève
    EnterpriseValidator --> EnterpriseAssertException : lève
    CommentaireService --> CommentaireNotFoundException : lève

    %% ─── RELATIONS RÈGLES ───
    MandatoryRule ..|> ValidationRule
    LengthRule ..|> ValidationRule
    RegexRule ..|> ValidationRule
    AmountRule ..|> ValidationRule
    SiretRule ..|> ValidationRule
    SiretRule --> LuhnValidator : délègue à

    ClientValidator --> MandatoryRule : utilise
    ClientValidator --> LengthRule : utilise
    ClientValidator --> RegexRule : utilise
    ClientValidator --> ValidationPattern : utilise

    DevisValidator --> MandatoryRule : utilise
    DevisValidator --> LengthRule : utilise
    DevisValidator --> AmountRule : utilise
    DevisValidator --> RegexRule : utilise

    CommercialValidator --> MandatoryRule : utilise
    CommercialValidator --> LengthRule : utilise
    CommercialValidator --> RegexRule : utilise

    EnterpriseValidator --> MandatoryRule : utilise
    EnterpriseValidator --> LengthRule : utilise
    EnterpriseValidator --> RegexRule : utilise
    EnterpriseValidator --> SiretRule : utilise
```

---

## 6. Diagramme de classes UML — Couche Vue / Contrôleurs

Ce diagramme couvre tous les contrôleurs JavaFX, les helpers UI, et les classes de démarrage.

```mermaid
classDiagram
    direction TB

    %% ─── APPLICATION ───
    class MainApp {
        +start(Stage) void
        +main(String[]) void
    }

    class AppLauncher {
        <<utility>>
        +startApplication(Stage, Class) void
    }

    class AppContext {
        -Enterprise enterprise
        -Commercial commercial
        -ICommercialService commercialService
        -IEnterpriseService enterpriseService
        +getEnterprise() Enterprise
        +getCommercial() Commercial
        +setEnterprise(Enterprise) void
        +setCommercial(Commercial) void
    }

    %% ─── HELPERS UI ───
    class UiErrorHandler {
        <<utility>>
        +showGlobalError(Label, String) void
        +clearGlobalError(Label) void
        +showFieldError(Label, String) void
        +clearFieldError(Label) void
    }

    class UiFxUtils {
        <<utility>>
        +loadFxml(String, Class) Parent
        +getNdIfNull(Object) String
        +addEditableField(VBox, String, String, Consumer) void
    }

    class CommentaireCell {
        -ICommentaireService commentaireService
        +updateItem(Commentaire, boolean) void
    }

    %% ─── FORM VALIDATORS (UI) ───
    class ClientForm {
        <<utility>>
        +setupValidation(TextField, TextField, TextField, TextField, TextField, TextField, ComboBox, Label, Label, Label, Label, Label, Label) void
    }

    class DevisForm {
        <<utility>>
        +setupValidation(TextField, TextField, DatePicker, Label, Label, Label) void
    }

    class CommercialForm {
        <<utility>>
        +setupValidation(TextField, TextField, TextField, TextField, DatePicker, TextField, Label, Label, Label, Label, Label, Label) void
    }

    class EnterpriseForm {
        <<utility>>
        +setupValidation(TextField, TextField, TextField, TextField, TextField, TextField, TextField, Label, Label, Label, Label, Label, Label, Label) void
    }

    class UtilsForm {
        <<utility>>
        +applyValidation(TextField, ValidationRule, Label) void
    }

    %% ─── CONTRÔLEURS ───
    class WelcomeController {
        -AppContext appContext
        -IEnterpriseService enterpriseService
        +initialize() void
        -handleSubmit() void
        -setupValidation() void
    }

    class ControllerAjouterCommercial {
        -AppContext appContext
        -ICommercialService commercialService
        +initialize() void
        -handleValider() void
        -setupValidation() void
    }

    class ControllerListClient {
        -IClientService clientService
        -ICommentaireService commentaireService
        -AppContext appContext
        +initialize() void
        -handleAjouterClient() void
        -handleDeleteClient(Client) void
        -handleVoirDetail(Client) void
        -openParameters() void
    }

    class ControllerAjouterClient {
        -IClientService clientService
        -Stage stage
        +initialize() void
        -handleSave() void
        -setupValidation() void
    }

    class ControllerModifierClient {
        -IClientService clientService
        -Client client
        -Stage stage
        +initialize() void
        +initData(Client) void
        -handleSave() void
    }

    class ControllerDetailClient {
        -IClientService clientService
        -ICommentaireService commentaireService
        -IDevisService devisService
        -Integer idClient
        -Stage stage
        +initialize() void
        +setIdClient(Integer) void
        -handleAjouterCommentaire() void
        -handleVoirDevis() void
        -handleModifier() void
        -handleRetour() void
    }

    class ControllerAjouterDevis {
        -IDevisService devisService
        -Integer idClient
        -Stage stage
        -boolean saved
        +initData(Integer, IDevisService, Stage) void
        -handleEnregistrer() void
        -setupValidation() void
    }

    class ControllerModifierDevis {
        -IDevisService devisService
        -Devis devis
        -Stage stage
        -boolean saved
        +initData(Devis, IDevisService, Stage) void
        -handleModifier() void
    }

    class ControllerListeDevis {
        -IDevisService devisService
        -Integer idClient
        -String nomClient
        +initialize() void
        -handleAjouterDevis() void
        -handleModifierDevis(Devis) void
        -handleSupprimerDevis(Devis) void
        -refreshTable() void
    }

    class InfosCommercialController {
        -AppContext appContext
        -Commercial commercial
        -ICommercialService commercialService
        +initialize() void
        -handleSave() void
    }

    class InfosEntrepriseController {
        -AppContext appContext
        -IEnterpriseService enterpriseService
        +setAppContext(AppContext) void
        -handleSave() void
    }

    class ParameterController {
        -AppContext appContext
        +initialize() void
        -openInfosCommercial() void
        -openInfosEntreprise() void
    }

    %% ─── RELATIONS ───
    MainApp --> AppLauncher : délègue à
    AppLauncher --> AppContext : crée
    AppLauncher --> WelcomeController : peut charger
    AppLauncher --> ControllerAjouterCommercial : peut charger
    AppLauncher --> ControllerListClient : peut charger

    AppContext --> Enterprise : stocke
    AppContext --> Commercial : stocke

    ControllerListClient --> AppContext : lit
    ControllerListClient --> ControllerAjouterClient : ouvre
    ControllerListClient --> ControllerDetailClient : ouvre
    ControllerListClient --> ParameterController : ouvre

    ControllerDetailClient --> ControllerModifierClient : ouvre
    ControllerDetailClient --> ControllerListeDevis : ouvre
    ControllerDetailClient --> CommentaireCell : utilise

    ControllerListeDevis --> ControllerAjouterDevis : ouvre
    ControllerListeDevis --> ControllerModifierDevis : ouvre

    ParameterController --> InfosCommercialController : ouvre
    ParameterController --> InfosEntrepriseController : ouvre

    WelcomeController --> EnterpriseForm : utilise
    ControllerAjouterClient --> ClientForm : utilise
    ControllerModifierClient --> ClientForm : utilise
    ControllerAjouterDevis --> DevisForm : utilise
    ControllerModifierDevis --> DevisForm : utilise
    ControllerAjouterCommercial --> CommercialForm : utilise

    ClientForm --> UtilsForm : délègue à
    DevisForm --> UtilsForm : délègue à
    CommercialForm --> UtilsForm : délègue à
    EnterpriseForm --> UtilsForm : délègue à

    WelcomeController --> UiErrorHandler : affiche erreurs
    ControllerAjouterClient --> UiErrorHandler : affiche erreurs
    ControllerModifierClient --> UiErrorHandler : affiche erreurs
    ControllerAjouterDevis --> UiErrorHandler : affiche erreurs
    ControllerAjouterCommercial --> UiErrorHandler : affiche erreurs
    InfosCommercialController --> UiErrorHandler : affiche erreurs
    InfosEntrepriseController --> UiErrorHandler : affiche erreurs
```

---

## 7. Diagramme de séquence — Démarrage de l'application

```mermaid
sequenceDiagram
    actor Utilisateur
    participant JVM as JVM / Main
    participant MainApp
    participant AppLauncher
    participant InitDB
    participant AppContext
    participant EnterpriseService
    participant CommercialService

    Utilisateur->>JVM: mvn javafx:run
    JVM->>MainApp: main(args)
    MainApp->>MainApp: launch(args)
    MainApp->>AppLauncher: startApplication(stage, clazz)

    AppLauncher->>InitDB: createTable()
    InitDB-->>AppLauncher: OK (tables créées ou déjà existantes)

    AppLauncher->>AppLauncher: crée EnterpriseDAO, EnterpriseService
    AppLauncher->>AppLauncher: crée CommercialDAO, CommercialService
    AppLauncher->>AppContext: new AppContext(commercialService, enterpriseService)
    AppContext->>EnterpriseService: getEnterprise()
    AppContext->>CommercialService: getCommercial()

    alt Aucune entreprise enregistrée
        AppLauncher->>AppLauncher: charge welcome.fxml
        AppLauncher-->>Utilisateur: Affiche formulaire création entreprise
    else Entreprise OK mais aucun commercial
        AppLauncher->>AppLauncher: charge ajouter_commercial.fxml
        AppLauncher-->>Utilisateur: Affiche formulaire création commercial
    else Entreprise + Commercial existants
        AppLauncher->>AppLauncher: charge liste_clients.fxml
        AppLauncher-->>Utilisateur: Affiche le dashboard (liste clients)
    end
```

---

## 8. Diagramme de séquence — Ajout d'un client

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (ajouter_client)
    participant Controller as ControllerAjouterClient
    participant Form as ClientForm
    participant UtilsForm
    participant Service as ClientService
    participant Assertion
    participant Validator as ClientValidator
    participant DAO as ClientDAO
    participant DB as SQLite

    Commercial->>UI: Remplit le formulaire et clique "Enregistrer"
    UI->>Controller: handleSave()
    Controller->>Form: validation réactive (déjà active à la saisie)
    Form->>UtilsForm: applyValidation(champ, règle, label)

    alt Formulaire UI invalide (champ vide / mauvais format)
        UtilsForm-->>Form: message d'erreur
        Form-->>Controller: invalide
        Controller-->>UI: Labels d'erreur affichés
    else Formulaire UI valide
        Form-->>Controller: valide
        Controller->>Controller: Construit objet Client depuis les champs
        Controller->>Service: saveClient(client)
        Service->>Assertion: assertNotNull(client, message)
        Service->>Validator: validateClient(client)

        alt Validation métier échoue (règle MandatoryRule/LengthRule/RegexRule)
            Validator-->>Service: lève ClientAssertException
            Service-->>Controller: propage exception
            Controller->>UI: UiErrorHandler.showGlobalError(message)
        else Validation métier OK
            Validator-->>Service: OK
            Service->>Service: client.setDateInscription(LocalDate.now())
            Service->>Service: client.setDateDerniereModification(LocalDate.now())
            Service->>DAO: saveClient(client)
            DAO->>DB: INSERT INTO client (...)
            DB-->>DAO: OK
            DAO-->>Service: OK
            Service-->>Controller: OK
            Controller->>UI: Ferme la fenêtre modale
            Controller-->>Commercial: Retour liste clients (rafraîchie)
        end
    end
```

---

## 9. Diagramme de séquence — Liste clients et recherche

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (liste_clients)
    participant Controller as ControllerListClient
    participant Service as ClientService
    participant DAO as ClientDAO
    participant DB as SQLite

    Commercial->>UI: Ouvre l'application (dashboard)
    UI->>Controller: initialize()
    Controller->>Service: findAll()
    Service->>DAO: findAll()
    DAO->>DB: SELECT * FROM client ORDER BY ...
    DB-->>DAO: ResultSet
    DAO-->>Service: List~Client~
    Service-->>Controller: List~Client~
    Controller->>UI: Charge le TableView avec les clients

    Commercial->>UI: Saisit un terme dans la barre de recherche
    UI->>Controller: (listener sur le champ de recherche)
    Controller->>Controller: FilteredList filtre sur societe / nom / prenom / email
    Controller->>UI: TableView mis à jour dynamiquement

    Commercial->>UI: Clique sur l'icône "Voir détail" d'un client
    UI->>Controller: handleVoirDetail(client)
    Controller->>UI: Ouvre ControllerDetailClient (modale ou scène)

    Commercial->>UI: Clique sur l'icône "Supprimer" d'un client
    UI->>Controller: handleDeleteClient(client)
    Controller->>Service: deleteClientById(client.id)
    Service->>DAO: deleteClientById(id)
    DAO->>DB: DELETE FROM client WHERE id=? (cascade sur devis et commentaires)
    DB-->>DAO: OK
    DAO-->>Service: OK
    Service-->>Controller: OK
    Controller->>UI: Rafraîchit le TableView
```

---

## 10. Diagramme de séquence — Détail client et ajout commentaire

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (detail_client)
    participant Controller as ControllerDetailClient
    participant ClientService
    participant CommentaireService
    participant ClientDAO
    participant CommentaireDAO
    participant DB as SQLite

    Commercial->>UI: Accède au détail d'un client
    UI->>Controller: setIdClient(id) + initialize()
    Controller->>ClientService: getClientWithCommentsById(id)
    ClientService->>ClientDAO: getClientWithCommentsById(id)
    ClientDAO->>DB: SELECT client + commentaires via JOIN
    DB-->>ClientDAO: ResultSet
    ClientDAO-->>ClientService: Client (avec commentaires)
    ClientService-->>Controller: Client
    Controller->>UI: Affiche infos client + ListView commentaires

    Commercial->>UI: Saisit un commentaire dans la TextArea et clique "Ajouter"
    UI->>Controller: handleAjouterCommentaire()
    Controller->>Controller: Construit objet Commentaire (texte, date now, idClient)
    Controller->>CommentaireService: save(commentaire)
    CommentaireService->>CommentaireDAO: saveCommentaire(commentaire)
    CommentaireDAO->>DB: INSERT INTO commentaire (...)
    DB-->>CommentaireDAO: OK
    CommentaireDAO-->>CommentaireService: OK
    CommentaireService-->>Controller: OK
    Controller->>Controller: Recharge les commentaires
    Controller->>UI: ListView mise à jour

    Commercial->>UI: Clique "Voir les devis"
    UI->>Controller: handleVoirDevis()
    Controller->>UI: Ouvre ControllerListeDevis (id client)
```

---

## 11. Diagramme de séquence — Modification d'un client

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (modifier_client)
    participant Controller as ControllerModifierClient
    participant Form as ClientForm
    participant Service as ClientService
    participant Validator as ClientValidator
    participant DAO as ClientDAO
    participant DB as SQLite

    Commercial->>UI: Ouvre la fenêtre de modification
    UI->>Controller: initialize() + initData(client)
    Controller->>UI: Pré-remplit tous les champs avec les données du client

    Commercial->>UI: Modifie les champs et clique "Enregistrer"
    UI->>Controller: handleSave()
    Controller->>Form: validateForm()

    alt Formulaire UI invalide
        Form-->>Controller: invalide avec messages d'erreur
        Controller-->>UI: Affiche les labels d'erreur
    else Formulaire UI valide
        Form-->>Controller: valide
        Controller->>Controller: Met à jour l'objet Client
        Controller->>Service: updateClient(client)
        Service->>Assertion: assertNotNull(client, message)
        Service->>Validator: validateClient(client)

        alt Validation métier échoue
            Validator-->>Service: lève ClientAssertException
            Service-->>Controller: propage exception
            Controller->>UI: UiErrorHandler.showGlobalError(message)
        else Validation OK
            Validator-->>Service: OK
            Service->>Service: client.setDateDerniereModification(LocalDate.now())
            Service->>DAO: updateClient(client)
            DAO->>DB: UPDATE client SET ... WHERE id=?
            DB-->>DAO: rowsAffected
            DAO-->>Service: rowsAffected
            Service-->>Controller: OK
            Controller->>UI: Ferme la modale
            Controller-->>Commercial: Détail client rafraîchi
        end
    end
```

---

## 12. Diagramme de séquence — Ajout d'un devis

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (ajouter_devis)
    participant Controller as ControllerAjouterDevis
    participant Form as DevisForm
    participant Service as DevisService
    participant Validator as DevisValidator
    participant DAO as DevisDAO
    participant DB as SQLite

    Commercial->>UI: Clique "Ajouter un devis" depuis le détail client
    UI->>Controller: initData(idClient, devisService, stage)
    Controller->>UI: Initialise ComboBox statut (ENVOYE par défaut) + date today

    Commercial->>UI: Remplit référence, montant, date, statut, description
    UI->>Controller: handleEnregistrer()
    Controller->>Form: validateForm()

    alt Formulaire invalide (référence vide, montant non numérique, etc.)
        Form-->>Controller: invalide
        Controller-->>UI: Labels d'erreur affichés
    else Formulaire valide
        Form-->>Controller: valide
        Controller->>Controller: Construit objet Devis (idClient, référence, montant, statut, date, description)
        Controller->>Service: saveDevis(devis)
        Service->>Validator: validateDevis(devis)

        alt Validation métier échoue (référence trop longue, montant invalide, etc.)
            Validator-->>Service: lève DevisAssertException
            Service-->>Controller: propage exception
            Controller->>UI: UiErrorHandler.showGlobalError(message)
        else Validation OK
            Validator-->>Service: OK
            Service->>DAO: saveDevis(devis)
            DAO->>DB: INSERT INTO devis (id_client, reference, date_creation, montant, statut, description)
            DB-->>DAO: OK
            DAO-->>Service: OK
            Service-->>Controller: OK
            Controller->>Controller: saved = true
            Controller->>UI: Ferme la modale
            Controller-->>Commercial: TableView devis rafraîchi
        end
    end
```

---

## 13. Diagramme de séquence — Modification et suppression d'un devis

```mermaid
sequenceDiagram
    actor Commercial
    participant UI as Vue FXML (liste_devis)
    participant ListCtrl as ControllerListeDevis
    participant ModifCtrl as ControllerModifierDevis
    participant Form as DevisForm
    participant Service as DevisService
    participant Validator as DevisValidator
    participant DAO as DevisDAO
    participant DB as SQLite

    Commercial->>UI: Clique "Modifier" sur un devis
    UI->>ListCtrl: handleModifierDevis(devis)
    ListCtrl->>ModifCtrl: initData(devis, devisService, stage)
    ModifCtrl->>UI: Pré-remplit les champs avec les données du devis

    Commercial->>UI: Modifie les champs et clique "Modifier"
    UI->>ModifCtrl: handleModifier()
    ModifCtrl->>Form: validateForm()

    alt Formulaire invalide
        Form-->>ModifCtrl: invalide + erreurs
        ModifCtrl-->>UI: Labels d'erreur
    else Formulaire valide
        Form-->>ModifCtrl: valide
        ModifCtrl->>ModifCtrl: Met à jour l'objet Devis
        ModifCtrl->>Service: updateDevis(devis)
        Service->>Validator: validateDevis(devis)
        alt Validation échoue
            Validator-->>Service: lève DevisAssertException
            Service-->>ModifCtrl: exception
            ModifCtrl->>UI: UiErrorHandler.showGlobalError(message)
        else Validation OK
            Validator-->>Service: OK
            Service->>DAO: updateDevis(devis)
            DAO->>DB: UPDATE devis SET ... WHERE id=?
            DB-->>DAO: rowsAffected
            DAO-->>Service: OK
            Service-->>ModifCtrl: OK
            ModifCtrl->>ModifCtrl: saved = true
            ModifCtrl->>UI: Ferme la modale
            ListCtrl->>ListCtrl: refreshTable()
        end
    end

    Commercial->>UI: Clique "Supprimer" sur un devis
    UI->>ListCtrl: handleSupprimerDevis(devis)
    ListCtrl->>Service: deleteDevisById(devis.id)
    Service->>DAO: deleteDevisById(id)
    DAO->>DB: DELETE FROM devis WHERE id=?
    DB-->>DAO: rowsAffected
    DAO-->>Service: OK
    Service-->>ListCtrl: OK
    ListCtrl->>ListCtrl: refreshTable()
    ListCtrl->>UI: TableView mis à jour
```

---

## 14. Diagramme de séquence — Saisie informations entreprise (premier lancement)

```mermaid
sequenceDiagram
    actor Utilisateur
    participant UI as Vue FXML (welcome)
    participant Controller as WelcomeController
    participant Form as EnterpriseForm
    participant Service as EnterpriseService
    participant Validator as EnterpriseValidator
    participant SiretRule
    participant LuhnValidator
    participant DAO as EnterpriseDAO
    participant DB as SQLite
    participant AppLauncher

    Utilisateur->>UI: Remplit le formulaire entreprise et clique "Valider"
    UI->>Controller: handleSubmit()
    Controller->>Form: validateForm()

    alt Formulaire UI invalide
        Form-->>Controller: invalide
        Controller-->>UI: Labels d'erreur affichés
    else Formulaire UI valide
        Form-->>Controller: valide
        Controller->>Controller: Construit objet Enterprise
        Controller->>Service: saveEnterprise(enterprise)
        Service->>Validator: validateEnterprise(enterprise)
        Validator->>SiretRule: siret(value)
        SiretRule->>LuhnValidator: isSiretValidForLuhn(siret)

        alt SIRET invalide (format ou Luhn)
            LuhnValidator-->>SiretRule: false
            SiretRule-->>Validator: message d'erreur
            Validator-->>Service: lève EnterpriseAssertException
            Service-->>Controller: propage exception
            Controller->>UI: UiErrorHandler.showGlobalError(message)
        else Validation OK
            Validator-->>Service: OK
            Service->>DAO: saveEnterprise(enterprise)
            DAO->>DB: INSERT INTO enterprise (...)
            DB-->>DAO: OK
            DAO-->>Service: OK
            Service-->>Controller: OK
            Controller->>AppLauncher: startApplication() → charge ajouter_commercial.fxml
        end
    end
```
