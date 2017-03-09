/***********HW2**********/
/* Ilayda Zeynep Ozdemir*/
/*      131044022       */
/************************/

flight(istanbul, trabzon).
flight(trabzon, istanbul).


flight(istanbul, kars).
flight(kars, istanbul).


flight(istanbul, ankara).
flight(ankara, istanbul).


flight(istanbul, gaziantep).
flight(gaziantep, istanbul).


flight(istanbul, konya).
flight(konya, istanbul).


flight(istanbul, antalya).
flight(antalya, istanbul).


flight(istanbul, izmir).
flight(izmir, istanbul).


flight(izmir, ankara).
flight(ankara, izmir).


flight(ankara, konya).
flight(konya, ankara).

flight(ankara, trabzon).
flight(trabzon, ankara).


flight(ankara, kars).
flight(kars, ankara).


flight(edirne, edremit).
flight(edremit, edirne).


flight(edremit, erzincan).
flight(erzincan, edremit).

distance(istanbul, trabzon, 902).
distance(istanbul, kars, 1189).
distance(istanbul, ankara, 350).
distance(istanbul, gaziantep, 848).
distance(istanbul, konya, 461).
distance(istanbul, antalya, 482).
distance(istanbul, izmir, 328).
distance(ankara, izmir, 521).
distance(ankara, konya, 231).
distance(ankara, trabzon, 593).
distance(ankara, kars, 872).
distance(edirne, edremit, 225).
distance(edremit, erzincan, 1044).

when(102,10).
when(108,12).
when(341,14).
when(455,16).
when(452,17).

where(102,z23).
where(108,z11).
where(341,z06).
where(455,207).
where(452,207).

enroll(a,102).
enroll(a,108).
enroll(b,102).
enroll(c,108).
enroll(d,341).
enroll(e,455).



/************************** PART1 ***********************/
/*“route(X,Y) a	route	between	X	and	Y	exists”	that	checks	if	there	is	a	route	between	any	given	two	cities.*/

route_help(X, Y, VISITED) :- flight(X, Y), not(member(Y, VISITED)). /*Y ziyaret edilmediyse ve direk ucus varsa*/
route_help(X, Y, VISITED) :- flight(X, Z), not(member(Z,VISITED)), 
							 route_help(Z, Y, [Z|VISITED]), not(flight(X, Y)). /*ara yer ziyaret edilmediyse Z Y icin recursive*/

route(X, Y) :- route_help(X, Y, [X]). 
/*kendisinden kendine ucus aramasin diye Xi arraye koyup cagirdim*/



/********************************************************/
/************************** PART2 ***********************/


/*sroute_help(X, Y,VISITED, RES) :- flight(X, Y),not(member(Y,VISITED)),distance_ask(X,Y,RES).*/
/*sroute_help(X, Y,VISITED, RES) :- not(flight(X, Y)),route(X,Y),flight(X,Z),not(member(Z,VISITED)), distance_ask(X,Z,RES1),*/
			 						/*sroute_help(Z, Y,[Z|VISITED], RES2), */
			 						/*RES is RES1 + RES2.*/



/**/

:-dynamic(solution/2).

distance_ask(X, Y, RES) :- distance(X, Y, RES).
distance_ask(X, Y, RES) :- distance(Y, X, RES).
/*distance_select(X, CHOOSE_Y , RES) :- distance_ask(X, A, RES1),*/
									  /*distance_ask(X, B, RES2), */
/*									  not(A == B), RES1 < RES2 , CHOOSE_Y is A , RES is RES1.*/
sroute_help(X,Y,VISITED,RES) :- route(X,Y), not(member(Y,VISITED)), distance_ask(X,Y,RES).
sroute_help(X,Y,VISITED,RES) :- route(X,Z),route(Z,Y), not(member(Z,VISITED)) , distance_ask(X,Z,RES1),
								(Y = Z,RES2 = 0   ; sroute_help(Z,Y,[X|VISITED],RES2)  ), RES is RES1 + RES2. 


deleteBigElement() :- solution(A) ,solution(B) , not(A == B), A > B, retract(solution(A)). 
/*en kucuk kalana kadar her adimda karsilastirir ve buyuk olani siler*/
/*buyukse solution icinden cikarilir sona en kucuk olan kalir*/

fill(X, Y) :- sroute_help(X, Y, [], R),assert(solution(R)),deleteBigElement(),fail.

smaller(X, Y, X) :-(X =< Y).
smaller(X, Y, Y) :-(Y < X).

/*indMin(F,S,M) :- (F == atom_length(solution)) ; (S == atom_length(solution)) .*/
findMin(M) :- solution(F) , solution(S) , smaller(F,S,M).

sroute(X, Y, _) :- fill(X,Y). /*RES bosken fill calisir*/
sroute(_,_, RES) :- findMin(RES),retract(solution(_)). /*sonra atom ici dolar ve findMin calisir, sonra atom ici bosaltilir*/
/*bosluklara harf verilince warning veriliyor, singleton!*/



/********************************************************/
/************************** PART3 ***********************/
/*“schedule(S,P,T)”	associates	a	student	to	a	place	and	time	of	class.*/
schedule(S, P, T) :- enroll(S, C),where(C,P), when(C, T).

/*“usage(P,T)”	that	gives	the	usage	times	of	a	classroom*/
usage(P, T) :- where(X,P), when(X,T). 

/*“conflict(X,Y)”	that gives true if	X and Y conflicts due to classroom or time.	*/
conflict(X, Y) :- not(X==Y),where(X,A), where(Y,B) , A == B.
conflict(X, Y) :- not(X==Y),when(X,A), when(Y,B) , A == B.

/*meet(X,Y)”	that	gives	true	if	student	X and student Y are present in the same classroom	at	the	same time.	*/
meet(X,Y) :- not(X==Y), enroll(X,A), enroll(Y, B), A == B. 

/********************************************************/
/************************** PART4 ***********************/

/**************** add ********************/
/* List icindeki tum elemanlar toplanir*/
/* S : add all element of L  */

add([], 0).
add([X|L], S) :- add(L, Temp_sum),S is X+Temp_sum.
/**************** unique ********************/
/* L1 icindeki tekrar eden elemanlari siler */
ismember(X,[H|_]) :- X==H.
ismember(X,[_|T]) :- ismember(X,T).

unique([],[]). /* bossa dursun diye*/
unique([X|L1],L2) :- ismember(X,L1), !, unique(L1,L2).  /*ilk eleman L1 icinde varsa L1 icin unique cagirilir*/
unique([X|L1],[X|L2]) :- unique(L1,L2). /*L1,L2 nin ilk elemanlari ayniysa L1 icin unique cagirilir*/

/**************** flatten ***************/
/*[[1,2],[1,2,3]] = [1,2,1,2,3]*/
/**/

flatten([], []) :- !. /*bossa cut*/
flatten([L|Ls], RES) :- !,flatten(L, L1), flatten(Ls, L2), append(L1, L2, RES). /*L Ls ayri ayri yollanir sonuclari birlestirilir*/
flatten(L, [L]). /*L eklenir*/

/********************************************************/