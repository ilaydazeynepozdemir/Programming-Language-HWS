; *********************************************
; *  341  Programming Languages               *
; *  Fall 2016                                *
; *  Author: Liu Liu                          *
; *          Ulrich Kremer                    *
; *          Furkan Tektas , clisp            *
; *			 Ilayda Zeynep Ozdemir
; *********************************************

;; ENVIRONMENT
;; "c2i, "i2c",and "apply-list"
(load "include.cl")

;; test document
(load "document.cl")

;; test-dictionary
;; this is needed for spell checking
(load "test-dictionary.cl")

;; (load "dictionary.cl") ;;  real dictionary (45K words)


;; -----------------------------------------------------
;; HELPERS
;; *** PLACE YOUR HELPER FUNCTIONS BELOW ***
;(format t "Hello ~a ~%" (car *document*))
;
;(let (*temp*) '(k)) 

;//////////////////////////////////////STRUCT//////////////////////////////////////////////

(defstruct (Controller(:conc-name cnt-)) cipherList IndexOfUsed move_paragraph move_dictionary CurrentIndexInDict forDelete  backSize) 
;CurrentIndexInDict : tum kelimelerin (gelen paragraftaki indexine) sozlukte guncel olduklari indexleri yazilir

;//////////////////////////////////////////////////////////////////////////////////////////


 ;(setf (dr-width my-door) 43.7) 
 ;(dr-width my-door) 

;(defstruct (myWord(:conc-name dr-)) indexOfDictionary Word )

;(let ((thisWord ( make-myWord :indexOfDictionary 2 : Word 'deneme))))
	;(setf thisWord )


;)

;;(nth n list)

;; ic ice liste donduruyor aldiginda get yap
(defun setElement(item all_list index )
	(append (subseq all_list 0 index) (append  (list item) (last all_list (- (length all_list) (+ index 1)))));index sonrasi ile verilen item birlestirilir
	;;sonra liste basi ile son kisimlar birlestirilir

	
)

(defun remove-nth (n lst)
	(if (eq n 0)
		(setf lst (subseq lst 1 (length lst)))
		(setf lst (append (subseq lst 0 n) (last lst  (- (- (length lst) n) 1) )))

	)

	
	(return-from remove-nth lst)
		;(format t " silinmis hali" lst)

)






;; Recursive solution, sharing the tail:

;;(defun insert-at (item list index)
;  (cond
 ;   ((< index 0) (error "Index too small ~A" index))
  ;  ((= index 0) (cons item list))
  ;  ((endp list) (error "Index too big"))
   ; (t (cons (first list) (insert-at item (rest list) (1- index))))))
;; -----------------------------------------------------
;; ENCODING
;; -----------------------------------------------------
(defun change(letter)
	(cond
		((eq letter 'a) 'd)
		((eq letter 'b) 'e)
		((eq letter 'c) 'f)
		((eq letter 'd) 'p)
		((eq letter 'e) 'q)
		((eq letter 'f) 'a)
		((eq letter 'g) 'b)
		((eq letter 'h) 'k)
		((eq letter 'i) 'l)
		((eq letter 'j) 'c)
		((eq letter 'k) 'r)
		((eq letter 'l) 's)
		((eq letter 'm) 't)
		((eq letter 'n) 'g)
		((eq letter 'o) 'y)
		((eq letter 'p) 'z)
		((eq letter 'q) 'h)
		((eq letter 'r) 'i)
		((eq letter 's) 'j)
		((eq letter 't) 'm)
		((eq letter 'u) 'n)
		((eq letter 'v) 'o)
		((eq letter 'w) 'u)
		((eq letter 'x) 'v)
		((eq letter 'y) 'w)
		((eq letter 'z) 'x)
		;((eq letter ' ) ' )
	)	
)
(defun changeWord(word)
	(let  (newWord) 
	(loop for y in word do 
			(push (change y) newWord )
		)
		(reverse (nth 0 (list newWord) ));return etmeyi ogrenince burayi duzelt
	)
)

(defun encodingParagraph(parag )
	(let (returnList) 
		
		(loop for x in parag do	
			(push (changeWord x) returnList )
		)
		(reverse (car (reverse(list returnList)) ));return etmeyi ogrenince burayi duzelt
	)
)

(defun encodingDocument(document )
	(let (returnList) 
		(loop for item in document do
			(push (encodingParagraph item) returnList)
		)
		(reverse (car (reverse(list returnList)) ));return etmeyi ogrenince burayi duzelt
	)
)
;//////////////////////////////////////////////////////////////////////////////////////////////////////////

(defun IsUsed(controller_ index)
	(loop for item in (cnt-IndexOfUsed controller_) do;indexOfUses icinde sozlukteki kullanilmis kelimelerin yerini tutar
		(if (eq item index)
			(progn 
				;(format t "kullanildi")
			(return-from IsUsed 1));kullanildi
			(return-from IsUsed 0);kullanilmadi
		)
	)


)

(defun clearCipher(word dict lst)
	(format t "sil:   word ~a   dict ~a 
		"word dict)
	
		(loop for d in dict do 
	(loop for i from 0 to (length lst) do 
		(let ((counter 0))
			(if (eq (c2i d) i )
				(if (eq (nth i lst) nil) 
					()
					(progn 
					(setf lst (setElement nil lst (c2i d)))
					)

				)

			)
			(setf counter (+ counter 1))
		)
		)
	)
	
	
	(return-from clearCipher lst)

)

;dictWord index olarak kullaniliyor mesela abcd wordumuz olsun karsiliginda da them olsun
;(c2i t)->a yapiyorum
;currentIndex sozluk icindeki index
(defun wordVsDictWord (word  dictWord toControl)
(format t "
oooooooooooooooooooooooooooooo")*
	(format t " 
word= ~a  
dictWord= ~a " word dictWord )
(format t "
~a"(cnt-cipherList toControl))
(format t "
oooooooooooooooooooooooooooooo
")
	

	(loop for i from 0 to (length word) do ;kelimeye bakar

		(let ((spell (nth i word)))
		(let ((dictSpell (nth i dictWord)))
			(if (eq spell nil) 
				()
				(if (eq dictSpell nil)
					()
					(if (eq (nth (c2i dictSpell) (cnt-cipherList toControl) ) nil);nil ise yerine harfi koy
						(progn ;nilken doldurunca
							(push (c2i dictSpell) (cnt-forDelete toControl))
							(setf (cnt-backSize toControl) (+ (cnt-backSize toControl) 1) )
							(format t "
Eklenen Harf Indexleri ~a " (cnt-forDelete toControl))
							(setf (cnt-cipherList toControl) (setElement spell (cnt-cipherList toControl) (c2i dictSpell) )) ;yeri bostu
						)
						(if (eq (nth (c2i dictSpell)  (cnt-cipherList toControl) ) spell );listede harfin karsiligi harfe esit yani onceden konulmus;nil degil
							();karsiligi ayni harfe esitse dogru yol bir sey yapma
							(progn ;yeri baska bir harfle dolu demek ki moveDictionary 1 olmali baska kelimeye gecmeli
							;(format t "sifre listinde cakisma var")
						;(setf (cnt-cipherList toControl) (clearCipher word dictWord (cnt-cipherList toControl))) ;dogru olanlari da siliyorum
						;(setf (cnt-cipherList toControl) (setElement nil (cnt-cipherList toControl) (c2i dictSpell)))
								(setf  (cnt-move_dictionary toControl) 1); suan geri donme sozlukte gez
								;(setf (cnt-cipherList toControl) (cnt-cipherList toControl))							
								(return-from wordVsDictWord toControl)
					 		)
						)
					)
				)

			)
		
		))

				;sifre liste update
		;(setf (cnt-cipherList toControl) cipherlistTemp)
		(setf  (cnt-move_dictionary toControl) 0) ;sozlugu durdur simdilik
		(setf (cnt-move_paragraph toControl) 1);paragraf devam 	



	



	)

	


	(return-from wordVsDictWord toControl)


	

)




;sadece geri dondugu zaman kaldigi yerden devam etmeli 

(defun dictionaryFunction(word wordIndex toControl index);basta index 0 -hep
	;i(format t "- ~a -"index);index kaldigi yerden devam ediyor (gerekli durumlarda)
	(if (eq index (length *dictionary*))
		(progn 
		(setf (cnt-move_paragraph toControl) 0);geri don diyor
		;(setf (cnt-CurrentIndexInDict toControl) (setElement index (cnt-CurrentIndexInDict toControl) wordIndex ))
		(return-from dictionaryFunction toControl)
		)
	)
	(if (eq (length word) (length (nth index *dictionary* )));eger bu indexteki kelime uzunlugu sozcukle esitse
		(progn  ;esit
			(if (eq (IsUsed toControl index) 1)
				(progn ;kullanilmis
					;(setf (cnt-CurrentIndexInDict toControl) (setElement index (cnt-CurrentIndexInDict toControl) wordIndex ))
					(dictionaryFunction word wordIndex toControl (+ index 1)) ;kullanilmis
					(return-from dictionaryFunction toControl)
				)
				(progn ; kullanilmamis
						;forDelete silinme ihtimali olan indexleri tutar
					(setf (cnt-backSize toControl) 0);backsize 0 dan baslar kelimeye gore degeri degisir
					(setf toControl (wordVsDictWord word (nth index *dictionary*) toControl));wordVsDict cagirilir
					(if (eq (cnt-move_dictionary toControl) 1);sozluk devam?
						(progn  ;sozluge devam et ;kelimeyi sil!!!
							(loop for a from 0 to (- (cnt-backSize toControl) 1) do
								(let ((del (pop (cnt-forDelete toControl)) )) ;backsize kadar pop edip siler
									(if (eq del nil)
										()
										(progn 
											
											(setf (cnt-cipherList toControl) (setElement nil (cnt-cipherList toControl) del ))
											
										)
									)
								
								)
							)

							(format t "
Uygun olmayanlar cikarildi 
~a" (cnt-cipherList toControl))
					
							;(setf cipherlistTemp (clearCipher word (nth index *dictionary*) (cnt-cipherList toControl))) ;dogru olanlari da sildigi icin bu elendi
							(setf (cnt-CurrentIndexInDict toControl) (setElement index (cnt-CurrentIndexInDict toControl) wordIndex ));guncel index update
							(dictionaryFunction word wordIndex toControl (+ index 1)) ;bir sonraki indxle cagirilir
							(return-from dictionaryFunction toControl )
						)
						(progn;sozluk durdur
							(push index (cnt-IndexOfUsed toControl));kullanilanlarin indexi eklendi
							(setf (cnt-CurrentIndexInDict toControl) (setElement index (cnt-CurrentIndexInDict toControl) wordIndex ))
							(return-from dictionaryFunction toControl );sozlugu durdurdu
						)
					)

				)

			)	
			
		)
		(progn  ;uzunluk esit degil
			(dictionaryFunction word wordIndex toControl (+ index 1) ) 
			(return-from dictionaryFunction toControl )
		);sozlukteki kelime uzunlugu kelimemize esit degil devam
	)
	
)

(defun wordFunction(word wordIndex toControl)
	(let ((current (nth wordIndex (cnt-CurrentIndexInDict toControl))))
		
		(if (eq current nil)
			(setf current -1)
		)
		(setf toControl (dictionaryFunction word wordIndex toControl (+ current 1) ));currentindex kelimenin sozlukteki yeri  


		(return-from wordFunction toControl);simdilik bir sey yapmadan dondurdu BURAYA DON

	)
	


)



;bakilma listesini degistir




(defun GenDecodeA_Recursive (part paragraph thisParagraphCont counter )
	

	(if (eq part nil)
		(progn 

			(return-from GenDecodeA_Recursive thisParagraphCont)
		)
		(if (eq (cnt-move_paragraph (wordFunction part counter thisParagraphCont)) 1);donen controller'in moveParagraph'i 1 ise 
			(progn ;paragrafta ilerlemeye devam ederiz

			 	(setf thisParagraphCont (GenDecodeA_Recursive (nth (+ counter 1) paragraph) paragraph thisParagraphCont (+ counter 1)) )
			 	(return-from GenDecodeA_Recursive thisParagraphCont)

			)
			(progn ;moveParagraf 0 oldugunda geri donulmesi gerekir
			
			(format t " 
~a	'dan/den geri donmek isteniyor -> ~a  " part (nth (- counter 1) paragraph))
				(format t "~a " (cnt-cipherList thisParagraphCont) )
				(loop for a from 0 to (- (length (nth (- counter 1) paragraph) ) 1) do
					(let ((del (pop (cnt-forDelete thisParagraphCont)) )) ;backsize kadar pop edip siler
						(if (eq del nil)
							()
							(progn 
							(setf (cnt-cipherList thisParagraphCont) (setElement nil (cnt-cipherList thisParagraphCont) del ))
											
							)
						)
								
					)
				)

				(loop for y from 0 to (length (cnt-IndexOfUsed thisParagraphCont)) do 
					(if (eq (nth counter (cnt-CurrentIndexInDict thisParagraphCont)) nil)
						()
						(if (eq (nth y (cnt-IndexOfUsed thisParagraphCont)) (nth counter (cnt-CurrentIndexInDict thisParagraphCont)))
							(progn 
								(setf (cnt-IndexOfUsed thisParagraphCont) (remove-nth y (cnt-IndexOfUsed thisParagraphCont) )))
						)
					)
				)

		
				(setf (cnt-move_paragraph thisParagraphCont) 1)

				(setf thisParagraphCont (GenDecodeA_Recursive (nth (- counter 1) paragraph) paragraph thisParagraphCont (- counter 1)))
				
				(return-from GenDecodeA_Recursive thisParagraphCont)
			)

		)

	)

)


;; -----------------------------------------------------
;; -----------------------------------------------------
;; END OF HELPER FUNCTIONS
;; -----------------------------------------------------
;; -----------------------------------------------------


;; -----------------------------------------------------
;; DECODE FUNCTIONS
;; -----------------------------------------------------
;;cipherlist olusturulacak ve return edilecek
(defun Gen-Decoder-A (paragraph)
	
	(let ((thisParagraphCont (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0) :CurrentIndexInDict (make-list (length paragraph)) :forDelete (make-list 0))))
	
		(GenDecodeA_Recursive (car paragraph) paragraph thisParagraphCont 0)

	
	)	
)








;(setq *documentEncoded* (encodingDocument *document*));
;(format t "
;	encoded  -> ~a " *documentEncoded*)
;(format t "
;	Original decoded  -> ~a " *document*)
;(MyCode-Breaker *documentEncoded*);

(defun frequencyAnalysis (paragraph listAlphabet)
	(loop for w in paragraph do 
		(loop for s in w do ;s spell w word
			
			(setf listAlphabet (setElement (+ (nth (c2i s) listAlphabet) 1) listAlphabet (c2i s)))
		)

	)

	(return-from frequencyAnalysis listAlphabet)


)



(defun Gen-Decoder-B-0 (paragraph)
 (let ((thisParagraphCont (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0) :CurrentIndexInDict (make-list (length paragraph)) :forDelete (make-list 0))))
 	(let ((listAlphabet (make-list 0))) 
 	(loop for i from 0 to 26 do 
		(push 0 listAlphabet) ;listAlphabet sifre harflerini temsil eder
	)
 		(setf listAlphabet (frequencyAnalysis paragraph listAlphabet))
;		(format t " 
;			frekans analizi
;~a 
;		"listAlphabet)

	)
	 (return-from Gen-Decoder-B-0 thisParagraphCont)
 )

)

(defun Code-Breaker (document decoder-function)
	
	(let ((allDocument (make-list 0)))

		(let ((listAlphabet (make-list 0))) 
		(loop for parag in document do
			(loop for word in parag do 
				(push word allDocument) 

				
			)
		)

		(let ((mainController (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0):CurrentIndexInDict (make-list (length allDocument)):forDelete (make-list 0) )) )

			(setf mainController (Gen-Decoder-A allDocument));;
			

		)	
		)
	)
)


;(Code-Breaker *documentEncoded* #'Gen-Decoder-A)


;(let ((liste '(1 2 3 4)))

;	(setf liste (remove-nth 1 liste))
;	(format t "liste ~a "liste)
;)


 ;(Code-Breaker *document*  (Gen-Decoder-A))






(defun Gen-Decoder-B-1 (paragraph)
  ;you should implement this function
)
