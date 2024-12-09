% Define facts about clients

% Consult the updatesTransactions.pl file
:- consult('updatesTransactions.pl').
:- consult('credit.pl').
:- consult('update_balance_plus_credit.pl').


% Predicate to save a transaction to a file
save_transaction(FilePath, Id, Amount, Data) :-
    open(FilePath, append, Stream),
    format(Stream, "bankTransaction(~w, ~w, '~w').~n", [Id, Amount, Data]),
    close(Stream).

% Dynamic predicate to store client information
:- dynamic client/5.

client(123, 'Alice', 'NYC-123', 'New York', '01-01-2020').
client(456, 'Bob', 'CHI-456', 'Chicago', '02-02-2020').
client(789, 'Charlie', 'LA-789', 'Los Angeles', '03-03-2020').
client(752, 'John', 'CHI-456', 'Chicago', '03-03-2020').

% Predicates to retrieve client information
get_clients(Number, Name, Agency, City, OpeningDate) :-
    client(Number, Name, Agency, City, OpeningDate).

% Predicates to retrieve city
% used downcase to make sure that finds the city with examples like :NEW YORK,new york,NEW york etc
clients_by_city(City, Number, Name) :-
    client(Number, Name, _, XCity, _),
    downcase_atom(City, LowerCity),
    downcase_atom(XCity, LowerXCity),
    LowerCity = LowerXCity.

% Dynamic predicate to store current balance plus credit
:- dynamic current_balance_plus_credit/2.







% Predicate to update the credit balance
update_credit_balance(Account, Credit) :-
  (balanceCredit(Account, _) ->
    retract(balanceCredit(Account, _)),
    assertz(balanceCredit(Account, Credit))
  ;
    assertz(balanceCredit(Account, Credit))
  ).

% Predicate to save the credit balance to a file
save_credit_balance(FilePath, Id, Amount) :-
    open(FilePath, append, Stream),
    write_canonical(Stream, balanceCredit(Id, Amount)),
    write(Stream, '.'),
    nl(Stream),
    close(Stream).

% Predicate to update the current balance plus credit
update_current_balance_plus_credit(Account, Credit) :-
    retract(current_balance_plus_credit(Account,_)),
    NewBalance is  + Credit,
    assertz(current_balance_plus_credit(Account, NewBalance)).

% Predicate to save the updated current balance plus credit to a file
save_update_current_balance_plus_credit(FilePath, Account, Credit) :-
    update_current_balance_plus_credit(Account, Credit),
    open(FilePath, write, Stream),
    forall(current_balance_plus_credit(AccountNumber, Balance),
           (writeq(Stream, current_balance_plus_credit(AccountNumber, Balance)),
            write(Stream, '.'),
            nl(Stream))),
    close(Stream).

% Predicate to get the credit balance for a client
get_credit_balance(Number, Value) :-
    (balanceCredit(Number, Balance_credit) -> Value = Balance_credit ; Value = 0).



% Predicate to get bank transactions for a client

get_Bank_Transactions(Number, Value, Date) :-
    bankTransaction(Number, Value, Date).

% Predicate to get the real balance for a client
get_real_balance(Number, Balance) :-
    current_balance_plus_credit(Number, Balance_plus_credit),
    (balanceCredit(Number, Balance_credit) -> Balance_credit_final = Balance_credit ; Balance_credit_final = 0),
    Balance is Balance_plus_credit - Balance_credit_final.

  eligible_clients(Number, Name, Agency, City, OpeningDate) :-
    client(Number, Name, Agency, City, OpeningDate),
    current_balance_plus_credit(Number, BalancePlusCredit),
    balanceCredit(Number, Credit),
    Credit = 0,
    BalancePlusCredit > 100.


    addTransaction(Account, Amount, Date) :-
    assertz(bankTransaction(Account, Amount, Date)).